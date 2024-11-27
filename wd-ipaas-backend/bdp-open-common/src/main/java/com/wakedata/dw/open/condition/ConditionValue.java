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

package com.wakedata.dw.open.condition;

import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import lombok.Data;

/**
 * 判断条件
 *
 * @author luomeng
 */
@Data
public class ConditionValue {

	/**
	 * 固定值，引用值，函数
	 *
	 * @see ParamMappingTypeEnum
	 */
	private String type;

	/**
	 * 属性的数据类型,暂时只在响应对象里面需要配置（如object,string...）
	 */
	private String dataType;

	/**
	 * 表达式，按照type记录对应的数据类型
	 */
	private String expression;


	public ConditionValue() {
	}

	public ConditionValue(String type, String dataType, String expression) {
		this.type = type;
		this.dataType = dataType;
		this.expression = expression;
	}
}
