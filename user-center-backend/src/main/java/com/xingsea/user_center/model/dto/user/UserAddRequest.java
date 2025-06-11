package com.xingsea.user_center.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加用户请求
 *
 */

@Data
public class UserAddRequest implements Serializable {


    private static final long serialVersionUID = 8868438198655091254L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户角色：user/admin
     */
    private String userRole;


}