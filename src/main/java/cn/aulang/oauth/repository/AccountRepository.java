package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 11:06
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Account findFirstByStatus(int status);

    Account findByUsername(String username);

    Account findByMobile(String username);

    Account findByEmail(String username);

    Account findByUsernameOrMobileOrEmail(String v1, String v2, String v3);
}
