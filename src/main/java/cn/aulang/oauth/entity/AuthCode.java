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
 * @date 2019/12/1 16:53
 */
@Data
@Document
public class AuthCode implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    private String authId;

    /**
     * 授权码（authorization code）有效期10分钟
     */
    @Indexed(name = "ttl", expireAfterSeconds = 600)
    private LocalDateTime createdDateTime = LocalDateTime.now();
}
