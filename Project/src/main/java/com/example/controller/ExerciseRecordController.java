package com.example.controller;

import com.example.common.Result;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.ExerciseRecord;
import com.example.service.ExerciseRecordService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 答题记录控制器
 */
@RestController
@RequestMapping("/exerciseRecord")
public class ExerciseRecordController {

  @Resource
  private ExerciseRecordService exerciseRecordService;

  /**
   * 提交答题
   */
  @PostMapping("/add")
  public Result add(@RequestBody ExerciseRecord record) {
    ExerciseRecord result = exerciseRecordService.add(record);
    return Result.success(result);
  }

  /**
   * 查询用户的答题记录
   */
  @GetMapping("/selectByUserId")
  public Result selectByUserId(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Account currentUser = TokenUtils.getCurrentUser();
    PageInfo<ExerciseRecord> pageInfo = exerciseRecordService.selectByUserId(currentUser.getId(), pageNum, pageSize);
    return Result.success(pageInfo);
  }

  /**
   * 查询习题的答题记录
   */
  @GetMapping("/selectByExerciseId/{exerciseId}")
  public Result selectByExerciseId(@PathVariable Integer exerciseId) {
    ExerciseRecord record = exerciseRecordService.selectByExerciseId(exerciseId);
    return Result.success(record);
  }

  /**
   * 查询用户在某课程的答题统计
   */
  @GetMapping("/getStatsByCourse/{courseId}")
  public Result getStatsByCourse(@PathVariable Integer courseId) {
    Account currentUser = TokenUtils.getCurrentUser();
    Map<String, Object> stats = exerciseRecordService.getStatsByCourse(currentUser.getId(), courseId);
    return Result.success(stats);
  }

  /**
   * 查询用户最近的答题记录
   */
  @GetMapping("/getRecentRecords")
  public Result getRecentRecords(@RequestParam(defaultValue = "5") Integer limit) {
    Account currentUser = TokenUtils.getCurrentUser();
    List<ExerciseRecord> records = exerciseRecordService.getRecentRecords(currentUser.getId(), limit);
    return Result.success(records);
  }

  /**
   * 分页查询习题记录
   */
  @GetMapping("/selectPage")
  public Result selectPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Integer userId,
      @RequestParam(required = false) Integer exerciseId,
      @RequestParam(required = false) Integer isCorrect) {

    // 如果未指定用户ID，则使用当前登录用户的ID
    if (userId == null) {
      Account currentUser = TokenUtils.getCurrentUser();
      userId = currentUser.getId();
    }

    PageInfo<ExerciseRecord> pageInfo = exerciseRecordService.selectPage(pageNum, pageSize, userId, exerciseId,
        isCorrect);
    return Result.success(pageInfo);
  }
}