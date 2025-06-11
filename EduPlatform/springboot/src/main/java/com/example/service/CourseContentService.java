package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.CourseContent;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 课程内容服务接口
 */
public interface CourseContentService extends IService<CourseContent> {

  /**
   * 添加课程内容
   * 
   * @param courseContent 课程内容
   */
  void add(CourseContent courseContent);

  /**
   * 根据ID删除课程内容
   * 
   * @param id 内容ID
   */
  void deleteById(Integer id);

  /**
   * 更新课程内容
   * 
   * @param courseContent 课程内容
   */
  void updateContentById(CourseContent courseContent);

  /**
   * 根据ID查询课程内容
   * 
   * @param id 内容ID
   * @return 课程内容
   */
  CourseContent selectById(Integer id);

  /**
   * 根据课程ID查询所有内容
   * 
   * @param courseId 课程ID
   * @return 内容列表
   */
  List<CourseContent> selectByCourseId(Integer courseId);

  /**
   * 分页查询课程内容
   * 
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @param courseId 课程ID（可选）
   * @return 分页结果
   */
  PageInfo<CourseContent> selectPage(Integer pageNum, Integer pageSize, Integer courseId);
}