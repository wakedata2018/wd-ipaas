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
package com.wakedata.dw.open.function;

/**
 * Function interface
 * 
 * @author Francis Dong
 *
 */
public interface IFunc {
	/**
	 * 函数固定前缀
	 */
	String NAME_SPACE_PREFIX = "fn.";

	/**
	 * 函数扫描包前缀
	 */
	String REFLECTIONS_PREFIX = "com.wakedata.dw.open.function";

	/**
	 * 执行方法
	 */
	String REFLECTIONS_METHOD = "getInstance";

	/**
	 * 竖线
	 */
	String SEPARATOR_VERTICAL_BAR = "|";

	/**
	 * 星号
	 */
	String SEPARATOR_ASTERISK = "*";

	/**
	 * 已$.开头
	 */
	String PRE_SPLIT = "$.";

	/**
	 * Init: Register functions to FuncExecutor in the initial stage <br>
	 * <br>
	 * Example: <br>
	 * FuncExecutor.register(NAME_SPACE_PREFIX + "date.timestamp", getInstance());<br>
	 * FuncExecutor.register(NAME_SPACE_PREFIX + "date.now", getInstance());<br>
	 * 
	 */
	void init();

}
