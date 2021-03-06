package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 14:24
 */
@Data
@Document
public class AuthState implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    /**
     * 认证请求ID
     */
    private String authId;
    /**
     * 第三方登录服务ID
     */
    private String thirdServerId;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * state有效期10分钟
     */
    @Indexed(name = "ttl", expireAfterSeconds = 600)
    private LocalDateTime createdDateTime = LocalDateTime.now();
}
