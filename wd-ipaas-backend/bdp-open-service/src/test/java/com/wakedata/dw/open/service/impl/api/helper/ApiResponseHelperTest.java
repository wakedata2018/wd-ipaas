package com.wakedata.dw.open.service.impl.api.helper;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wakedata.dw.open.service.utils.JsonUtils.*;
import static com.wakedata.dw.open.service.utils.RequestParamUtils.toCheckRequired;


@SpringBootTest
public class ApiResponseHelperTest {


    /**
     * API文档JsonSchema
     */
    @Test
    public void testBug(){
        String jsonSchema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"properties\":{\"field_0.838558470193778\":" +
                "{\"type\":\"string\",\"name\":\"code\"},\"field_0.8507605369483249\":{\"type\":\"array\",\"name\":\"data\",\"items\"" +
                ":{\"type\":\"object\",\"properties\":{\"field_0.7296895318645737\":{\"type\":\"string\",\"name\":\"attrCode\"," +
                "\"description\":\"组织属性标识\"},\"field_0.7827271814946437\":{\"type\":\"string\",\"name\":\"attrName\",\"description\":" +
                "\"组织属性名字\"},\"field_0.533627734605969\":{\"type\":\"array\",\"name\":\"children\",\"items\":{\"type\":\"string\"},\"" +
                "description\":\"下级组织\"},\"field_0.9885772733162888\":{\"type\":\"string\",\"name\":\"childrenNum\",\"description\":\"" +
                "下级节点总数\"},\"field_0.1350946826088777\":{\"type\":\"string\",\"name\":\"ID\",\"description\":\"自增id\"},\"field_0.615484" +
                "938648595\":{\"type\":\"string\",\"name\":\"levelName\",\"description\":\"层级名称\"},\"field_0.8337089377429701\":{\"type\":\"ob" +
                "ject\",\"name\":\"needCountAttrCode\",\"description\":\"统计的节点属性的数量\",\"properties\":{\"field_0.0791836250049418\":{\"type\":" +
                "\"string\",\"name\":\"nodeCode\",\"description\":\"节点编码(唯一标识)\"},\"field_0.6025693329567143\":{\"type\":\"string\",\"name\":\"nod" +
                "eLeaf\",\"description\":\"是否叶子节点,1是，0否\"},\"field_0.4979434289260656\":{\"type\":\"string\",\"name\":\"nodeLevel\",\"description\"" +
                ":\"节点级别\"},\"field_0.3252185794445861\":{\"type\":\"string\",\"name\":\"nodeName\",\"description\":\"节点名字\"},\"field_0.046280609" +
                "356569524\":{\"type\":\"string\",\"name\":\"nodeStatus\",\"description\":\"状态，1 启用，0 禁用\"},\"field_0.7745145578076076\":{\"type\":\"st" +
                "ring\",\"name\":\"nodeType\",\"description\":\"节点类型,org 组织,resource 资源\"},\"field_0.7577239896944399\":{\"type\":\"string\",\"name\":" +
                "\"parentId\",\"description\":\"父id\"},\"field_0.24627229670229345\":{\"type\":\"string\",\"name\":\"parentIds\",\"description\"" +
                ":\"上级节点ids\"},\"field_0.07905248044138435\":{\"type\":\"string\",\"name\":\"permission\",\"description\":\"是否有权限,1 是，0 否" +
                "\"},\"field_0.3445173315490462\":{\"type\":\"string\",\"name\":\"resourceChildrenNum\",\"description\":\"所有下级资源总数\"},\"" +
                "field_0.5154622113849943\":{\"type\":\"string\",\"name\":\"resourceId\",\"description\":\"资源id\"},\"field_0.6780607109420107\"" +
                ":{\"type\":\"string\",\"name\":\"resourceNextChildrenNum\",\"description\":\"所在本节点的下一层的资源总数\"},\"field_0.6890867438261354\"" +
                ":{\"type\":\"string\",\"name\":\"seq\",\"description\":\"节点顺序\"},\"field_0.02177509945911904\":{\"type\":\"string\",\"name\":\"updateTime\"" +
                ",\"description\":\"更新时间\"}}}}}}}}}";
        String json = JsonSchemaToJson(jsonSchema);
        System.out.println(json);
    }

    /**
     * 测试多数组连续嵌套(4层)
     */
    @Test
    public void testAllArray(){
        String jsonSchema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{" +
                "\"field_0.6073706256887417\":{\"type\":\"array\",\"name\":\"1数组\",\"items\":{\"type\":\"object\",\"name" +
                "\":\"items\",\"properties\":{\"field_0.3230068852523269\":{\"type\":\"array\",\"name\":\"2数组\",\"items\"" +
                ":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.578768625465476\":{\"type\":\"array\"," +
                "\"name\":\"3数组\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.11037344761250623\"" +
                ":{\"type\":\"array\",\"name\":\"4数组\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":" +
                "{\"field_0.9746493195160537\":{\"type\":\"string\",\"name\":\"name\"}}}}}}}}}}}}}}}}";
        String json = JsonSchemaToJson(jsonSchema);
        System.out.println(json);
    }

    /**
     * 测试多层对象连续嵌套(4层)
     */
    @Test
    public void testAllObject(){
        String jsonSchema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":" +
                "{\"field_0.39578277619646984\":{\"type\":\"object\",\"name\":\"1对象\",\"properties\":{\"field_0.645240163810453\"" +
                ":{\"type\":\"object\",\"name\":\"2对象\",\"properties\":{\"field_0.3716001180877526\":{\"type\":\"object\",\"name\"" +
                ":\"3对象\",\"properties\":{\"field_0.5861718887118237\":{\"type\":\"object\",\"name\":\"4对象\",\"properties\":" +
                "{\"field_0.10027920970219562\":{\"type\":\"string\",\"name\":\"name\"}}}}}}}}}}}}";
        String json = JsonSchemaToJson(jsonSchema);
        System.out.println(json);
    }

    /**
     * 测试选择Api返回参数模版列表的情况(实际使用场景)
     */
    @Test
    public void testLiftFlowResult() {
        List<ApiRespParamDTO> apiRespParamDTOList = new ArrayList<>();

        ApiRespParamDTO body = new ApiRespParamDTO();
        body.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        body.setAssetColumns("wcsBody");
        body.setId(12439);
        body.setAssetDataType("string");
        body.setType(HttpParamKind.BODY);
        body.setNodeId("8f42c0e0-2e88-11ed-ae13-2f89604e0757");
        body.setNodeName("wcstest009");
//        body.setResponsePostData("{\"root\":{\"name\":\"root\",\"type\":\"object\",\"properties\":{\"field_0_1\":" +
//                "{\"name\":\"x\",\"type\":\"number\",\"paramValueType\":\"reference\",\"defaultValue\":null,\"expression\":\"" +
//                "$.getimg.BODY.data.x\",\"description\":null,\"operatorId\":\"73bce980-300a-11ed-b854-c18ad522259f\"},\"field_0_2" +
//                "\":{\"name\":\"y\",\"type\":\"number\",\"paramValueType\":\"reference\",\"defaultValue\":null,\"expression\":\"$.getimg.HEAD.Set" +
//                "\\\\-Cookie\",\"description\":null,\"operatorId\":\"73bce980-300a-11ed-b854-c18ad522259f\"},\"field_0_3\":{\"name\":\"type\",\"type\":\"string\"" +
//                ",\"paramValueType\":\"reference\",\"defaultValue\":null,\"expression\":\"$.start.type\",\"description\":null},\"field_0_4\":{\"name\":\"redisKey\",\"" +
//                "type\":\"string\",\"paramValueType\":\"reference\",\"defaultValue\":null,\"expression\":\"$.getimg.BODY.data.redisKey\",\"description\":null,\"" +
//                "operatorId\":\"73bce980-300a-11ed-b854-c18ad522259f\"},\"field_0.03627385525298066\":{\"type\":\"object\",\"name\":\"test\",\"properties\":" +
//                "{\"field_0.4091531676088018\":{\"type\":\"string\",\"name\":\"t1\",\"paramValueType\":\"fixed\",\"defaultValue\":\"1111\"},\"field_0.8838503108" +
//                "743352\":{\"type\":\"string\",\"name\":\"t2\",\"paramValueType\":\"reference\",\"expression\":\"$.start.type\",\"operatorId\":\"start\"},\"field_0.87" +
//                "82626843057724\":{\"type\":\"object\",\"name\":\"t3\",\"properties\":{\"field_0.8942910095876944\":{\"type\":\"string\",\"name\":\"t31\",\"paramValue" +
//                "Type\":\"fixed\",\"defaultValue\":\"tt1\"},\"field_0.28414580298301106\":{\"type\":\"string\",\"name\":\"t32\",\"paramValueType\":\"reference\"" +
//                ",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"73bce980-300a-11ed-b854-c18ad522259f\"}}}}}}}}");

        body.setResponsePostData("{\"root\":{\"type\":\"object\",\"name\":\"root\",\"properties\":{\"field_0.68311" +
                "56818001083\":{\"type\":\"string\",\"name\":\"head\",\"paramValueType\":\"method\",\"expression\":\"fn" +
                ".codec.md5({$.getimg.BODY.body})\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0." +
                "3331421403810302\":{\"type\":\"object\",\"name\":\"对象1\",\"properties\":{\"field_0.36467924655291717\"" +
                ":{\"type\":\"object\",\"name\":\"对象21\",\"properties\":{\"field_0.7422071749459862\":{\"type\":\"string\"" +
                ",\"name\":\"对象211\",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"" +
                "operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0.5025646608950354\":{\"type\":\"object\"" +
                ",\"name\":\"对象212\",\"properties\":{\"field_0.2656334465597081\":{\"type\":\"string\",\"name\":\"对象21" +
                "21\",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"b" +
                "e566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0.5646706026909305\":{\"type\":\"string\",\"name\":\"\",\"" +
                "paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"be566bb" +
                "0-35b9-11ed-bee5-3b89dd814a95\"}}}}},\"field_0.4078545386691603\":{\"type\":\"string\",\"name\":\"对象22\"" +
                ",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"be566b" +
                "b0-35b9-11ed-bee5-3b89dd814a95\"}}}}}}");
        apiRespParamDTOList.add(body);

        List<ApiRespParamDTO> apiRespParamDTOList1 = ApiResponseHelper.buildApiResponseDTOList(apiRespParamDTOList);
        JSONArray jsonArray = JSONUtil.parseArray(apiRespParamDTOList1);
        System.out.println(jsonArray);
    }

    /**
     * 测试选择Api返回参数模版列表的情况(多层对象嵌套)
     */
    @Test
    public void testLiftFlowResultAllObject() {
        List<ApiRespParamDTO> apiRespParamDTOList = new ArrayList<>();

        ApiRespParamDTO body = new ApiRespParamDTO();
        body.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        body.setAssetColumns("wcsBody");
        body.setId(12439);
        body.setAssetDataType("string");
        body.setType(HttpParamKind.BODY);
        body.setNodeId("8f42c0e0-2e88-11ed-ae13-2f89604e0757");
        body.setNodeName("wcstest009");
        body.setResponsePostData("{\"root\":{\"type\":\"object\",\"name\":\"root\",\"properties\":{\"field_0.6831156818001083\"" +
                ":{\"type\":\"string\",\"name\":\"head\",\"paramValueType\":\"method\",\"expression\":\"fn.codec.md5({$.getimg.BODY.body})\",\"" +
                "operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0.9808321035626113\":{\"type\":\"object\",\"name\":\"1对象\",\"" +
                "properties\":{\"field_0.35726468286587165\":{\"type\":\"object\",\"name\":\"2对象\",\"properties\":{\"field_0.24935081202274634\":" +
                "{\"type\":\"object\",\"name\":\"3对象\",\"properties\":{\"field_0.9654523449660615\":{\"type\":\"object\",\"name\":\"4对象\",\"properties\"" +
                ":{\"field_0.0011299899856249684\":{\"type\":\"string\",\"name\":\"name\",\"paramValueType\":\"reference\",\"expression\":\"" +
                "$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"}}}}}}}}}}}}");
        apiRespParamDTOList.add(body);

        List<ApiRespParamDTO> apiRespParamDTOList1 = ApiResponseHelper.buildApiResponseDTOList(apiRespParamDTOList);
        JSONArray jsonArray = JSONUtil.parseArray(apiRespParamDTOList1);
        System.out.println(jsonArray);
    }

    @Test
    public void testRequired(){
        String jsonSchema = "{\n" +
                "    \"root\":{\n" +
                "        \"type\":\"object\",\n" +
                "        \"name\":\"root\",\n" +
                "        \"description\":\"根层级\",\n" +
                "        \"properties\":{\n" +
                "            \"field_0.9080003077913297\":{\n" +
                "                \"type\":\"string\",\n" +
                "                \"name\":\"test1\",\n" +
                "                \"value\":\"test1\"\n" +
                "            },\n" +
                "            \"field_0.16229193158407096\":{\n" +
                "                \"type\":\"string\",\n" +
                "                \"name\":\"test2\",\n" +
                "                \"value\":\"test2\"\n" +
                "            },\n" +
                "            \"field_0.7171718488183838\":{\n" +
                "                \"name\":\"test3\",\n" +
                "                \"type\":\"object\",\n" +
                "                \"properties\":{\n" +
                "                    \"field_0.024671623591719483\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"name\":\"test4.1\",\n" +
                "                        \"value\":\"test4\"\n" +
                "                    },\n" +
                "                    \"field_0.502016098303824\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"name\":\"test5.1\",\n" +
                "                        \"value\":\"test5\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"required\":[\n" +
                "                    \"field_0.024671623591719483\",\n" +
                "                    \"field_0.502016098303824\"\n" +
                "                ]\n" +
                "            },\n" +
                "            \"field_0.18802628785958486\":{\n" +
                "                \"name\":\"array1\",\n" +
                "                \"type\":\"array\",\n" +
                "                \"items\":{\n" +
                "                    \"type\":\"object\",\n" +
                "                    \"name\":\"items\",\n" +
                "                    \"properties\":{\n" +
                "                        \"field_0.6322035985715468\":{\n" +
                "                            \"type\":\"string\",\n" +
                "                            \"name\":\"test4\"\n" +
                "                        },\n" +
                "                        \"field_0.599805356421508\":{\n" +
                "                            \"type\":\"string\",\n" +
                "                            \"name\":\"test5\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"required\":[\n" +
                "                    \"field_0.599805356421508\",\n" +
                "                    \"field_0.6322035985715468\"\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        \"required\":[\n" +
                "            \"field_0.9080003077913297\"\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        List<String> strings = JsonSchemaToRequiredColum(jsonSchema);
        System.out.println(strings);

        String json = "{\"test1\":\"test1\",\"test2\":\"test2\",\"test3\":{\"test4.1\":\"test4\",\"test5.1\":\"test5\"},\"array1\":[{\"test4\":\"test4\",\"test5\":\"test5\"}]}";
        Map<String, Object> map = JsonToMap(json);
        System.out.println(map);

        toCheckRequired(jsonSchema,json);
    }

    /**
     * 测试选择Api返回参数模版列表的情况(多层数组嵌套)
     */
    @Test
    public void testLiftFlowResultAllArray() {
        List<ApiRespParamDTO> apiRespParamDTOList = new ArrayList<>();

        ApiRespParamDTO body = new ApiRespParamDTO();
        body.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        body.setAssetColumns("wcsBody");
        body.setId(12439);
        body.setAssetDataType("string");
        body.setType(HttpParamKind.BODY);
        body.setNodeId("8f42c0e0-2e88-11ed-ae13-2f89604e0757");
        body.setNodeName("wcstest000");
        body.setResponsePostData("{\"root\":{\"type\":\"object\",\"name\":\"root\",\"properties\":{\"field_0.6831156818001083\":{\"ty" +
                "pe\":\"string\",\"name\":\"head\",\"paramValueType\":\"method\",\"expression\":\"fn.codec.md5({$.getimg.BODY" +
                ".body})\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0.3331421403810302\":{\"type\":\"" +
                "object\",\"name\":\"对象1\",\"properties\":{\"field_0.36467924655291717\":{\"type\":\"object\",\"name\":\"对象2" +
                "1\",\"properties\":{\"field_0.7422071749459862\":{\"type\":\"string\",\"name\":\"对象211\",\"paramValueType\":\"" +
                "reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a" +
                "95\"},\"field_0.5025646608950354\":{\"type\":\"object\",\"name\":\"对象212\",\"properties\":{\"field_0.265633446" +
                "5597081\":{\"type\":\"string\",\"name\":\"对象2121\",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.H" +
                "EAD.Set\\\\-Cookie\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"},\"field_0.5646706026909305\":{\"t" +
                "ype\":\"string\",\"name\":\"\",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\"" +
                ",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"}}}}},\"field_0.4078545386691603\":{\"type\":\"string\",\"" +
                "name\":\"对象22\",\"paramValueType\":\"reference\",\"expression\":\"$.getimg.HEAD.Set\\\\-Cookie\",\"operatorId\":" +
                "\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"}}},\"field_0.5571633116610879\":{\"type\":\"array\",\"name\":\"数组1\",\"i" +
                "tems\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.3794515769692739\":{\"type\":\"array\",\"n" +
                "ame\":\"数组2\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.676549629723141\":{\"type\"" +
                ":\"array\",\"name\":\"数组3\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.17324307823" +
                "31822\":{\"type\":\"array\",\"name\":\"数组4\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"f" +
                "ield_0.41782999870433724\":{\"type\":\"string\",\"name\":\"\",\"paramValueType\":\"reference\",\"expression\":\"$.get" +
                "img.HEAD.Set\\\\-Cookie\",\"operatorId\":\"be566bb0-35b9-11ed-bee5-3b89dd814a95\"}}}}}}}}}}}}}}}}");
        apiRespParamDTOList.add(body);


        List<ApiRespParamDTO> apiRespParamDTOList1 = ApiResponseHelper.buildApiResponseDTOList(apiRespParamDTOList);
        JSONArray jsonArray = JSONUtil.parseArray(apiRespParamDTOList1);
        System.out.println(jsonArray);
    }

}