package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.thirdserver.core.Profile;
import cn.aulang.oauth.thirdserver.core.ProfileExtractor;

/**
 * @author wulang
 */
public class DefaultProfileExtractor implements ProfileExtractor {

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        return Constants.JSON_MAPPER.readValue(responseBody, type);
    }
}
