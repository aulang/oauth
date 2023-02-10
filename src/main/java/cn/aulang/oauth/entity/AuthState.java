package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "auth_state")
public class AuthState extends StringIdEntity {
    /**
     * 认证请求ID
     */
    @Column(name = "authorize_id", nullable = false)
    private String authorizeId;
    /**
     * 第三方登录服务ID
     */
    @Column(name = "third_server_id", nullable = false)
    private String thirdServerId;

    /**
     * 账号ID
     */
    @Column(name = "account_id")
    private String accountId;

    /**
     * state有效期10分钟
     */
    @Column(name = "create_date", nullable = false)
    private Date createDate = new Date();
}
