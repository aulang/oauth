CREATE database if NOT EXISTS `oauth` default character set utf8mb4 collate utf8mb4_unicode_ci;

use `oauth`;

SET NAMES utf8mb4;


DROP TABLE IF EXISTS `oauth_client`;
CREATE TABLE `oauth_client`
(
    `id`                       VARCHAR(100)  NOT NULL COMMENT '客户端ID',
    `name`                     VARCHAR(255)  NOT NULL COMMENT '客户端名称',
    `secret`                   VARCHAR(128)  NOT NULL COMMENT '客户端密钥',

    `grants`                   VARCHAR(255)  DEFAULT NULL COMMENT '授权方式',
    `redirect_uris`            VARCHAR(1024) DEFAULT NULL COMMENT '重定向URI',

    `logo_url`                 VARCHAR(255)  DEFAULT NULL COMMENT '图标URL',
    `login_page`               VARCHAR(128)  DEFAULT NULL COMMENT '登录页面',

    `access_token_expires_in`  INT(11)       DEFAULT 28800 COMMENT '令牌有效期',
    `refresh_token_expires_in` INT(11)       DEFAULT 2592000 COMMENT '刷新令牌有效期',

    `account_id`               VARCHAR(100)  DEFAULT NULL COMMENT '开发者ID',
    `description`              VARCHAR(255)  DEFAULT NULL COMMENT '描述',

    `status`                   TINYINT(4)    NOT NULL DEFAULT 1 COMMENT '状态',

    `creator`                  VARCHAR(255)  DEFAULT NULL COMMENT '创建人',
    `create_date`              datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`              datetime      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = 'OAuth客户端';


DROP TABLE IF EXISTS `oauth_request`;
CREATE TABLE `oauth_request`
(
    `id`             VARCHAR(100) NOT NULL COMMENT 'ID',

    `client_id`      VARCHAR(64)  NOT NULL COMMENT '客户端ID',
    `auth_grant`     VARCHAR(64)  NOT NULL COMMENT '授权方式',
    `redirect_uri`   VARCHAR(255) NOT NULL COMMENT '重定向URI',
    `code_challenge` VARCHAR(255) DEFAULT NULL COMMENT 'Code Challenge',
    `state`          VARCHAR(255) DEFAULT NULL COMMENT 'state',

    `login_page`     VARCHAR(128)  DEFAULT NULL COMMENT '登录页面',

    `tried_times`    TINYINT(4)   NOT NULL DEFAULT 0 COMMENT '重试次数',

    `captcha`        VARCHAR(255) DEFAULT NULL COMMENT '验证码',
    `mobile`         VARCHAR(64)  DEFAULT NULL COMMENT '手机号',

    `account_id`     VARCHAR(100) DEFAULT NULL COMMENT '账号ID',
    `authenticated`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '已认证',

    `must_chpwd`     TINYINT(1)   DEFAULT 0 COMMENT '必须修改密码',
    `chpwd_reason`   VARCHAR(255) DEFAULT NULL COMMENT '修改密码提示',

    `create_date`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`    datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '认证请求';


DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`
(
    `id`             VARCHAR(100) NOT NULL COMMENT 'ID',

    `client_id`      VARCHAR(64)  NOT NULL COMMENT '客户端ID',
    `redirect_uri`   VARCHAR(255) NOT NULL COMMENT '重定向URI',
    `code_challenge` VARCHAR(255) DEFAULT NULL COMMENT 'Code Challenge',
    `account_id`     VARCHAR(100) NOT NULL COMMENT '账号ID',

    `create_date`    datetime     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '认证Code';


DROP TABLE IF EXISTS `oauth_state`;
CREATE TABLE `oauth_state`
(
    `id`              VARCHAR(100) NOT NULL COMMENT 'ID',

    `authorize_id`    VARCHAR(100) NOT NULL COMMENT '认证请求ID',
    `server_id`       VARCHAR(100) NOT NULL COMMENT '第三方服务ID',
    `account_id`      VARCHAR(100) DEFAULT NULL COMMENT '账号ID',

    `create_date`     datetime     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '认证State';


DROP TABLE IF EXISTS `oauth_token`;
CREATE TABLE `oauth_token`
(
    `id`                       VARCHAR(100) NOT NULL COMMENT 'ID',
    `access_token`             TEXT         NOT NULL COMMENT '令牌',
    `access_token_expires_at`  datetime     NOT NULL COMMENT '令牌失效时间',


    `refresh_token`            VARCHAR(255) NOT NULL COMMENT '刷新令牌',
    `refresh_token_expires_at` datetime     NOT NULL COMMENT '刷新令牌失效时间',

    `client_id`                VARCHAR(64)  NOT NULL COMMENT '客户端ID',

    `redirect_uri`             VARCHAR(255) NOT NULL COMMENT '重定向URI',
    `account_id`               VARCHAR(100) NOT NULL COMMENT '账号ID',

    `create_date`              datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`              datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_refresh_token(`refresh_token`),
    UNIQUE KEY uk_client_account(`client_id`, `redirect_uri`, `account_id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = 'OAuth令牌';


DROP TABLE IF EXISTS `third_server`;
CREATE TABLE `third_server`
(
    `id`                  VARCHAR(100)  NOT NULL COMMENT 'ID',

    `name`                VARCHAR(255)  NOT NULL COMMENT '名称',
    `type`                VARCHAR(64)   NOT NULL COMMENT '类型',
    `visible`             TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '是否显示',
    `auto_register`       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '自动注册账号',
    `logo_url`            VARCHAR(255)  DEFAULT NULL COMMENT '图标URL',

    `authorize_url`       VARCHAR(255)  DEFAULT NULL COMMENT '认证URL',
    `access_token_url`    VARCHAR(255)  DEFAULT NULL COMMENT '获取令牌URL',
    `profile_url`         VARCHAR(255)  DEFAULT NULL COMMENT '用户信息URL',

    `authorize_params`    VARCHAR(1024) DEFAULT NULL COMMENT '认证请求参数',

    `access_token_method` VARCHAR(64)   DEFAULT 'post' COMMENT '获取令牌请求方式',
    `access_token_params` VARCHAR(1024) DEFAULT NULL COMMENT '获取令牌请求参数',
    `access_token_type`   VARCHAR(64)   DEFAULT 'json' COMMENT '令牌格式',
    `access_token_key`    VARCHAR(64)   DEFAULT 'access_token' COMMENT '取令牌键',
    `refresh_token_key`   VARCHAR(64)   DEFAULT 'refresh_token' COMMENT '取刷新令牌键',
    `expires_in_key`      VARCHAR(64)   DEFAULT 'expires_in' COMMENT '取超时时间键',

    `profile_method`      VARCHAR(64)   DEFAULT 'header' COMMENT '用户信息请求方式',
    `profile_bearer`      VARCHAR(64)   DEFAULT 'Bearer' COMMENT '用户信息请求认证头',
    `profile_params`      VARCHAR(1024) DEFAULT NULL COMMENT '用户信息请求参数',

    `sort`                INT(11)       DEFAULT NULL COMMENT '排序字段',

    `creator`             VARCHAR(255)  DEFAULT NULL COMMENT '创建人',
    `create_date`         datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`         datetime      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_third_server(`name`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '第三方服务';


DROP TABLE IF EXISTS `third_account`;
CREATE TABLE `third_account`
(
    `id`          VARCHAR(100) NOT NULL COMMENT 'ID',

    `server_id`   VARCHAR(100) NOT NULL COMMENT '第三方服务ID',
    `server_type` VARCHAR(64)  NOT NULL COMMENT '第三方服务类型',

    `third_id`    VARCHAR(128) NOT NULL COMMENT '第三方账号ID',
    `third_name`  VARCHAR(255) DEFAULT NULL COMMENT '第三方账号名称',

    `open_id`     VARCHAR(128) DEFAULT NULL COMMENT '第三方openId',
    `union_id`    VARCHAR(128) DEFAULT NULL COMMENT '第三方unionId',

    `account_id`  VARCHAR(100) NOT NULL COMMENT '系统账号ID',

    `create_date` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_third_account(`server_type`, `third_id`),
    KEY idx_third_account_union_id(`server_type`, `union_id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '第三方账号';


DROP TABLE IF EXISTS `oauth_account`;
CREATE TABLE `oauth_account`
(
    `id`           VARCHAR(100) NOT NULL COMMENT 'ID',
    `username`     VARCHAR(64)  DEFAULT NULL COMMENT '用户名',
    `mobile_phone` VARCHAR(64)  DEFAULT NULL COMMENT '手机',
    `email`        VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    `password`     VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `nickname`     VARCHAR(255) DEFAULT NULL COMMENT '昵称',
    `avatar`       VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `must_chpwd`   TINYINT(1)   DEFAULT 0 COMMENT '必须修改密码',
    `chpwd_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '密码修改时间',
    `locked`       TINYINT(1)   DEFAULT 0 COMMENT '是否锁定',
    `lock_time`    datetime     DEFAULT NULL COMMENT '锁定时间',
    `tried_times`  TINYINT(4)   DEFAULT 0 COMMENT '重试次数',
    `chpwd_reason` VARCHAR(255) DEFAULT '密码已过期，请修改密码！' COMMENT '修改密码提示',
    `status`       TINYINT(4)   NOT NULL DEFAULT 1 COMMENT '状态',
    `creator`      VARCHAR(255) DEFAULT NULL COMMENT '创建人',
    `create_date`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date`  datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_account_username(`username`),
    UNIQUE KEY uk_account_phone(`mobile_phone`),
    UNIQUE KEY uk_account_email(`email`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '账号';


DROP TABLE IF EXISTS `mail_server`;
CREATE TABLE `mail_server`
(
    `id`         VARCHAR(100) NOT NULL COMMENT 'ID',

    `host`       VARCHAR(255) NOT NULL COMMENT '主机',
    `port`       INT(11)      NOT NULL COMMENT '端口',
    `ssl_enable` TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '加密连接',
    `mail`       VARCHAR(255) NOT NULL COMMENT '邮箱',
    `auth`       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '启用认证',
    `password`   VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `mail_from`  VARCHAR(255) DEFAULT NULL COMMENT '发件人',
    PRIMARY KEY (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '邮件服务配置';