package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * 账号Token
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "account_token")
public class AccountToken extends StringIdEntity {

    @Column(name = "access_token", nullable = false)
    private String accessToken;
    @Column(name = "access_token_expires_at", nullable = false)
    private Date accessTokenExpiresAt;

    @Column(name = "refresh_token", unique = true, nullable = false)
    private String refreshToken;
    @Column(name = "refresh_token_expires_at", nullable = false)
    private Date refreshTokenExpiresAt;

    @Column(name = "client_id", nullable = false)
    private String clientId;
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;
    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
