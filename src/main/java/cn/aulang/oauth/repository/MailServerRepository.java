package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.MailServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-08-14 13:30
 */
@Repository
public interface MailServerRepository extends MongoRepository<MailServer, String> {

    /**
     * 查找邮件服务配置信息
     *
     * @return 邮件服务配置信息
     */
    MailServer findFirstBy();

}
