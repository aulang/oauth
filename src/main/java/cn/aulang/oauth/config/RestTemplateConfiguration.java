package cn.aulang.oauth.config;


import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:59
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
                .sslSocketFactory(sslSocketFactory(), trustManager())
                .hostnameVerifier(hostnameVerifier())
                .build();
    }

    public SSLSocketFactory sslSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager()}, new SecureRandom());
        return sslContext.getSocketFactory();
    }

    public HostnameVerifier hostnameVerifier() {
        return (hostname, sslSession) -> true;
    }

    public X509TrustManager trustManager() {
        return new X509ExtendedTrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }
        };
    }
}
