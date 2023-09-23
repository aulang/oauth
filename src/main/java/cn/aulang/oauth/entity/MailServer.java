package cn.aulang.oauth.entity;

import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tk.mybatis.mapper.annotation.KeySql;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "mail_server")
public class MailServer extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    @NotBlank
    private String host;
    @NotNull
    private Integer port;
    @NotNull
    private Boolean sslEnable;
    @NotBlank
    private String mail;
    @NotNull
    private Boolean auth;
    private String password;
    private String mailFrom;
}
