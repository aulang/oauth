package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthState;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface AuthStateRepository extends MybatisRepository<AuthState, String> {
}
