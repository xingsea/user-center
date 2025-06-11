package com.xingsea.user_center.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 */

@Data
public class UserVO implements Serializable {


    private static final long serialVersionUID = 3320241699593379139L;
    /**
     * id
     */
    private Long id;

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



    /**
     * 创建时间
     */
    private Date createTime;



}