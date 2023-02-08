package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface AuthStateRepository extends JpaRepository<AuthState, String> {
}
