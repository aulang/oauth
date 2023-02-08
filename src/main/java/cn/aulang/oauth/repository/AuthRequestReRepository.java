package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface AuthRequestReRepository extends JpaRepository<AuthRequest, String> {
}
