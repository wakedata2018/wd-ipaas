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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Common Functions
 * 
 * @author Francis Dong
 *
 */
public class CommonFunc implements IFunc {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonFunc.class);

	private static CommonFunc singleton;

	public static CommonFunc getInstance() {
		if (singleton == null) {
			synchronized (CommonFunc.class) {
				if (singleton == null) {
					CommonFunc instance = new CommonFunc();
					instance.init();
					singleton = instance;
				}
			}
		}
		return singleton;
	}

	private CommonFunc() {
	}

	@Override
	public void init() {
        FunctionEnumSet.CommonEnum[] commonEnums = FunctionEnumSet.CommonEnum.values();
        Arrays.stream(commonEnums).forEach(x-> FuncExecutor.register(x.method(), this));
	}

	/**
	 * Immediate if function (iif)
	 * 
	 * @param exprResult the result of expression that is to be evaluated.
	 * @param truepart   defines what the iif function returns if the exprResult is
	 *                   true.
	 * @param falsepart  defines what the iif function returns if the exprResult is
	 *                   false.
	 * @return returns the truepart or falsepart based on the value of the
	 *         exprResult
	 */
	public Object iif(boolean exprResult, Object truepart, Object falsepart) {
		return exprResult ? truepart : falsepart;
	}

	/**
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public boolean equals(Object obj1, Object obj2) {
		if (obj1 == null) {
			if (obj2 == null) {
				return true;
			}
			return false;
		}
		return obj1.equals(obj2);
	}

	public boolean isNull(Object obj) {
		return null == obj;
	}

	public boolean isNotNull(Object obj) {
		return null != obj;
	}

	public boolean isBlank(String obj) {
		return StringUtils.isBlank(obj);
	}

	public boolean isNotBlank(String obj) {
		return StringUtils.isNotBlank(obj);
	}

	@SuppressWarnings("rawtypes")
	public boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		} else if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		} else if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		return false;
	}

	public boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

}
