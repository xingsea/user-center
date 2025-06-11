-- 建库
create database if not exists my_db;

-- 使用库
use my_db;

-- 用户表
CREATE TABLE IF NOT EXISTS user
(
    id            bigint AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userAccount   varchar(256)                           NOT NULL COMMENT '账号',
    userPassword  varchar(512)                           NOT NULL COMMENT '密码',
    userName      varchar(256)                           NULL COMMENT '用户昵称',
    userAvatar    varchar(1024)                          NULL COMMENT '用户头像',
    userProfile   varchar(512)                           NULL COMMENT '用户简介',
    phone         varchar(20)                            NULL COMMENT '手机号',
    email         varchar(256)                           NULL COMMENT '邮箱',
    userRole      varchar(256) DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin',
    vipExpireTime datetime                               NULL COMMENT '会员过期时间',
    vipCode       varchar(128)                           NULL COMMENT '会员兑换码',
    vipNumber     bigint                                 NULL COMMENT '会员编号',
    shareCode     varchar(20)  DEFAULT                   NULL COMMENT '分享码',
    inviteUser    bigint       DEFAULT                   NULL COMMENT '邀请用户 id',
    editTime      datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    createTime    datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime    datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete      bigint      DEFAULT 0                 NOT NULL COMMENT '是否删除',
    UNIQUE KEY uk_userAccount (userAccount,isDelete),
    INDEX idx_userName (userName)
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;

