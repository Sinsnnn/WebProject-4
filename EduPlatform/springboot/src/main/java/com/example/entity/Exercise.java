package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 习题实体类
 */
@Data
@TableName("exercise")
public class Exercise {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 题目标题
   */
  private String title;

  /**
   * 题目内容
   */
  private String content;

  /**
   * 参考答案
   */
  private String answer;

  /**
   * 题目类型：0-选择题，1-填空题，2-判断题，3-简答题
   */
  private Integer type;

  /**
   * 选项（选择题使用）
   */
  private String options;

  /**
   * 所属课程ID
   */
  private Integer courseId;

  /**
   * 创建教师ID
   */
  private Integer teacherId;

  /**
   * 难度：0-简单，1-中等，2-困难
   */
  private Integer difficulty;

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
   * 课程名称（非数据库字段）
   */
  @TableField(exist = false)
  private String courseName;

  /**
   * 分值（非数据库字段，用于测试中）
   */
  @TableField(exist = false)
  private Integer score;
}