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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Francis Dong
 */
public class DateFuncTests {
    @Test
    public void contextLoads() {
    }

    @Test
    public void testTimestamp() {
        String funcExpression = "fn.date.timestamp()";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        System.out.println(result);
    }

    @Test
    public void testNow() {
        String funcExpression = "fn.date.now(null)";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        System.out.println(result);
    }

//	@Test
//	public void testNow2() {
//		String funcExpression = "fn.date.now()";
//		Object result = FuncExecutor.getInstance().exec(null, funcExpression);
//		System.out.println(result);
//	}

    @Test
    public void testGetTime() {
        String funcExpression = "fn.date.getTime(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\")";
        Long result = (Long) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals(1625841895000l, result.longValue());
    }

    @Test
    public void testAdd() {
        String funcExpression = "fn.date.add(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\", 1, 1000)";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:56", result.toString());
    }

    @Test
    public void testEmbeddedAdd() {
        String funcExpression = "fn.date.add(fn.date.add(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\", 1, 1000), \"yyyy-MM-dd HH:mm:ss\", 1, 1000)";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-07-09 22:44:57", result.toString());
    }


    @Test
    public void testFormatTs() {
        String funcExpression = "fn.date.formatTs(1628825352227, \"yyyy-MM-dd HH:mm:ss\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("2021-08-13 11:29:12", result.toString());
    }

    @Test
    public void testChangePattern() {
        String funcExpression = "fn.date.changePattern(\"2021-07-09 22:44:55\", \"yyyy-MM-dd HH:mm:ss\", \"MM-dd HH:mm\")";
        Object result = FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("07-09 22:44", result.toString());
    }

}