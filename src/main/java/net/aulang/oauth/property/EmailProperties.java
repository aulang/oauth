package net.aulang.oauth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 17:51
 */
@Data
@ConfigurationProperties("email")
public class EmailProperties {
    private String host;
    private Integer port;
    private Boolean sslEnable;
    private Boolean auth;
    private String user;
    private String pass;
    private String from;
}
