package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.Client;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface ClientRepository extends MybatisRepository<Client, String> {

}
