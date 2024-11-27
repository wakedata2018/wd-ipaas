package com.wakedata.dw.open.liteflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent;
import com.wakedata.dw.open.liteflow.component.script.GroovyScriptNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorTools;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.simple.SimpleOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.UnionTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.script.GroovyScriptTransformOperator;
import com.wakedata.dw.open.parammapping.ResponseParamMappings;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = DwOpenWebApplication.class)
public class LiteFlowTest {

    private Integer appId = 1;
    private File file = new File("liteflow");
    private File liteFlowFile = new File(file, appId + ".json");
    private ExecutorService executorService;

    @Before
    public void before() {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.executorService = Executors.newFixedThreadPool(10);
    }

    @After
    public void after() {
    }

    @Test
    public void testParseLiteFlowJson() throws Exception {
        JSONObject params = getParam();
        JSONObject liteFlow = getLiteFlowJson(appId, params).getKey();
        System.out.println(ParamMappingsUtils.prettyFormatJson(liteFlow));
    }

    @Test
    public void testLiteFlowWithMultiThread() throws Exception {
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    testLiteFlow();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
        executorService.awaitTermination(20, TimeUnit.SECONDS);
        executorService.shutdown();
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void testLiteFlow() throws Exception {
        JSONObject params = getParam();
        Pair<JSONObject, DAGTaskEngine.OperatorContainer> liteFlow = getLiteFlowJson(appId, params);
        liteFlowFile.createNewFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(liteFlowFile)) {
            fileOutputStream.write(ParamMappingsUtils.prettyFormatJson(liteFlow.getKey()).getBytes());
        }
//
        FlowExecutor executor = new FlowExecutor();
        LiteflowConfig liteflowConfig = new LiteflowConfig();
        liteflowConfig.setRuleSource("liteflow/" + appId + ".json");
        executor.setLiteflowConfig(liteflowConfig);
        executor.init(false);
        LiteflowResponse response = executor.execute2Resp("main_chain", liteFlow.getRight(), ApiFlowSlot.class);
        JSON jsonObject =  response.getContextBean(ApiFlowSlot.class).getFinalOutputs(Sets.newHashSet());
        System.out.println(ParamMappingsUtils.prettyFormatJson(jsonObject));

        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * 测试循环算子
     */
    @Test
    public void testForeachLiteflow(){
        String data = "{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.ApiFlowAttr\",\"id\":259,\"apiId\":2143,\"locationJson\":\"{\\\"nodes\\\":[{\\\"x\\\":128,\\\"y\\\":443,\\\"outPoints\\\":[[1,0.5]],\\\"uniqueName\\\":\\\"start\\\",\\\"label\\\":\\\"开始算子\\\",\\\"title\\\":\\\"开始\\\",\\\"id\\\":\\\"start\\\",\\\"component\\\":{\\\"name\\\":\\\"start\\\",\\\"desc\\\":\\\"开始算子\\\"}},{\\\"x\\\":900,\\\"y\\\":440,\\\"inPoints\\\":[[0,0.5]],\\\"uniqueName\\\":\\\"end\\\",\\\"label\\\":\\\"结束算子\\\",\\\"title\\\":\\\"结束\\\",\\\"id\\\":\\\"end\\\",\\\"component\\\":{\\\"name\\\":\\\"end\\\",\\\"desc\\\":\\\"结束算子\\\"}},{\\\"x\\\":496.5,\\\"y\\\":452,\\\"outPoints\\\":[[1,0.5]],\\\"inPoints\\\":[[0,0.5]],\\\"uniqueName\\\":\\\"api_external_http\\\",\\\"label\\\":\\\"查询组织属性列表\\\",\\\"title\\\":\\\"Http API\\\",\\\"id\\\":\\\"93d1d3a0-79ef-11ed-83d9-070c0e7e5340\\\",\\\"component\\\":{\\\"componentId\\\":null,\\\"name\\\":\\\"attr_page_query\\\"}}],\\\"edges\\\":[{\\\"id\\\":\\\"edge94d783d1-79ef-11ed-83d9-070c0e7e5340\\\",\\\"sourceId\\\":\\\"start\\\",\\\"targetId\\\":\\\"93d1d3a0-79ef-11ed-83d9-070c0e7e5340\\\",\\\"start\\\":{\\\"x\\\":100,\\\"y\\\":0},\\\"end\\\":{\\\"x\\\":-100,\\\"y\\\":0}},{\\\"id\\\":\\\"edge972da8d1-79ef-11ed-83d9-070c0e7e5340\\\",\\\"sourceId\\\":\\\"93d1d3a0-79ef-11ed-83d9-070c0e7e5340\\\",\\\"targetId\\\":\\\"end\\\",\\\"start\\\":{\\\"x\\\":100,\\\"y\\\":0},\\\"end\\\":{\\\"x\\\":-100,\\\"y\\\":0}}],\\\"count\\\":2}\",\"operators\":{\"start\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.VertexOperator\",\"operatorId\":\"start\",\"parentOperatorId\":\"\",\"name\":\"start\",\"desc\":\"开始算子\",\"outputOperators\":[\"3c1838a0-79e7-11ed-8ec1-8df87d514a6a\"]},\"end\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.EndOperator\",\"operatorId\":\"end\",\"parentOperatorId\":\"\",\"name\":\"end\",\"desc\":\"结束算子\"},\"3c1838a0-79e7-11ed-8ec1-8df87d514a6a\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator\",\"operatorId\":\"3c1838a0-79e7-11ed-8ec1-8df87d514a6a\",\"parentOperatorId\":\"\",\"name\":\"foreach\",\"desc\":\"循环算子\",\"component\":{\"loopCondition\":{\"operatorId\":\"start\",\"field\":\"foreach\",\"dataType\":\"array\",\"type\":\"reference\",\"fixedValue\":null,\"expression\":\"$.start.BODY.list\",\"httpParamKind\":\"BODY\",\"expressionIsJson\":null,\"parentFiled\":null},\"loopCount\":3,\"loopType\":1},\"outputOperators\":[\"end\"]},\"93d1d3a0-79ef-11ed-83d9-070c0e7e5340\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator\",\"operatorId\":\"93d1d3a0-79ef-11ed-83d9-070c0e7e5340\",\"parentOperatorId\":\"3c1838a0-79e7-11ed-8ec1-8df87d514a6a\",\"name\":\"attr_page_query\",\"desc\":\"查询组织属性列表\",\"component\":{\"componentId\":null,\"name\":\"attr_page_query\",\"kind\":null,\"dataAssetApi\":{\"createTime\":\"2022-12-07 11:56:23\",\"updateTime\":\"2022-12-07 14:17:36\",\"lesseeId\":1,\"dataAssetApiId\":2090,\"dataAssetName\":null,\"dataAssetDescription\":null,\"inCharge\":\"testadmin1\",\"dataAssetPublishStatus\":\"PUBLISH\",\"publishTime\":\"2022-12-07 14:17:36\",\"dataAssetApiMethod\":\"organization/wd-organization/rpc/attr.page.query\",\"dataSourceId\":null,\"apiName\":\"查询组织属性列表\",\"updateFrequency\":\"DAY\",\"protocol\":\"HTTP\",\"secret\":\"PRIVATE\",\"apiGroupId\":106,\"apiDescription\":\"支持分页；支持按组织属性名称进行模糊筛选；\",\"responseContentType\":\"JSON\",\"reqMethod\":\"POST\",\"publisher\":\"testadmin1\",\"apiType\":\"EXTERNAL_HTTP\",\"operationType\":\"INSERT\",\"apiSql\":null,\"iconUrl\":null,\"isHttpSubscriber\":null,\"eventCenterId\":null,\"dataAssetApiUri\":null,\"approvalStatus\":null,\"apiAttr\":{\"clazzName\":\"com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr\",\"id\":1311,\"apiId\":2090,\"host\":\"http://172.26.63.113:10860\",\"path\":\"wd-organization/rpc/attr/page.query\",\"timeout\":3000,\"resultExample\":\"{\\n\\t\\\"code\\\":0,\\n\\t\\\"cursor\\\":0,\\n\\t\\\"data\\\":[\\n\\t\\t{\\n\\t\\t\\t\\\"attrCode\\\":\\\"string\\\",\\n\\t\\t\\t\\\"attrName\\\":\\\"string\\\",\\n\\t\\t\\t\\\"id\\\":0,\\n\\t\\t\\t\\\"nodeNumber\\\":0,\\n\\t\\t\\t\\\"sysConfig\\\":0,\\n\\t\\t\\t\\\"updateTime\\\":\\\"2022-12-07 11:55:54\\\"\\n\\t\\t}\\n\\t],\\n\\t\\\"msg\\\":\\\"string\\\",\\n\\t\\\"pageNo\\\":0,\\n\\t\\\"pageSize\\\":0,\\n\\t\\\"success\\\":true,\\n\\t\\\"totalCount\\\":0\\n}\",\"errorExample\":\"{\\n\\t\\\"errorCode\\\":500,\\n\\t\\\"errorMessage\\\":\\\"系统异常\\\",\\n\\t\\\"success\\\":false\\n}\",\"wsMethod\":null,\"wsNameSpaceUri\":null,\"httpCodes\":[{\"errorCode\":\"201\",\"errorMsg\":\"Created\",\"solution\":null},{\"errorCode\":\"401\",\"errorMsg\":\"Unauthorized\",\"solution\":null},{\"errorCode\":\"403\",\"errorMsg\":\"Forbidden\",\"solution\":null},{\"errorCode\":\"404\",\"errorMsg\":\"Not Found\",\"solution\":null}],\"responseResult\":null,\"responseJsonSchema\":null},\"apiAttrs\":null,\"accessAppId\":null,\"accessAppName\":null,\"apiTagInfo\":null,\"apiGroupName\":null},\"parameters\":[{\"createTime\":\"2022-12-07 11:56:23\",\"updateTime\":\"2022-12-07 11:56:23\",\"id\":40341,\"dataAssetId\":2090,\"assetColumns\":\"query\",\"assetDatatype\":\"json\",\"assetColumnsLength\":null,\"descriptions\":\"query\",\"type\":\"PARAMETERS\",\"typeAttr\":\"OPERATOR\",\"httpParamKind\":\"BODY\",\"sample\":\"{\\n\\t\\\"attrName\\\":\\\"string\\\",\\n\\t\\\"id\\\":0,\\n\\t\\\"offset\\\":0,\\n\\t\\\"pageNo\\\":0,\\n\\t\\\"pageSize\\\":0,\\n\\t\\\"searchCount\\\":true,\\n\\t\\\"sortingFields\\\":[\\n\\t\\t{\\n\\t\\t\\t\\\"asc\\\":true,\\n\\t\\t\\t\\\"column\\\":\\\"string\\\"\\n\\t\\t}\\n\\t],\\n\\t\\\"tenantId\\\":0\\n}\",\"required\":true,\"multiValue\":null,\"jsonSchema\":\"{\\\"root\\\":{\\\"type\\\":\\\"object\\\",\\\"required\\\":[\\\"tenantId\\\"],\\\"properties\\\":{\\\"attrName\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"组织属性名称\\\",\\\"name\\\":\\\"attrName\\\"},\\\"id\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int64\\\",\\\"name\\\":\\\"id\\\"},\\\"offset\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"name\\\":\\\"offset\\\"},\\\"pageNo\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"example\\\":1,\\\"description\\\":\\\"页码\\\",\\\"name\\\":\\\"pageNo\\\",\\\"paramValueType\\\":\\\"fixed\\\",\\\"expression\\\":\\\"\\\",\\\"defaultValue\\\":\\\"1\\\"},\\\"pageSize\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"example\\\":10,\\\"description\\\":\\\"每页数量\\\",\\\"maximum\\\":200,\\\"exclusiveMaximum\\\":false,\\\"name\\\":\\\"pageSize\\\",\\\"paramValueType\\\":\\\"fixed\\\",\\\"expression\\\":\\\"\\\",\\\"defaultValue\\\":\\\"10\\\"},\\\"searchCount\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否查询总条数\\\",\\\"name\\\":\\\"searchCount\\\"},\\\"sortingFields\\\":{\\\"type\\\":\\\"array\\\",\\\"description\\\":\\\"排序\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"asc\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否升序, 默认升序\\\",\\\"name\\\":\\\"asc\\\"},\\\"column\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"排序字段\\\",\\\"name\\\":\\\"column\\\"}},\\\"title\\\":\\\"SortingField\\\",\\\"name\\\":\\\"items\\\"},\\\"name\\\":\\\"sortingFields\\\"},\\\"tenantId\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int64\\\",\\\"description\\\":\\\"租户Id\\\",\\\"name\\\":\\\"tenantId\\\",\\\"paramValueType\\\":\\\"reference\\\",\\\"expression\\\":\\\"$.start.QUERY.tenantId\\\",\\\"defaultValue\\\":\\\"\\\"}},\\\"title\\\":\\\"AttributeListPageQuery\\\",\\\"description\\\":\\\"组织属性查询条件\\\",\\\"father_node\\\":[\\\"AttributeListPageQuery\\\"],\\\"name\\\":\\\"root\\\"}}\",\"isSchema\":true,\"defaultInputParam\":null,\"autoPare\":true}],\"results\":[{\"createTime\":\"2022-12-07 11:56:23\",\"updateTime\":\"2022-12-07 11:56:23\",\"id\":40342,\"dataAssetId\":2090,\"assetColumns\":\"__ALL__\",\"assetDatatype\":null,\"assetColumnsLength\":null,\"descriptions\":\"所有信息\",\"type\":\"RESULT\",\"typeAttr\":null,\"httpParamKind\":\"QUERY\",\"sample\":null,\"required\":false,\"multiValue\":false,\"jsonSchema\":null,\"isSchema\":null,\"defaultInputParam\":null,\"autoPare\":true}],\"publicKind\":\"share\"},\"requestParamMappings\":[{\"operatorId\":\"start\",\"field\":\"body\",\"dataType\":\"object\",\"type\":\"reference\",\"fixedValue\":null,\"expression\":\"{\\\"pageNo\\\":{\\\"dataType\\\":\\\"integer\\\",\\\"type\\\":\\\"fixed\\\",\\\"operatorId\\\":\\\"start\\\",\\\"value\\\":\\\"1\\\"},\\\"pageSize\\\":{\\\"dataType\\\":\\\"integer\\\",\\\"type\\\":\\\"fixed\\\",\\\"operatorId\\\":\\\"start\\\",\\\"value\\\":\\\"10\\\"},\\\"tenantId\\\":{\\\"dataType\\\":\\\"integer\\\",\\\"type\\\":\\\"reference\\\",\\\"operatorId\\\":\\\"start\\\",\\\"value\\\":\\\"$.start.QUERY.tenantId\\\"}}\",\"httpParamKind\":\"BODY\",\"expressionIsJson\":null,\"parentFiled\":null}],\"outputOperators\":[\"3c1838a0-79e7-11ed-8ec1-8df87d514a6b\"]},\"3c1838a0-79e7-11ed-8ec1-8df87d514a6b\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator\",\"operatorId\":\"3c1838a0-79e7-11ed-8ec1-8df87d514a6b\",\"parentOperatorId\":\"3c1838a0-79e7-11ed-8ec1-8df87d514a6a\",\"name\":\"forbreak\",\"component\":{\"conditionList\":[{\"id\":\"rule1\",\"desc\":null,\"value1\":{\"type\":\"fixed\",\"dataType\":\"number\",\"expression\":\"123\"},\"operator\":\"lt\",\"value2\":{\"type\":\"fixed\",\"dataType\":\"number\",\"expression\":\"124\"}},{\"id\":\"rule2\",\"desc\":null,\"value1\":{\"type\":\"fixed\",\"dataType\":\"number\",\"expression\":\"21\"},\"operator\":\"gt\",\"value2\":{\"type\":\"fixed\",\"dataType\":\"number\",\"expression\":\"23\"}}],\"ruleExpression\":\"rule1 and rule2\"},\"desc\":\"退出循环算子\",\"outputOperators\":[]}},\"operatorId\":null}";


    }


    @Test
    public void testApiFlowAttr() {
        String json = "{\n" +
                "\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.ApiFlowAttr\",\n" +
                "\t\t\t\"id\": 2,\n" +
                "\t\t\t\"apiId\": 215,\n" +
                "\t\t\t\"locationJson\": \"{}\",\n" +
                "\t\t\t\"operators\": {\n" +
                "\t\t\t\t\"start\": {\n" +
                "\t\t\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.operator.VertexOperator\",\n" +
                "\t\t\t\t\t\"operatorId\": \"start\",\n" +
                "\t\t\t\t\t\"name\": \"start\",\n" +
                "\t\t\t\t\t\"desc\": \"start\",\n" +
                "\t\t\t\t\t\"outputOperators\": [\n" +
                "\t\t\t\t\t\t\"table_api_for_dag\"\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"table_api_for_dag\": {\n" +
                "\t\t\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator\",\n" +
                "\t\t\t\t\t\"operatorId\": \"table_api_for_dag\",\n" +
                "\t\t\t\t\t\"name\": \"table_api_for_dag\",\n" +
                "\t\t\t\t\t\"component\": {\n" +
                "\t\t\t\t\t\t\"dataAssetApi\": {\n" +
                "\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 19:35:50\",\n" +
                "\t\t\t\t\t\t\t\"lesseeId\": 1,\n" +
                "\t\t\t\t\t\t\t\"dataAssetApiId\": 213,\n" +
                "\t\t\t\t\t\t\t\"dataAssetName\": \"T_STREAM_TASK\",\n" +
                "\t\t\t\t\t\t\t\"inCharge\": \"zhangxuejun\",\n" +
                "\t\t\t\t\t\t\t\"dataAssetPublishStatus\": \"UN_PUBLISH\",\n" +
                "\t\t\t\t\t\t\t\"dataAssetApiMethod\": \"ljtest/table_api_for_dag\",\n" +
                "\t\t\t\t\t\t\t\"dataSourceId\": 56,\n" +
                "\t\t\t\t\t\t\t\"apiName\": \"table_api_for_dag\",\n" +
                "\t\t\t\t\t\t\t\"updateFrequency\": \"DAY\",\n" +
                "\t\t\t\t\t\t\t\"protocol\": \"HTTP\",\n" +
                "\t\t\t\t\t\t\t\"secret\": \"PRIVATE\",\n" +
                "\t\t\t\t\t\t\t\"apiGroupId\": 80,\n" +
                "\t\t\t\t\t\t\t\"responseContentType\": \"JSON\",\n" +
                "\t\t\t\t\t\t\t\"reqMethod\": \"GET\",\n" +
                "\t\t\t\t\t\t\t\"publisher\": \"zhangxuejun\",\n" +
                "\t\t\t\t\t\t\t\"apiType\": \"NORMAL_TABLE\",\n" +
                "\t\t\t\t\t\t\t\"apiSql\": \"\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"parameters\": [{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6668,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"接入key\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6669,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"seqNo\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"请求流水号，唯一\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622012755682\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6670,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"timestamp\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"请求时间戳\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622012755682\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6671,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"version\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"版本，默认为1.0\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622012755682\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6672,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"sign\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"加密串，所有请求参数按照字典顺序排序后再md5Hex加密\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622012755682\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6673,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"int\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"页码，默认1\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6674,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"int\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"页大小，默认10\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"10\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6675,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"bigint\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t],\n" +
                "\t\t\t\t\t\t\"results\": [{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6676,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"bigint\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6677,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"TASK_NAME\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"任务名称\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6678,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"TASK_DESC\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"任务描述\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:05:56\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6679,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 213,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"TASK_TYPE\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"任务类型:sql,jar,canvas\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t],\n" +
                "\t\t\t\t\t\t\"publicKind\": \"self\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"requestParamMappings\": [{\n" +
                "\t\t\t\t\t\t\t\"field\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"table_api_for_dag\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t],\n" +
                "\t\t\t\t\t\"outputOperators\": [\n" +
                "\t\t\t\t\t\t\"sql_api_for_dag\"\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"sql_api_for_dag\": {\n" +
                "\t\t\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator\",\n" +
                "\t\t\t\t\t\"operatorId\": \"sql_api_for_dag\",\n" +
                "\t\t\t\t\t\"name\": \"sql_api_for_dag\",\n" +
                "\t\t\t\t\t\"component\": {\n" +
                "\t\t\t\t\t\t\"dataAssetApi\": {\n" +
                "\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:16:30\",\n" +
                "\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:48:06\",\n" +
                "\t\t\t\t\t\t\t\"lesseeId\": 1,\n" +
                "\t\t\t\t\t\t\t\"dataAssetApiId\": 214,\n" +
                "\t\t\t\t\t\t\t\"inCharge\": \"zhangxuejun\",\n" +
                "\t\t\t\t\t\t\t\"dataAssetPublishStatus\": \"UN_PUBLISH\",\n" +
                "\t\t\t\t\t\t\t\"dataAssetApiMethod\": \"ljtest/sql_api_for_dag\",\n" +
                "\t\t\t\t\t\t\t\"dataSourceId\": 56,\n" +
                "\t\t\t\t\t\t\t\"apiName\": \"sql_api_for_dag\",\n" +
                "\t\t\t\t\t\t\t\"updateFrequency\": \"DAY\",\n" +
                "\t\t\t\t\t\t\t\"protocol\": \"HTTP\",\n" +
                "\t\t\t\t\t\t\t\"secret\": \"PRIVATE\",\n" +
                "\t\t\t\t\t\t\t\"apiGroupId\": 80,\n" +
                "\t\t\t\t\t\t\t\"apiDescription\": \"\",\n" +
                "\t\t\t\t\t\t\t\"responseContentType\": \"JSON\",\n" +
                "\t\t\t\t\t\t\t\"reqMethod\": \"GET\",\n" +
                "\t\t\t\t\t\t\t\"publisher\": \"zhangxuejun\",\n" +
                "\t\t\t\t\t\t\t\"apiType\": \"CUSTOM_SQL\",\n" +
                "\t\t\t\t\t\t\t\"apiSql\": \"select ID, TASK_NAME from T_STREAM_TASK where ID = ${ID:long}\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\"parameters\": [{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6689,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"接入key\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6690,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"seqNo\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"请求流水号，唯一\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622014584222\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6691,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"timestamp\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"请求时间戳\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622014584222\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6692,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"version\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"版本，默认为1.0\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622014584222\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6693,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"sign\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"varchar\",\n" +
                "\t\t\t\t\t\t\t\t\"assetColumnsLength\": 32,\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"加密串，所有请求参数按照字典顺序排序后再md5Hex加密\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1622014584222\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6694,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"int\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"页码，默认1\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"1\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6695,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"int\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"页大小，默认10\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"sample\": \"10\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true,\n" +
                "\t\t\t\t\t\t\t\t\"multiValue\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6696,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\t\"assetDatatype\": \"long\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"PARAMETERS\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": true\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t],\n" +
                "\t\t\t\t\t\t\"results\": [{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6697,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\"createTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"updateTime\": \"2021-05-26 15:36:24\",\n" +
                "\t\t\t\t\t\t\t\t\"id\": 6698,\n" +
                "\t\t\t\t\t\t\t\t\"dataAssetId\": 214,\n" +
                "\t\t\t\t\t\t\t\t\"assetColumns\": \"TASK_NAME\",\n" +
                "\t\t\t\t\t\t\t\t\"descriptions\": \"\",\n" +
                "\t\t\t\t\t\t\t\t\"type\": \"RESULT\",\n" +
                "\t\t\t\t\t\t\t\t\"httpParamKind\": \"QUERY\",\n" +
                "\t\t\t\t\t\t\t\t\"required\": false\n" +
                "\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t],\n" +
                "\t\t\t\t\t\t\"publicKind\": \"self\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"requestParamMappings\": [{\n" +
                "\t\t\t\t\t\t\t\"field\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"ID\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"table_api_for_dag\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"appId\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"pageNo\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"field\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\"expression\": \"pageSize\",\n" +
                "\t\t\t\t\t\t\t\"operatorId\": \"start\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t],\n" +
                "\t\t\t\t\t\"outputOperators\": [\"select-column\", \"select-row\"]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"select-column\": {\n" +
                "                    \"name\": \"select-column\",\n" +
                "\t\t\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.operator.transform.ColumnSelectTransformOperator\",\n" +
                "                    \"operatorId\": \"select-column\",\n" +
                "\t\t\t\t\t\"responseParamMappings\": [\n" +
                "                        {\"field\":\"field1\",  \"expression\":\"$.sql_api_for_dag.ID\"},\n" +
                "                        {\"field\":\"field2\",  \"expression\":\"$.sql_api_for_dag.ID\"}\n" +
                "                      ]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"select-row\": {\n" +
                "                    \"name\": \"select-row\",\n" +
                "\t\t\t\t\t\"clazzName\": \"com.wakedata.dw.open.model.api.flow.operator.transform.RowSelectTransformOperator\",\n" +
                "                    \"operatorId\": \"select-row\",\n" +
                "\t\t\t\t\t\"rowExpression\": \"$.sql_api_for_dag[?(ID >= 250)]\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}";
        ApiFlowAttr apiFlowAttr = new ApiFlowAttr();
        apiFlowAttr.setDagJson(json);
        apiFlowAttr = apiFlowAttr.convert();
        System.out.println(apiFlowAttr);
    }

    @Test
    public void testScript() throws Exception {
        GroovyScriptTransformOperator groovyScriptTransformOperator = createGroovyScriptOperator("test", "test");
        GroovyScriptNodeComponent groovyScriptNodeComponent = new GroovyScriptNodeComponent();
        JSON json = groovyScriptNodeComponent.executeScript(groovyScriptTransformOperator,new JSONObject());
        System.out.println(json.toJSONString());
    }

    private Pair<JSONObject, DAGTaskEngine.OperatorContainer> getLiteFlowJson(
            Integer appId,
            JSONObject params) throws Exception {
        ApiFlowAttr apiFlowAttr = new ApiFlowAttr();
        Map<String, Operator> operators = assembleOperators();
        apiFlowAttr.setOperators(operators);

        DAGTaskEngine taskEngine = DAGTaskEngine.getInstance(apiFlowAttr, 1, params);
        DAGTaskEngine.OperatorContainer operatorContainer = taskEngine.execute();

        JSONObject liteFlow = LiteFlowUtils.parseJsonLiteFlow(apiFlowAttr.getApiId(), operatorContainer.getFullOperators());
        return ImmutablePair.of(liteFlow, operatorContainer);
    }

    private JSONObject getParam() {
        JSONObject params = new JSONObject();
        params.put("param1", "param1");
        return params;
    }

    /**
     * 构造: vertex :
     *              node1:
     *                  node1_1
     *                      scrpit_1_1_1
     *                  node1_2
     *              node2:
     *                  node2_1
     *                      node2_1_a
     *                      node2_1_b
     *                          union_x
     *                              node2_3
     *                  node2_2
     *                  node2_3
     *
     * @return
     */
    private Map<String, Operator> assembleOperators() {
        Map<String, Operator> operators = new HashMap<>();
        VertexOperator vertexOperator = new VertexOperator();

        SimpleOperator node1 = createMockOperator("node1", "node1_Name");

        SimpleOperator node1_1 = createMockOperator("node1_1", "node1_1_Name");

        GroovyScriptTransformOperator scriptOperator = createGroovyScriptOperator("scrpit_1_1_1", "scrpit_1_1_1_Name");
        OperatorTools.addOutOperator(node1_1, scriptOperator);

        SimpleOperator node1_2 = createMockOperator("node1_2", "node1_2_Name");
        OperatorTools.addOutOperator(node1, node1_1, node1_2);

        SimpleOperator node2 = createMockOperator("node2", "node2_Name");
        SimpleOperator node2_1 = createMockOperator("node2_1", "node2_1_Name");
        SimpleOperator node2_1_a = createMockOperator("node2_1_a", "node2_1_a_Name");
        SimpleOperator node2_1_b = createMockOperator("node2_1_b", "node2_1_b_Name");
        OperatorTools.addOutOperator(node2_1, node2_1_a, node2_1_b);

        UnionTransformOperator union_x = createUnionTransformOperator("union_x", "union_x_Name");
        OperatorTools.addOutOperator(node2_1_a, union_x);
        OperatorTools.addOutOperator(node2_1_b, union_x);


        SimpleOperator node2_2 = createMockOperator("node2_2", "node2_2_Name");
        SimpleOperator node2_3 = createMockOperator("node2_3", "node2_3_Name");
        OperatorTools.addOutOperator(union_x, node2_3);
        OperatorTools.addOutOperator(node2, node2_1, node2_2, node2_3);

        //加入循环算子和退出循环算子
        ForOperator forOperator = createMockForOperator("for1","for1_Name");
        ApiOperator apiOperator1 = createMockApiOperator("for_api1","for_api1_Name",forOperator.getOperatorId());
        ApiOperator apiOperator2 = createMockApiOperator("for_api2","for_api2_Name",forOperator.getOperatorId());
        ApiOperator apiOperator3 = createMockApiOperator("for_api3","for_api3_Name",forOperator.getOperatorId());
        ApiOperator apiOperator4 = createMockApiOperator("for_api4","for_api4_Name",forOperator.getOperatorId());
        BreakOperator breakOperator = createMockBreakOperator("for_break1","for_break1_Name",forOperator.getOperatorId());

        OperatorTools.addOutOperator(forOperator,apiOperator1);
        OperatorTools.addOutOperator(apiOperator1,apiOperator2);
        OperatorTools.addOutOperator(apiOperator3,apiOperator4);
        OperatorTools.addOutOperator(apiOperator4,breakOperator);
        OperatorTools.addOutOperator(apiOperator2,apiOperator3);

        OperatorTools.addOutOperator(vertexOperator, node1, node2,forOperator);

        operators.put(VertexOperator.VERTEX_OPERATOR_ID, vertexOperator);
        operators.put(node1.getOperatorId(), node1);
        operators.put(node1_1.getOperatorId(), node1_1);
        operators.put(scriptOperator.getOperatorId(), scriptOperator);


        operators.put(node1_2.getOperatorId(), node1_2);

        operators.put(node2.getOperatorId(), node2);
        operators.put(node2_1.getOperatorId(), node2_1);
        operators.put(node2_1_a.getOperatorId(), node2_1_a);
        operators.put(node2_1_b.getOperatorId(), node2_1_b);
        operators.put(union_x.getOperatorId(), union_x);


        operators.put(node2_2.getOperatorId(), node2_2);
        operators.put(node2_3.getOperatorId(), node2_3);

        operators.put(forOperator.getOperatorId(),forOperator);
        operators.put(apiOperator1.getOperatorId(),apiOperator1);
        operators.put(apiOperator2.getOperatorId(),apiOperator2);
        operators.put(apiOperator3.getOperatorId(),apiOperator3);
        operators.put(apiOperator4.getOperatorId(),apiOperator4);
        operators.put(breakOperator.getOperatorId(), breakOperator);
        return operators;
    }

    private UnionTransformOperator createUnionTransformOperator(String operatorId, String operatorName) {
        UnionTransformOperator unionTransformOperator = new UnionTransformOperator();
        unionTransformOperator.setOperatorId(operatorId);
        unionTransformOperator.setName(operatorName);
        return unionTransformOperator;
    }

    private GroovyScriptTransformOperator createGroovyScriptOperator(String operatorId, String operatorName) {
        GroovyScriptTransformOperator scriptOperator = new GroovyScriptTransformOperator();
        scriptOperator.setOperatorId(operatorId);
        scriptOperator.setName(operatorName);
        try {
            File scriptFile = new File("../bdp-open-service/src/main/java/com/wakedata/dw/open/liteflow/utils/SimpleGroovyDataConvert.groovy");
            try (FileInputStream inputStream = new FileInputStream(scriptFile)) {
                scriptOperator.setGroovy(StringUtils.join(IOUtils.readLines(inputStream, StandardCharsets.UTF_8), "\n"));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return scriptOperator;
    }

    private SimpleOperator createMockOperator(String operatorId, String operatorName) {
        SimpleOperator simpleOperator = new SimpleOperator();
        simpleOperator.setOperatorId(operatorId);
        simpleOperator.setName(operatorName);

        if (operatorId.equals("node2_1")) {
            List<ResponseParamMappings> responseMappings = Arrays.asList(
                    new ResponseParamMappings(SimpleNodeComponent.ID + "_refector", "$." + SimpleNodeComponent.ID),
                    new ResponseParamMappings(SimpleNodeComponent.DATA + "_refector", "$." + SimpleNodeComponent.DATA)
            );
        }
        return simpleOperator;
    }

    private ForOperator createMockForOperator(String operatorId, String operatorName) {
        ForOperator simpleOperator = new ForOperator();
        simpleOperator.setOperatorId(operatorId);
        simpleOperator.setName(operatorName);
        simpleOperator.setOutputOperators(new HashSet<>());
        return simpleOperator;
    }

    private BreakOperator createMockBreakOperator(String operatorId, String operatorName,String parentOperatorId) {
        BreakOperator simpleOperator = new BreakOperator();
        simpleOperator.setOperatorId(operatorId);
        simpleOperator.setName(operatorName);
        simpleOperator.setParentOperatorId(parentOperatorId);
        simpleOperator.setOutputOperators(new HashSet<>());
        return simpleOperator;
    }

    private ApiOperator createMockApiOperator(String operatorId, String operatorName, String parentOperatorId) {
        ApiOperator simpleOperator = new ApiOperator();
        simpleOperator.setOperatorId(operatorId);
        simpleOperator.setName(operatorName);
        simpleOperator.setParentOperatorId(parentOperatorId);
        simpleOperator.setOutputOperators(new HashSet<>());
        return simpleOperator;
    }
}