package com.wakedata.dw.open.swagger;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Swagger v3.0文档参数嵌套定义解决
 *
 * @author chenshaopeng
 * @date 2021/10/25
 */
public class SwaggerV3ReferResolve extends AbstractReferResolve {

    public final static String VERSIONS = "3.0";

    /**
     * 嵌套引用字段检查
     */
    @Override
    protected boolean referObjectCheck(LinkedTreeMap<String, Object> sourceObject, Map<String, Object> jsonObject, Object father
            , Iterator<Map.Entry<String, Object>> iterator, Map.Entry<String, Object> entry){
        if (entry.getKey().equals(REFER_KEY) || entry.getKey().equals(REFER_V3_KEY)) {
            // 记录 definitions 顶层子节点，用于判断是否出现 a->b->c->a 这种循环依赖的情况
            ((LinkedTreeMap<String, List<String>>) sourceObject.get(DEFINE_KEY)).get(APPEARED_NODE_KEY).add((String)((LinkedTreeMap) father).get(FATHER_NODE_NAME));
            return referObjectDispose(sourceObject, jsonObject, father, iterator, entry);
        }
        return false;
    }

    /**
     * 嵌套引用对象值处理
     */
    @Override
    protected boolean referObjectDispose(LinkedTreeMap<String, Object> sourceObject, Map<String, Object> object, Object father
            , Iterator<Map.Entry<String, Object>> iterator, Map.Entry<String, Object> entry){
        String defineFullPath = String.valueOf(entry.getValue());
        String defineObjectName = super.getReferObjectName(defineFullPath);

        LinkedTreeMap<String, Object> fatherObject = (LinkedTreeMap) father;
        if(fatherObject.containsKey(FATHER_NODE_KEY)){
            List<String> nodeList = (List<String>) fatherObject.get(FATHER_NODE_KEY);
            if(nodeList.contains(defineObjectName)){
                // 解决无限迭代
                iterator.remove();
                return true;
            }
            nodeList.add(defineObjectName);
            fatherObject.put(FATHER_NODE_KEY, nodeList);
        }
        Object result = super.getReferValue(sourceObject, defineFullPath);
        iterator.remove();
        Map<String, Object> r = (Map<String, Object>) result;
        for (Map.Entry<String, Object> e : r.entrySet()) {
            object.put(e.getKey(), e.getValue());
        }
        return false;
    }

}
