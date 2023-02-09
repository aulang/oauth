package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.thirdserver.core.AccessToken;
import cn.aulang.oauth.thirdserver.core.Api;
import cn.aulang.oauth.thirdserver.core.ProfileExtractor;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author wulang
 */
public abstract class AbstractApi<T extends AbstractProfile> implements Api<T> {

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public AbstractApi() {
        entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(UTF_8);
            }
        }
    }

    protected RestTemplate restTemplate = new RestTemplate();
    protected ProfileExtractor profileExtractor = new DefaultProfileExtractor();

    protected String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTION);
        params.forEach(
                (k, v) -> builder.append(k)
                        .append(Constants.EQUAL)
                        .append(v)
                        .append(Constants.AND)
        );
        return builder.toString();
    }

    protected Map<String, String> parseAccessToken(String accessToken) {
        Map<String, String> map = new HashMap<>(3);
        String[] params = accessToken.split(Constants.AND);
        for (String param : params) {
            String[] kv = param.split(Constants.EQUAL);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    protected HttpEntity<MultiValueMap<String, String>> buildPostParams(Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        params.forEach(map::add);
        return new HttpEntity<>(map, headers);
    }

    protected String getHttpResponse(String url,
                                     String method,
                                     Map<String, String> params,
                                     String accessToken,
                                     String authorization) {
        switch (method.toLowerCase()) {
            case Constants.HEADER -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, authorization + " " + accessToken);
                HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
                return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            }
            case Constants.POST -> {
                HttpEntity<MultiValueMap<String, String>> requestEntity = buildPostParams(params);
                return restTemplate.postForEntity(url, requestEntity, String.class).getBody();
            }
            case Constants.GET -> {
                String getUrl = buildGetUrl(url, params);
                return restTemplate.getForEntity(getUrl, String.class).getBody();
            }
            default -> throw new IllegalArgumentException("暂不支持的请求类型：" + method);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AccessToken getAccessToken(ThirdServer server, String code) throws Exception {
        String accessTokenUrl = server.getAccessTokenUrl();
        Map<String, String> accessTokenParams = Constants.toMap(server.getAccessTokenParams());
        accessTokenParams.put(OAuthConstants.CODE, code);

        String response;
        if (HttpMethod.POST.matches(server.getAccessTokenMethod().toUpperCase())) {
            HttpEntity<MultiValueMap<String, String>> requestEntity = buildPostParams(accessTokenParams);
            response = restTemplate.postForEntity(accessTokenUrl, requestEntity, String.class).getBody();
        } else {
            String url = buildGetUrl(accessTokenUrl, accessTokenParams);
            response = restTemplate.getForEntity(url, String.class).getBody();
        }

        if (StrUtil.isBlank(response)) {
            throw new IOException("获取AccessToken响应为空！");
        }

        Map<String, String> responseMap;
        if (Constants.JSON.equalsIgnoreCase(server.getAccessTokenType())) {
            responseMap = Constants.JSON_MAPPER.readValue(response, Map.class);
        } else {
            responseMap = parseAccessToken(response);
        }

        AccessToken token = new AccessToken();

        Object expiresIn = responseMap.get(server.getExpiresInKey());
        token.setAccessToken(responseMap.get(server.getAccessTokenKey()));
        token.setRefreshToken(responseMap.get(server.getRefreshTokenKey()));
        token.setExpiresIn(expiresIn != null ? expiresIn.toString() : null);
        token.setAttributes(responseMap);

        return token;
    }

    @Override
    public T getProfile(ThirdServer server, AccessToken accessToken) throws Exception {
        String profileUrl = server.getProfileUrl();
        String profileMethod = server.getProfileMethod();

        Map<String, String> profileParams = Constants.toMap(server.getProfileParams());
        profileParams.put(OAuthConstants.ACCESS_TOKEN, accessToken.getAccessToken());

        String responseBody = getHttpResponse(
                profileUrl,
                profileMethod,
                profileParams,
                accessToken.getAccessToken(),
                server.getProfileBearer());

        T profile = profileExtractor.extract(responseBody, entityClass);
        profile.setServerName(server.getName());
        return profile;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setProfileExtractor(ProfileExtractor profileExtractor) {
        this.profileExtractor = profileExtractor;
    }
}
