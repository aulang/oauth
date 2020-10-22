package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AccountToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:18
 */
@Repository
public interface AccountTokenRepository extends MongoRepository<AccountToken, String> {

    AccountToken findByRefreshToken(String refreshToken);

    AccountToken findByAccessToken(String accessToken);

    AccountToken findByAccountIdAndClientIdAndRedirectUri(String accountId, String clientId, String redirectUri);

}
