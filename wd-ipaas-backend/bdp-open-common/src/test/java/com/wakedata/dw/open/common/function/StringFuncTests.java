/*
 *  Copyright (C) 2021 the original author or authors.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wakedata.dw.open.common.function;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.input.PathMapping;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * @author Francis Dong
 */
public class StringFuncTests {

    @Test
    public void contextLoads() {
    }


    @Test
    public void testCheck(){
        String func = "fn.string.substring(\"13075611505,1\",0, fn.common.iif(fn.math.lt(fn.string.indexOf(\"13075611505,1\",\",\"), 0),-2,fn.string.indexOf(\"13075611505,1\",\",\")))";
        Object result = FuncExecutor.getInstance().exec(null, func);
        System.out.println("result = " + result);
    }
    @Test
    public void testEquals() {
        String funcExpression = "fn.string.equals(\"abc\", \"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(true, result);
    }

    @Test
    public void testEquals2() {
        String funcExpression = "fn.string.equals(null, \"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(false, result);
    }

    @Test
    public void testEquals3() {
        String funcExpression = "fn.string.equals(\"ab\\\"c\", \"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(false, result);
    }

    @Test
    public void testEqualsIgnoreCase() {
        String funcExpression = "fn.string.equalsIgnoreCase(\"abc\", \"Abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(true, result);
    }

    @Test
    public void testcompare() {
        String funcExpression = "fn.string.compare(\"abc\", \"cde\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(-1, result);
    }

    @Test
    public void testConcat() {
        String funcExpression = "fn.string.concat(\"2021-07-09 22:44:55\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55", result.toString());
    }


    @Test
    public void testConcat2() {
        String funcExpression = "fn.string.concat(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55yyyy-MM-dd HH:mm:ss", result.toString());
    }

    @Test
    public void testConcat3() {
        String funcExpression = "fn.string.concat(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\",1)";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55yyyy-MM-dd HH:mm:ss1", result.toString());
    }

    @Test
    public void testConcatws() {
        String funcExpression = "fn.string.concatws(\",\" ,  \"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\"  )";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55,yyyy-MM-dd HH:mm:ss", result.toString());
    }

    @Test
    public void testSubstring() {
        String funcExpression = "fn.string.substring(\"2021-07-09 22:44:55\",  1  ,   4)";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55".substring(1, 4), result.toString());
    }

    @Test
    public void testSubstring2() {
        ONode ctxNode = ONode.load(new HashMap());

        Map<String, Object> m = new HashMap<>();
        m.put("a", "1");
        m.put("b", "1");

        PathMapping.setByPath(ctxNode, "data.dateStr", "2021-07-09 22:44:55", true);
        PathMapping.setByPath(ctxNode, "data.startIndex", 1, true);
        PathMapping.setByPath(ctxNode, "data", m, false);

        String funcExpression = "fn.string.substring({data.dateStr},  {data.startIndex})";
//		String funcExpression = "fn.string.substring(\"2021-07-09 22:44:55\",  1)";
        Object result = FuncExecutor.getInstance().exec(ctxNode, funcExpression);
        System.out.println(result);
        assertEquals("2021-07-09 22:44:55".substring(1), result.toString());
    }


    @Test
    public void testSubstring3(){
        ONode ctxNode = ONode.load(new HashMap());
        String originJsonStr = "{age:1.2,name:1,id:\"2\",\"HEAD\":{\"Server\":[\"Werkzeug/1.0.1 Python/3.6.6rc1\"],\"Content-Length\":[\"78\"],\"Date\":[\"Wed, 03 Nov 2021 13:16:46 GMT\"],\"Content-Type\":[\"text/html; charset=utf-8\"]},\"BODY\":{\"msg\":\"success\",\"code\":1000,\"data\":[{\"msg1\":1234567},{\"msg2\":\"bbb\"}]}}";
        JSONObject jsonObject = JSONObject.parseObject(originJsonStr);
        PathMapping.setByPath(ctxNode, IFunc.SEPARATOR_ASTERISK,jsonObject,false);
        String funcExpression = "fn.string.toUpperCase(fn.string.substring({$.BODY.data[1].msg2},  {$.name}))";
        Object result = FuncExecutor.getInstance().exec(ctxNode, funcExpression);
        System.out.println(result);
    }

    @Test
    public void testIndexOf() {
        String funcExpression = "fn.string.indexOf(\"2021-07-09 22:44:55\",  \"07\")";
        int result = (int) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55".indexOf("07"), result);
    }

    @Test
    public void testStartsWith() {
        String funcExpression = "fn.string.startsWith(\"2021-07-09 22:44:55\",  \"2021\")";
        boolean result = (boolean) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55".startsWith("2021"), result);
    }

    @Test
    public void testEndsWith() {
        String funcExpression = "fn.string.endsWith(\"2021-07-09 22:44:55\",  \"44:55\")";
        boolean result = (boolean) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55".endsWith("44:55"), result);
    }

    @Test
    public void testToUpperCase() {
        String funcExpression = "fn.string.toUpperCase(\"aBc\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("aBc".toUpperCase(), result);
    }

    @Test
    public void testToLowerCase() {
        String funcExpression = "fn.string.toLowerCase(\"aBc\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("aBc".toLowerCase(), result);
    }

    @Test
    public void testToString() {
        String funcExpression = "fn.string.toString(\"aBc\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("aBc", result);
    }

    @Test
    public void testToString2() {
        String funcExpression = "fn.string.toString(true)";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("true", result);
    }

    @Test
    public void testToString3() {
        String funcExpression = "fn.string.toString(234)";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("234", result);
    }


    @Test
    public void testToString4() {
        String funcExpression = "fn.string.toString(fn.date.timestamp())";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        System.out.println(result);
    }

    @Test
    public void testReplace() {
        String funcExpression = "fn.string.replace(\"2021-07-09 22:44:55\",  \"44:55\", \"00:00\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55".replace("44:55", "00:00"), result);
    }

    @Test
    public void testReplaceAll() {
        String funcExpression = "fn.string.replaceAll(\"2021-07-09 22:44:55 44:55\",  \"44:55\", \"00:00\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55 44:55".replaceAll("44:55", "00:00"), result);
    }

    @Test
    public void testReplaceAll2() {
        String funcExpression = "fn.string.replaceAll(\"1.2.3\",  \"\\.\", \"\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("1.2.3".replaceAll("\\.", ""), result);
    }

    @Test
    public void testReplaceFirst() {
        String funcExpression = "fn.string.replaceFirst(\"2021-07-09 22:44:55 44:55\",  \"44:55\", \"00:00\")";
        String result = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:55 44:55".replaceFirst("44:55", "00:00"), result);
    }

}