package cn.aulang.oauth.entity;

import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 第三方账号
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "third_account")
public class ThirdAccount extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;
    /**
     * 第三方服务ID
     */
    @NotBlank
    private String serverId;
    /**
     * 第三方服务类型
     */
    @NotBlank
    private String serverType;
    /**
     * 第三方账号ID
     */
    @NotBlank
    private String thirdId;

    /**
     * 第三方账号名称
     */
    private String thirdName;

    /**
     * 第三方openId
     */
    private String openId;
    /**
     * 第三方unionId
     */
    private String unionId;

    /**
     * 关联账号ID
     */
    @NotBlank
    private String accountId;

    private Date createDate;
    private Date updateDate;
}
