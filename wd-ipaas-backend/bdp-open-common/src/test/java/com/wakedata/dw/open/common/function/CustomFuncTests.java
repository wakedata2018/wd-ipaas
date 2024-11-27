package com.wakedata.dw.open.common.function;

import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.function.FunctionEnumUtil;
import com.wakedata.dw.open.function.FunctionVo;
import com.wakedata.dw.open.function.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * 自定义函数测试
 * @author luomeng
 * @date 2022/10/31 21:10
 */
@Slf4j
public class CustomFuncTests {
    @Test
    public void contextLoads() {
    }

    @Test
    public void testMatchCustomFunc(){
//        String content = "fn.custom.test(fn.string.toString(fn.custom.test(),fn.custom.(),fn.custom()),fn.custom.nowTime9(),,fn.custom._md5())";
        String content = "{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.ApiFlowAttr\",\"id\":195,\"apiId\":1941,\"locationJson\":\"{\\\"nodes\\\":[{\\\"x\\\":156,\\\"y\\\":421,\\\"outPoints\\\":[[1,0.5]],\\\"uniqueName\\\":\\\"start\\\",\\\"label\\\":\\\"开始算子\\\",\\\"title\\\":\\\"开始\\\",\\\"id\\\":\\\"start\\\",\\\"component\\\":{\\\"name\\\":\\\"start\\\",\\\"desc\\\":\\\"开始算子\\\"}},{\\\"x\\\":895,\\\"y\\\":430,\\\"inPoints\\\":[[0,0.5]],\\\"uniqueName\\\":\\\"end\\\",\\\"label\\\":\\\"结束算子\\\",\\\"title\\\":\\\"结束\\\",\\\"id\\\":\\\"end\\\",\\\"component\\\":{\\\"name\\\":\\\"end\\\",\\\"desc\\\":\\\"结束算子\\\"}},{\\\"x\\\":465.5,\\\"y\\\":440,\\\"outPoints\\\":[[1,0.5]],\\\"inPoints\\\":[[0,0.5]],\\\"uniqueName\\\":\\\"api_external_http\\\",\\\"label\\\":\\\"分页查询数据字典项\\\",\\\"title\\\":\\\"Http API\\\",\\\"id\\\":\\\"b8447940-5bef-11ed-9f9a-9d21e039766e\\\",\\\"component\\\":{\\\"componentId\\\":null,\\\"name\\\":\\\"rpc_dict_rpc_dict_e\\\"}}],\\\"edges\\\":[{\\\"id\\\":\\\"edgebf4d02c1-5bef-11ed-9f9a-9d21e039766e\\\",\\\"sourceId\\\":\\\"start\\\",\\\"targetId\\\":\\\"b8447940-5bef-11ed-9f9a-9d21e039766e\\\",\\\"start\\\":{\\\"x\\\":100,\\\"y\\\":0},\\\"end\\\":{\\\"x\\\":-100,\\\"y\\\":0}},{\\\"id\\\":\\\"edgec03b8171-5bef-11ed-9f9a-9d21e039766e\\\",\\\"sourceId\\\":\\\"b8447940-5bef-11ed-9f9a-9d21e039766e\\\",\\\"targetId\\\":\\\"end\\\",\\\"start\\\":{\\\"x\\\":100,\\\"y\\\":0},\\\"end\\\":{\\\"x\\\":-100,\\\"y\\\":0}}],\\\"count\\\":2}\",\"operators\":{\"start\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.VertexOperator\",\"operatorId\":\"start\",\"name\":\"start\",\"desc\":\"开始算子\",\"outputOperators\":[\"b8447940-5bef-11ed-9f9a-9d21e039766e\"]},\"end\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.EndOperator\",\"operatorId\":\"end\",\"name\":\"end\",\"desc\":\"结束算子\"},\"b8447940-5bef-11ed-9f9a-9d21e039766e\":{\"clazzName\":\"com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator\",\"operatorId\":\"b8447940-5bef-11ed-9f9a-9d21e039766e\",\"name\":\"rpc_dict_rpc_dict_e\",\"desc\":\"分页查询数据字典项\",\"component\":{\"componentId\":null,\"name\":\"rpc_dict_rpc_dict_e\",\"kind\":null,\"dataAssetApi\":{\"createTime\":\"2022-10-26 21:55:20\",\"updateTime\":\"2022-10-26 21:57:46\",\"lesseeId\":1,\"dataAssetApiId\":1865,\"dataAssetName\":null,\"dataAssetDescription\":null,\"inCharge\":\"testadmin1\",\"dataAssetPublishStatus\":\"PUBLISH\",\"publishTime\":\"2022-10-26 21:57:46\",\"dataAssetApiMethod\":\"dict/wd/general/rpc.dict.rpc.dict.entry.query\",\"dataSourceId\":null,\"apiName\":\"分页查询数据字典项\",\"updateFrequency\":\"DAY\",\"protocol\":\"HTTP\",\"secret\":\"PRIVATE\",\"apiGroupId\":82,\"apiDescription\":\"分页查询数据字典项\",\"responseContentType\":\"JSON\",\"reqMethod\":\"POST\",\"publisher\":\"testadmin1\",\"apiType\":\"EXTERNAL_HTTP\",\"operationType\":\"INSERT\",\"apiSql\":null,\"iconUrl\":null,\"isHttpSubscriber\":null,\"eventCenterId\":null,\"dataAssetApiUri\":null,\"approvalStatus\":null,\"apiAttr\":{\"clazzName\":\"com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr\",\"id\":1167,\"apiId\":1865,\"host\":\"http://172.26.63.188:25322\",\"path\":\"wd/general/rpc/dict/rpc/dict/entry/query\",\"timeout\":3000,\"resultExample\":\"{\\n\\t\\\"code\\\":0,\\n\\t\\\"cursor\\\":0,\\n\\t\\\"data\\\":[\\n\\t\\t{\\n\\t\\t\\t\\\"businessDomainId\\\":0,\\n\\t\\t\\t\\\"businessDomainName\\\":\\\"string\\\",\\n\\t\\t\\t\\\"createTime\\\":\\\"2022-10-26 21:55:09\\\",\\n\\t\\t\\t\\\"dictEntryDescription\\\":\\\"string\\\",\\n\\t\\t\\t\\\"dictEntryName\\\":\\\"string\\\",\\n\\t\\t\\t\\\"dictEntryValue\\\":\\\"string\\\",\\n\\t\\t\\t\\\"dictId\\\":0,\\n\\t\\t\\t\\\"id\\\":0,\\n\\t\\t\\t\\\"isEnable\\\":0,\\n\\t\\t\\t\\\"updateTime\\\":\\\"2022-10-26 21:55:09\\\"\\n\\t\\t}\\n\\t],\\n\\t\\\"msg\\\":\\\"string\\\",\\n\\t\\\"pageNo\\\":0,\\n\\t\\\"pageSize\\\":0,\\n\\t\\\"success\\\":true,\\n\\t\\\"totalCount\\\":0\\n}\",\"errorExample\":\"{\\n\\t\\\"errorCode\\\":500,\\n\\t\\\"errorMessage\\\":\\\"系统异常\\\",\\n\\t\\\"success\\\":false\\n}\",\"wsMethod\":null,\"wsNameSpaceUri\":null,\"httpCodes\":[{\"errorCode\":\"201\",\"errorMsg\":\"Created\",\"solution\":null},{\"errorCode\":\"401\",\"errorMsg\":\"Unauthorized\",\"solution\":null},{\"errorCode\":\"403\",\"errorMsg\":\"Forbidden\",\"solution\":null},{\"errorCode\":\"404\",\"errorMsg\":\"Not Found\",\"solution\":null}],\"responseResult\":null,\"responseJsonSchema\":null},\"apiAttrs\":null,\"accessAppId\":null,\"accessAppName\":null,\"apiTagInfo\":null,\"apiGroupName\":null},\"parameters\":[{\"createTime\":\"2022-10-26 21:57:31\",\"updateTime\":\"2022-10-26 21:57:31\",\"id\":29322,\"dataAssetId\":1865,\"assetColumns\":\"body\",\"assetDatatype\":\"json\",\"assetColumnsLength\":null,\"descriptions\":\"请求体参数\",\"type\":\"PARAMETERS\",\"typeAttr\":\"OPERATOR\",\"httpParamKind\":\"BODY\",\"sample\":\"{\\\"dictEntryName\\\":\\\"\\\",\\\"dictEntryValue\\\":\\\"\\\",\\\"dictId\\\":0,\\\"id\\\":0,\\\"isEnable\\\":0,\\\"offset\\\":0,\\\"pageNo\\\":0,\\\"pageSize\\\":0,\\\"searchCount\\\":false,\\\"sortingFields\\\":[{\\\"asc\\\":false,\\\"column\\\":\\\"\\\"}]}\",\"required\":false,\"multiValue\":null,\"jsonSchema\":\"{\\\"root\\\":{\\\"type\\\":\\\"object\\\",\\\"required\\\":[\\\"dictId\\\"],\\\"properties\\\":{\\\"dictEntryName\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"字典项名称\\\",\\\"name\\\":\\\"dictEntryName\\\"},\\\"dictEntryValue\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"字典值\\\",\\\"name\\\":\\\"dictEntryValue\\\"},\\\"dictId\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int64\\\",\\\"description\\\":\\\"数据字典id\\\",\\\"name\\\":\\\"dictId\\\",\\\"paramValueType\\\":\\\"fixed\\\",\\\"expression\\\":\\\"\\\",\\\"defaultValue\\\":\\\"1\\\"},\\\"id\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int64\\\",\\\"name\\\":\\\"id\\\"},\\\"isEnable\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"description\\\":\\\"是否启用，0:启用、1:不启用\\\",\\\"name\\\":\\\"isEnable\\\"},\\\"offset\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"name\\\":\\\"offset\\\"},\\\"pageNo\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"example\\\":1,\\\"description\\\":\\\"页码(不能为空)\\\",\\\"name\\\":\\\"pageNo\\\"},\\\"pageSize\\\":{\\\"type\\\":\\\"integer\\\",\\\"format\\\":\\\"int32\\\",\\\"example\\\":10,\\\"description\\\":\\\"每页数量(不能为空)\\\",\\\"maximum\\\":200,\\\"exclusiveMaximum\\\":false,\\\"name\\\":\\\"pageSize\\\",\\\"paramValueType\\\":\\\"function\\\",\\\"expression\\\":\\\"fn.custom.addNum(1,2)\\\",\\\"defaultValue\\\":\\\"\\\"},\\\"searchCount\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否查询总条数\\\",\\\"name\\\":\\\"searchCount\\\"},\\\"sortingFields\\\":{\\\"type\\\":\\\"array\\\",\\\"description\\\":\\\"排序\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"asc\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否升序, 默认升序\\\",\\\"name\\\":\\\"asc\\\"},\\\"column\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"排序字段\\\",\\\"name\\\":\\\"column\\\"}},\\\"title\\\":\\\"SortingField\\\",\\\"name\\\":\\\"items\\\"},\\\"name\\\":\\\"sortingFields\\\"}},\\\"title\\\":\\\"DictEntryQueryVO\\\",\\\"description\\\":\\\"数据字典项查询VO\\\",\\\"father_node\\\":[\\\"DictEntryQueryVO\\\"],\\\"name\\\":\\\"root\\\"}}\",\"isSchema\":null,\"defaultInputParam\":null,\"autoPare\":true}],\"results\":[{\"createTime\":\"2022-10-26 21:57:31\",\"updateTime\":\"2022-10-26 21:57:31\",\"id\":29323,\"dataAssetId\":1865,\"assetColumns\":\"__ALL__\",\"assetDatatype\":null,\"assetColumnsLength\":null,\"descriptions\":\"所有信息\",\"type\":\"RESULT\",\"typeAttr\":null,\"httpParamKind\":\"QUERY\",\"sample\":null,\"required\":false,\"multiValue\":false,\"jsonSchema\":null,\"isSchema\":null,\"defaultInputParam\":null,\"autoPare\":true}],\"publicKind\":\"share\"},\"requestParamMappings\":[{\"operatorId\":\"start\",\"field\":\"body\",\"dataType\":null,\"type\":\"reference\",\"fixedValue\":null,\"expression\":\"{\\\"dictId\\\":{\\\"dataType\\\":\\\"integer\\\",\\\"type\\\":\\\"fixed\\\",\\\"operatorId\\\":\\\"start\\\",\\\"value\\\":\\\"1\\\"},\\\"pageSize\\\":{\\\"dataType\\\":\\\"integer\\\",\\\"type\\\":\\\"function\\\",\\\"operatorId\\\":\\\"start\\\",\\\"value\\\":\\\"fn.custom.addNum(1,2)\\\"}}\",\"httpParamKind\":\"BODY\",\"expressionIsJson\":null,\"parentFiled\":null}],\"outputOperators\":[\"end\"]}},\"operatorId\":null}";
        Set<String> result = CustomFunctionUtil.extractCustomFuncNames(content);
        log.info("match func:{}",JSONUtil.toJsonPrettyStr(result));
    }

    @Test
    public void testGroovyScriptExecute(){
        CustomFunction customFunction = new CustomFunction();
        customFunction.setName("addNum");
        customFunction.setDesc("测试");
        customFunction.setLanguage(CustomFunctionTemplateEnum.GROOVY_SCRIPT.getLanguage());
        customFunction.setReturnType(CustomFunctionReturnTypeEnum.STRING.getType());
        List<CustomFunction.FunctionParam> paramList = new ArrayList<>();
        paramList.add(new CustomFunction.FunctionParam("a", 1));
        paramList.add(new CustomFunction.FunctionParam("b", 2));
        customFunction.setParamList(paramList);
        customFunction.setCode("package com.wakedata.dw.open.function.custom\n" +
                "\n" +
                "/**\n" +
                " * Groovy Script脚本实现\n" +
                " */\n " +
                "class CustomFunction {\n" +
                "\n" +
                "   \n //xxx\n" +
                "    Integer addNum(Integer a,Integer b) {\n" +
                "        \n" +
                "        return a + b\n" +
                "    }\n" +
                "   \n" +
                "    Integer sum() {\n" +
                "        \n" +
                "        return 15+20\n" +
                "    }\n" +
                "}");
        Object result = GroovyScriptEngine.execute(customFunction);
        log.info("result:{}", result);
        assertEquals("3", result);

    }

}
