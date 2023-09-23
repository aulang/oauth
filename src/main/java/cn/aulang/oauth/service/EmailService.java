package cn.aulang.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * @author wulang
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSenderImpl mailSender;

    @Autowired
    public EmailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public boolean send(String email, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailSender.getUsername());
            message.setTo(email);
            message.setSubject("验证码");
            message.setText(content);
            return true;
        } catch (Exception e) {
            log.error("验证码邮件发送失败！", e);
            return false;
        }
    }
}
