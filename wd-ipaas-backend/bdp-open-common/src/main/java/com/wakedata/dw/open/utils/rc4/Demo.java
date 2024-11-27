package com.wakedata.dw.open.utils.rc4;

import java.io.UnsupportedEncodingException;

/**
 * @author yiyufeng
 * @title Demo
 * @projectName bdp-open
 * @date
 * @description
 */
public class Demo {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String key = "123456789";
        String bgyUsername = "penghe";

        // 休眠0毫秒，预期验签成功
        fun(bgyUsername, key, 0);
        // 休眠900毫秒，预期验签成功
        fun(bgyUsername, key, 900);
        // 休眠1100毫秒，预期验签失败
        fun(bgyUsername, key, 1100);
    }

    private static void fun(String message, String key, long sleepTime) {
        System.out.println(String.format("--------------%nmessage:%s, key:%s, sleepTime:%s", message, key, sleepTime));
        // 对原参数进行加密签名，获取密文
        String cipherText = SecureHelper.encryptAndSign(message, key);
        System.out.println(String.format("密文:%s", cipherText));
        try {
            // 密文有效期由SecureHelper中TIME_OUT_REQUEST常量控制，休眠等待密文过期
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
        // 对密文进行解密验签，若休眠后密文未过期，则获取到原参数，否则为空字符串
        String originalText = SecureHelper.decryptAndVerify(cipherText, key);
        System.out.println(String.format("原文:%s", originalText));
        if (message.equals(originalText)) {
            System.out.println("验签成功");
        } else {
            System.out.println("验签失败");
        }
    }
}
