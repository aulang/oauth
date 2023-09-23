package cn.aulang.oauth.entity;

import cn.aulang.oauth.common.Constants;
import cn.aulang.common.crud.id.StringIdEntity;
import cn.aulang.common.crud.id.UUIDGenId;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.annotation.KeySql;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "oauth_client")
public class Client extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    private String secret;
    @NotNull
    private Integer status;

    private String logoUrl;
    private String loginPage;

    private String grants;
    private String redirectUris;

    private Integer accessTokenExpiresIn = 28800;
    private Integer refreshTokenExpiresIn = 2592000;

    private String devId;
    private String remark;

    private String creator;
    private Date createDate;
    private Date updateDate;
    private Date releaseDate;

    @Transient
    private Set<String> authorizationGrants = null;
    @Transient
    private Set<String> registeredUris = null;

    public Set<String> getAuthorizationGrants() {
        if (authorizationGrants != null) {
            return authorizationGrants;
        }

        authorizationGrants = toSet(grants);

        return authorizationGrants;
    }

    public Set<String> getRegisteredUris() {
        if (registeredUris != null) {
            return registeredUris;
        }

        registeredUris = toSet(redirectUris);

        return registeredUris;
    }

    public Set<String> toSet(String str) {
        if (StringUtils.isBlank(str)) {
            return new HashSet<>();
        } else {
            String[] strings = StringUtils.split(str, Constants.SEPARATOR);
            return new HashSet<>(Arrays.asList(strings));
        }
    }
}
