package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:41
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

    Client findFirstByEnabled(boolean enabled);

}
