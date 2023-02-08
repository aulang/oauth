package cn.aulang.oauth.entity;

import cn.aulang.oauth.common.Constants;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wulang
 */
@Data
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String secret;
    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "logo_page")
    private String logoPage;

    private String grants;
    @Column(name = "redirect_uris")
    private String redirectUris;

    @Column(name = "access_token_expires_in")
    private Integer accessTokenExpiresIn = 28800;
    @Column(name = "refresh_token_expires_in")
    private Integer refreshTokenExpiresIn = 2592000;

    private String creator;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;


    @Transient
    private Set<String> authorizationGrants = null;
    @Transient
    private List<String> registeredUris = null;

    public Set<String> getAuthorizationGrants() {
        if (authorizationGrants == null && StrUtil.isBlank(grants)) {
            return new HashSet<>();
        } else {
            List<String> strings = StrUtil.split(grants, Constants.SEPARATOR);
            authorizationGrants = new HashSet<>(strings);
        }

        return authorizationGrants;
    }

    public List<String> getRegisteredUris() {
        if (registeredUris == null && StrUtil.isBlank(redirectUris)) {
            return new ArrayList<>();
        } else {
            registeredUris = StrUtil.split(redirectUris, Constants.SEPARATOR);
        }

        return registeredUris;
    }
}
