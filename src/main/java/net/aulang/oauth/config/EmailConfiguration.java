package net.aulang.oauth.config;

import cn.hutool.extra.mail.MailAccount;
import net.aulang.oauth.property.EmailProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 17:53
 */
@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfiguration {
    @Autowired
    private EmailProperties properties;

    @Bean
    public MailAccount mailAccount() {
        MailAccount account = new MailAccount();

        BeanUtils.copyProperties(properties, account);

        return account;
    }
}
