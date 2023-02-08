package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wulang
 */
@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, String> {
}
