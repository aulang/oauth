package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:57
 */
@Repository
public interface ThirdServerRepository extends MongoRepository<ThirdServer, String> {

    List<ThirdServer> findByEnabledOrderBySortAsc(boolean enabled);

    ThirdServer findByNameAndEnabledIsTrue(String name);

    ThirdServer findFirstByEnabled(boolean enabled);
}
