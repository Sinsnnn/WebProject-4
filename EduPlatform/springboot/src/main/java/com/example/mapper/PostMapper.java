package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习动态Mapper接口
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}