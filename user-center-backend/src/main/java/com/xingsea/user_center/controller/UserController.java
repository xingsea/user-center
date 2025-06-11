package com.xingsea.user_center.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingsea.user_center.annotaion.AuthCheck;
import com.xingsea.user_center.common.BaseResponse;
import com.xingsea.user_center.common.DeleteRequest;
import com.xingsea.user_center.constant.UserConstant;
import com.xingsea.user_center.exception.ErrorCode;
import com.xingsea.user_center.model.dto.user.*;
import com.xingsea.user_center.model.entity.User;
import com.xingsea.user_center.model.vo.UserLoginVO;
import com.xingsea.user_center.model.vo.UserVO;
import com.xingsea.user_center.service.UserService;
import com.xingsea.user_center.utils.ResultUtil;
import com.xingsea.user_center.utils.ThrowUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtil.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        long id = userService.userRegister(userRegisterRequest);
        return ResultUtil.success(id);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        UserLoginVO userLoginVO = userService.userLogin(userLoginRequest, request);
        return ResultUtil.success(userLoginVO);
    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean b = userService.userLogout(request);
        return ResultUtil.success(b);
    }

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    @PostMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        UserLoginVO loginUser = userService.getLoginUser(request);
        return ResultUtil.success(loginUser);
    }

    /**
     * 根据id获取用户包装类
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id){
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        UserVO userVO = userService.getUserVO(user);
        return ResultUtil.success(userVO);
    }

    /**
     * 创建用户（管理员）
     * @param userAddRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtil.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        Long id = userService.addUser(userAddRequest);
        return ResultUtil.success(id);
    }

    /**
     * 根据id获取用户（管理员）
     * @param id
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id){
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        User user = userService.getById(id);

        return ResultUtil.success(user);
    }

    /**
     * 根据id删除用户（管理员）
     * @param deleteRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest){
        ThrowUtil.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);

        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtil.success(b);
    }

    /**
     * 更新用户（管理员）
     * @param userUpdateRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        ThrowUtil.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest,user);

        boolean b = userService.updateById(user);
        ThrowUtil.throwIf(!b,ErrorCode.OPERATION_ERROR);
        return ResultUtil.success(true);
    }

    /**
     * 分页获取封装用户列表（管理员）
     * @param userQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        ThrowUtil.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<User> userPage = userService.page(new Page<>(current,pageSize),queryWrapper);

        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());

        Page<UserVO> result = userVOPage.setRecords(userVOList);

        return ResultUtil.success(result);
    }

}
