package com.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Constants;
import com.example.common.config.TokenUtils;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 新增
     */
    @Override
    public void add(User user) {
        // 唯一校验
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User dbUser = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole("user");
        this.save(user);
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
    public void updateUserById(User user) {
        this.updateById(user);
    }

    /**
     * 根据ID查询
     */
    @Override
    public User selectById(Integer id) {
        return this.getById(id);
    }

    /**
     * 查询所有
     */
    @Override
    public List<User> selectAll(User user) {
        return this.list();
    }

    /**
     * 分页查询
     */
    @Override
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<User> list = this.selectAll(user);

        return PageInfo.of(list);
    }

    /**
     * 注册
     */
    @Override
    public void register(Account account) {
        User user = new User();
        user.setUsername(account.getUsername());
        user.setPassword(account.getPassword());
        this.add(user);
    }

    public User login(Account account) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,account.getUsername());
        User dbUser = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-user";
        String token = TokenUtils.genToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);


        return dbUser;
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(Account account) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,account.getUsername());
        User dbUser = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        this.updateById(dbUser);
    }
}


