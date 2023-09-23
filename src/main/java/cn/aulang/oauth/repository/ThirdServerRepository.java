package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface ThirdServerRepository extends MybatisRepository<ThirdServer, String> {
}
