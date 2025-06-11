package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.PostComment;

import java.util.List;

/**
 * 动态评论服务接口
 */
public interface PostCommentService extends IService<PostComment> {

  /**
   * 添加评论
   * 
   * @param comment 评论信息
   */
  void add(PostComment comment);

  /**
   * 删除评论
   * 
   * @param id 评论ID
   */
  void deleteById(Integer id);

  /**
   * 查询动态的评论列表
   * 
   * @param postId 动态ID
   * @return 评论列表
   */
  List<PostComment> selectByPostId(Integer postId);

  /**
   * 根据ID查询评论
   * 
   * @param id 评论ID
   * @return 评论信息
   */
  PostComment selectById(Integer id);
}