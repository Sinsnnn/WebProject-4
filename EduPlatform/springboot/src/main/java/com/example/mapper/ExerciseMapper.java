package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Exercise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 习题Mapper接口
 */
@Mapper
public interface ExerciseMapper extends BaseMapper<Exercise> {
}