package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface AccountTokenRepository extends JpaRepository<AccountToken, String> {

    AccountToken findByRefreshToken(String refreshToken);

    AccountToken findByClientIdAndRedirectUriAndAccountId(String clientId, String redirectUri, String accountId);
}
