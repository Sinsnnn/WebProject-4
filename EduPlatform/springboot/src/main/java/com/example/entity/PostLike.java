package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 动态点赞实体类
 */
@Data
@TableName("post_like")
public class PostLike {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 动态ID
   */
  private Integer postId;

  /**
   * 点赞用户ID
   */
  private Integer userId;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
  private Date createTime;
}