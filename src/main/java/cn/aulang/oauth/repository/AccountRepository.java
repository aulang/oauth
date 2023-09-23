package cn.aulang.oauth.repository;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.oauth.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author wulang
 */
@Mapper
public interface AccountRepository extends MybatisRepository<Account, String> {

    int updateLockedToUnlock();

    Account getByUsernameOrMobileOrEmail(@Param("loginName") String loginName);

    int registerThirdAccount(@Param("account") Account account);

    int updatePassword(@Param("id") String id, @Param("password") String password, @Param("date") Date date);
}
