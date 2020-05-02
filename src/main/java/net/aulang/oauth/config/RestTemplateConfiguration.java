package net.aulang.oauth.config;


import net.aulang.oauth.factory.HttpConnectionFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:59
 */
@Configuration
public class RestTemplateConfiguration {
    @Bean
    @Qualifier("trustStoreSslSocketFactory")
    public SSLConnectionSocketFactory trustStoreSslSocketFactory() {
        return new SSLConnectionSocketFactory(HttpConnectionFactory.sslSocketFactory(), HttpConnectionFactory.hostnameVerifier());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(HttpConnectionFactory.clientHttpRequestFactory());
    }
}
