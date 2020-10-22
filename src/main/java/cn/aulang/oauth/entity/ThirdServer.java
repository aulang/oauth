package cn.aulang.oauth.entity;

import lombok.Data;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:41
 * 第三方登录服务
 */
@Data
@Document
public class ThirdServer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Indexed(unique = true, sparse = true)
    private String name;
    private String logoUrl;
    private boolean enabled = true;
    private String authorizeUrl = "";
    private String accessTokenUrl = "";
    private String profileUrl = "";

    /**
     * 第三方认证请求（get）参数
     */
    private Map<String, String> authorizeParams = new HashMap<>();

    /**
     * 获取access_token的http请求方式：post、get
     */
    private String accessTokenMethod = Constants.POST;
    /**
     * 获取access_token的http请求参数
     */
    private Map<String, String> accessTokenParams = new HashMap<>();
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
    private String profileAuthorization = Constants.BEARER;
    /**
     * 获取第三方用户信息的http请求参数，post和get时有效
     */
    private Map<String, String> profileParams = new HashMap<>();

    /**
     * 排序字段
     */
    private int sort = 1;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
