package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.thirdserver.core.Profile;
import cn.aulang.oauth.thirdserver.core.ProfileExtractor;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:23
 */
public class DefaultProfileExtractor implements ProfileExtractor {

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        T t = Constants.JSON_MAPPER.readValue(responseBody, type);

        t.setOriginInfo(responseBody);

        return t;
    }
}
