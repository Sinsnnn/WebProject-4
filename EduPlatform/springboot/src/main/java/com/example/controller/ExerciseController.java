package com.example.controller;

import com.example.common.Result;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Exercise;
import com.example.service.ExerciseService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 习题控制器
 */
@RestController
@RequestMapping("/exercise")
public class ExerciseController {

  @Resource
  private ExerciseService exerciseService;

  /**
   * 新增习题
   */
  @PostMapping("/add")
  public Result add(@RequestBody Exercise exercise) {
    exerciseService.add(exercise);
    return Result.success();
  }

  /**
   * 删除习题
   */
  @DeleteMapping("/delete/{id}")
  public Result delete(@PathVariable Integer id) {
    exerciseService.deleteById(id);
    return Result.success();
  }

  /**
   * 批量删除习题
   */
  @DeleteMapping("/delete/batch")
  public Result delete(@RequestBody List<Integer> ids) {
    exerciseService.deleteBatch(ids);
    return Result.success();
  }

  /**
   * 更新习题
   */
  @PutMapping("/update")
  public Result update(@RequestBody Exercise exercise) {
    exerciseService.updateExerciseById(exercise);
    return Result.success();
  }

  /**
   * 查询单个习题
   */
  @GetMapping("/selectById/{id}")
  public Result selectById(@PathVariable Integer id) {
    Exercise exercise = exerciseService.selectById(id);
    return Result.success(exercise);
  }

  /**
   * 查询所有习题
   */
  @GetMapping("/selectAll")
  public Result selectAll(Exercise exercise) {
    List<Exercise> list = exerciseService.selectAll(exercise);
    return Result.success(list);
  }

  /**
   * 分页查询习题
   */
  @GetMapping("/selectPage")
  public Result selectPage(
      Exercise exercise,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    PageInfo<Exercise> pageInfo = exerciseService.selectPage(exercise, pageNum, pageSize);
    return Result.success(pageInfo);
  }

  /**
   * 查询当前教师的所有习题
   */
  @GetMapping("/selectMyExercises")
  public Result selectMyExercises() {
    Account currentUser = TokenUtils.getCurrentUser();
    List<Exercise> list = exerciseService.selectByTeacherId(currentUser.getId());
    return Result.success(list);
  }

  /**
   * 查询指定课程的所有习题
   */
  @GetMapping("/selectByCourseId/{courseId}")
  public Result selectByCourseId(@PathVariable Integer courseId) {
    List<Exercise> list = exerciseService.selectByCourseId(courseId);
    return Result.success(list);
  }
}