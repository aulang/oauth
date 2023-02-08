package cn.aulang.oauth.jwt;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.model.Profile;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.util.DateUtils;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * JWT解析器
 *
 * @author wulang
 */
public class JwtDecoder {

    private final RSASSAVerifier verifier;

    public JwtDecoder(RSAPublicKey publicKey) {
        verifier = new RSASSAVerifier(publicKey);
    }

    public Profile decode(String jwt) throws Exception {
        JWT tmp = JWTParser.parse(jwt);

        if (!(tmp instanceof SignedJWT signedJwt)) {
            throw new BadJWTException("Not a signed JWT");
        }

        if (!signedJwt.verify(verifier)) {
            throw new BadJWSException("Invalid signature");
        }

        JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();

        Date now = new Date();

        Date exp = claimsSet.getExpirationTime();
        if (exp != null) {
            if (!DateUtils.isAfter(exp, now, Constants.DEFAULT_MAX_CLOCK_SKEW_SECONDS)) {
                throw new BadJWTException("Expired JWT");
            }
        }

        String id = claimsSet.getStringClaim(Profile.ID);
        String username = claimsSet.getStringClaim(Profile.USERNAME);
        String nickname = claimsSet.getStringClaim(Profile.NICKNAME);
        String clientId = claimsSet.getStringClaim(Profile.CLIENT_ID);

        return new Profile(id, username, nickname, clientId);
    }
}
