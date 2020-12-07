package cn.aulang.oauth.server.qq;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.server.core.AccessToken;
import cn.aulang.oauth.server.impl.AbstractApi;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:53
 */
@Slf4j
public class QQApi extends AbstractApi<QQProfile> {
    public static final String GET_USER_INFO = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    public QQApi() {
        setProfileExtractor(new QQProfileExtractor());
    }

    @Override
    public void getDetail(ThirdServer server, AccessToken accessToken, QQProfile profile) {
        try {
            String url = String.format(GET_USER_INFO,
                    accessToken.getAccessToken(),
                    profile.getClient_id(),
                    profile.getOpenid());
            String json = restTemplate.getForEntity(url, String.class).getBody();
            Map<String, ?> map = MAPPER.readValue(json, Map.class);
            profile.setNickname(map.get("nickname").toString());
        } catch (Exception e) {
            log.error("获取QQ账号信息失败", e);
        }
    }
}