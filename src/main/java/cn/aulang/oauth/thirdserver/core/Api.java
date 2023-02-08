package cn.aulang.oauth.thirdserver.core;

import cn.aulang.oauth.entity.ThirdServer;

/**
 * 调用第三方服务API接口
 * @author wulang
 */
public interface Api<T extends Profile> {

    AccessToken getAccessToken(ThirdServer server, String code) throws Exception;

    T getProfile(ThirdServer server, AccessToken accessToken) throws Exception;

    void getDetail(ThirdServer server, AccessToken accessToken, T t);
}
