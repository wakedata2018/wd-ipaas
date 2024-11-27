package com.wakedata.dw.open.swagger.utils;


import cn.hutool.core.text.CharSequenceUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Map工具类
 *
 * @author chenshaopeng
 * @date 2021/11/9
 */
public class MapUtils {


    /**
     * 判断对象及其子元素是否包含“key”
     */
    public static boolean containsKey(Object object, Object key){
        if(object instanceof Map){
            return contains((Map<?, ?>) object, key);
        } else if (object instanceof List) {
            for(Object o : (List<?>) object){
                if(containsKey(o, key)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean contains(Map<?, ?> map, Object key){
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (Objects.equals(entry.getKey(), key)
                    || containsKey(entry.getValue(), key)){
                return true;
            }
        }
        return false;
    }

    public static void pruning(Object object, Object key, List<String> appearedNode, LinkedTreeMap<String, Object> objectValue) {
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                // appearedNode 记录了整个 definitions 最顶层出现的节点
                // 假设1.类A引用类B，2.类B引用类C，3.类C引用类A
                // 但递归到类A中的引用时，entry.getValue为类B，此时 appearedNode 中仅有类A，可放行
                // 但递归到类B中的引用时，entry.getValue为类C，此时 appearedNode 中仅有类A和类B，可放行
                // 但递归到类C中的引用时，entry.getValue为类A，此时 appearedNode 中仅有类A和类B和类C，不可放行
                if (Objects.equals(entry.getKey(), key)) {
                    String value = (String) entry.getValue();
                    if (CharSequenceUtil.isNotBlank(value)) {
                        String tmp = value.substring(value.lastIndexOf("/") + 1);
                        if (appearedNode.contains(tmp)) {
                            // 去掉嵌套依赖
                            entry.setValue(null);
                            // 设置为 object 类型
                            ((Map<String, Object>) map).put("type", "object");
//                            ((Map<String, Object>) map).put("properties", ((LinkedTreeMap<String, Object>)objectValue.get(tmp)).get("properties"));
                        }
                    }
                    return;
                } else {
                    pruning(entry.getValue(), key, appearedNode, objectValue);
                }
            }
        }
    }

}
