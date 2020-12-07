package cn.aulang.oauth.server.core;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:13
 * 第三方认证服务
 */
public interface AuthService {
    <T extends Profile> Api<T> getApi();

    Account authenticate(ThirdServer server, String code) throws AuthException;

    ThirdAccount bind(ThirdServer server, String code, String accountId) throws AuthException;

    boolean supports(ThirdServer server);
}
