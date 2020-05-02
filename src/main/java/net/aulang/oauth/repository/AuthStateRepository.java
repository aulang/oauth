package net.aulang.oauth.repository;

import net.aulang.oauth.entity.AuthState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 18:03
 */
@Repository
public interface AuthStateRepository extends MongoRepository<AuthState, String> {
}
