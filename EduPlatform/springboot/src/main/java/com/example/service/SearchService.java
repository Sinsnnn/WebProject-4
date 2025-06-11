package com.example.service;

import com.example.entity.Course;
import com.example.entity.CourseContent;
import com.example.entity.Post;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 */
public interface SearchService {

  /**
   * 综合搜索
   * 
   * @param keyword 关键词
   * @return 搜索结果
   */
  Map<String, Object> search(String keyword);

  /**
   * 搜索课程
   * 
   * @param keyword 关键词
   * @return 课程列表
   */
  List<Course> searchCourses(String keyword);

  /**
   * 搜索课程内容
   * 
   * @param keyword 关键词
   * @return 课程内容列表
   */
  List<CourseContent> searchCourseContents(String keyword);

  /**
   * 搜索动态
   * 
   * @param keyword 关键词
   * @return 动态列表
   */
  List<Post> searchPosts(String keyword);
}