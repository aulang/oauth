package net.aulang.oauth.repository;

import net.aulang.oauth.entity.ApprovedScope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/6 13:36
 */
@Repository
public interface ApprovedScopeRepository extends MongoRepository<ApprovedScope, String> {

    ApprovedScope findByAccountIdAndClientId(String accountId, String clientId);

}
