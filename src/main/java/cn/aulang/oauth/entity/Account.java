package cn.aulang.oauth.entity;

import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "oauth_account")
public class Account extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    private String username;
    private String mobilePhone;
    private String email;
    private String password;

    private String nickname;
    private String avatar;
    private Boolean mustChpwd = false;
    private Date chpwdTime = new Date();

    private Date lockTime;
    private Boolean locked = false;
    private Integer triedTimes = 0;
    private String chpwdReason = "请修改初始密码！";

    private Integer status;

    private String creator;
    private Date createDate;
    private Date updateDate;
}
