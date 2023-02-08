package cn.aulang.oauth.config;

import cn.aulang.oauth.entity.MailServer;
import cn.aulang.oauth.manage.MailServerBiz;
import cn.hutool.extra.mail.MailAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wulang
 */
@Configuration
public class EmailConfiguration {

    private final MailServerBiz mailServerBiz;

    @Autowired
    public EmailConfiguration(MailServerBiz mailServerBiz) {
        this.mailServerBiz = mailServerBiz;
    }

    @Bean
    public MailAccount mailAccount() {
        MailServer server = mailServerBiz.get();

        MailAccount account = new MailAccount();
        if (server == null) {
            return account;
        }

        account.setHost(server.getHost());
        account.setPort(server.getPort());
        account.setUser(server.getMail());
        account.setAuth(server.getAuth());
        account.setPass(server.getPassword());
        account.setFrom(server.getMailFrom());
        account.setSslEnable(server.getSslEnable());

        return account;
    }
}
