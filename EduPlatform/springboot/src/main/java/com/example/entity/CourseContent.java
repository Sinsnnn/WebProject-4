package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 课程内容实体类
 */
@Data
@TableName("course_content")
public class CourseContent {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 课程ID
   */
  private Integer courseId;

  /**
   * 章节标题
   */
  private String title;

  /**
   * 章节内容
   */
  private String content;

  /**
   * 视频链接
   */
  private String video;

  /**
   * 附件链接
   */
  private String attachment;

  /**
   * 排序
   */
  private Integer sort;

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

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }
}