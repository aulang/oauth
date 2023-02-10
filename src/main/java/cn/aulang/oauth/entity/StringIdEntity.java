package cn.aulang.oauth.entity;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * 短UUID生成实体基类
 *
 * @author wulang
 */
@Data
@MappedSuperclass
public class StringIdEntity {

    @Id
    @GeneratedValue(generator = "SimpleUUID", strategy = GenerationType.UUID)
    @GenericGenerator(name = "SimpleUUID", strategy = "cn.aulang.oauth.util.SimpleUUIDGenerator")
    protected String id;

    public boolean isNew() {
        return StrUtil.isBlank(id);
    }
}