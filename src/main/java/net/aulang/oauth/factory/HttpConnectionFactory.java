package net.aulang.oauth.factory;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:19
 */

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.X509Certificate;

/**
 * HTTP连接工厂，忽略HTTPS证书
 */
public class HttpConnectionFactory {
    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    public static CloseableHttpClient httpClient() {
        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext(),
                hostnameVerifier());
        return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    }

    public static SSLSocketFactory sslSocketFactory() {
        return sslContext().getSocketFactory();
    }

    public static SSLContext sslContext() {
        try {
            return SSLContexts.custom().loadTrustMaterial(null, trustStrategy()).build();
        } catch (Exception e) {
            return SSLContexts.createDefault();
        }
    }

    public static TrustStrategy trustStrategy() {
        return (X509Certificate[] chain, String authType) -> true;
    }

    public static HostnameVerifier hostnameVerifier() {
        return (hostname, sslSession) -> true;
    }
}
