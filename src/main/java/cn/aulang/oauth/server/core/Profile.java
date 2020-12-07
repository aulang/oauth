package cn.aulang.oauth.server.core;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:12
 * 第三方用户信息
 */
public interface Profile {
    String getId();

    String getUsername();

    String getServerName();

    String getOriginInfo();

    void setOriginInfo(String originInfo);
}
