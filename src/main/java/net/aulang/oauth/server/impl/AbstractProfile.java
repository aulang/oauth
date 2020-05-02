package net.aulang.oauth.server.impl;

import net.aulang.oauth.server.core.Profile;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:23
 */
public abstract class AbstractProfile implements Profile {
    protected String serverName;

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}