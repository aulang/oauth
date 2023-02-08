package cn.aulang.oauth.thirdserver.qq;

import cn.aulang.oauth.thirdserver.impl.AbstractProfile;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wulang
 */
public class QQProfile extends AbstractProfile {

    @JsonProperty("client_id")
    private String clientId;
    private String openid;
    private String nickname;

    @Override
    public String getId() {
        return openid;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}