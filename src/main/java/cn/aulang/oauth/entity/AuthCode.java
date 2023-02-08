package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * @author wulang
 */
@Data
@Entity
@Table(name = "auth_code")
public class AuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId;
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;
    @Column(name = "code_challenge")
    private String codeChallenge;
    @Column(name = "account_id", nullable = false)
    private String accountId;

    /**
     * 授权码（authorization code）有效期10分钟
     */
    @Column(name = "create_date", nullable = false)
    private Date createDate = new Date();
}
