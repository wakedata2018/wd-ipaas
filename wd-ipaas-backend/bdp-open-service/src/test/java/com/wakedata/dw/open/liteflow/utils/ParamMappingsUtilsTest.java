package com.wakedata.dw.open.liteflow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import org.junit.Test;

import java.util.List;

public class ParamMappingsUtilsTest {

    @Test
    public void mappingJsonObject() {
        JSONObject jsonObject = getSimpleJsonObject();
        List<MockParamMappings> paramMappings = Lists.newArrayList();
        paramMappings.add(getSimpleParamMappings("a", "$.a"));
        paramMappings.add(getSimpleParamMappings("b", "$.b"));
        JSON json = ParamMappingsUtils.mappingJson(jsonObject, paramMappings);
        System.out.println(ParamMappingsUtils.prettyFormatJson(json));
    }

    @Test
    public void mappingJsonArray() {
        JSONArray jsonArray = getSimpleJsonArray();
        List<MockParamMappings> paramMappings = Lists.newArrayList();
        paramMappings.add(getSimpleParamMappings("a", "$.a"));
        paramMappings.add(getSimpleParamMappings("b", "$.b"));
        JSON json = ParamMappingsUtils.mappingJson(jsonArray, paramMappings);
        System.out.println(ParamMappingsUtils.prettyFormatJson(json));
    }

    @Test
    public void mappingComplexJsonArray() {

        String jsonStr = "{\n" +
                "\t\"page\":1,\n" +
                "\t\"pageSize\":2,\n" +
                "\t\"flow\":{\n" +
                "\t\t\"nodes\":{\n" +
                "\t\t\t\"node\":[\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"union_x\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.sql.UnionNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"scrpit_1_1_1\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.transform.GroovyScriptComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2_3\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2_2\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node1_2\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2_1\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node1_1\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2_1_b\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node2_1_a\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\":\"node1\",\n" +
                "\t\t\t\t\t\"class\":\"com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t},\n" +
                "\t\t\"chain\":[\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"name\":\"main_chain\",\n" +
                "\t\t\t\t\"condition\":[\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"type\":\"then\",\n" +
                "\t\t\t\t\t\t\"value\":\"chain1,chain2,chain3,chain4\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"name\":\"chain1\",\n" +
                "\t\t\t\t\"condition\":[\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"type\":\"when\",\n" +
                "\t\t\t\t\t\t\"value\":\"node2,node1\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"name\":\"chain2\",\n" +
                "\t\t\t\t\"condition\":[\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"type\":\"when\",\n" +
                "\t\t\t\t\t\t\"value\":\"node2_3,node2_2,node1_2,node2_1,node1_1\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"name\":\"chain3\",\n" +
                "\t\t\t\t\"condition\":[\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"type\":\"when\",\n" +
                "\t\t\t\t\t\t\"value\":\"scrpit_1_1_1,node2_1_b,node2_1_a\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"name\":\"chain4\",\n" +
                "\t\t\t\t\"condition\":[\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"type\":\"when\",\n" +
                "\t\t\t\t\t\t\"value\":\"union_x\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        List<MockParamMappings> paramMappings = Lists.newArrayList();
        paramMappings.add(getSimpleParamMappings("a", "$.page"));
        paramMappings.add(getSimpleParamMappings("b", "$.pageSize"));
        paramMappings.add(getSimpleParamMappings("c", "$.flow.nodes.node.id"));
        JSON json = ParamMappingsUtils.mappingJson(jsonObject, paramMappings);
        System.out.println(ParamMappingsUtils.prettyFormatJson(json));
        System.out.println(ParamMappingsUtils.prettyFormatJson(jsonObject));
    }


    private JSONObject getSimpleJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "a1");
        jsonObject.put("b", "b1");
        jsonObject.put("c", "1");
        return jsonObject;
    }

    private JSONArray getSimpleJsonArray() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "a1");
        jsonObject.put("b", "b1");
        jsonObject.put("c", "1");
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("a", "a2");
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    private MockParamMappings getSimpleParamMappings(String field, String expression) {
        MockParamMappings paramMapping = new MockParamMappings(field, expression);
        return paramMapping;
    }

    private class MockParamMappings extends AbstractParamMapping {
        public MockParamMappings() {
        }

        public MockParamMappings(String field, String expression) {
            super(field, expression);
        }
    }
}