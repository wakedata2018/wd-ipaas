package com.wakedata.dw.open.accesstoken;

import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author pengxu
 * @Date 2018/12/4
 */
@Slf4j
public class SignUtil {

    /**
     * 签名
     * @param params http url请求参数
     * @param postData http post的json数据
     * @param secret 应用的appSecret
     * @return
     */
    public static String signApiRequest(Map<String, String> params,String postData, String secret){
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            if(DwOpenConstant.SIGN_EXCLUDE.equals(key)){
                continue;
            }
            String value = params.get(key);
            if (!StringUtil.areEmpty(key, value)) {
                query.append(key).append(value);
            }
        }
        if(!StringUtil.isEmpty(postData)){
            query.append(postData);
        }
        // 第三步：使用MD5加密
        query.append(secret);
        return encodeMD5(query.toString());
    }

    /**
     * sign校验 （比较传递的sign和实际的sign是否相等）
     * @param paramMap 入参Map
     * @param secret 秘钥
     * @param sign 签名
     * @return
     */
    public static Boolean checkSignAndThrowExc(Map<String,String> paramMap, String postData, String secret , String sign) {
        String actualSign = SignUtil.signApiRequest(paramMap, postData, secret);
        Boolean result = actualSign.equals(sign);
        if(!result){
            log.error("签名验证失败,param = {},postData = {},secret = {},actualSign = {}",paramMap,postData,secret,actualSign);
            throw new OpenException(OpenApiMsgCodeEnum.s_sign_check_fail);
        }
        return true;
    }



    public static String encodeMD5(String src) {
        log.info("验证时待加密的串：{}", src);
        return DigestUtils.md5Hex(src);
    }

    public static String signRequest(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        }
        Arrays.sort(params);
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params) {
            stringBuilder.append(param);
        }
        return encodeMD5(stringBuilder.toString());
    }



}
