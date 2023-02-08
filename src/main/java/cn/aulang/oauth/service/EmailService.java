package cn.aulang.oauth.service;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wulang
 */
@Slf4j
@Service
public class EmailService {

    private final MailAccount mailAccount;

    @Autowired
    public EmailService(MailAccount mailAccount) {
        this.mailAccount = mailAccount;
    }

    public int send(String email, String content) {
        try {
            MailUtil.send(mailAccount, email, "验证码", content, false);
            return 1;
        } catch (Exception e) {
            log.error("验证码邮件发送失败！", e);
            return -1;
        }
    }
}
