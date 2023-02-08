package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Modifying
    @Query("update Account a set a.locked = false, a.triedTimes = 0 where a.locked = true")
    int updateLockedToUnlock();

    @Query("select a from Account a where a.username = ?1 or a.phone = ?1 or a.email = ?1")
    Account findByLoginName(String loginName);
}
