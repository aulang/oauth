package cn.aulang.oauth.jwt;

import cn.aulang.oauth.model.Profile;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.util.DateUtils;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT编码器
 *
 * @author wulang
 */
public class JwtEncoder {

    private final RSASSASigner signer;

    public JwtEncoder(RSAPrivateKey privateKey) {
        signer = new RSASSASigner(privateKey);
    }

    public String encode(Profile profile, Date expiresAt) throws Exception {
        Map<String, Object> jsonObject = new HashMap<>();

        jsonObject.put(Profile.ID, profile.getId());
        jsonObject.put(Profile.USERNAME, profile.getUsername());
        jsonObject.put(Profile.NICKNAME, profile.getNickname());
        jsonObject.put(Profile.CLIENT_ID, profile.getClientId());

        // 失效时间, 格式固定Unix时间戳（到秒）
        jsonObject.put(JWTClaimNames.EXPIRATION_TIME, DateUtils.toSecondsSinceEpoch(expiresAt));

        Payload payload = new Payload(jsonObject);
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.RS256);
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }
}
