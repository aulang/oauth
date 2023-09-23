package cn.aulang.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wulang
 */
@Slf4j
@Service
public class EmailService {

    public boolean send(String email, String content) {
        try {
            // TODO 发送验证码
            return true;
        } catch (Exception e) {
            log.error("验证码邮件发送失败！", e);
            return false;
        }
    }
}
