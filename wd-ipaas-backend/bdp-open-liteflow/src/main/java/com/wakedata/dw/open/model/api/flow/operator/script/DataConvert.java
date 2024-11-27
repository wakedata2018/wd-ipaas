package com.wakedata.dw.open.model.api.flow.operator.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Groovy数据类型转换
 *
 * @author ZhangXueJun
 * @title DataConvert
 * @date 2021/3/29 11:32
 * @projectName bdp-open
 * @description
 */
public interface DataConvert<T extends JSON> {

    /**
     * 数据类型转换
     *
     * @param jsonObject
     * @return
     */
    <T extends JSON> T convert(JSONObject jsonObject);
}
