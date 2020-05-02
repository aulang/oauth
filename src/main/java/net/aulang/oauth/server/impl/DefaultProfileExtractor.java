package net.aulang.oauth.server.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.aulang.oauth.server.core.Profile;
import net.aulang.oauth.server.core.ProfileExtractor;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:23
 */
public class DefaultProfileExtractor implements ProfileExtractor {
    public static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        return MAPPER.readValue(responseBody, type);
    }
}
