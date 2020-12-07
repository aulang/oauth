package cn.aulang.oauth.server.qq;

import cn.aulang.oauth.server.core.Profile;
import cn.aulang.oauth.server.impl.DefaultProfileExtractor;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:52
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

        T t = MAPPER.readValue(json, type);

        t.setOriginInfo(responseBody);

        return t;
    }
}
