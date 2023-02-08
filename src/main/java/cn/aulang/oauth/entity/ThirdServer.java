package cn.aulang.oauth.entity;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * 第三方登录服务
 *
 * @author wulang
 */
@Data
@Entity
@Table(name = "third_server")
public class ThirdServer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "logo_url")
    private String logoUrl;
    @Column(name = "authorize_url")
    private String authorizeUrl;
    @Column(name = "access_token_url")
    private String accessTokenUrl;
    @Column(name = "profile_url")
    private String profileUrl;

    /**
     * 第三方认证请求（get）参数
     */
    @Column(name = "authorize_params")
    private String authorizeParams;

    /**
     * 获取access_token的http请求方式：post、get
     */
    @Column(name = "access_token_method")
    private String accessTokenMethod = Constants.POST;
    /**
     * 获取access_token的http请求参数
     */
    @Column(name = "access_token_params")
    private String accessTokenParams;
    /**
     * 返回access_token数据类型：json、text
     */
    @Column(name = "access_token_type")
    private String accessTokenType = Constants.JSON;

    /**
     * 返回access_token数据access_token的key
     */
    @Column(name = "access_token_key")
    private String accessTokenKey = OAuthConstants.ACCESS_TOKEN;
    /**
     * 返回access_token数据refresh_token的key
     */
    @Column(name = "refresh_token_key")
    private String refreshTokenKey = OAuthConstants.REFRESH_TOKEN;
    /**
     * 返回access_token数据expires_in的key
     */
    @Column(name = "expires_in_key")
    private String expiresInKey = OAuthConstants.EXPIRES_IN;

    /**
     * 获取第三方用户信息的http请求方式：post、get、header
     */
    @Column(name = "profile_method")
    private String profileMethod = Constants.HEADER;
    /**
     * 获取第三方用户信息的http请求方式为header时有效，加在access_token前
     */
    @Column(name = "profile_authorization")
    private String profileAuthorization = Constants.BEARER;
    /**
     * 获取第三方用户信息的http请求参数，post和get时有效
     */
    @Column(name = "profile_params")
    private String profileParams;

    /**
     * 排序字段
     */
    private Integer sort;

    private String creator;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
}
