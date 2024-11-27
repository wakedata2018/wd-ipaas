package com.wakedata.dw.open.liteflow.component.variable.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class VariableUtilTest {

    /**
     * 模拟全局上下文变量
     * 开始算子的变量被更新变量算子引用并将创建变量算子的变量值更新
     */
    public static final String CONTEXT = "{\n" +
            "    \"start\":{\n" +
            "        \"BODY\":{\n" +
            "            \"arrayInteger\":[\n" +
            "                1,\n" +
            "                2,\n" +
            "                3\n" +
            "            ],\n" +
            "            \"arrayObject\":[\n" +
            "\n" +
            "            ],\n" +
            "            \"IntegerTest\":1,\n" +
            "            \"arrayString\":[\n" +
            "                \"wcs1\",\n" +
            "                \"wcs2\",\n" +
            "                \"wcs3\"\n" +
            "            ],\n" +
            "            \"booleanTest\":false,\n" +
            "            \"stringTest\":\"wcsString\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"创建变量算子1\":{\n" +
            "        \"BODY\":{\n" +
            "            \"arrayInteger\":[\n" +
            "                4,\n" +
            "                5,\n" +
            "                6\n" +
            "            ],\n" +
            "            \"arrayObject\":[\n" +
            "\n" +
            "            ],\n" +
            "            \"IntegerTest\":2,\n" +
            "            \"arrayString\":[\n" +
            "                \"wcs1创建1\",\n" +
            "                \"wcs2创建1\",\n" +
            "                \"wcs3创建1\"\n" +
            "            ],\n" +
            "            \"booleanTest\":false,\n" +
            "            \"stringTest\":\"wcsString创建1\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    /**
     * 模拟更新变量更新某个创建变量算子的某个数组
     */
    public static final String PARAMS_JSON_SCHEMA = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.08669662607052175\":{\"name\":\"创建变量算子1.stringTest\",\"type\":\"array\",\"items\":{\"type\":\"string\",\"name\":\"items\"},\"paramValueType\":\"reference\",\"expression\":\"$.start.BODY.arrayString\",\"defaultValue\":\"\"}}}}";


    @Test
    public void test(){

        // 上下文
        Map<String, JSON> contextMap = contextMap();
        List<ApiRespParamDTO> apiRespParamDTOList = JsonSchemaConvertUtil.convert(PARAMS_JSON_SCHEMA).getChildApiRespParams();
        // 每个创建变量算子需要更新对变量
        Map<String,List<ApiRespParamDTO>> map = new HashMap<>();
//        buildUpdateVariableFiledMap(apiRespParamDTOList);

        // 更新指定的变量
//        updateValueAndPutContext(null, contextMap, map);


    }

    private Map<String,JSON> contextMap() {
        JSONObject context = JSONObject.parseObject(CONTEXT);
        Map<String,JSON> contextMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey();
            JSON value = (JSON) entry.getValue();
            contextMap.put(key,value);
        }
        return contextMap;
    }

}