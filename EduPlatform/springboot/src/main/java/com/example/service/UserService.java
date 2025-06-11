package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.common.config.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface UserService extends IService<User> {


    /**
     * 新增
     */
    public void add(User user);

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
    public void updateUserById(User user);

    /**
     * 根据ID查询
     */
    public User selectById(Integer id);

    /**
     * 查询所有
     */
    public List<User> selectAll(User user);

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize);

    /**
     * 注册
     */
    public void register(Account account);

    public User login(Account account);

    /**
     * 修改密码
     */
    public void updatePassword(Account account);

}