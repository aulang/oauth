package cn.aulang.oauth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 11:56
 */
@Slf4j
@Service
public class SMSService {

    public int send(String mobile, String content) {
        log.warn("未实现发送短信验证码功能，手机号：{}，内容：{}", mobile, content);
        return 0;
    }

}
