package cn.aulang.oauth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 登录配置参数
 *
 * @author Aulang
 * @date 2021-06-19 11:04
 */
@Data
@ConfigurationProperties("login")
public class LoginProperties {

    /**
     * 密码错误次数需要验证码
     */
    private int needCaptchaTimes = 2;
    /**
     * 密码错误次数锁定账号
     */
    private int lockAccountTimes = 6;

}
