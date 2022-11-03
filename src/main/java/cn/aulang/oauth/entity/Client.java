package cn.aulang.oauth.entity;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:39
 */
@Data
@Document
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private String secret = IdUtil.fastSimpleUUID();
    private boolean enabled = true;

    private String logoUrl;
    /**
     * {code,名称}
     */
    private Map<String, String> scopes = new HashMap<>();
    private Set<String> autoApprovedScopes = new HashSet<>();
    private Set<String> authorizationGrants = new HashSet<>();
    private Set<String> registeredRedirectUris = new HashSet<>();
    private int accessTokenValiditySeconds = 2592000;
    private int refreshTokenValiditySeconds = 7776000;
    private int approvalValiditySeconds = 2592000;

    /**
     * client_credentials凭证模式对应的账号ID
     */
    private String accountId;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
