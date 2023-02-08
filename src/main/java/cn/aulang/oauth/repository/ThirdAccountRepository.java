package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface ThirdAccountRepository extends JpaRepository<ThirdAccount, String> {

    ThirdAccount findByThirdTypeAndThirdId(String thirdType, String thirdId);
}
