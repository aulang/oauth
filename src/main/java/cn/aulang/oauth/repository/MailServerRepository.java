package cn.aulang.oauth.repository;

import cn.aulang.common.crud.rdbm.MybatisRepository;
import cn.aulang.oauth.entity.MailServer;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wulang
 */
@Mapper
public interface MailServerRepository extends MybatisRepository<MailServer, String> {
}
