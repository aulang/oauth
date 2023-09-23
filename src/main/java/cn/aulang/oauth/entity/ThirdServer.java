package cn.aulang.oauth.entity;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
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
 * 第三方登录服务
 *
 * @author wulang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "third_server")
public class ThirdServer extends StringIdEntity {

    @Id
    @KeySql(genId = UUIDGenId.class)
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    private String type;
    private String logoUrl;
    @NotNull
    private Boolean visible;
    @NotNull
    private Boolean autoRegister;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String profileUrl;

    /**
     * 第三方认证请求（get）参数
     */
    private String authorizeParams;

    /**
     * 获取access_token的http请求方式：post、get
     */
    private String accessTokenMethod = Constants.POST;
    /**
     * 获取access_token的http请求参数
     */
    private String accessTokenParams;
    /**
     * 返回access_token数据类型：json、text
     */
    private String accessTokenType = Constants.JSON;

    /**
     * 返回access_token数据access_token的key
     */
    private String accessTokenKey = OAuthConstants.ACCESS_TOKEN;
    /**
     * 返回access_token数据refresh_token的key
     */
    private String refreshTokenKey = OAuthConstants.REFRESH_TOKEN;
    /**
     * 返回access_token数据expires_in的key
     */
    private String expiresInKey = OAuthConstants.EXPIRES_IN;

    /**
     * 获取第三方用户信息的http请求方式：post、get、header
     */
    private String profileMethod = Constants.HEADER;
    /**
     * 获取第三方用户信息的http请求方式为header时有效，加在access_token前
     */
    private String profileBearer = Constants.BEARER;
    /**
     * 获取第三方用户信息的http请求参数，post和get时有效
     */
    private String profileParams;

    /**
     * 排序字段
     */
    private Integer sort;

    private String creator;
    private Date createDate;
    private Date updateDate;
}
