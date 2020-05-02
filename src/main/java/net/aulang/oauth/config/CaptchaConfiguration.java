package net.aulang.oauth.config;

import net.aulang.oauth.factory.CaptchaFactory;
import net.aulang.oauth.property.CaptchaProperties;
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
    @Autowired
    private CaptchaProperties properties;

    @Bean
    public CaptchaFactory captchaFactory() {
        return new CaptchaFactory(properties);
    }
}
