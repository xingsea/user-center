package com.xingsea.user_center.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getUserRoleEnum(String value) {
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for(UserRoleEnum e : UserRoleEnum.values()) {
            if(e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
