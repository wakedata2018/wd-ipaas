package com.wakedata.dw.open.liteflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.Data;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * http://jsonpath.herokuapp.com/?path=$.store.book%5B
 *
 * @author ZhangXueJun
 * @title JSONPathUtils
 * @date 2021/3/29 15:45
 * @projectName bdp-open
 * @description
 */
public class JSONPathTests {

    @After
    public void after() {
    }

    @Test
    public void testJsonPath1() {
        Entity entity = new Entity(123, new Object());

        Assert.assertSame(entity.getValue(), JSONPath.eval(entity, "$.value !=1"));
        Assert.assertTrue(JSONPath.contains(entity, "$.value"));
        Assert.assertTrue(JSONPath.containsValue(entity, "$.id", 123));
        Assert.assertTrue(JSONPath.containsValue(entity, "$.value", entity.getValue()));
        assertEquals(2, JSONPath.size(entity, "$"));
        assertEquals(0, JSONPath.size(new Object[]{}, "$"));

        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));

        /**
         * 读取集合多个元素的某个属性
         */
        List<String> names = (List<String>) JSONPath.eval(entities, "$.name"); // 返回enties的所有名称
        Assert.assertSame(entities.get(0).getName(), names.get(0));
        Assert.assertSame(entities.get(1).getName(), names.get(1));
    }

    @Test
    public void testJsonPath2() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));
        entities.add(new Entity("Yako"));

        /**
         * 返回集合中多个元素
         */
        List<Entity> result = (List<Entity>) JSONPath.eval(entities, "[1,2]"); // 返回下标为1和2的元素
        assertEquals(2, result.size());
        Assert.assertSame(entities.get(1), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));

        /**
         * 按范围返回集合的子集
         */
        result = (List<Entity>) JSONPath.eval(entities, "[0:2]"); // 返回下标从0到2的元素
        assertEquals(3, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(1), result.get(1));
        Assert.assertSame(entities.get(2), result.get(1));
    }

    /**
     * 通过条件过滤，返回集合的子集
     */
    @Test
    public void testJsonPath3() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity(1001, "ljw2083"));
        entities.add(new Entity(1002, "wenshao"));
        entities.add(new Entity(1003, "yakolee"));
        entities.add(new Entity(1004, null));

        List<Object> result = (List<Object>) JSONPath.eval(entities, "[id in (1001)]");
        assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    /**
     * 根据属性值过滤条件判断是否返回对象，修改对象，数组属性添加元素
     */
    @Test
    public void testJsonPath4() {
        Entity entity = new Entity(1001, "ljw2083");
        Assert.assertSame(entity, JSONPath.eval(entity, "[id = 1001]"));
        Assert.assertNull(JSONPath.eval(entity, "[id = 1002]"));

        JSONPath.set(entity, "id", 123456); //将id字段修改为123456
        assertEquals(123456, entity.getId().intValue());

        JSONPath.set(entity, "value", new int[0]); //将value字段赋值为长度为0的数组
        JSONPath.arrayAdd(entity, "value", 1, 2, 3); //将value字段的数组添加元素1,2,3
    }


    @Test
    public void testJsonPath5() {
        Map root = Collections.singletonMap("company", //
                Collections.singletonMap("departs", //
                        Arrays.asList( //
                                Collections.singletonMap("id",
                                        1001), //
                                Collections.singletonMap("id",
                                        1002), //
                                Collections.singletonMap("id", 1003) //
                        ) //
                ));

        List<Object> ids = (List<Object>) JSONPath.eval(root, "$..id");
        assertEquals(3, ids.size());
        assertEquals(1001, ids.get(0));
        assertEquals(1002, ids.get(1));
        assertEquals(1003, ids.get(2));
    }

    @Test
    public void testParse() {
        Object o = null;
        try {
            o = JSONArray.parse("txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        o = JSONObject.parse("txt");
        System.out.println(o);
    }


    @Test
    public void test01() {
        String json = "{\n" +
                "\t\"a\":[\n" +
                "\t\t{\"a\": \"a\",\"b\": \"b\"},\n" +
                "\t\t{\"a\": \"a\", \"b\": \"b1\"}\n" +
                "\t],\n" +
                "\t\"b\":[\n" +
                "\t\t{\"oo\": \"001\", \"tt\": \"b\"},\n" +
                "\t\t{\"oo\": \"002\", \"tt\": \"b1\"}\n" +
                "\t],\n" +
                "\t\"curent\":[\n" +
                "\t\t{\"x\": \"a\", \"oo\": \"001\"},\n" +
                "\t\t{\"x\": \"a\", \"oo\": \"002\"}\n" +
                "\t]\n" +
                "}";

        Object o = JSONPath.eval(JSON.parse(json), "$.a[b='b1']");
        System.out.println(o);
        o = JSONPath.eval(JSON.parse(json), "$.a[?(b='b1' || b='b')]");
        System.out.println(o);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", 111);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("ID", 222);
        jsonArray.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("ID", 333);
        jsonArray.add(jsonObject);

        o = JSONPath.eval(jsonArray, "[?(ID < 258 || ID > 200)]");
        System.out.println(o);

//
//        o = JSONPath.eval(json, "$.[a([b='b1'])]");
//        System.out.println(o);
    }


    /**
     * @author ZhangXueJun
     * @title Entity
     * @date 2021/3/29 14:22
     * @projectName bdp-open
     * @description
     */
    @Data
    public static class Entity {

        private Integer id;
        private String name;
        private Object value;

        public Entity() {
        }

        public Entity(Integer id, Object value) {
            this.id = id;
            this.value = value;
        }

        public Entity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Entity(String name) {
            this.name = name;
        }
    }
}
