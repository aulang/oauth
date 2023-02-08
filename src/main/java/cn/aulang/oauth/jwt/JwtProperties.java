package cn.aulang.oauth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wulang
 */
@Data
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String filePath;
    private String password;
}
