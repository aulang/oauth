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
import java.util.concurrent.TimeUnit;

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
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(httpClient());

        int timeout = 60 * 1000;

        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        factory.setWriteTimeout(timeout);

        return factory;
    }

    public OkHttpClient httpClient() throws Exception {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
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
