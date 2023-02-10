package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 登录认证请求
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "auth_request")
public class AuthRequest extends StringIdEntity {

    @Column(name = "client_Id", nullable = false)
    private String clientId;
    @Column(name = "auth_grant", nullable = false)
    private String authGrant;
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;
    @Column(name = "code_challenge")
    private String codeChallenge;
    private String state;

    /**
     * 密码错误尝试次数
     */
    @Column(name = "tried_times", nullable = false)
    private Integer triedTimes = 0;
    private String captcha;
    private String mobile;

    @Column(name = "account_id")
    private String accountId;
    /**
     * 是否已认证通过
     */
    @Column(nullable = false)
    private Boolean authenticated = false;
    /**
     * 是否需要修改密码
     */
    @Column(name = "must_chpwd")
    private Boolean mustChpwd = false;
    /**
     * 需要修改密码的理由
     */
    @Column(name = "chpwd_reason")
    private String chpwdReason = "请修改密码！";

    /**
     * 登录认证请求有效时间8小时
     */
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
