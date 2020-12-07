package cn.aulang.oauth.server.impl;

import cn.aulang.oauth.server.core.Profile;
import cn.aulang.oauth.server.core.ProfileExtractor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:23
 */
public class DefaultProfileExtractor implements ProfileExtractor {
    public static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        T t = MAPPER.readValue(responseBody, type);

        t.setOriginInfo(responseBody);

        return t;
    }
}
