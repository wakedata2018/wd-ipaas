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

package com.wakedata.dw.open.enums;

/**
 * Operator condition条件枚举
 *
 * @author luomeng
 */
public enum OperatorConditionEnum {

	/**
	 * 比较符
	 */
    EQ("eq","等于",true),
	NE("ne","不等于",true),
	GT("gt","大于",true),
	GE("ge","大于或等于",true),
	LT("lt","小于",true),
	LE("le","小于或等于",true),
	CONTAINS("contains","包含",false),
	NOTCONTAIN("notContain","不包含",false),
	CONTAINSANY("containsAny","包含任何一个",false),
    ISNULL("isNull","为空",true),
	ISNOTNULL("isNotNull","不为空",true),
	ISBLANK("isBlank","is blank",false),
	ISNOTBLANK("isNotBlank","is not blank",false),
	ISEMPTY("isEmpty","is empty",true),
    ISNOTEMPTY("isNotEmpty","is not empty",true);

	/**
	 * 编码
	 */
    private String code;
	/**
	 * 说明
	 */
	private String desc;

	/**
	 * 是否启用
	 */
	private Boolean enabled;

    OperatorConditionEnum(String code, String desc,Boolean enabled) {
        this.code = code;
		this.desc = desc;
		this.enabled = enabled;
    }


	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * 获取枚举
	 * @param code
	 * @return
	 */
	public static OperatorConditionEnum getEnumByCode(String code) {
		for (OperatorConditionEnum conditionEnum : OperatorConditionEnum.values()) {
			if (conditionEnum.getCode().equals(code)) {
				return conditionEnum;
			}
		}
		return null;
	}
}
