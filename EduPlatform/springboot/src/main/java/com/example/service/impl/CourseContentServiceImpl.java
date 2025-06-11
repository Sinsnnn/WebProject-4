package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.CourseContent;
import com.example.mapper.CourseContentMapper;
import com.example.service.CourseContentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.entity.Course;
import com.example.mapper.CourseMapper;

import java.util.Date;
import java.util.List;

/**
 * 课程内容服务实现类
 */
@Service
public class CourseContentServiceImpl extends ServiceImpl<CourseContentMapper, CourseContent>
    implements CourseContentService {

  @Resource
  private CourseContentMapper courseContentMapper;

  @Resource
  private CourseMapper courseMapper;

  /**
   * 添加课程内容
   * 
   * @param courseContent 课程内容
   */
  @Override
  public void add(CourseContent courseContent) {
    courseContent.setCreateTime(new Date());
    courseContent.setUpdateTime(new Date());
    courseContentMapper.insert(courseContent);
  }

  /**
   * 根据ID删除课程内容
   * 
   * @param id 内容ID
   */
  @Override
  public void deleteById(Integer id) {
    courseContentMapper.deleteById(id);
  }

  /**
   * 更新课程内容
   * 
   * @param courseContent 课程内容
   */
  @Override
  public void updateContentById(CourseContent courseContent) {
    courseContent.setUpdateTime(new Date());
    courseContentMapper.updateById(courseContent);
  }

  /**
   * 根据ID查询课程内容
   * 
   * @param id 内容ID
   * @return 课程内容
   */
  @Override
  public CourseContent selectById(Integer id) {
    return courseContentMapper.selectById(id);
  }

  /**
   * 根据课程ID查询所有内容
   * 
   * @param courseId 课程ID
   * @return 内容列表
   */
  @Override
  public List<CourseContent> selectByCourseId(Integer courseId) {
    LambdaQueryWrapper<CourseContent> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(CourseContent::getCourseId, courseId);
    wrapper.orderByAsc(CourseContent::getSort);
    return courseContentMapper.selectList(wrapper);
  }

  /**
   * 分页查询课程内容
   * 
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @param courseId 课程ID（可选）
   * @return 分页结果
   */
  @Override
  public PageInfo<CourseContent> selectPage(Integer pageNum, Integer pageSize, Integer courseId) {
    PageHelper.startPage(pageNum, pageSize);

    LambdaQueryWrapper<CourseContent> wrapper = new LambdaQueryWrapper<>();

    // 如果指定了课程ID，则按课程ID查询
    if (courseId != null) {
      wrapper.eq(CourseContent::getCourseId, courseId);
    }

    // 按创建时间降序排序
    wrapper.orderByDesc(CourseContent::getCreateTime);

    List<CourseContent> contents = courseContentMapper.selectList(wrapper);

    // 填充课程信息
    for (CourseContent content : contents) {
      Course course = courseMapper.selectById(content.getCourseId());
      if (course != null) {
        content.setCourseName(course.getName());
      }
    }

    return new PageInfo<>(contents);
  }
}