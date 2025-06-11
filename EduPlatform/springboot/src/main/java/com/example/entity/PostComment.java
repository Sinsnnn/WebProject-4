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
 * 动态评论实体类
 */
@Data
@TableName("post_comment")
public class PostComment {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 动态ID
   */
  private Integer postId;

  /**
   * 评论用户ID
   */
  private Integer userId;

  /**
   * 评论内容
   */
  private String content;

  /**
   * 父评论ID（用于回复）
   */
  private Integer parentId;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
  private Date createTime;

  /**
   * 评论用户信息（非数据库字段）
   */
  @TableField(exist = false)
  private Account user;

  /**
   * 回复列表（非数据库字段）
   */
  @TableField(exist = false)
  private List<PostComment> replies;
}