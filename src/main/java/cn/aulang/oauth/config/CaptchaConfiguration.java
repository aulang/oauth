package cn.aulang.oauth.config;

import cn.aulang.oauth.factory.CaptchaFactory;
import cn.aulang.oauth.property.CaptchaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:28
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaConfiguration {

    private final CaptchaProperties properties;

    @Autowired
    public CaptchaConfiguration(CaptchaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CaptchaFactory captchaFactory() {
        return new CaptchaFactory(properties);
    }
}
