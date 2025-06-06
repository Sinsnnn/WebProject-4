package com.example.controller;

import com.example.common.Result;
import com.example.entity.Course;
import com.example.entity.CourseContent;
import com.example.entity.Post;
import com.example.service.SearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {

  @Resource
  private SearchService searchService;

  /**
   * 综合搜索
   */
  @GetMapping
  public Result search(@RequestParam String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Result.error("400", "搜索关键词不能为空");
    }

    Map<String, Object> result = searchService.search(keyword);
    return Result.success(result);
  }

  /**
   * 搜索课程
   */
  @GetMapping("/courses")
  public Result searchCourses(@RequestParam String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Result.error("400", "搜索关键词不能为空");
    }

    List<Course> courses = searchService.searchCourses(keyword);
    return Result.success(courses);
  }

  /**
   * 搜索课程内容
   */
  @GetMapping("/courseContents")
  public Result searchCourseContents(@RequestParam String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Result.error("400", "搜索关键词不能为空");
    }

    List<CourseContent> contents = searchService.searchCourseContents(keyword);
    return Result.success(contents);
  }

  /**
   * 搜索动态
   */
  @GetMapping("/posts")
  public Result searchPosts(@RequestParam String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Result.error("400", "搜索关键词不能为空");
    }

    List<Post> posts = searchService.searchPosts(keyword);
    return Result.success(posts);
  }
}