package com.example.controller;

import com.example.common.Result;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Post;
import com.example.service.PostService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 学习动态控制器
 */
@RestController
@RequestMapping("/post")
public class PostController {

  @Resource
  private PostService postService;

  /**
   * 发布动态
   */
  @PostMapping("/add")
  public Result add(@RequestBody Post post) {
    postService.add(post);
    return Result.success();
  }

  /**
   * 删除动态
   */
  @DeleteMapping("/delete/{id}")
  public Result delete(@PathVariable Integer id) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();

    // 查询动态信息
    Post post = postService.selectById(id);
    if (post == null) {
      return Result.error("动态不存在");
    }

    // 只有动态发布者或管理员可以删除
    if (post.getUserId().equals(currentUser.getId()) || "admin".equals(currentUser.getRole())) {
      postService.deleteById(id);
      return Result.success();
    } else {
      return Result.error("无权限删除");
    }
  }

  /**
   * 查询动态详情
   */
  @GetMapping("/selectById/{id}")
  public Result selectById(@PathVariable Integer id) {
    Post post = postService.selectById(id);
    return Result.success(post);
  }

  /**
   * 分页查询动态列表
   */
  @GetMapping("/selectPage")
  public Result selectPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    PageInfo<Post> pageInfo = postService.selectPage(pageNum, pageSize);
    return Result.success(pageInfo);
  }

  /**
   * 点赞/取消点赞
   */
  @PostMapping("/toggleLike/{postId}")
  public Result toggleLike(@PathVariable Integer postId) {
    Account currentUser = TokenUtils.getCurrentUser();
    boolean isLiked = postService.toggleLike(postId, currentUser.getId());
    return Result.success(isLiked);
  }

  /**
   * 查询用户的动态列表
   */
  @GetMapping("/selectByUserId/{userId}")
  public Result selectByUserId(
      @PathVariable Integer userId,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    PageInfo<Post> pageInfo = postService.selectByUserId(userId, pageNum, pageSize);
    return Result.success(pageInfo);
  }

  /**
   * 查询我的动态列表
   */
  @GetMapping("/selectMyPosts")
  public Result selectMyPosts(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Account currentUser = TokenUtils.getCurrentUser();
    PageInfo<Post> pageInfo = postService.selectByUserId(currentUser.getId(), pageNum, pageSize);
    return Result.success(pageInfo);
  }
}