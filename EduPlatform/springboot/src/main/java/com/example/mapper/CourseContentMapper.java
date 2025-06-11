package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.CourseContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程内容Mapper接口
 */
@Mapper
public interface CourseContentMapper extends BaseMapper<CourseContent> {
}