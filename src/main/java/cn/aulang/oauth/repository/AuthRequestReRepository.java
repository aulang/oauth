package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:49
 */
@Repository
public interface AuthRequestReRepository extends MongoRepository<AuthRequest, String> {

}
