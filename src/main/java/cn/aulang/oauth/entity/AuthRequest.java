package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-3 22:13
 * <p>
 * 登录认证请求
 */
@Data
@Document
public class AuthRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * authorizeId
     */
    @Id
    private String id;

    private String clientId;
    private String authorizationGrant;
    private String redirectUri;
    private Set<String> scopes;
    private String codeChallenge;
    private String state;

    /**
     * 密码错误尝试次数
     */
    private int triedTimes = 0;
    private String captcha;
    private String mobile;

    private String accountId;
    /**
     * 是否已认证通过
     */
    private boolean authenticated = false;
    /**
     * 是否需要修改密码
     */
    private boolean mustChangePassword = false;
    /**
     * 需要修改密码的理由
     */
    private String mustChangePasswordReason;

    /**
     * 登录认证请求有效时间8小时
     */
    @Indexed(expireAfterSeconds = 28800)
    private LocalDateTime createdDateTime = LocalDateTime.now();
}
