package cn.aulang.oauth.thirdserver.wechat;

import cn.aulang.oauth.thirdserver.impl.AbstractProfile;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wulang
 */
public class WeChatProfile extends AbstractProfile {

    @JsonProperty("openid")
    private String openId;
    @JsonProperty("unionid")
    private String unionId;
    private String nickname;

    @Override
    public String getId() {
        return StringUtils.firstNonBlank(openId, unionId);
    }

    @Override
    public String getUsername() {
        return StringUtils.firstNonBlank(nickname, openId, unionId);
    }

    @Override
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
