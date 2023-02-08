package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.MailServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface MailServerRepository extends JpaRepository<MailServer, String> {
}
