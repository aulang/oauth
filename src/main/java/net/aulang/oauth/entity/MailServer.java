package net.aulang.oauth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-08-14 13:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MailServer {
    private String host;
    private Integer port;
    private Boolean sslEnable;
    private Boolean auth;
    private String user;
    private String pass;
    private String from;
}
