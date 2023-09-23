package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface AuthRequestReRepository extends MybatisRepository<AuthRequest, String> {
}
