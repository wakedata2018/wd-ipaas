package com.wakedata.dw.open.swagger;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Swagger v2.0文档参数嵌套定义解决
 *
 * @author chenshaopeng
 * @date 2021/10/25
 */
public class SwaggerV2ReferResolve extends AbstractReferResolve {

    public final static String VERSIONS = "2.0";


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
            // 这里解决的是a嵌套a或者a嵌套list<a>的情况
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
        if (result != null) {
            Map<String, Object> r = (Map<String, Object>) result;
            for (Map.Entry<String, Object> e : r.entrySet()) {
                object.put(e.getKey(), e.getValue());
            }
        }
        return false;
    }


}
