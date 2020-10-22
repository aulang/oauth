package cn.aulang.oauth.config;

import cn.aulang.oauth.entity.MailServer;
import cn.aulang.oauth.manage.MailServerBiz;
import cn.hutool.extra.mail.MailAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 17:53
 */
@Configuration
public class EmailConfiguration {
    @Autowired
    private MailServerBiz mailServerBiz;

    @Bean
    public MailAccount mailAccount() {
        MailServer server = mailServerBiz.get();

        MailAccount account = new MailAccount();

        BeanUtils.copyProperties(server, account);

        return account;
    }
}
