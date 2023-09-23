package cn.aulang.oauth.thirdserver.wechat;

import cn.aulang.oauth.thirdserver.impl.AbstractApi;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.thirdserver.core.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author wulang
 */
@Slf4j
public class WeChatApi extends AbstractApi<WeChatProfile> {

    public static final String GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

    @Override
    public WeChatProfile getProfile(ThirdServer server, AccessToken accessToken) {
        String openId = accessToken.getAttributes().get("openid");
        String unionId = accessToken.getAttributes().get("unionid");

        if (StringUtils.isAllBlank(openId, unionId)) {
            throw new RuntimeException("openId和unionId都为空");
        }

        WeChatProfile profile = new WeChatProfile();

        profile.setServerId(server.getId());
        profile.setServerType(server.getType());
        profile.setUnionId(unionId);
        profile.setOpenId(openId);

        return profile;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getDetail(ThirdServer server, AccessToken accessToken, WeChatProfile profile) {
        try {
            String url = String.format(GET_USER_INFO,
                    accessToken.getAccessToken(),
                    profile.getOpenId());
            String json = restTemplate.getForEntity(url, String.class).getBody();
            Map<String, ?> map = Constants.JSON_MAPPER.readValue(json, Map.class);
            profile.setNickname(map.get("nickname").toString());

            Object unionId = map.get("unionid");
            if (unionId != null) {
                profile.setUnionId(unionId.toString());
            }
        } catch (Exception e) {
            log.error("获取微信账号信息失败", e);
        }
    }
}