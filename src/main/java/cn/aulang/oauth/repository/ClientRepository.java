package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}
