package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 13:36
 * <p>
 * 第三方账号
 */
@Data
@Document
@CompoundIndexes({
        @CompoundIndex(
                unique = true,
                name = "idx_thirdType_thirdId",
                def = "{'thirdType':1, 'thirdId':1}"
        )
})
public class ThirdAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    /**
     * 第三方账号类型
     */
    private String thirdType;
    /**
     * 第三方账号ID
     */
    private String thirdId;
    /**
     * 第三方账号名称
     */
    private String thirdName;
    /**
     * 关联账号ID
     */
    @Indexed
    private String accountId;
    /**
     * 原始用户信息
     */
    private String profile;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
