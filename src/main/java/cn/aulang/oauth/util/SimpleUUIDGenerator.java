package cn.aulang.oauth.util;

import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Simple UUID
 *
 * @author wulang@unicloud.com
 */
public class SimpleUUIDGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return IdUtil.fastSimpleUUID();
    }
}
