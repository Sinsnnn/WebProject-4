package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.config.TokenUtils;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.service.CourseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

  @Resource
  private CourseMapper courseMapper;

  /**
   * 添加课程
   * 
   * @param course 课程信息
   */
  @Override
  public void add(Course course) {
    // 获取当前登录用户
    Account currentUser = TokenUtils.getCurrentUser();
    course.setTeacherId(currentUser.getId());
    course.setTeacherName(currentUser.getName());
    course.setCreateTime(new Date());
    course.setUpdateTime(new Date());
    courseMapper.insert(course);
  }

  /**
   * 根据ID删除课程
   * 
   * @param id 课程ID
   */
  @Override
  public void deleteById(Integer id) {
    courseMapper.deleteById(id);
  }

  /**
   * 批量删除课程
   * 
   * @param ids 课程ID列表
   */
  @Override
  public void deleteBatch(List<Integer> ids) {
    courseMapper.deleteBatchIds(ids);
  }

  /**
   * 更新课程信息
   * 
   * @param course 课程信息
   */
  @Override
  public void updateCourseById(Course course) {
    course.setUpdateTime(new Date());
    courseMapper.updateById(course);
  }

  /**
   * 根据ID查询课程
   * 
   * @param id 课程ID
   * @return 课程信息
   */
  @Override
  public Course selectById(Integer id) {
    return courseMapper.selectById(id);
  }

  /**
   * 查询所有课程
   * 
   * @param course 查询条件
   * @return 课程列表
   */
  @Override
  public List<Course> selectAll(Course course) {
    LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
    // 根据课程名称模糊查询
    if (course.getName() != null) {
      wrapper.like(Course::getName, course.getName());
    }
    // 根据教师ID查询
    if (course.getTeacherId() != null) {
      wrapper.eq(Course::getTeacherId, course.getTeacherId());
    }
    // 根据状态查询
    if (course.getStatus() != null) {
      wrapper.eq(Course::getStatus, course.getStatus());
    }
    // 按创建时间降序排序
    wrapper.orderByDesc(Course::getCreateTime);
    return courseMapper.selectList(wrapper);
  }

  /**
   * 分页查询课程
   * 
   * @param course   查询条件
   * @param pageNum  页码
   * @param pageSize 每页数量
   * @return 分页结果
   */
  @Override
  public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Course> list = selectAll(course);
    return new PageInfo<>(list);
  }

  /**
   * 查询教师的所有课程
   * 
   * @param teacherId 教师ID
   * @return 课程列表
   */
  @Override
  public List<Course> selectByTeacherId(Integer teacherId) {
    LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Course::getTeacherId, teacherId);
    wrapper.orderByDesc(Course::getCreateTime);
    return courseMapper.selectList(wrapper);
  }
}