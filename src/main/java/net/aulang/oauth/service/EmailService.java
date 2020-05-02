package net.aulang.oauth.service;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 11:56
 */
@Slf4j
@Service
public class EmailService {
    @Autowired
    private MailAccount mailAccount;

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
