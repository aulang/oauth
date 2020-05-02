package net.aulang.oauth.server.qq;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:52
 */

import net.aulang.oauth.server.core.Profile;
import net.aulang.oauth.server.impl.DefaultProfileExtractor;

/**
 * callback( {"client_id":"APPID","openid":"OPENID"} );
 */
public class QQProfileExtractor extends DefaultProfileExtractor {
    private String getJson(String callback) {
        return callback
                .replace("callback(", "")
                .replace(");", "")
                .trim();
    }

    @Override
    public <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception {
        String json = getJson(responseBody);
        return MAPPER.readValue(json, type);
    }
}
