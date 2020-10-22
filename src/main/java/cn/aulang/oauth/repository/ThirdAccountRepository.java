package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:30
 */
@Repository
public interface ThirdAccountRepository extends MongoRepository<ThirdAccount, String> {

    ThirdAccount findByThirdTypeAndThirdId(String thirdType, String thirdId);

}
