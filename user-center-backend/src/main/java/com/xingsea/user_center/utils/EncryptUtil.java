package com.xingsea.user_center.utils;

import cn.hutool.crypto.digest.MD5;

public class EncryptUtil {

    public static String getEncryptPassword(String password){
        MD5 md5 = new MD5();
        final String SALT = "xingsea";
        return md5.digestHex(SALT + password);
    }
}
