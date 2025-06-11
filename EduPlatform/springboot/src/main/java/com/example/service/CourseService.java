package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Course;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

  /**
   * 添加课程
   * 
   * @param course 课程信息
   */
  void add(Course course);

  /**
   * 根据ID删除课程
   * 
   * @param id 课程ID
   */
  void deleteById(Integer id);

  /**
   * 批量删除课程
   * 
   * @param ids 课程ID列表
   */
  void deleteBatch(List<Integer> ids);

  /**
   * 更新课程信息
   * 
   * @param course 课程信息
   */
  void updateCourseById(Course course);

  /**
   * 根据ID查询课程
   * 
   * @param id 课程ID
   * @return 课程信息
   */
  Course selectById(Integer id);

  /**
   * 查询所有课程
   * 
   * @param course 查询条件
   * @return 课程列表
   */
  List<Course> selectAll(Course course);

  /**
   * 分页查询课程
   * 
   * @param course   查询条件
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize);

  /**
   * 查询教师的所有课程
   * 
   * @param teacherId 教师ID
   * @return 课程列表
   */
  List<Course> selectByTeacherId(Integer teacherId);
}