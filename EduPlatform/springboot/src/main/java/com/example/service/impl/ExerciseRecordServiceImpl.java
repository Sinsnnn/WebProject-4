package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Exercise;
import com.example.entity.ExerciseRecord;
import com.example.entity.Course;
import com.example.mapper.ExerciseMapper;
import com.example.mapper.ExerciseRecordMapper;
import com.example.mapper.CourseMapper;
import com.example.service.ExerciseRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 答题记录服务实现类
 */
@Service
public class ExerciseRecordServiceImpl extends ServiceImpl<ExerciseRecordMapper, ExerciseRecord>
    implements ExerciseRecordService {

  @Resource
  private ExerciseRecordMapper exerciseRecordMapper;

  @Resource
  private ExerciseMapper exerciseMapper;

  @Resource
  private CourseMapper courseMapper;

  /**
   * 添加答题记录
   * 
   * @param record 答题记录
   * @return 答题结果，包含是否正确
   */
  @Override
  public ExerciseRecord add(ExerciseRecord record) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();
    record.setUserId(currentUser.getId());
    record.setCreateTime(new Date());

    // 查询习题信息
    Exercise exercise = exerciseMapper.selectById(record.getExerciseId());
    if (exercise != null) {
      // 判断答案是否正确
      boolean isCorrect = checkAnswer(exercise, record.getUserAnswer());
      record.setIsCorrect(isCorrect ? 1 : 0);

      // 计算得分
      record.setScore(isCorrect ? 100 : 0);

      // 保存记录
      exerciseRecordMapper.insert(record);
    }

    return record;
  }

  /**
   * 检查答案是否正确
   * 
   * @param exercise   习题
   * @param userAnswer 用户答案
   * @return 是否正确
   */
  private boolean checkAnswer(Exercise exercise, String userAnswer) {
    if (userAnswer == null || exercise.getAnswer() == null) {
      return false;
    }

    // 根据题目类型判断答案
    switch (exercise.getType()) {
      case 0: // 选择题
        return userAnswer.trim().equalsIgnoreCase(exercise.getAnswer().trim());
      case 1: // 填空题
        return userAnswer.trim().equalsIgnoreCase(exercise.getAnswer().trim());
      case 2: // 判断题
        return userAnswer.trim().equalsIgnoreCase(exercise.getAnswer().trim());
      case 3: // 简答题
        // 简答题需要教师评分，暂时返回false
        return userAnswer.trim().equalsIgnoreCase(exercise.getAnswer().trim());
      default:
        return false;
    }
  }

  /**
   * 查询用户的答题记录
   * 
   * @param userId   用户ID
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  @Override
  public PageInfo<ExerciseRecord> selectByUserId(Integer userId, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);

    LambdaQueryWrapper<ExerciseRecord> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ExerciseRecord::getUserId, userId);
    wrapper.orderByDesc(ExerciseRecord::getCreateTime);
    List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);

    // 填充习题信息
    for (ExerciseRecord record : records) {
      Exercise exercise = exerciseMapper.selectById(record.getExerciseId());
      if (exercise != null) {
        // 填充课程名称
        if (exercise.getCourseId() != null) {
          Course course = courseMapper.selectById(exercise.getCourseId());
          if (course != null) {
            exercise.setCourseName(course.getName());
          }
        }
        record.setExercise(exercise);
      }
    }

    return new PageInfo<>(records);
  }

  /**
   * 查询习题的答题记录
   * 
   * @param exerciseId 习题ID
   * @return 答题记录
   */
  @Override
  public ExerciseRecord selectByExerciseId(Integer exerciseId) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();

    LambdaQueryWrapper<ExerciseRecord> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ExerciseRecord::getExerciseId, exerciseId);
    wrapper.eq(ExerciseRecord::getUserId, currentUser.getId());
    wrapper.orderByDesc(ExerciseRecord::getCreateTime);
    wrapper.last("LIMIT 1"); // 只取最新的一条

    ExerciseRecord record = exerciseRecordMapper.selectOne(wrapper);
    if (record != null) {
      Exercise exercise = exerciseMapper.selectById(record.getExerciseId());
      if (exercise != null) {
        // 填充课程名称
        if (exercise.getCourseId() != null) {
          Course course = courseMapper.selectById(exercise.getCourseId());
          if (course != null) {
            exercise.setCourseName(course.getName());
          }
        }
        record.setExercise(exercise);
      }
    }

    return record;
  }

  /**
   * 查询用户在某课程的答题统计
   * 
   * @param userId   用户ID
   * @param courseId 课程ID
   * @return 统计结果
   */
  @Override
  public Map<String, Object> getStatsByCourse(Integer userId, Integer courseId) {
    Map<String, Object> result = new HashMap<>();

    // 查询课程下的所有习题
    LambdaQueryWrapper<Exercise> exerciseWrapper = new LambdaQueryWrapper<>();
    exerciseWrapper.eq(Exercise::getCourseId, courseId);
    int totalExercises = Math.toIntExact(exerciseMapper.selectCount(exerciseWrapper));

    // 查询用户已答题数量
    LambdaQueryWrapper<ExerciseRecord> recordWrapper = new LambdaQueryWrapper<>();
    recordWrapper.eq(ExerciseRecord::getUserId, userId);
    recordWrapper.inSql(ExerciseRecord::getExerciseId,
        "SELECT id FROM exercise WHERE course_id = " + courseId);
    int answeredExercises = Math.toIntExact(exerciseRecordMapper.selectCount(recordWrapper));

    // 查询用户正确答题数量
    LambdaQueryWrapper<ExerciseRecord> correctWrapper = new LambdaQueryWrapper<>();
    correctWrapper.eq(ExerciseRecord::getUserId, userId);
    correctWrapper.eq(ExerciseRecord::getIsCorrect, 1);
    correctWrapper.inSql(ExerciseRecord::getExerciseId,
        "SELECT id FROM exercise WHERE course_id = " + courseId);
    int correctExercises = Math.toIntExact(exerciseRecordMapper.selectCount(correctWrapper));

    // 计算正确率
    double correctRate = totalExercises > 0 ? (double) correctExercises / answeredExercises * 100 : 0;

    result.put("totalExercises", totalExercises);
    result.put("answeredExercises", answeredExercises);
    result.put("correctExercises", correctExercises);
    result.put("correctRate", Math.round(correctRate * 10) / 10.0); // 保留一位小数

    return result;
  }

  /**
   * 查询用户最近的答题记录
   * 
   * @param userId 用户ID
   * @param limit  限制数量
   * @return 答题记录列表
   */
  @Override
  public List<ExerciseRecord> getRecentRecords(Integer userId, Integer limit) {
    LambdaQueryWrapper<ExerciseRecord> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ExerciseRecord::getUserId, userId);
    wrapper.orderByDesc(ExerciseRecord::getCreateTime);
    wrapper.last("LIMIT " + limit);

    List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);

    // 填充习题信息
    for (ExerciseRecord record : records) {
      Exercise exercise = exerciseMapper.selectById(record.getExerciseId());
      if (exercise != null) {
        // 填充课程名称
        if (exercise.getCourseId() != null) {
          Course course = courseMapper.selectById(exercise.getCourseId());
          if (course != null) {
            exercise.setCourseName(course.getName());
          }
        }
        record.setExercise(exercise);
      }
    }

    return records;
  }

  /**
   * 分页查询习题记录
   * 
   * @param pageNum    页码
   * @param pageSize   每页数量
   * @param userId     用户ID
   * @param exerciseId 习题ID（可选）
   * @param isCorrect  是否正确（可选）
   * @return 分页结果
   */
  @Override
  public PageInfo<ExerciseRecord> selectPage(Integer pageNum, Integer pageSize, Integer userId, Integer exerciseId,
      Integer isCorrect) {
    PageHelper.startPage(pageNum, pageSize);

    LambdaQueryWrapper<ExerciseRecord> wrapper = new LambdaQueryWrapper<>();

    // 按用户ID查询
    if (userId != null) {
      wrapper.eq(ExerciseRecord::getUserId, userId);
    }

    // 按习题ID查询
    if (exerciseId != null) {
      wrapper.eq(ExerciseRecord::getExerciseId, exerciseId);
    }

    // 按是否正确查询
    if (isCorrect != null) {
      wrapper.eq(ExerciseRecord::getIsCorrect, isCorrect);
    }

    // 按创建时间降序排序
    wrapper.orderByDesc(ExerciseRecord::getCreateTime);

    List<ExerciseRecord> records = exerciseRecordMapper.selectList(wrapper);

    // 填充习题信息
    for (ExerciseRecord record : records) {
      Exercise exercise = exerciseMapper.selectById(record.getExerciseId());
      if (exercise != null) {
        // 填充课程名称
        if (exercise.getCourseId() != null) {
          Course course = courseMapper.selectById(exercise.getCourseId());
          if (course != null) {
            exercise.setCourseName(course.getName());
          }
        }
        record.setExercise(exercise);
      }
    }

    return new PageInfo<>(records);
  }
}