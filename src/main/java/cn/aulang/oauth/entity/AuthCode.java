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
@Table(name = "oauth_code")
public class AuthCode extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    @NotBlank
    private String clientId;
    @NotBlank
    private String redirectUri;
    private String codeChallenge;
    @NotBlank
    private String accountId;

    /**
     * 授权码（authorization code）有效期10分钟
     */
    @NotNull
    private Date createDate = new Date();
}
