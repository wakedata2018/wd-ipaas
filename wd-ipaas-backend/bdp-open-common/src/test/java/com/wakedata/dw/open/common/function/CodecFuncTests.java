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

import com.wakedata.dw.open.function.FuncExecutor;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Francis Dong
 */
public class CodecFuncTests {
    @Test
    public void contextLoads() {
    }


    @Test
    public void testMd5() {
        String funcExpression = "fn.codec.md5(\"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("900150983cd24fb0d6963f7d28e17f72", result.toString());
    }

    @Test
    public void testMd5_2() {
        String funcExpression = "fn.codec.md5(fn.date.add(fn.date.add(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\", 1, fn.math.addExact(999,1)), \"yyyy-MM-dd HH:mm:ss\", fn.math.addExact(0,1), 1000))";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(DigestUtils.md5Hex("2021-07-09 22:44:57"), result.toString());
    }

    @Test
    public void testSha1() {
        String funcExpression = "fn.codec.sha1(\"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", result.toString());
    }

    @Test
    public void testSha256() {
        String funcExpression = "fn.codec.sha256(\"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", result.toString());
    }

    @Test
    public void testSha384() {
        String funcExpression = "fn.codec.sha384(\"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7", result.toString());
    }

    @Test
    public void testSha512() {
        String funcExpression = "fn.codec.sha512(\"abc\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f", result.toString());
    }

    @Test
    public void testBase64Encode() {
        String funcExpression = "fn.codec.base64Encode(\"Base64编码介绍\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("QmFzZTY057yW56CB5LuL57uN", result.toString());
    }

    // @Test
    public void testBase64Decode() {
        String funcExpression = "fn.codec.base64Decode(\"QmFzZTY057yW56CB5LuL57uN\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("Base64编码介绍", result.toString());
    }

    @Test
    public void testAesEncrypt() {
        String funcExpression = "fn.codec.aesEncrypt(\"abc\", \"1234567812345678\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("ucSL5R/jQigQ1dxzsWi2kg==", result.toString());
    }

    @Test
    public void testAesDecrypt() {
        String funcExpression = "fn.codec.aesDecrypt(\"ucSL5R/jQigQ1dxzsWi2kg==\", \"1234567812345678\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("abc", result.toString());
    }

    @Test
    public void testDesEncrypt() {
        String funcExpression = "fn.codec.desEncrypt(\"abc\", \"12345678123456781234567812345678\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("9YR6ZPdZufM=", result.toString());
    }

    @Test
    public void testDesDecrypt() {
        String funcExpression = "fn.codec.desDecrypt(\"9YR6ZPdZufM=\", \"12345678123456781234567812345678\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("abc", result.toString());
    }

}