package com.example.controller;

import com.example.common.Result;
import com.example.entity.CourseContent;
import com.example.service.CourseContentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程内容控制器
 */
@RestController
@RequestMapping("/courseContent")
public class CourseContentController {

  @Resource
  private CourseContentService courseContentService;

  /**
   * 新增课程内容
   */
  @PostMapping("/add")
  public Result add(@RequestBody CourseContent courseContent) {
    courseContentService.add(courseContent);
    return Result.success();
  }

  /**
   * 删除课程内容
   */
  @DeleteMapping("/delete/{id}")
  public Result delete(@PathVariable Integer id) {
    courseContentService.deleteById(id);
    return Result.success();
  }

  /**
   * 更新课程内容
   */
  @PutMapping("/update")
  public Result update(@RequestBody CourseContent courseContent) {
    courseContentService.updateContentById(courseContent);
    return Result.success();
  }

  /**
   * 查询单个课程内容
   */
  @GetMapping("/selectById/{id}")
  public Result selectById(@PathVariable Integer id) {
    CourseContent courseContent = courseContentService.selectById(id);
    return Result.success(courseContent);
  }

  /**
   * 根据课程ID查询所有内容
   */
  @GetMapping("/selectByCourseId/{courseId}")
  public Result selectByCourseId(@PathVariable Integer courseId) {
    List<CourseContent> list = courseContentService.selectByCourseId(courseId);
    return Result.success(list);
  }

  /**
   * 分页查询课程内容
   */
  @GetMapping("/selectPage")
  public Result selectPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Integer courseId) {

    PageInfo<CourseContent> pageInfo = courseContentService.selectPage(pageNum, pageSize, courseId);
    return Result.success(pageInfo);
  }
}