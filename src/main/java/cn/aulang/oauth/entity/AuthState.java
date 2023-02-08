package cn.aulang.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * @author wulang
 */
@Data
@Entity
@Table(name = "auth_state")
public class AuthState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
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
