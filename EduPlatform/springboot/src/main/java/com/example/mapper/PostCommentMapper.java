package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态评论Mapper接口
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
}