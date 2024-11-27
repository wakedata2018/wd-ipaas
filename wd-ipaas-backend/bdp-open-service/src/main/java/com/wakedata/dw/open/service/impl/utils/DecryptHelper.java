package com.wakedata.dw.open.service.impl.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 密文解密Helper
 *
 * @author wujunqiang
 * @since 2022/5/13 3:19 PM
 */
@Slf4j
public class DecryptHelper {

    /**
     * 根据密文和密钥key进行解密
     *
     * @param ciphertext 密文
     * @param key        密钥key
     * @return 解密后明文
     */
    public static String decrypt(String ciphertext, String key) {
        AES aes;
        if (StringUtils.isBlank(key)) {
            aes = new AES(Mode.ECB, Padding.PKCS5Padding);
        } else {
            // 判断密钥key长度是否满足16位，超过16位截取前16位，不足16位在末尾补0
            key = key.length() > 16 ? key.substring(0, 16) : key.length() < 16 ? StrUtil.fillAfter(key, '0', 16) : key;
            aes = new AES(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
        }
        try {
            return aes.decryptStr(ciphertext, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            //如果解密出异常，要返回密文，防止返回系统繁忙
            return ciphertext;
        }
    }

}
