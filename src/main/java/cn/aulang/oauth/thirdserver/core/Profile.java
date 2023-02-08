package cn.aulang.oauth.thirdserver.core;

/**
 * 第三方用户信息
 * @author wulang
 */
public interface Profile {

    String getId();

    String getUsername();

    String getServerName();

    String getOriginInfo();

    void setOriginInfo(String originInfo);
}
