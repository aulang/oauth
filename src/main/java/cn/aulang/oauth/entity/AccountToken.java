package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
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
@CompoundIndexes({
        @CompoundIndex(
                unique = true,
                name = "idx_accountId_clientId_redirectUri",
                def = "{'accountId':1, 'clientId':1, 'redirectUri':1}"
        )
})
public class AccountToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    /**
     * access_token
     */
    @Indexed(unique = true, sparse = true)
    private String accessToken;
    /**
     * access_token失效时间
     */
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime accessTokenExpiresAt;

    /**
     * refresh_token
     */
    @Indexed(unique = true, sparse = true)
    private String refreshToken;
    /**
     * refresh_token失效时间
     */
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime refreshTokenExpiresAt;

    private String clientId;
    private Set<String> scopes;
    private String redirectUri;
    private String accountId;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
