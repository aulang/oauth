package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "mail_server")
public class MailServer extends StringIdEntity {

    @Column(nullable = false)
    private String host;
    @Column(nullable = false)
    private Integer port;
    @Column(name = "ssl_enable", nullable = false)
    private Boolean sslEnable = true;
    @Column(nullable = false)
    private String mail;
    @Column(nullable = false)
    private Boolean auth = true;
    private String password;
    private String mailFrom;
}
