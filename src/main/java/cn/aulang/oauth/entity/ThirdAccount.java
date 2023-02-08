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
 * 第三方账号
 *
 * @author wulang
 */
@Data
@Entity
@Table(name = "third_account")
public class ThirdAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 第三方账号类型
     */
    @Column(name = "third_type", nullable = false)
    private String thirdType;
    /**
     * 第三方账号ID
     */
    @Column(name = "third_id", nullable = false)
    private String thirdId;
    /**
     * 关联账号ID
     */
    @Column(name = "account_id", nullable = false)
    private String accountId;
    /**
     * 第三方账号名称
     */
    @Column(name = "third_name")
    private String thirdName;
    /**
     * 原始用户信息
     */
    private String profile;

    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
