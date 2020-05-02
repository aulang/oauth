package net.aulang.oauth.repository;

import net.aulang.oauth.entity.BeiAn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 11:57
 */
@Repository
public interface BeiAnRepository extends MongoRepository<BeiAn, String> {

    /**
     * 查询备案信息
     *
     * @return 备案信息
     */
    BeiAn findFirstBy();

}
