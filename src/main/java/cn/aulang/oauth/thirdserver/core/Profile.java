package cn.aulang.oauth.thirdserver.core;

/**
 * 第三方用户信息
 *
 * @author wulang
 */
public interface Profile {

    String getId();

    String getUsername();

    String getServerId();

    String getServerType();

    default String getOpenId() {
        return null;
    }

    default String getUnionId() {
        return null;
    }
}
