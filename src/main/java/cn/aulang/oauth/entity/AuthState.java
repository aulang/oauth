package cn.aulang.oauth.entity;

import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "oauth_state")
public class AuthState extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;
    /**
     * 认证请求ID
     */
    @NotBlank
    private String authorizeId;
    /**
     * 第三方登录服务ID
     */
    @NotBlank
    private String serverId;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * state有效期10分钟
     */
    @NotNull
    private Date createDate = new Date();
}
