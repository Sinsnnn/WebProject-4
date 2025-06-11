package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 习题记录实体类
 */
@Data
@TableName("exercise_record")
public class ExerciseRecord {

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 习题ID
   */
  private Integer exerciseId;

  /**
   * 学生ID
   */
  private Integer userId;

  /**
   * 学生答案
   */
  private String userAnswer;

  /**
   * 得分
   */
  private Integer score;

  /**
   * 是否正确：0-错误，1-正确
   */
  private Integer isCorrect;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /**
   * 习题信息（非数据库字段）
   */
  @TableField(exist = false)
  private Exercise exercise;

  public Exercise getExercise() {
    return exercise;
  }

  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }
}