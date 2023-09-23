package cn.aulang.oauth.config;

import cn.aulang.oauth.entity.MailServer;
import cn.aulang.oauth.manage.MailServerBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

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
    public JavaMailSenderImpl mailSender() {
        MailServer server = mailServerBiz.get();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        if (server == null) {
            return mailSender;
        }

        mailSender.setHost(server.getHost());
        mailSender.setPort(server.getPort());
        mailSender.setUsername(server.getMail());
        mailSender.setPassword(server.getPassword());

        mailSender.setDefaultEncoding("UTF-8");

        Properties properties = new Properties();

        if (server.getAuth() != null && server.getAuth()) {
            properties.setProperty("mail.smtp.auth", "true");
        } else {
            properties.setProperty("mail.smtp.auth", "false");
        }

        if (server.getSslEnable() != null && server.getSslEnable()) {
            properties.setProperty("mail.smtp.ssl.enable", "true");
        } else {
            properties.setProperty("mail.smtp.ssl.enable", "false");
        }

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
