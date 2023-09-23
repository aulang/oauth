package cn.aulang.oauth.thirdserver.mini;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.aulang.oauth.thirdserver.impl.AbstractProfile;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wulang
 */
public class MiniProfile extends AbstractProfile {

    @JsonProperty("openid")
    private String openId;
    @JsonProperty("unionid")
    private String unionId;
    @JsonProperty("session_key")
    private String sessionKey;

    @JsonProperty("errmsg")
    private String errMsg;
    @JsonProperty("errcode")
    private Integer errCode;

    @Override
    public String getId() {
        return StringUtils.firstNonBlank(openId, unionId);
    }

    @Override
    public String getUsername() {
        return getId();
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

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }
}
