package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.Course;
import com.example.entity.CourseContent;
import com.example.entity.Post;
import com.example.mapper.CourseContentMapper;
import com.example.mapper.CourseMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.UserMapper;
import com.example.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务实现类
 */
@Service
public class SearchServiceImpl implements SearchService {

  @Resource
  private CourseMapper courseMapper;

  @Resource
  private CourseContentMapper courseContentMapper;

  @Resource
  private PostMapper postMapper;

  @Resource
  private UserMapper userMapper;

  /**
   * 综合搜索
   * 
   * @param keyword 关键词
   * @return 搜索结果
   */
  @Override
  public Map<String, Object> search(String keyword) {
    Map<String, Object> result = new HashMap<>();

    // 搜索课程
    List<Course> courses = searchCourses(keyword);
    result.put("courses", courses);

    // 搜索课程内容
    List<CourseContent> courseContents = searchCourseContents(keyword);
    result.put("courseContents", courseContents);

    // 搜索动态
    List<Post> posts = searchPosts(keyword);
    result.put("posts", posts);

    return result;
  }

  /**
   * 搜索课程
   * 
   * @param keyword 关键词
   * @return 课程列表
   */
  @Override
  public List<Course> searchCourses(String keyword) {
    LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(Course::getName, keyword)
        .or()
        .like(Course::getDescription, keyword);
    wrapper.eq(Course::getStatus, 1); // 只搜索已发布的课程
    return courseMapper.selectList(wrapper);
  }

  /**
   * 搜索课程内容
   * 
   * @param keyword 关键词
   * @return 课程内容列表
   */
  @Override
  public List<CourseContent> searchCourseContents(String keyword) {
    LambdaQueryWrapper<CourseContent> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(CourseContent::getTitle, keyword)
        .or()
        .like(CourseContent::getContent, keyword);

    List<CourseContent> contents = courseContentMapper.selectList(wrapper);

    // 填充课程信息
    for (CourseContent content : contents) {
      Course course = courseMapper.selectById(content.getCourseId());
      if (course != null && course.getStatus() == 1) { // 只返回已发布课程的内容
        content.setCourseName(course.getName());
      } else {
        content.setCourseName("未知课程");
      }
    }

    return contents;
  }

  /**
   * 搜索动态
   * 
   * @param keyword 关键词
   * @return 动态列表
   */
  @Override
  public List<Post> searchPosts(String keyword) {
    LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(Post::getContent, keyword);

    List<Post> posts = postMapper.selectList(wrapper);

    // 填充用户信息
    for (Post post : posts) {
      post.setUser(userMapper.selectById(post.getUserId()));
    }

    return posts;
  }
}