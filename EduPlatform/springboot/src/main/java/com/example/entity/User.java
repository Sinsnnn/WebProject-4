package com.example.entity;

import cn.hutool.core.annotation.Alias;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 用户
 */
@Data
@TableName("user")
public class User extends Account {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 账号 */
	private String username;
	/** 密码 */
	private String password;
	/** 名称 */
	private String name;
	/** 头像 */
	private String avatar;
	/** 手机 */
	private String phone;
	/** 邮箱 */
	private String email;



}
