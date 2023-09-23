package cn.aulang.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wulang
 */
@Slf4j
@Service
public class SmsService {

    public boolean send(String mobile, String content) {
        try {
            // TODO 发送验证码
            return true;
        } catch (Exception e) {
            log.error("验证码短信发送失败！", e);
            return false;
        }
    }
}
