package cn.aulang.oauth.server.impl;

import cn.aulang.oauth.server.core.Profile;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:23
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