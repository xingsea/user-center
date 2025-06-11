package com.xingsea.user_center.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xingsea.user_center.model.dto.user.UserAddRequest;
import com.xingsea.user_center.model.dto.user.UserLoginRequest;
import com.xingsea.user_center.model.dto.user.UserQueryRequest;
import com.xingsea.user_center.model.dto.user.UserRegisterRequest;
import com.xingsea.user_center.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingsea.user_center.model.vo.UserLoginVO;
import com.xingsea.user_center.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author XingHai
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-07 17:09:33
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 新用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);


    /**
     * 用户登录
     * @param userLoginRequest
     * @return 脱敏后的用户信息
     */
    UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取脱敏后的登录用户信息
     * @param user
     * @return
     */
    UserLoginVO getUserLoginVO(User user);


    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    UserLoginVO getLoginUser(HttpServletRequest request);


    /**
     * 退出登录
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 创建用户（管理员）
     * @param userAddRequest
     * @return
     */
    Long addUser(UserAddRequest userAddRequest);

}
