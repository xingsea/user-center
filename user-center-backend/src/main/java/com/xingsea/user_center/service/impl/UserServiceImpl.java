package com.xingsea.user_center.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingsea.user_center.constant.UserConstant;
import com.xingsea.user_center.exception.ErrorCode;
import com.xingsea.user_center.model.dto.user.UserAddRequest;
import com.xingsea.user_center.model.dto.user.UserLoginRequest;
import com.xingsea.user_center.model.dto.user.UserQueryRequest;
import com.xingsea.user_center.model.dto.user.UserRegisterRequest;
import com.xingsea.user_center.model.entity.User;
import com.xingsea.user_center.model.enums.UserRoleEnum;
import com.xingsea.user_center.model.vo.UserLoginVO;
import com.xingsea.user_center.model.vo.UserVO;
import com.xingsea.user_center.service.UserService;
import com.xingsea.user_center.mapper.UserMapper;
import com.xingsea.user_center.utils.EncryptUtil;
import com.xingsea.user_center.utils.ThrowUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author XingHai
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-06-07 17:09:33
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 1. 校验参数

        ThrowUtil.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtil.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号过短");
        ThrowUtil.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码过短");
        ThrowUtil.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入密码不一致");

        // 2. 检查用户账号是否和数据库已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        ThrowUtil.throwIf(count>0,ErrorCode.PARAMS_ERROR,"账号重复");
        // 3. 密码一定要加密
        String encryptPassword = EncryptUtil.getEncryptPassword(userPassword);
        // 4. 插入数据到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        user.setUserName("用户"+randomNumber);
        user.setUserRole(UserRoleEnum.USER.getValue());

        boolean saveResult = this.save(user);
        ThrowUtil.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"注册失败，数据库错误");
        return user.getId();
    }



    @Override
    public UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1. 校验
        ThrowUtil.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtil.throwIf(userAccount.length() < 4 || userPassword.length() < 8,ErrorCode.PARAMS_ERROR,"账号或者密码错误");
        // 3. 查询数据库是否存在账号
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("userAccount", userAccount));
        ThrowUtil.throwIf(user == null,ErrorCode.PARAMS_ERROR,"账号不存在");
        // 2. 对用户传递的密码进行加密
        String encryptPassword = EncryptUtil.getEncryptPassword(userPassword);
        // 4. 校验密码
        ThrowUtil.throwIf(!encryptPassword.equals(user.getUserPassword()),ErrorCode.PARAMS_ERROR, "账号或者密码错误");
        // 5. 保存用户状态
        UserLoginVO userLoginVO = this.getUserLoginVO(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userLoginVO);
        return userLoginVO;
    }

    @Override
    public UserLoginVO getUserLoginVO(User user) {
        if(user == null){
            return null;
        }
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtil.copyProperties(user, userLoginVO);

        return userLoginVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);

        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }

        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserLoginVO getLoginUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        UserLoginVO userSession = (UserLoginVO) user;
        ThrowUtil.throwIf(userSession == null || userSession.getId() == null,ErrorCode.NOT_LOGIN_ERROR);

        //追求性能直接返回上面
        User user1 = userMapper.selectById(userSession.getId());
        ThrowUtil.throwIf(user1 == null,ErrorCode.NOT_LOGIN_ERROR);
        return this.getUserLoginVO(user1);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtil.throwIf(attribute == null,ErrorCode.NOT_LOGIN_ERROR);

        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtil.throwIf(ObjUtil.isEmpty(userQueryRequest),ErrorCode.PARAMS_ERROR,"请求参数为空");

        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String phone = userQueryRequest.getPhone();
        String email = userQueryRequest.getEmail();
        String userRole = userQueryRequest.getUserRole();
        Long vipNumber = userQueryRequest.getVipNumber();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotEmpty(id),"id", id);
        queryWrapper.eq(StrUtil.isNotEmpty(userAccount),"userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotEmpty(userName),"userName", userName);
        queryWrapper.like(StrUtil.isNotEmpty(userProfile),"userProfile", userProfile);
        queryWrapper.like(StrUtil.isNotEmpty(phone),"phone", phone);
        queryWrapper.like(StrUtil.isNotEmpty(email),"email", email);
        queryWrapper.eq(StrUtil.isNotEmpty(userRole),"userRole", userRole);
        queryWrapper.eq(ObjUtil.isNotEmpty(vipNumber),"vipNumber", vipNumber);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);

        return queryWrapper;
    }

    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        ThrowUtil.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        long count = userMapper.selectCount(new QueryWrapper<User>().eq("userAccount", userAddRequest.getUserAccount()));
        ThrowUtil.throwIf(count >= 1,ErrorCode.PARAMS_ERROR,"账号重复");
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = EncryptUtil.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        userMapper.insert(user);
        return user.getId();
    }


}




