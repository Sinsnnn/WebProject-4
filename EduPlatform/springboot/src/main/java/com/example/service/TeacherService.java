package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Account;
import com.example.entity.Teacher;
import com.example.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface TeacherService extends IService<Teacher> {

    /**
     * 新增
     */
    public void add(Teacher teacher);

    /**
     * 删除
     */
    public void deleteById(Integer id);

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids);

    /**
     * 修改
     */
    public void updateTeacherById(Teacher teacher);

    /**
     * 根据ID查询
     */
    public Teacher selectById(Integer id);

    /**
     * 查询所有
     */
    public List<Teacher> selectAll(Teacher teacher);

    /**
     * 分页查询
     */
    public PageInfo<Teacher> selectPage(Teacher teacher, Integer pageNum, Integer pageSize);

    /**
     * 注册
     */
    public void register(Account account);

    public Teacher login(Account account);

    /**
     * 修改密码
     */
    public void updatePassword(Account account);
}
