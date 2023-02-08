package cn.aulang.oauth.thirdserver.qq;

import cn.aulang.oauth.thirdserver.core.Profile;
import cn.aulang.oauth.thirdserver.impl.DefaultProfileExtractor;
import cn.aulang.oauth.common.Constants;

/**
 * callback( {"client_id":"APPID","openid":"OPENID"} );
 * @author wulang
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

        T t = Constants.JSON_MAPPER.readValue(json, type);

        t.setOriginInfo(responseBody);

        return t;
    }
}
