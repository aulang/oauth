package cn.aulang.oauth.thirdserver.qq;

import cn.aulang.oauth.thirdserver.impl.AbstractProfile;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wulang
 */
public class QQProfile extends AbstractProfile {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("openid")
    private String openId;
    private String nickname;

    @Override
    public String getId() {
        return openId;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}