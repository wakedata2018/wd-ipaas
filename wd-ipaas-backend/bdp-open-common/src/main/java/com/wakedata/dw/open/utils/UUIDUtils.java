package com.wakedata.dw.open.utils;

import com.wakedata.common.core.hashids.HashidsConstant;
import com.wakedata.common.core.hashids.HashidsUtil;

import java.util.UUID;

/**
 * @author yiyufeng
 * @title UUIDUtils
 * @projectName bdp-open
 * @date
 * @description
 */
public class UUIDUtils {

    public static String generateSimpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 租户ID编码
     * @param lesseeId
     * @return
     */
    public static String generateLesseeIdEncode(Long lesseeId){
        return "." + HashidsUtil.encode(lesseeId, HashidsConstant.DEFAULT_SALT,8);
    }


    /**
     * 生成app调用前缀
     * @param appId
     * @param lesseeId
     * @return
     */
    public static String generateAppPrefix(Integer appId,Long lesseeId){
        return HashidsUtil.encode(appId, HashidsConstant.DEFAULT_SALT,6) + generateLesseeIdEncode(lesseeId);
    }
}
