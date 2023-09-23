package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.thirdserver.core.Profile;

/**
 * @author wulang
 */
public abstract class AbstractProfile implements Profile {

    protected String serverId;
    protected String serverType;

    @Override
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
}