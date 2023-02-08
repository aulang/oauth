package cn.aulang.oauth.config;

import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.jwt.JwtProperties;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * JWT配置
 *
 * @author wulang
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {

    private final JwtProperties properties;

    @Autowired
    public JwtConfiguration(JwtProperties properties) {
        this.properties = properties;
    }

    @Bean
    public JwtHelper jwtHelper() throws Exception {
        String filePath = properties.getFilePath();
        String password = properties.getPassword();

        if (StrUtil.hasBlank(filePath, password)) {
            throw new RuntimeException("JWT keystore config error!");
        }

        char[] passwordChars = password.toCharArray();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        if (filePath.startsWith("classpath:")) {
            String resource = filePath.substring(10).trim();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
                keyStore.load(is, passwordChars);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                keyStore.load(fis, passwordChars);
            }
        }

        String alias = keyStore.aliases().nextElement();

        RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(alias).getPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(alias, passwordChars);

        return new JwtHelper(privateKey, publicKey);
    }
}
