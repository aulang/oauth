package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author wulang
 */
@Data
@Entity
@Table(name = "mail_server")
public class MailServer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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
