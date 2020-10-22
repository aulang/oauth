package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-08-14 13:27
 */
@Data
@Document
public class MailServer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String host;
    private Integer port;
    private Boolean sslEnable;
    private Boolean auth;
    private String user;
    private String pass;
    private String from;
}
