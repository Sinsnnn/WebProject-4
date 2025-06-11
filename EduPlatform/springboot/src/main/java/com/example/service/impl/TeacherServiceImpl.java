package com.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Constants;
import com.example.common.config.TokenUtils;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Teacher;
import com.example.exception.CustomException;
import com.example.mapper.TeacherMapper;
import com.example.service.TeacherService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    /**
     * 新增
     */
    @Override
    public void add(Teacher teacher) {
        // 唯一校验
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getUsername, teacher.getUsername());
        Teacher dbTeacher = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(teacher.getPassword())) {
            teacher.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(teacher.getName())) {
            teacher.setName(teacher.getUsername());
        }
        teacher.setRole("teacher");
        this.save(teacher);
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
        this.removeBatchByIds(ids);
    }

    /**
     * 修改
     */
    @Override
    public void updateTeacherById(Teacher teacher) {
        this.updateById(teacher);
    }

    /**
     * 根据ID查询
     */
    @Override
    public Teacher selectById(Integer id) {
        return this.getById(id);
    }

    /**
     * 查询所有
     */
    @Override
    public List<Teacher> selectAll(Teacher teacher) {
        return this.list();
    }

    /**
     * 分页查询
     */
    @Override
    public PageInfo<Teacher> selectPage(Teacher teacher, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Teacher> list = this.selectAll(teacher);

        return PageInfo.of(list);
    }

    /**
     * 注册
     */
    @Override
    public void register(Account account) {
        Teacher teacher = new Teacher();
        teacher.setUsername(account.getUsername());
        teacher.setPassword(account.getPassword());
        this.add(teacher);
    }

    public Teacher login(Account account) {
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getUsername, account.getUsername());
        Teacher dbTeacher = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbTeacher.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbTeacher.getId() + "-teacher";
        String token = TokenUtils.genToken(tokenData, dbTeacher.getPassword());
        dbTeacher.setToken(token);

        return dbTeacher;
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(Account account) {
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getUsername, account.getUsername());
        Teacher dbTeacher = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbTeacher.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbTeacher.setPassword(account.getNewPassword());
        this.updateById(dbTeacher);
    }
}
