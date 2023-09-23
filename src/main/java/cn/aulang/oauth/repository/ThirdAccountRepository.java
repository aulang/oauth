package cn.aulang.oauth.repository;

import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.common.crud.rdbm.MybatisRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface ThirdAccountRepository extends MybatisRepository<ThirdAccount, String> {

}
