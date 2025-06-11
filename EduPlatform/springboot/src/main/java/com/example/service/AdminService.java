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

public interface AdminService extends IService<Admin> {
    /**
     * 新增
     */
    public void add(Admin admin) ;

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
    public void updateAdminById(Admin admin);

    /**
     * 根据ID查询
     */
    public Admin selectById(Integer id);

    /**
     * 查询所有
     */
    public List<Admin> selectAll(Admin admin);

    /**
     * 分页查询
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) ;

    /**
     * 注册
     */
    public void register(Account account) ;

    public Admin login(Account account) ;

    /**
     * 修改密码
     */
    public void updatePassword(Account account) ;

}