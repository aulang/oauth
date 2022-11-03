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
 * @email aulang@aq.com
 * @date 2019/12/1 16:53
 */
@Data
@Document
public class AuthCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    private String clientId;
    private Set<String> scopes;
    private String redirectUri;
    private String accountId;

    /**
     * 授权码（authorization code）有效期10分钟
     */
    @Indexed(expireAfterSeconds = 600)
    private LocalDateTime createdDateTime = LocalDateTime.now();
}
