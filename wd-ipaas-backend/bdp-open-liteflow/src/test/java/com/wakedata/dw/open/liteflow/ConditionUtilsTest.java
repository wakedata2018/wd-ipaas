package com.wakedata.dw.open.liteflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.wakedata.dw.open.condition.Condition;
import com.wakedata.dw.open.condition.ConditionValue;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OperatorConditionEnum;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;

/**
 * 条件判断
 * @author luomeng
 * @date 2022/9/19 19:29
 */
@Slf4j
public class ConditionUtilsTest {


    @Test
    public void testConditionRuleExpression(){
        String ruleExpression = "!1 and (2 or 3) and 2 and (2 or 3)";

        List<Condition> conditions = new ArrayList<>();
        conditions.add(getCondition("1",new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.NUMBER.getType(),"$.getimg.BODY.code")
                ,OperatorConditionEnum.LE
                ,new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(),DataTypeEnum.NUMBER.getType(),"$.ddd_login.BODY.code")));
        conditions.add(getCondition("2",new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.BOOLEAN.getType(),"true")
                ,OperatorConditionEnum.EQ
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(),DataTypeEnum.BOOLEAN.getType(),"true")));
        conditions.add(getCondition("3",new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.ARRAY.getType(),"[123,234]")
                ,OperatorConditionEnum.NOTCONTAIN
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.STRING.getType(),"123")));

        boolean rs = ConditionUtils.checkRuleExpression(conditions, ruleExpression);
        assertFalse(rs);
    }

    @Test
    public void testConditionExpressionExce(){

        String ruleExpression = "a and (b or c) and b and (b or c)";
        JSON ctxNode = getContextData();
        List<Condition> conditions = new ArrayList<>();
        conditions.add(getCondition("a",new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.NUMBER.getType(),"$.getimg.BODY.code")
                ,OperatorConditionEnum.LE
                ,new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(),DataTypeEnum.NUMBER.getType(),"$.ddd_login.BODY.code")));
        conditions.add(getCondition("b",new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.BOOLEAN.getType(),"true")
                ,OperatorConditionEnum.EQ
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(),DataTypeEnum.BOOLEAN.getType(),"true")));
        conditions.add(getCondition("c",new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.ARRAY.getType(),"[123,234]")
                ,OperatorConditionEnum.NOTCONTAIN
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.STRING.getType(),"123")));

        boolean rs = ConditionUtils.exec(conditions,ctxNode,ruleExpression);
        assertTrue(rs);
    }

    private Condition getCondition(String id,ConditionValue bValue1,OperatorConditionEnum conditionEnum,ConditionValue bValue2){
        return new Condition(id,null,bValue1,conditionEnum.getCode(),bValue2);
    }

    @Test
    public void testExce(){
        JSON ctxNode = getContextData();

        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.ARRAY.getType(),"$.getimg.HEAD.Content\\-Type")
                ,OperatorConditionEnum.CONTAINS
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.OBJECT.getType(),"{\"key\":\"application/json\"}")
                ,true);

        //固定值条件判断
		this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.BOOLEAN.getType(),"true")
                ,OperatorConditionEnum.EQ
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(),DataTypeEnum.BOOLEAN.getType(),"true")
                ,true);

        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.STRING.getType(),"true")
                ,OperatorConditionEnum.ISNOTNULL
                ,null
                ,true);

        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.ARRAY.getType(),"[123,234]")
                ,OperatorConditionEnum.NOTCONTAIN
                ,new ConditionValue(ParamMappingTypeEnum.FIXED_TYPE.getType(), DataTypeEnum.STRING.getType(),"123")
                ,false);


        //引用值条件判断
        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.STRING.getType(),"$.ddd_login.BODY.code")
                ,OperatorConditionEnum.EQ
                ,new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(),DataTypeEnum.STRING.getType(),"$.getimg.BODY.code")
                ,false);
        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.NUMBER.getType(),"$.ddd_login.BODY.code")
                ,OperatorConditionEnum.GT
                ,new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(),DataTypeEnum.NUMBER.getType(),"$.getimg.BODY.code")
                ,true);

        this.run(ctxNode, new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(), DataTypeEnum.NUMBER.getType(),"$.getimg.BODY.code")
                ,OperatorConditionEnum.LE
                ,new ConditionValue(ParamMappingTypeEnum.REFERENCE_TYPE.getType(),DataTypeEnum.NUMBER.getType(),"$.ddd_login.BODY.code")
                ,true);
    }

    private void run(JSON json, ConditionValue bValue1,OperatorConditionEnum conditionEnum,ConditionValue bValue2,Boolean result) {
        Condition c = new Condition(null,null, bValue1, conditionEnum.getCode(), bValue2);
        boolean rs = ConditionUtils.exec(c,json);
        assertEquals(result, rs);
    }

    private JSON getContextData(){
        String str = "{\n" +
                "  \"getimg\": {\n" +
                "    \"HEAD\": {\n" +
                "      \"Keep-Alive\": [\n" +
                "        \"timeout=60\"\n" +
                "      ],\n" +
                "      \"Connection\": [\n" +
                "        \"keep-alive\"\n" +
                "      ],\n" +
                "      \"Date\": [\n" +
                "        \"Thu, 15 Sep 2022 11:08:54 GMT\"\n" +
                "      ],\n" +
                "      \"Set-Cookie\": [\n" +
                "        \"dpjsid=d8a5e237-e615-46f3-bf8d-9f5acff58f8b; Domain=.wakedt.cn; Path=/; HttpOnly\"\n" +
                "      ],\n" +
                "      \"Content-Length\": [\n" +
                "        \"398226\",\n" +
                "        \"398227\"\n" +
                "      ],\n" +
                "      \"Content-Type\": [\n" +
                "        {\"key\":\"application/json\"},\n" +
                "        {\"key\":\"application/json1\"}\n" +
                "      ]\n" +
                "    },\n" +
                "    \"BODY\": {\n" +
                "      \"msg\": \"操作成功!\",\n" +
                "      \"code\": 100,\n" +
                "      \"data\": {\n" +
                "        \"cutoutImage\": \"AASUVORK5CYII=\",\n" +
                "        \"type\": \"slide\",\n" +
                "        \"shadeImage\": \"A2qVGGGwEkgDArzK7kbYee1PgULCuPSk1d2L+GLaLAIUYFFR5ora5zWP//Z\",\n" +
                "        \"x\": 246,\n" +
                "        \"redisKey\": \"captcha_login_c47f119e-956d-4a52-a095-cc5988f09f24\",\n" +
                "        \"y\": 80\n" +
                "      },\n" +
                "      \"success\": true\n" +
                "    }\n" +
                "  },\n" +
                "  \"start\": {\n" +
                "    \"start\": {\n" +
                "      \"pageSize\": 10,\n" +
                "      \"type\": \"slide\",\n" +
                "      \"param1\": \"param1value\",\n" +
                "      \"param2\": \"param2value\",\n" +
                "      \"pageNo\": 1\n" +
                "    },\n" +
                "    \"type\": \"slide\",\n" +
                "    \"__enable_log__\": \"false\",\n" +
                "    \"param1\": \"param1value\",\n" +
                "    \"param2\": \"param2value\",\n" +
                "    \"BODY\": \"\"\n" +
                "  },\n" +
                "  \"ddd_login\": {\n" +
                "    \"HEAD\": {\n" +
                "      \"Keep-Alive\": [\n" +
                "        \"timeout=60\"\n" +
                "      ],\n" +
                "      \"Connection\": [\n" +
                "        \"keep-alive\"\n" +
                "      ],\n" +
                "      \"Date\": [\n" +
                "        \"Thu, 15 Sep 2022 11:08:54 GMT\"\n" +
                "      ],\n" +
                "      \"Set-Cookie\": [\n" +
                "        \"dpjsid=406bf4a1-5019-46ab-9298-440877786fc6; Domain=.wakedt.cn; Path=/; HttpOnly\"\n" +
                "      ],\n" +
                "      \"Content-Length\": [\n" +
                "        \"74\"\n" +
                "      ],\n" +
                "      \"Content-Type\": [\n" +
                "        \"application/json\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"BODY\": {\n" +
                "      \"msg\": \"系统繁忙，请稍后再试\",\n" +
                "      \"code\": 105,\n" +
                "      \"success\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        return JSON.parseObject(str);
    }

}
