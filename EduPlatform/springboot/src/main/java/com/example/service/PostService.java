package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Post;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 学习动态服务接口
 */
public interface PostService extends IService<Post> {

  /**
   * 发布动态
   * 
   * @param post 动态信息
   */
  void add(Post post);

  /**
   * 删除动态
   * 
   * @param id 动态ID
   */
  void deleteById(Integer id);

  /**
   * 查询动态详情
   * 
   * @param id 动态ID
   * @return 动态详情
   */
  Post selectById(Integer id);

  /**
   * 分页查询动态列表
   * 
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<Post> selectPage(Integer pageNum, Integer pageSize);

  /**
   * 点赞/取消点赞
   * 
   * @param postId 动态ID
   * @param userId 用户ID
   * @return 点赞状态：true-已点赞，false-未点赞
   */
  boolean toggleLike(Integer postId, Integer userId);

  /**
   * 查询用户是否点赞
   * 
   * @param postId 动态ID
   * @param userId 用户ID
   * @return 是否点赞
   */
  boolean isLiked(Integer postId, Integer userId);

  /**
   * 查询用户的动态列表
   * 
   * @param userId   用户ID
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<Post> selectByUserId(Integer userId, Integer pageNum, Integer pageSize);
}