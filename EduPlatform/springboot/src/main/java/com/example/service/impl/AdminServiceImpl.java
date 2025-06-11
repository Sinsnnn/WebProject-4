package com.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Constants;
import com.example.common.config.TokenUtils;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    /**
     * 新增
     */
    @Override
    public void add(Admin admin) {
        // 唯一校验
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,admin.getUsername());
        Admin dbAdmin = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        admin.setRole("admin");
        this.save(admin);
    }

    /**
     * 删除
     */
    @Override
    public void deleteById(Integer id) {
        this.removeById(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.removeById(id);
        }
    }

    /**
     * 修改
     */
    @Override
    public void updateAdminById(Admin admin) {
        this.updateById(admin);
    }

    /**
     * 根据ID查询
     */
    @Override
    public Admin selectById(Integer id) {
        return this.getById(id);
    }

    /**
     * 查询所有
     */
    @Override
    public List<Admin> selectAll(Admin admin) {
        return this.list();
    }

    /**
     * 分页查询
     */
    @Override
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = this.selectAll(admin);

        return PageInfo.of(list);
    }

    /**
     * 注册
     */
    @Override
    public void register(Account account) {
        Admin admin = new Admin();
        admin.setUsername(account.getUsername());
        admin.setPassword(account.getPassword());
        this.add(admin);
    }

    public Admin login(Account account) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,account.getUsername());
        Admin dbAdmin = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbAdmin.getId() + "-admin";
        String token = TokenUtils.genToken(tokenData, dbAdmin.getPassword());
        dbAdmin.setToken(token);


        return dbAdmin;
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(Account account) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,account.getUsername());
        Admin dbAdmin = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbAdmin.setPassword(account.getNewPassword());
        this.updateById(dbAdmin);
    }
}
