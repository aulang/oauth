package net.aulang.oauth.repository;

import net.aulang.oauth.entity.AuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 17:00
 */
@Repository
public interface AuthCodeRepository extends MongoRepository<AuthCode, String> {
}
