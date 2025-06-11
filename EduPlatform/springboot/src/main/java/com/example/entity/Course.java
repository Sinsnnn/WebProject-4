package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 课程名称
   */
  private String name;

  /**
   * 课程描述
   */
  private String description;

  /**
   * 课程封面
   */
  private String cover;

  /**
   * 教师ID
   */
  private Integer teacherId;

  /**
   * 教师名称
   */
  private String teacherName;

  /**
   * 课程状态：0-未发布，1-已发布
   */
  private Integer status;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
  private Date createTime;

  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
  private Date updateTime;
}