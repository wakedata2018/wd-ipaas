package com.wakedata.dw.open.swagger;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.wakedata.dw.open.swagger.utils.MapUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * Swagger 文档参数嵌套定义解决
 *
 * @author chenshaopeng
 * @date 2021/11/1
 */
public abstract class AbstractReferResolve implements SwaggerConstants{

    /**
     * 解决swagger文档数据中的参数相互引用导致的无法获取问题
     *
     * @param apiDocJson 源json串
     * @return 处理后的json对象
     */
    public LinkedTreeMap<String, Object> resolveObjectRefer(String apiDocJson) {
        return resolveObjectRefer(jsonToMapConvert(apiDocJson));
    }

    /**
     * 解决swagger文档数据中的参数相互引用导致的无法获取问题
     *
     * @param apiDocMap 源对象
     * @return 处理后的json对象
     */
    public LinkedTreeMap<String, Object> resolveObjectRefer(LinkedTreeMap<String, Object> apiDocMap) {
        defineReferResolve(apiDocMap, (LinkedTreeMap) apiDocMap.get(DEFINE_KEY));
        referResolve(apiDocMap, new LinkedTreeMap<>(), apiDocMap.get(PATHS_KEY));
        apiDocMap.remove(DEFINE_KEY);
        return apiDocMap;
    }

    /**
     * json字符串转换为Map对象
     *
     * 使用Gson格式化，避免FastJson格式化部分数据解析后格式错误
     */
    public static LinkedTreeMap<String, Object> jsonToMapConvert(String apiDocJson){
        try {
            return new Gson().fromJson(apiDocJson, LinkedTreeMap.class);
        }catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("json解析失败, error:{}", e);
        }
    }

    /**
     * 先把定义解析并赋值
     */
    protected void defineReferResolve(LinkedTreeMap<String, Object> sourceObject, LinkedTreeMap<String, Object> object) {
        // 记录所有在 definitions 中出现过的类
        object.put(APPEARED_NODE_KEY, new ArrayList<String>());
        do {
            referResolve(sourceObject, null, object);
        } while (MapUtils.containsKey(object, REFER_KEY));
    }

    /**
     * 嵌套引用解决
     */
    protected void referResolve(LinkedTreeMap<String, Object> sourceObject, Object forbears, Object object) {
        if (object instanceof JSONObject
                || object instanceof LinkedTreeMap) {
            // 最终都到这里
            objectParser(sourceObject, forbears, (Map) object);
        } else if (object instanceof List) {
            arrayParser(sourceObject, forbears, (List) object);
        }
    }

    /**
     * 返回API文档中接口正常的响应格式key
     *
     * @return 接口响应格式key
     */
    protected String getNormalResponsesKey() {
        return SwaggerConstants.NORMAL_RESPONSES_KEY;
    }

    /**
     * 数组递归解析
     */
    private void arrayParser(LinkedTreeMap<String, Object> sourceObject, Object forbears, List<Object> arrayObject) {
        for (Object jsonVal : arrayObject) {
            referResolve(sourceObject, forbears, jsonVal);
        }
    }

    /**
     * 对象解析
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void objectParser(LinkedTreeMap<String, Object> sourceObject, Object forbears, Map<String, Object> object) {
        // sourceObject 是全部，object 是 definitions

        removeInvalidKey(object);

        Set<Entry<String, Object>> entrySet = object.entrySet();
        Iterator<Entry<String, Object>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();

            Object father = forbears;
            if (forbears == null) {
                father = entry.getValue();

                List<String> list = new ArrayList<>();
                if (APPEARED_NODE_KEY.equals(entry.getKey())) {
                    return;
                }
                list.add(entry.getKey());
                ((LinkedTreeMap) father).put(FATHER_NODE_KEY, list);
                ((LinkedTreeMap) father).put(FATHER_NODE_NAME, entry.getKey());
            }
            if (referObjectCheck(sourceObject, object, father, iterator, entry)) {
                continue;
            }

            //获取当前父节点数量
            List<String> fatherList = (List<String>) ((LinkedTreeMap) father).get(FATHER_NODE_KEY);
            int preNodeNum = Objects.nonNull(fatherList) ? fatherList.size() : 0;

            referResolve(sourceObject, father, entry.getValue());
            //移除上个节点的子节点添加的引用
            if (Objects.nonNull(fatherList)) {
                int resolveAfterNodeNum = fatherList.size();
                if (resolveAfterNodeNum > preNodeNum) {
                    ((LinkedTreeMap) father)
                        .put(FATHER_NODE_KEY, Lists.newArrayList(fatherList.subList(0, preNodeNum)));
                }
            }

        }
    }

    /**
     * 引用对象检查
     */
    protected boolean referObjectCheck(LinkedTreeMap<String, Object> sourceObject
            , Map<String, Object> object, Object father, Iterator<Entry<String, Object>> iterator, Entry<String, Object> entry){
        if (entry.getKey().equals(REFER_KEY)) {
            // 记录 definitions 顶层子节点，用于判断是否出现 a->b->c->a 这种循环依赖的情况
            ((LinkedTreeMap<String, List<String>>) sourceObject.get(DEFINE_KEY)).get(APPEARED_NODE_KEY).add((String)((LinkedTreeMap) father).get(FATHER_NODE_NAME));
            return referObjectDispose(sourceObject, object, father, iterator, entry);
        }
        return false;
    }

    /**
     * 引用对象处理
     */
    protected abstract boolean referObjectDispose(LinkedTreeMap<String, Object> sourceObject
            , Map<String, Object> jsonObject, Object father, Iterator<Entry<String, Object>> iterator, Entry<String, Object> entry);

    /**
     * 根据路径获取引用对象的名称
     */
    protected String getReferObjectName(String fullPath){
        String[] ref = fullPath.split("/");
        return ref[ref.length-1];
    }

    /**
     * 获取引用对象的值
     */
    protected Object getReferValue(LinkedTreeMap<String, Object> sourceObject, String fullPath){
        Object json = sourceObject;
        String[] fullPathArray = fullPath.replaceFirst("#/","").split("/");
        for (String path : fullPathArray) {
            if(json == null){
                break;
            }
            if(json instanceof JSONObject){
                json = ((JSONObject) json).get(path);
            } else if (json instanceof LinkedTreeMap) {
                json = ((LinkedTreeMap) json).get(path);
            }else {
                break;
            }
        }
        List<String> appearedNode = ((LinkedTreeMap<String, List<String>>) sourceObject.get(DEFINE_KEY)).get(APPEARED_NODE_KEY);
        LinkedTreeMap<String, Object> objectValue = (LinkedTreeMap<String, Object>) sourceObject.get(DEFINE_KEY);
        MapUtils.pruning(json, REFER_KEY, appearedNode,objectValue);
        return json == null ? new HashMap<>() : json;
    }

    /**
     * 移除对象不需要解析的key
     */
    void removeInvalidKey(Map<String, Object> object){
        for (String invalidKey : INVALID_KEYS) {
            object.remove(invalidKey);
        }
    }

}
