package com.xingsea.user_center.aop;

import com.xingsea.user_center.annotaion.AuthCheck;
import com.xingsea.user_center.exception.ErrorCode;
import com.xingsea.user_center.model.enums.UserRoleEnum;
import com.xingsea.user_center.model.vo.UserLoginVO;
import com.xingsea.user_center.service.UserService;
import com.xingsea.user_center.utils.ThrowUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Autowired
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //获取方法上@AuthCheck注解的值
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getUserRoleEnum(mustRole);

        //获取当前登录用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        UserLoginVO loginUser = userService.getLoginUser(request);

        //如果不需要权限放行
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }

        //以下是必须有权限才会通过
        UserRoleEnum loginUserRole = UserRoleEnum.getUserRoleEnum(loginUser.getUserRole());

        ThrowUtil.throwIf(loginUserRole == null, ErrorCode.NO_AUTH_ERROR);
        //必须有管理员权限，方法上有管理员注解，但是用户想要调用管理员的接口，无管理员权限则拒绝。
        ThrowUtil.throwIf(
                UserRoleEnum.ADMIN.equals(mustRoleEnum)
                && !UserRoleEnum.ADMIN.equals(loginUserRole),
                ErrorCode.NO_AUTH_ERROR);
        // 放行
        return joinPoint.proceed();
    }
}
