package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Post;
import com.example.entity.PostComment;
import com.example.mapper.PostCommentMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.UserMapper;
import com.example.service.PostCommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 动态评论服务实现类
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

  @Resource
  private PostCommentMapper postCommentMapper;

  @Resource
  private PostMapper postMapper;

  @Resource
  private UserMapper userMapper;

  /**
   * 添加评论
   * 
   * @param comment 评论信息
   */
  @Override
  @Transactional
  public void add(PostComment comment) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();
    comment.setUserId(currentUser.getId());
    comment.setCreateTime(new Date());
    postCommentMapper.insert(comment);

    // 更新动态评论数
    Post post = postMapper.selectById(comment.getPostId());
    if (post != null) {
      post.setCommentCount(post.getCommentCount() + 1);
      postMapper.updateById(post);
    }
  }

  /**
   * 删除评论
   * 
   * @param id 评论ID
   */
  @Override
  @Transactional
  public void deleteById(Integer id) {
    PostComment comment = postCommentMapper.selectById(id);
    if (comment != null) {
      // 删除评论
      postCommentMapper.deleteById(id);

      // 删除回复
      LambdaQueryWrapper<PostComment> replyWrapper = new LambdaQueryWrapper<>();
      replyWrapper.eq(PostComment::getParentId, id);
      int replyCount = Math.toIntExact(postCommentMapper.selectCount(replyWrapper));
      postCommentMapper.delete(replyWrapper);

      // 更新动态评论数
      Post post = postMapper.selectById(comment.getPostId());
      if (post != null) {
        post.setCommentCount(post.getCommentCount() - replyCount - 1);
        postMapper.updateById(post);
      }
    }
  }

  /**
   * 查询动态的评论列表
   * 
   * @param postId 动态ID
   * @return 评论列表
   */
  @Override
  public List<PostComment> selectByPostId(Integer postId) {
    // 查询一级评论
    LambdaQueryWrapper<PostComment> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(PostComment::getPostId, postId);
    wrapper.isNull(PostComment::getParentId);
    wrapper.orderByDesc(PostComment::getCreateTime);
    List<PostComment> comments = postCommentMapper.selectList(wrapper);

    // 填充用户信息和回复
    for (PostComment comment : comments) {
      // 设置评论用户信息
      Account user = userMapper.selectById(comment.getUserId());
      comment.setUser(user);

      // 查询回复
      LambdaQueryWrapper<PostComment> replyWrapper = new LambdaQueryWrapper<>();
      replyWrapper.eq(PostComment::getParentId, comment.getId());
      replyWrapper.orderByAsc(PostComment::getCreateTime);
      List<PostComment> replies = postCommentMapper.selectList(replyWrapper);

      // 填充回复用户信息
      for (PostComment reply : replies) {
        Account replyUser = userMapper.selectById(reply.getUserId());
        reply.setUser(replyUser);
      }

      comment.setReplies(replies);
    }

    return comments;
  }

  /**
   * 根据ID查询评论
   * 
   * @param id 评论ID
   * @return 评论信息
   */
  @Override
  public PostComment selectById(Integer id) {
    return postCommentMapper.selectById(id);
  }
}