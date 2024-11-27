package com.wakedata.dw.open.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    //设置或略不存在的字段
    static {
/*		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
		//mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);
		//mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, false);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) ; 
		mapper.configure(Feature.AUTO_CLOSE_SOURCE, true) ; */
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * 自定义序列化
     *
     * @param module
     */
    public static void registerModules(Module module) {
        mapper.registerModule(module);
    }

    /**
     * 将对象转成json.
     *
     * @param obj 对象
     * @return
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {

            String str = mapper.writeValueAsString(obj);
            return str;
        } catch (JsonGenerationException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (JsonMappingException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (IOException e) {

            throw new JsonException(e.getMessage(), e);
        }
    }


    /**
     * json转List.
     *
     * @param <T>
     * @param content   json数据
     * @param valueType 泛型数据类型
     * @return
     */
    public static <T> List<T> toListObject(String content, Class<T> valueType) {
        if (content == null || content.length() == 0) {
            return null;
        }
        try {
            final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, valueType);
            return mapper.readValue(content, javaType);
        } catch (JsonParseException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (JsonMappingException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (IOException e) {

            throw new JsonException(e.getMessage(), e);
        }
    }

    /**
     * json转List.
     *
     * @param <T>       json数据
     * @param valueType 泛型数据类型
     * @return
     */
    public static <T> List<T> toListObject(InputStream in, Class<T> valueType) {
        if (in == null) {
            return null;
        }
        try {
            final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, valueType);
            return mapper.readValue(in, javaType);
        } catch (JsonParseException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (JsonMappingException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (IOException e) {

            throw new JsonException(e.getMessage(), e);
        }
    }

    public static <T> List<T> toListObjectFromUrl(URL source, Class<T> valueType) {
        if (source == null) {
            return null;
        }
        try {
            final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, valueType);

            return mapper.readValue(source, javaType);
        } catch (JsonParseException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (JsonMappingException e) {

            throw new JsonException(e.getMessage(), e);
        } catch (IOException e) {

            throw new JsonException(e.getMessage(), e);
        }
    }

    public static <T> List<T> toObject(List<String> jsonList, Class<T> valueType) {
        if (jsonList == null || jsonList.isEmpty()) {
            return null;
        }
        List<T> list = new ArrayList<T>();
        for (String json : jsonList) {
            list.add(JsonUtil.toObject(json, valueType));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String content) {
        return JsonUtil.toObject(content, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Set<Object> toSet(String content) {
        return JsonUtil.toObject(content, Set.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toMap(String json, Class<T> clazz) {
        return JsonUtil.toObject(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(String json, Class<T> clazz) {
        return JsonUtil.toObject(json, Set.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toNotNullMap(String json) {
        Map<String, Object> map = JsonUtil.toObject(json, Map.class);
        if (map == null) {
            map = new LinkedHashMap<String, Object>();
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toNotNullMap(String json, Class<T> clazz) {
        Map<String, T> map = JsonUtil.toObject(json, Map.class);
        if (map == null) {
            map = new LinkedHashMap<String, T>();
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> toNotNullSet(String json, Class<T> clazz) {
        Set<T> set = JsonUtil.toObject(json, Set.class);
        if (set == null) {
            set = new LinkedHashSet<T>();
        }
        return set;
    }

    /**
     * 类型转换.
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static <T> T convert(Object obj, Class<T> clazz) {
        String json = JsonUtil.toJson(obj);
        return toObject(json, clazz);
    }

    /**
     * 将Json转换成对象.
     *
     * @param json
     * @param valueType
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null || json.length() == 0) {
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            throw new JsonException(e.getMessage(), e);
        } catch (JsonMappingException e) {
            throw new JsonException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JsonException(e.getMessage(), e);
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void printMap(Map map, String name) {
        if (map == null) {
            return;
        }
        if (map.size() == 0) {
            return;
        }
        Iterator<Entry> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
        }

    }

    @SuppressWarnings({"rawtypes"})
    public static void printList(List list, String name) {
        if (list == null) {
            System.out.println("json info " + name + "::null");
            return;
        }
        if (list.size() == 0) {
            System.out.println("json info " + name + "::");
            return;
        }
        for (Object element : list) {
            System.out.println("json info " + name + "::" + JsonUtil.toJson(element));
        }

    }

    public static <T> T getParam(JSONObject json, String key, T defaultValue) {
        if (json == null) {
            return defaultValue;
        }
        if (!json.containsKey(key)) {
            return defaultValue;
        }
        return (T) json.get(key);
    }

    /**
     * 从JSONObject中获取分页参数，如果不存在则获取默认值
     *
     * @param json         JSONObject
     * @param key          key参数
     * @param defaultValue 默认值
     * @return 分页参数
     */
    public static Integer getPageParam(JSONObject json, String key, Integer defaultValue) {
        if (json == null) {
            return defaultValue;
        }
        if (!json.containsKey(key)) {
            return defaultValue;
        }
        return json.getInteger(key);
    }
}