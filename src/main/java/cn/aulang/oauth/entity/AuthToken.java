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
 * 账号Token
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "oauth_token")
public class AuthToken extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    /**
     * access_token
     */
    @NotBlank
    private String accessToken;
    /**
     * access_token失效时间
     */
    @NotNull
    private Date accessTokenExpiresAt;

    /**
     * refresh_token
     */
    @NotBlank
    private String refreshToken;
    /**
     * refresh_token失效时间
     */
    @NotNull
    private Date refreshTokenExpiresAt;

    @NotBlank
    private String clientId;
    @NotBlank
    private String redirectUri;
    @NotBlank
    private String accountId;

    private Date createDate;
    private Date updateDate;
}
