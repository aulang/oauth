package cn.aulang.oauth.config;


import cn.hutool.core.net.DefaultTrustManager;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;

/**
 * @author wulang
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() throws Exception {
        return new RestTemplate(clientHttpRequestFactory());
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
        return new OkHttp3ClientHttpRequestFactory(httpClient());
    }

    public OkHttpClient httpClient() throws Exception {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), DefaultTrustManager.INSTANCE)
                .hostnameVerifier(hostnameVerifier())
                .build();
    }

    public SSLSocketFactory sslSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{DefaultTrustManager.INSTANCE}, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    public HostnameVerifier hostnameVerifier() {
        return (hostname, sslSession) -> true;
    }
}
