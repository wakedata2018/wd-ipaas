package com.wakedata.dw.open.swagger;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.internal.LinkedTreeMap;
import com.wakedata.dw.open.swagger.utils.MapUtils;
import com.wakedata.dw.open.swagger.utils.SwaggerDataUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OpenApi v3文档参数嵌套定义解决
 *
 * @author wujunqiang
 * @since 2023/3/3 10:25
 */
public class OpenApiV3ReferResolve extends AbstractReferResolve {

    public final static String VERSIONS_301 = "3.0.1";

    public final static String VERSIONS_310 = "3.1.0";

    /**
     * 解决swagger文档数据中的参数相互引用导致的无法获取问题
     *
     * @param apiDocMap 源对象
     * @return 处理后的json对象
     */
    @Override
    public LinkedTreeMap<String, Object> resolveObjectRefer(LinkedTreeMap<String, Object> apiDocMap) {
        defineReferResolve(apiDocMap, (LinkedTreeMap<String, Object>) SwaggerDataUtils.getObject(apiDocMap, COMPONENTS_SCHEMAS_KEY));
        referResolve(apiDocMap, new LinkedTreeMap<>(), apiDocMap.get(PATHS_KEY));
        apiDocMap.remove(COMPONENTS_KEY);
        return apiDocMap;
    }


    /**
     * 嵌套引用字段检查
     */
    @Override
    protected boolean referObjectCheck(LinkedTreeMap<String, Object> sourceObject, Map<String, Object> jsonObject, Object father
            , Iterator<Map.Entry<String, Object>> iterator, Map.Entry<String, Object> entry) {
        if (entry.getKey().equals(REFER_KEY) || entry.getKey().equals(REFER_V3_KEY)) {
            // 记录 components.schemas 顶层子节点，用于判断是否出现 a->b->c->a 这种循环依赖的情况
            ((LinkedTreeMap<String, List<String>>) SwaggerDataUtils.getObject(sourceObject, COMPONENTS_SCHEMAS_KEY)).get(APPEARED_NODE_KEY).add((String) ((LinkedTreeMap) father).get(FATHER_NODE_NAME));
            return referObjectDispose(sourceObject, jsonObject, father, iterator, entry);
        }
        return false;
    }

    /**
     * 嵌套引用对象值处理
     */
    @Override
    protected boolean referObjectDispose(LinkedTreeMap<String, Object> sourceObject, Map<String, Object> object, Object father
            , Iterator<Map.Entry<String, Object>> iterator, Map.Entry<String, Object> entry) {
        String defineFullPath = String.valueOf(entry.getValue());
        String defineObjectName = super.getReferObjectName(defineFullPath);

        LinkedTreeMap<String, Object> fatherObject = (LinkedTreeMap) father;
        if (fatherObject.containsKey(FATHER_NODE_KEY)) {
            List<String> nodeList = (List<String>) fatherObject.get(FATHER_NODE_KEY);
            // 这里解决的是a嵌套a或者a嵌套list<a>的情况
            if (nodeList.contains(defineObjectName)) {
                // 解决无限迭代
                iterator.remove();
                return true;
            }
            nodeList.add(defineObjectName);
            fatherObject.put(FATHER_NODE_KEY, nodeList);
        }
        Object result = this.getReferValue(sourceObject, defineFullPath);
        iterator.remove();
        Map<String, Object> r = (Map<String, Object>) result;
        for (Map.Entry<String, Object> e : r.entrySet()) {
            object.put(e.getKey(), e.getValue());
        }
        return false;
    }

    /**
     * 获取引用对象的值
     */
    protected Object getReferValue(LinkedTreeMap<String, Object> sourceObject, String fullPath) {
        Object json = sourceObject;
        String[] fullPathArray = fullPath.replaceFirst("#/", "").split("/");
        for (String path : fullPathArray) {
            if (json == null) {
                break;
            }
            if (json instanceof JSONObject) {
                json = ((JSONObject) json).get(path);
            } else if (json instanceof LinkedTreeMap) {
                json = ((LinkedTreeMap) json).get(path);
            } else {
                break;
            }
        }
        List<String> appearedNode = ((LinkedTreeMap<String, List<String>>) SwaggerDataUtils.getObject(sourceObject, COMPONENTS_SCHEMAS_KEY)).get(APPEARED_NODE_KEY);
        LinkedTreeMap<String, Object> objectValue = (LinkedTreeMap<String, Object>) SwaggerDataUtils.getObject(sourceObject, COMPONENTS_SCHEMAS_KEY);
        MapUtils.pruning(json, REFER_KEY, appearedNode, objectValue);
        return json == null ? new HashMap<>(16) : json;
    }

    /**
     * 返回API文档中接口正常的响应格式key
     *
     * @return 接口响应格式key
     */
    @Override
    protected String getNormalResponsesKey() {
        return SwaggerConstants.OPEN_API_NORMAL_RESPONSES_KEY;
    }

}
