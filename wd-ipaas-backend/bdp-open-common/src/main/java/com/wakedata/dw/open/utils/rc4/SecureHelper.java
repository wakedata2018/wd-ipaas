package com.wakedata.dw.open.utils.rc4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @author yiyufeng
 * @title SecureHelper
 * @projectName bdp-open
 * @date
 * @description
 */
public class SecureHelper {

    private final static String ENCODE = "UTF-8";

    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_MESSAGE = "message";

    private static final long TIME_OUT_REQUEST = 1000;

    public static String decryptAndVerify(String cipherText, String key) {
        try {
            RC4 rc4 = new RC4(key.getBytes(ENCODE));
            byte[] decipherBytes = rc4.decrypt(HexEncoder.hexToByteArray(cipherText));
            JSONObject jsonObject = JSON.parseObject(new String(decipherBytes, ENCODE));
            if (System.currentTimeMillis() - jsonObject.getLongValue(KEY_TIMESTAMP) > TIME_OUT_REQUEST) {
                throw new RuntimeException("sign timeout");
            }
            return jsonObject.getString(KEY_MESSAGE);
        } catch (Exception e) {
            // TODO
        }
        return "";
    }

    public static String encryptAndSign(String message, String key) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_TIMESTAMP, System.currentTimeMillis());
            jsonObject.put(KEY_MESSAGE, message);
            RC4 rc4 = new RC4(key.getBytes(ENCODE));
            return HexEncoder.bytesToHex(rc4.encrypt(jsonObject.toJSONString().getBytes(ENCODE)));
        } catch (Exception e) {
            // TODO
        }
        return "";
    }

}
