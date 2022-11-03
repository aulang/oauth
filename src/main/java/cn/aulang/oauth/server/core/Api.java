package cn.aulang.oauth.server.core;

import cn.aulang.oauth.entity.ThirdServer;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:11
 * 调用第三方服务API接口
 */
public interface Api<T extends Profile> {

    AccessToken getAccessToken(ThirdServer server, String code) throws Exception;

    T getProfile(ThirdServer server, AccessToken accessToken) throws Exception;

    void getDetail(ThirdServer server, AccessToken accessToken, T t);
}
