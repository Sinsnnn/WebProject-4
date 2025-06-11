package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Post;
import com.example.entity.PostComment;
import com.example.entity.PostLike;
import com.example.mapper.PostCommentMapper;
import com.example.mapper.PostLikeMapper;
import com.example.mapper.PostMapper;
import com.example.mapper.UserMapper;
import com.example.service.PostService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.TimeZone;

/**
 * 学习动态服务实现类
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

  @Resource
  private PostMapper postMapper;

  @Resource
  private PostCommentMapper postCommentMapper;

  @Resource
  private PostLikeMapper postLikeMapper;

  @Resource
  private UserMapper userMapper;

  /**
   * 发布动态
   * 
   * @param post 动态信息
   */
  @Override
  public void add(Post post) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();
    post.setUserId(currentUser.getId());
    post.setLikeCount(0);
    post.setCommentCount(0);
    post.setCreateTime(new Date());
    post.setUpdateTime(new Date());
    postMapper.insert(post);
  }

  /**
   * 删除动态
   * 
   * @param id 动态ID
   */
  @Override
  @Transactional
  public void deleteById(Integer id) {
    // 删除动态
    postMapper.deleteById(id);

    // 删除相关评论
    LambdaQueryWrapper<PostComment> commentWrapper = new LambdaQueryWrapper<>();
    commentWrapper.eq(PostComment::getPostId, id);
    postCommentMapper.delete(commentWrapper);

    // 删除相关点赞
    LambdaQueryWrapper<PostLike> likeWrapper = new LambdaQueryWrapper<>();
    likeWrapper.eq(PostLike::getPostId, id);
    postLikeMapper.delete(likeWrapper);
  }

  /**
   * 查询动态详情
   * 
   * @param id 动态ID
   * @return 动态详情
   */
  @Override
  public Post selectById(Integer id) {
    Post post = postMapper.selectById(id);
    if (post != null) {
      // 填充用户信息
      Account user = userMapper.selectById(post.getUserId());
      post.setUser(user);

      // 填充评论信息
      LambdaQueryWrapper<PostComment> commentWrapper = new LambdaQueryWrapper<>();
      commentWrapper.eq(PostComment::getPostId, id);
      commentWrapper.isNull(PostComment::getParentId); // 只查询一级评论
      commentWrapper.orderByDesc(PostComment::getCreateTime);
      List<PostComment> comments = postCommentMapper.selectList(commentWrapper);

      // 填充评论用户信息和回复
      for (PostComment comment : comments) {
        // 设置评论用户信息
        Account commentUser = userMapper.selectById(comment.getUserId());
        comment.setUser(commentUser);

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

      post.setComments(comments);

      // 检查当前用户是否点赞
      Account currentUser = TokenUtils.getCurrentUser();
      if (currentUser != null) {
        post.setIsLiked(isLiked(id, currentUser.getId()));
      }
    }
    return post;
  }

  /**
   * 分页查询动态列表
   * 
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  @Override
  public PageInfo<Post> selectPage(Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);

    // 查询动态列表
    LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
    wrapper.orderByDesc(Post::getCreateTime);
    List<Post> posts = postMapper.selectList(wrapper);

    // 填充用户信息
    for (Post post : posts) {
      Account user = userMapper.selectById(post.getUserId());
      post.setUser(user);

      // 检查当前用户是否点赞
      Account currentUser = TokenUtils.getCurrentUser();
      if (currentUser != null) {
        post.setIsLiked(isLiked(post.getId(), currentUser.getId()));
      }
    }

    return new PageInfo<>(posts);
  }

  /**
   * 点赞/取消点赞
   * 
   * @param postId 动态ID
   * @param userId 用户ID
   * @return 点赞状态：true-已点赞，false-未点赞
   */
  @Override
  @Transactional
  public boolean toggleLike(Integer postId, Integer userId) {
    // 查询是否已点赞
    LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(PostLike::getPostId, postId);
    wrapper.eq(PostLike::getUserId, userId);
    PostLike like = postLikeMapper.selectOne(wrapper);

    Post post = postMapper.selectById(postId);
    if (post == null) {
      return false;
    }

    if (like == null) {
      // 未点赞，添加点赞
      like = new PostLike();
      like.setPostId(postId);
      like.setUserId(userId);
      like.setCreateTime(new Date());
      postLikeMapper.insert(like);

      // 更新点赞数
      post.setLikeCount(post.getLikeCount() + 1);
      postMapper.updateById(post);

      return true;
    } else {
      // 已点赞，取消点赞
      postLikeMapper.deleteById(like.getId());

      // 更新点赞数
      post.setLikeCount(post.getLikeCount() - 1);
      postMapper.updateById(post);

      return false;
    }
  }

  /**
   * 查询用户是否点赞
   * 
   * @param postId 动态ID
   * @param userId 用户ID
   * @return 是否点赞
   */
  @Override
  public boolean isLiked(Integer postId, Integer userId) {
    LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(PostLike::getPostId, postId);
    wrapper.eq(PostLike::getUserId, userId);
    return postLikeMapper.selectCount(wrapper) > 0;
  }

  /**
   * 查询用户的动态列表
   * 
   * @param userId   用户ID
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  @Override
  public PageInfo<Post> selectByUserId(Integer userId, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);

    // 查询用户动态列表
    LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Post::getUserId, userId);
    wrapper.orderByDesc(Post::getCreateTime);
    List<Post> posts = postMapper.selectList(wrapper);

    // 填充用户信息
    for (Post post : posts) {
      Account user = userMapper.selectById(post.getUserId());
      post.setUser(user);

      // 检查当前用户是否点赞
      Account currentUser = TokenUtils.getCurrentUser();
      if (currentUser != null) {
        post.setIsLiked(isLiked(post.getId(), currentUser.getId()));
      }
    }

    return new PageInfo<>(posts);
  }

  static {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai")); // 或者其他适合的时区
  }
}