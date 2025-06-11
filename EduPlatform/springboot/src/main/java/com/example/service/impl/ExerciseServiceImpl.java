package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.entity.Exercise;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExerciseMapper;
import com.example.service.ExerciseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 习题服务实现类
 */
@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements ExerciseService {

  @Resource
  private ExerciseMapper exerciseMapper;

  @Resource
  private CourseMapper courseMapper;

  /**
   * 添加习题
   * 
   * @param exercise 习题信息
   */
  @Override
  public void add(Exercise exercise) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();
    exercise.setTeacherId(currentUser.getId());
    exercise.setCreateTime(new Date());
    exercise.setUpdateTime(new Date());

    // 获取课程名称 - 只用于显示，不保存到数据库
    if (exercise.getCourseId() != null) {
      Course course = courseMapper.selectById(exercise.getCourseId());
      if (course != null) {
        exercise.setCourseName(course.getName());
      }
    }

    // 确保不会尝试保存 courseName 到数据库
    String courseName = exercise.getCourseName();
    exercise.setCourseName(null);
    exerciseMapper.insert(exercise);
    exercise.setCourseName(courseName); // 恢复 courseName 用于返回
  }

  /**
   * 根据ID删除习题
   * 
   * @param id 习题ID
   */
  @Override
  public void deleteById(Integer id) {
    exerciseMapper.deleteById(id);
  }

  /**
   * 批量删除习题
   * 
   * @param ids 习题ID列表
   */
  @Override
  public void deleteBatch(List<Integer> ids) {
    exerciseMapper.deleteBatchIds(ids);
  }

  /**
   * 更新习题信息
   * 
   * @param exercise 习题信息
   */
  @Override
  public void updateExerciseById(Exercise exercise) {
    exercise.setUpdateTime(new Date());

    // 获取课程名称 - 只用于显示，不保存到数据库
    if (exercise.getCourseId() != null) {
      Course course = courseMapper.selectById(exercise.getCourseId());
      if (course != null) {
        exercise.setCourseName(course.getName());
      }
    }

    // 确保不会尝试保存 courseName 到数据库
    String courseName = exercise.getCourseName();
    exercise.setCourseName(null);
    exerciseMapper.updateById(exercise);
    exercise.setCourseName(courseName); // 恢复 courseName 用于返回
  }

  /**
   * 根据ID查询习题
   * 
   * @param id 习题ID
   * @return 习题信息
   */
  @Override
  public Exercise selectById(Integer id) {
    Exercise exercise = exerciseMapper.selectById(id);
    if (exercise != null) {
      // 填充课程名称
      Course course = courseMapper.selectById(exercise.getCourseId());
      if (course != null) {
        exercise.setCourseName(course.getName());
      }
    }
    return exercise;
  }

  /**
   * 查询所有习题
   * 
   * @param exercise 查询条件
   * @return 习题列表
   */
  @Override
  public List<Exercise> selectAll(Exercise exercise) {
    LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();
    // 根据题目标题模糊查询
    if (exercise.getTitle() != null) {
      wrapper.like(Exercise::getTitle, exercise.getTitle());
    }
    // 根据课程ID查询
    if (exercise.getCourseId() != null) {
      wrapper.eq(Exercise::getCourseId, exercise.getCourseId());
    }
    // 根据教师ID查询
    if (exercise.getTeacherId() != null) {
      wrapper.eq(Exercise::getTeacherId, exercise.getTeacherId());
    }
    // 根据题目类型查询
    if (exercise.getType() != null) {
      wrapper.eq(Exercise::getType, exercise.getType());
    }
    // 根据难度查询
    if (exercise.getDifficulty() != null) {
      wrapper.eq(Exercise::getDifficulty, exercise.getDifficulty());
    }
    // 按创建时间降序排序
    wrapper.orderByDesc(Exercise::getCreateTime);

    List<Exercise> exercises = exerciseMapper.selectList(wrapper);

    // 填充课程名称
    for (Exercise e : exercises) {
      if (e.getCourseId() != null) {
        Course course = courseMapper.selectById(e.getCourseId());
        if (course != null) {
          e.setCourseName(course.getName());
        }
      }
    }

    return exercises;
  }

  /**
   * 分页查询习题
   * 
   * @param exercise 查询条件
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  @Override
  public PageInfo<Exercise> selectPage(Exercise exercise, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Exercise> list = selectAll(exercise);
    return new PageInfo<>(list);
  }

  /**
   * 查询教师的所有习题
   * 
   * @param teacherId 教师ID
   * @return 习题列表
   */
  @Override
  public List<Exercise> selectByTeacherId(Integer teacherId) {
    Exercise exercise = new Exercise();
    exercise.setTeacherId(teacherId);
    return selectAll(exercise);
  }

  /**
   * 查询课程的所有习题
   * 
   * @param courseId 课程ID
   * @return 习题列表
   */
  @Override
  public List<Exercise> selectByCourseId(Integer courseId) {
    Exercise exercise = new Exercise();
    exercise.setCourseId(courseId);
    return selectAll(exercise);
  }

  /**
   * 查询课程的所有习题
   * 
   * @param courseId   课程ID
   * @param difficulty 难度
   * @param type       题目类型
   * @return 习题列表
   */
  @Override
  public PageInfo<Exercise> selectPage(Integer pageNum, Integer pageSize, Integer courseId, Integer difficulty,
      Integer type) {
    PageHelper.startPage(pageNum, pageSize);

    LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();
    if (courseId != null) {
      wrapper.eq(Exercise::getCourseId, courseId);
    }
    if (difficulty != null) {
      wrapper.eq(Exercise::getDifficulty, difficulty);
    }
    if (type != null) {
      wrapper.eq(Exercise::getType, type);
    }
    wrapper.orderByDesc(Exercise::getCreateTime);

    List<Exercise> exercises = exerciseMapper.selectList(wrapper);

    // 填充课程名称
    for (Exercise exercise : exercises) {
      Course course = courseMapper.selectById(exercise.getCourseId());
      if (course != null) {
        exercise.setCourseName(course.getName());
      }
    }

    return new PageInfo<>(exercises);
  }
}