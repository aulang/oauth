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
@Table(name = "account")
public class Account extends StringIdEntity {

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;

    private String nickname;
    private String avatar;
    @Column(name = "must_chpwd")
    private Boolean mustChpwd = false;
    @Column(name = "chpwd_time")
    private Date chpwdTime = new Date();

    private Boolean locked = false;
    @Column(name = "tried_times")
    private Integer triedTimes = 0;
    @Column(name = "chpwd_reason")
    private String chpwdReason = "密码已过期，请修改密码！";

    @Column(nullable = false)
    private Integer status;

    private String creator;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
