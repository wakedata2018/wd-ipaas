package com.wakedata.dw.open.parammapping.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.input.PathMapping;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TestJson
 *
 * @author focus
 * @date 2021/11/3
 **/

public class JsonPathHelper {

    public static final String PRE_SPLIT = "$.";

    public static final String PRE_SPLIT_NO_COMMA = "$";

    public static final String START_SPLIT = "$.start.";

    public static final String START_NODE_NAME = "start";

    /**
     * 根节点的引用值表达式
     */
    public static final String ROOT_EXPRESSION = "$.start.BODY";

    /**
     * 使用json字符串作为表达式时表示参数的使用的类型
     *
     * @see ParamMappingTypeEnum
     */
    private static final String TYPE_KEY = "type";

    /**
     * 表达式为json
     */
    private static final String EXPRESSION_IS_JSON = "expressionIsJson";

    /**
     * 使用json字符串作为表达式时表示参数的取值（jsonPath表达式或固定值常量）
     */
    public static final String VALUE_KEY = "value";

    /**
     * 使用json字符串作为表达式时表示参数的算子来源id
     */
    public static final String OPERATOR_ID_KEY = "operatorId";

    /**
     * 使用json字符串作为表达式时表示参数的算子来源名称
     */
    public static final String OPERATOR_NAME_KEY = "operatorName";

    private Set<String> jsonPaths = new HashSet<>();

    /**
     * 获取最终的结果
     */
    private Object result;

    /**
     * 目标值对象
     */
    private JSON originJson;

    public JsonPathHelper(String expression, JSON originJson) {
        this.originJson = originJson;
        if (StringUtils.isBlank(expression)) {
            return;
        }
        // 判断表达式开头是否为$.
        if (isEval(expression)) {
            jsonPaths.add(expression);
            this.result = JSONPath.eval(originJson, expression);
            return;
        }
        // 判断表达式是否为json对象字符串
        if (JsonUtil.isJson(expression)) {
            JSONObject json = JSONObject.parseObject(expression);
            parseJson(json, null);
            this.result = json;
        }
        // 判断表达式是否为json数组字符串
        if (JsonUtil.isJsonArr(expression)) {
            JSONArray arr = JSONArray.parseArray(expression);
            parseJson(arr, null);
            this.result = arr;
        }
    }

    public JsonPathHelper(String expression, Map<String, JSON> multiInputMappings) {
        // 判断表达式是否为json对象字符串
        if (JsonUtil.isJson(expression)) {
            JSONObject json = JSONObject.parseObject(expression);
            parseJson(json, multiInputMappings);
            this.result = json;
        }
        // 判断表达式是否为json数组字符串
        if (JsonUtil.isJsonArr(expression)) {
            JSONArray arr = JSONArray.parseArray(expression);
            parseJson(arr, multiInputMappings);
            this.result = arr;
        }
    }

    public Object getResult() {
        return result;
    }

    public static boolean isEval(Object str) {
        return str.toString().trim().startsWith(PRE_SPLIT);
    }

    /**
     * 将json表达式对象转换为json请求参数
     *
     * @param json               json表达式对象
     * @param multiInputMappings 算子运行结果数据Map，只有表达式为JSON字符串时才会传入
     */
    private void parseJson(JSONObject json, Map<String, JSON> multiInputMappings) {
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            Object value = entry.getValue();
            // 如果value是数组对象
            if (JsonUtil.isJsonArr(value)) {
                parseJson((JSONArray) value, multiInputMappings);
                continue;
            }
            // 如果value是JSON对象
            if (JsonUtil.isJson(value)) {
                JSONObject jsonObject = json.getJSONObject(entry.getKey());
//                if (!jsonObject.containsKey(TYPE_KEY) && !jsonObject.containsKey(VALUE_KEY)) {
//                    parseJson(jsonObject, multiInputMappings);
//                }
                //前端不好传值，判断是否需要遍历改为判断value是不是对象
                if (jsonObject.containsKey(VALUE_KEY) && JsonUtil.isJson(jsonObject.get(VALUE_KEY))) {
                    parseJson(jsonObject.getJSONObject(VALUE_KEY), multiInputMappings);
                    entry.setValue(jsonObject.get(VALUE_KEY));
                    continue;
                }
                String type = jsonObject.getString(TYPE_KEY);
                if (type == null) {
                    entry.setValue(null);
                }
                if (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(type)) {
                    entry.setValue(jsonObject.getString(VALUE_KEY));
                }
                if (ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(type)) {
                    jsonPaths.add(jsonObject.getString(VALUE_KEY));
                    JSON originJson = multiInputMappings == null ? this.originJson : ParamMappingsUtils.multiInputMappingsConvert(multiInputMappings);
                    entry.setValue(JSONPath.eval(originJson, jsonObject.getString(VALUE_KEY)));
                }
                if (ParamMappingTypeEnum.FUNCTION_TYPE.getType().equals(type)) {
                    ONode ctxNode = ONode.load(new HashMap());
                    JSON originJson = multiInputMappings == null ? this.originJson : ParamMappingsUtils.multiInputMappingsConvert(multiInputMappings);
                    PathMapping.setByPath(ctxNode, IFunc.SEPARATOR_ASTERISK, originJson, false);
                    entry.setValue(FuncExecutor.getInstance().exec(ctxNode,jsonObject.getString(VALUE_KEY)));
                }
            }
        }
    }

    /**
     * 将json表达式数组转换为json请求参数
     *
     * @param json               json表达式数组
     * @param multiInputMappings 算子运行结果数据Map，只有表达式为JSON字符串时才会传入
     */
    private void parseJson(JSONArray json, Map<String, JSON> multiInputMappings) {
        for (int i = 0; i < json.size(); i++) {
            Object o = json.get(i);
            if (JsonUtil.isJsonArr(o)) {
                parseJson((JSONArray) o, multiInputMappings);
            }
            if (JsonUtil.isJson(o)) {
                parseJson((JSONObject) o, multiInputMappings);
            }

            if (isEval(o)) {
                jsonPaths.add(o.toString());
                json.set(i, JSONPath.eval(originJson, o.toString()));
            }
        }
    }

    public static void main(String[] args) {
        String json = "{\"array\":[{\"arrayObj1\":{\"type\":\"reference\",\"value\":\"$.BODY.data[0].msg1\"}}]}";
        String originJsonStr = "{age:1.2,name:1,id:\"2\",\"HEAD\":{\"Server\":[\"Werkzeug/1.0.1 Python/3.6.6rc1\"],\"Content-Length\":[\"78\"],\"Date\":[\"Wed, 03 Nov 2021 13:16:46 GMT\"],\"Content-Type\":[\"text/html; charset=utf-8\"]},\"BODY\":{\"msg\":\"success\",\"code\":1000,\"data\":[{\"msg1\":1234567},{\"msg2\":\"bbb\"}]}}";
        JSONObject jsonObject = JSONObject.parseObject(originJsonStr);
        JsonPathHelper parse = new JsonPathHelper(json, jsonObject);
        System.out.println(parse.getResult());
    }

}
