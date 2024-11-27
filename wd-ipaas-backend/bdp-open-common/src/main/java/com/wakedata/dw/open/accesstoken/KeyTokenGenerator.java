package com.wakedata.dw.open.accesstoken;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.NoSuchAlgorithmException;

/**
 * @author pengxu
 * @Date 2019/2/12.
 */
public class KeyTokenGenerator {

    private static final String TOKEN_ALG = "HmacMD5";

    /**
     * 生成指定长度的token
     * @param length 长度
     * @return token串
     */
    public static String generateToken(int length) {
        //HmacMD5,HmacSHA1,AES
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(TOKEN_ALG);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 要生成多少位，只需要修改这里即可128, 192或256
        kg.init(length);
        SecretKey sk = kg.generateKey();
        byte[] data = sk.getEncoded();

        return new HexBinaryAdapter().marshal(data);
    }
}
