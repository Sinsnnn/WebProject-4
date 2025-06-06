package com.example.controller;

import com.example.common.Result;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.PostComment;
import com.example.service.PostCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态评论控制器
 */
@RestController
@RequestMapping("/postComment")
public class PostCommentController {

  @Resource
  private PostCommentService postCommentService;

  /**
   * 添加评论
   */
  @PostMapping("/add")
  public Result add(@RequestBody PostComment comment) {
    postCommentService.add(comment);
    return Result.success();
  }

  /**
   * 删除评论
   */
  @DeleteMapping("/delete/{id}")
  public Result delete(@PathVariable Integer id) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();

    // 查询评论信息
    PostComment comment = postCommentService.selectById(id);
    if (comment == null) {
      return Result.error("评论不存在");
    }

    // 只有评论发布者或管理员可以删除
    if (comment.getUserId().equals(currentUser.getId()) || "admin".equals(currentUser.getRole())) {
      postCommentService.deleteById(id);
      return Result.success();
    } else {
      return Result.error("无权限删除");
    }
  }

  /**
   * 查询动态的评论列表
   */
  @GetMapping("/selectByPostId/{postId}")
  public Result selectByPostId(@PathVariable Integer postId) {
    List<PostComment> comments = postCommentService.selectByPostId(postId);
    return Result.success(comments);
  }
}