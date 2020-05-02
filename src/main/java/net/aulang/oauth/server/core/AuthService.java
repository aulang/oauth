package net.aulang.oauth.server.core;

import net.aulang.oauth.entity.Account;
import net.aulang.oauth.entity.ThirdAccount;
import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.exception.AuthException;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:13
 * 第三方认证服务
 */
public interface AuthService {
    Api getApi();

    Account authenticate(ThirdServer server, String code) throws AuthException;

    ThirdAccount bind(ThirdServer server, String code, String accountId) throws AuthException;

    boolean supports(ThirdServer server);
}
