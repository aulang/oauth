package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.thirdserver.core.Profile;

/**
 * @author wulang
 */
public abstract class AbstractProfile implements Profile {

    protected String serverName;
    protected String originInfo;

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String getOriginInfo() {
        return originInfo;
    }

    @Override
    public void setOriginInfo(String originInfo) {
        this.originInfo = originInfo;
    }
}