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
 * @email aulang@aq.com
 * @date 2019/12/2 18:22
 * 账号Token
 */
@Data
@Document
public class AccountToken implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    /**
     * 认证ID，Web端单点登录用
     */
    @Indexed(unique = true, sparse = true)
    private String authId;
    /**
     * access_token
     */
    @Indexed(unique = true)
    private String accessToken;
    /**
     * access_token失效时间
     */
    private LocalDateTime accessTokenExpiresAt;

    /**
     * access_token失效秒数
     */
    private int expiresIn;

    /**
     * refresh_token
     */
    @Indexed(unique = true)
    private String refreshToken;
    /**
     * refresh_token失效时间
     */
    @Indexed(name = "ttl_refresh_token", expireAfterSeconds = 0)
    private LocalDateTime refreshTokenExpiresAt;

    private String clientId;
    private Set<String> scopes;
    private String redirectUri;
    private String accountId;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
