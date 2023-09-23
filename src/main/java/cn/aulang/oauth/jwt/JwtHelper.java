package cn.aulang.oauth.jwt;

import cn.aulang.oauth.model.JwtUser;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * JWT帮助类
 *
 * @author wulang
 */
public class JwtHelper {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtHelper(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.encoder = new JwtEncoder(privateKey);
        this.decoder = new JwtDecoder(publicKey);
    }

    public String encode(JwtUser jwtUser, Date expiresAt) throws Exception {
        return encoder.encode(jwtUser, expiresAt);
    }

    public JwtUser decode(String jwt) throws Exception {
        return decoder.decode(jwt);
    }
}
