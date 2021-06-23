package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-3 22:23
 * <p>
 * 已经授权的Scope
 */
@Data
@Document
@CompoundIndex(
        unique = true,
        name = "idx_approval_accountId_clientId",
        def = "{'accountId':1, 'clientId':1}"
)
public class ApprovedScope implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String clientId;
    private String accountId;

    /**
     * 已授权的
     */
    private Set<String> approved = new HashSet<>();

    /**
     * 失效时间
     */
    @Indexed(name = "ttl", expireAfterSeconds = 0)
    private LocalDateTime expiresAt;

    private int expiresIn;

    private LocalDateTime lastUpdatedAt;
}
