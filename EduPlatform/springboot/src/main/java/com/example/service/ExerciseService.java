package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Exercise;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 习题服务接口
 */
public interface ExerciseService extends IService<Exercise> {

  /**
   * 添加习题
   * 
   * @param exercise 习题信息
   */
  void add(Exercise exercise);

  /**
   * 根据ID删除习题
   * 
   * @param id 习题ID
   */
  void deleteById(Integer id);

  /**
   * 批量删除习题
   * 
   * @param ids 习题ID列表
   */
  void deleteBatch(List<Integer> ids);

  /**
   * 更新习题信息
   * 
   * @param exercise 习题信息
   */
  void updateExerciseById(Exercise exercise);

  /**
   * 根据ID查询习题
   * 
   * @param id 习题ID
   * @return 习题信息
   */
  Exercise selectById(Integer id);

  /**
   * 查询所有习题
   * 
   * @param exercise 查询条件
   * @return 习题列表
   */
  List<Exercise> selectAll(Exercise exercise);

  /**
   * 分页查询习题
   * 
   * @param exercise 查询条件
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<Exercise> selectPage(Exercise exercise, Integer pageNum, Integer pageSize);

  /**
   * 查询教师的所有习题
   * 
   * @param teacherId 教师ID
   * @return 习题列表
   */
  List<Exercise> selectByTeacherId(Integer teacherId);

  /**
   * 查询课程的所有习题
   * 
   * @param courseId 课程ID
   * @return 习题列表
   */
  List<Exercise> selectByCourseId(Integer courseId);

  /**
   * 分页查询习题（按条件筛选）
   * 
   * @param pageNum    页码
   * @param pageSize   每页数量
   * @param courseId   课程ID
   * @param difficulty 难度
   * @param type       题目类型
   * @return 分页结果
   */
  PageInfo<Exercise> selectPage(Integer pageNum, Integer pageSize, Integer courseId, Integer difficulty, Integer type);
}