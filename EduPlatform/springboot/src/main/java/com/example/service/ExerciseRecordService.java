package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.ExerciseRecord;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 答题记录服务接口
 */
public interface ExerciseRecordService extends IService<ExerciseRecord> {

  /**
   * 添加答题记录
   * 
   * @param record 答题记录
   * @return 答题结果，包含是否正确
   */
  ExerciseRecord add(ExerciseRecord record);

  /**
   * 查询用户的答题记录
   * 
   * @param userId   用户ID
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<ExerciseRecord> selectByUserId(Integer userId, Integer pageNum, Integer pageSize);

  /**
   * 查询习题的答题记录
   * 
   * @param exerciseId 习题ID
   * @return 答题记录
   */
  ExerciseRecord selectByExerciseId(Integer exerciseId);

  /**
   * 查询用户在某课程的答题统计
   * 
   * @param userId   用户ID
   * @param courseId 课程ID
   * @return 统计结果
   */
  Map<String, Object> getStatsByCourse(Integer userId, Integer courseId);

  /**
   * 查询用户最近的答题记录
   * 
   * @param userId 用户ID
   * @param limit  限制数量
   * @return 答题记录列表
   */
  List<ExerciseRecord> getRecentRecords(Integer userId, Integer limit);

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
  PageInfo<ExerciseRecord> selectPage(Integer pageNum, Integer pageSize, Integer userId, Integer exerciseId,
      Integer isCorrect);
}