package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 学习动态实体类
 */
@Data
@TableName("post")
public class Post {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 发布用户ID
   */
  private Integer userId;

  /**
   * 动态内容
   */
  private String content;

  /**
   * 图片列表（JSON格式）
   */
  private String images;

  /**
   * 点赞数
   */
  private Integer likeCount;

  /**
   * 评论数
   */
  private Integer commentCount;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  /**
   * 发布用户信息（非数据库字段）
   */
  @TableField(exist = false)
  private Account user;

  /**
   * 评论列表（非数据库字段）
   */
  @TableField(exist = false)
  private List<PostComment> comments;

  /**
   * 当前用户是否点赞（非数据库字段）
   */
  @TableField(exist = false)
  private Boolean isLiked;
}