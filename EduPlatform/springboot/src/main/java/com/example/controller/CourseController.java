package com.example.controller;

import com.example.common.Result;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.service.CourseService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/course")
public class CourseController {

  @Resource
  private CourseService courseService;

  /**
   * 新增课程
   */
  @PostMapping("/add")
  public Result add(@RequestBody Course course) {
    courseService.add(course);
    return Result.success();
  }

  /**
   * 删除课程
   */
  @DeleteMapping("/delete/{id}")
  public Result delete(@PathVariable Integer id) {
    courseService.deleteById(id);
    return Result.success();
  }

  /**
   * 批量删除课程
   */
  @DeleteMapping("/delete/batch")
  public Result delete(@RequestBody List<Integer> ids) {
    courseService.deleteBatch(ids);
    return Result.success();
  }

  /**
   * 更新课程
   */
  @PutMapping("/update")
  public Result update(@RequestBody Course course) {
    courseService.updateCourseById(course);
    return Result.success();
  }

  /**
   * 查询单个课程
   */
  @GetMapping("/selectById/{id}")
  public Result selectById(@PathVariable Integer id) {
    Course course = courseService.selectById(id);
    return Result.success(course);
  }

  /**
   * 查询所有课程
   */
  @GetMapping("/selectAll")
  public Result selectAll(Course course) {
    List<Course> list = courseService.selectAll(course);
    return Result.success(list);
  }

  /**
   * 分页查询课程
   */
  @GetMapping("/selectPage")
  public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer status) {
    Course course = new Course();
    course.setName(name);
    course.setStatus(status);

    PageInfo<Course> pageInfo = courseService.selectPage(course, pageNum, pageSize);
    return Result.success(pageInfo);
  }

  /**
   * 查询当前教师的所有课程
   */
  @GetMapping("/selectMyCourses")
  public Result selectMyCourses() {
    Account currentUser = TokenUtils.getCurrentUser();
    List<Course> list = courseService.selectByTeacherId(currentUser.getId());
    return Result.success(list);
  }
}