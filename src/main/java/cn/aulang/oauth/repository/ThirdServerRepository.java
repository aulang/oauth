package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wulang
 */
@Repository
public interface ThirdServerRepository extends JpaRepository<ThirdServer, String> {

    List<ThirdServer> findByEnabledOrderBySortAsc(boolean enabled);

    ThirdServer findByNameAndEnabled(String name, boolean enabled);
}
