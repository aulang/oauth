package cn.aulang.oauth.entity;

import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 登录认证请求
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "oauth_request")
public class AuthRequest extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    @NotBlank
    private String clientId;
    @NotBlank
    private String authGrant;
    @NotBlank
    private String redirectUri;
    private String codeChallenge;
    private String state;

    private String loginPage;
    /**
     * 密码错误尝试次数
     */
    @NotNull
    private Integer triedTimes = 0;
    private String captcha;
    private String mobile;

    private String accountId;
    /**
     * 是否已认证通过
     */
    @NotNull
    private Boolean authenticated = false;
    /**
     * 是否需要修改密码
     */
    private Boolean mustChpwd = false;
    /**
     * 需要修改密码的理由
     */
    private String chpwdReason = "请修改密码！";

    /**
     * 登录认证请求有效时间8小时
     */
    private Date createDate;
    private Date updateDate;
}
