package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.ExerciseRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 习题记录Mapper接口
 */
@Mapper
public interface ExerciseRecordMapper extends BaseMapper<ExerciseRecord> {
}