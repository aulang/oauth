package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA密钥对
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class RSAKeyPair {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
}
