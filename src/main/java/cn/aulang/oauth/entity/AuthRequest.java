package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private static final long serialVersionUID = 1L;

    /**
     * authId
     */
    @Id
    private String id;

    private String clientId;
    private String responseType;
    private String redirectUri;
    private String codeChallenge;
    private Set<String> scopes;
    private String state;

    /**
     * 密码错误尝试次数
     */
    private Integer triedTimes = 0;
    private String captcha;
    private String mobile;

    private String accountId;
    /**
     * 是否已认证通过
     */
    private boolean authenticated = false;
    /**
     * 登录认证请求有效时间8小时
     */
    @Indexed(name = "ttl", expireAfterSeconds = 28800)
    private LocalDateTime createdDateTime = LocalDateTime.now();
}
