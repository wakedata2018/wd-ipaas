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

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.enums.OperatorConditionEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * condition 判断类
 *
 * @author luomeng
 */
@Data
@Slf4j
public class Condition {

    /**
     * 定义条件id -唯一值，在一组条件中不能重复
     */
    private String id;

    /**
     * 描述
     */
    private String desc;

    /**
     * 比较值1
     */
    private ConditionValue value1;

    /**
     * 比较符
     * @see OperatorConditionEnum
     */
    private String operator;

    /**
     * 比较值2
     */
    private ConditionValue value2;

    public Condition() {
    }

    public Condition(String id, String desc, ConditionValue value1, String operator, ConditionValue value2) {
        this.id = id;
        this.desc = desc;
        this.value1 = value1;
        this.operator = operator;
        this.value2 = value2;
    }

    /**
     * 条件判断
     *
     * @param v1
     * @param v2
     * @return
     */
    public boolean conditionCompare(Object v1, Object v2) {
        OperatorConditionEnum conditionEnum = OperatorConditionEnum.getEnumByCode(operator);
        if(conditionEnum == null){
            return false;
        }
        boolean rs = false;
        switch (conditionEnum) {
            case EQ:
                if (v1 == null && v2 == null) {
                    rs = true;
                } else if (v1 != null && v2 != null) {
                    rs = this.compare(v1, v2) == 0;
                }
                break;
            case NE:
                if (v1 == null && v2 == null) {
                    rs = false;
                } else if ((v1 == null && v2 != null) || (v1 != null && v2 == null)) {
                    rs = true;
                } else if (v1 != null && v2 != null) {
                    rs = this.compare(v1, v2) != 0;
                }
                break;
            case GT:
                rs = this.compare(v1, v2) > 0;
                break;
            case GE:
                rs = this.compare(v1, v2) >= 0;
                break;
            case LT:
                rs = this.compare(v1, v2) < 0;
                break;
            case LE:
                rs = this.compare(v1, v2) <= 0;
                break;
            case CONTAINS:
                if (v1 == null) {
                    rs = false;
                    break;
                }
                if (v1 instanceof Collection && !(v2 instanceof Collection)) {
                    Collection coll1 = (Collection) v1;
                    if (v2 instanceof Integer || v2 instanceof Long) {
                        Long el = Long.valueOf(v2.toString());
                        rs = containsLong(coll1, el);
                    } else if (v2 instanceof Float || v2 instanceof Double) {
                        Double el = Double.valueOf(v2.toString());
                        rs = containsDouble(coll1, el);
                    } else {
                        rs = CollectionUtils.contains(coll1.iterator(), v2);
                    }
                } else if (!(v1 instanceof Collection)) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value1 must be a collection");
                } else if (v2 instanceof Collection) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value2 can not be a collection");
                }
                break;
            case NOTCONTAIN:
                if (v1 == null) {
                    rs = true;
                    break;
                }
                if (v1 instanceof Collection && !(v2 instanceof Collection)) {
                    Collection coll1 = (Collection) v1;
                    if (v2 instanceof Integer || v2 instanceof Long) {
                        Long el = Long.valueOf(v2.toString());
                        rs = !containsLong(coll1, el);
                    } else if (v2 instanceof Float || v2 instanceof Double) {
                        Double el = Double.valueOf(v2.toString());
                        rs = !containsDouble(coll1, el);
                    } else {
                        rs = !CollectionUtils.contains(coll1.iterator(), v2);
                    }
                } else if (!(v1 instanceof Collection)) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value1 must be a collection");
                } else if (v2 instanceof Collection) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value2 can not be a collection");
                }
                break;
            case CONTAINSANY:
                if (v1 == null || v2 == null) {
                    rs = false;
                    break;
                }
                if (v1 instanceof Collection && v2 instanceof Collection) {
                    Collection coll1 = (Collection) v1;
                    Collection coll2 = (Collection) v2;
                    rs = CollectionUtils.containsAny(coll1, coll2);
                } else if (!(v1 instanceof Collection)) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value1 must be a collection");
                } else if (!(v2 instanceof Collection)) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value2 must be a collection");
                }
                break;
            case ISNULL:
                rs = v1 == null;
                break;
            case ISNOTNULL:
                rs = v1 != null;
                break;
            case ISBLANK:
                rs = v1 == null || StringUtils.isBlank(v1.toString());
                break;
            case ISNOTBLANK:
                rs = v1 != null && StringUtils.isNotBlank(v1.toString());
                break;
            case ISEMPTY:
                rs = v1 == null || (v1 instanceof Collection && ((Collection) v1).isEmpty())
                        || (v1 instanceof Map && ((Map) v1).isEmpty())
                        || (v1 instanceof String && StringUtils.isEmpty(v1.toString()));
                break;
            case ISNOTEMPTY:
                if (v1 != null) {
                    if (v1 instanceof Collection) {
                        rs = !((Collection) v1).isEmpty();
                    } else if (v1 instanceof Map) {
                        rs = !((Map) v1).isEmpty();
                    } else if(v1 instanceof String){
                        rs = StringUtils.isNotEmpty(v1.toString());
                    }
                }
                break;
            default:
                break;
        }
        return rs;
    }

    @SuppressWarnings("rawtypes")
    private int compare(Object v1, Object v2) {
        if (v1 == null || v2 == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),"value1 and value2 can not be null");
        }
        if (v1 instanceof Boolean && v2 instanceof Boolean) {
            Boolean n1 = (Boolean) v1;
            Boolean n2 = (Boolean) v2;
            return n1.compareTo(n2);
        } else if ((v1 instanceof Integer || v1 instanceof Long || v1 instanceof Float || v1 instanceof Double)
                && (v2 instanceof Integer || v2 instanceof Long || v2 instanceof Float || v2 instanceof Double)) {
            // compare value if both are numbers
            Double n1 = Double.valueOf(v1.toString());
            Double n2 = Double.valueOf(v2.toString());
            return n1.compareTo(n2);
        } else if (v1 instanceof String && v2 instanceof String) {
            String s1 = v1.toString();
            String s2 = v2.toString();
            return s1.compareTo(s2);
        } else if (v1 instanceof String || v2 instanceof String) {
            //当两个类型不一致，且有一个为字符串时，都按字符串进行比较
            String s1 = v1.toString();
            String s2 = v2.toString();
            return s1.compareTo(s2);
        } else {
            throw new OpenException(OpenApiMsgCodeEnum.w_condition_exception_msg.getCode(),
                    "types of value1 and value2 are not consistent or not supported for comparision");
        }
    }

    /**
     * 基础数据类型转换
     * @param type
     * @param val
     * @return
     */
    public Object cast(DataTypeEnum type, Object val) {
        if (type != null && val != null) {
            switch (type) {
                case INTEGER:
                    val = Integer.valueOf(val.toString());
                    break;
                case NUMBER:
                    val = Double.valueOf(val.toString());
                    break;
                case BOOLEAN:
                    val = Boolean.valueOf(val.toString());
                    break;
                case STRING:
                    val = val.toString();
                    break;
                case ARRAY:
                    if(val instanceof String){
                        String str = (String)val;
                        //普通数组转换成集合
                        if(str.startsWith(DwOpenConstant.MIDDLE_BRACKET_LEFT)
                                && str.endsWith(DwOpenConstant.MIDDLE_BRACKET_RIGHT)){
                            if(!str.contains(DwOpenConstant.JSON_IDENTIFIER_LEFT)) {
                                val = Arrays.asList(str.substring(1, str.length() - 1).split(","));
                            }else{
                                val = JSONUtil.toBean(str,List.class);
                            }
                        }
                    }
                    if(val instanceof JSONArray){
                        val = JSONArray.parseArray(((JSONArray) val).toJSONString(), Object.class);
                    }
                    break;
                case OBJECT:
                    val = com.alibaba.fastjson.JSON.parseObject(val.toString());
                    break;
                default:
                    break;
            }
        }
        return val;
    }

    @SuppressWarnings("rawtypes")
    private boolean containsLong(Collection coll, Long el) {
        if (CollectionUtils.isEmpty(coll)) {
            return false;
        }

        for (Object obj : coll) {
            Long obj2 = null;
            if (obj instanceof Integer) {
                obj2 = Long.valueOf(obj.toString());
            }
            if (ObjectUtils.nullSafeEquals(obj2, el)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean containsDouble(Collection coll, Double el) {
        if (CollectionUtils.isEmpty(coll)) {
            return false;
        }

        for (Object obj : coll) {
            Double obj2 = null;
            if (obj instanceof Float) {
                obj2 = Double.valueOf(obj.toString());
            }
            if (ObjectUtils.nullSafeEquals(obj2, el)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将条件输出成字符串
     *
     * @return 条件字符串
     */
    public String parseConditionStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.value1.getExpression());
        OperatorConditionEnum conditionEnum = OperatorConditionEnum.getEnumByCode(this.operator);
        String operatorCondition = conditionEnum == null ? "" : conditionEnum.getDesc();
        sb.append(" ").append(operatorCondition);
        if (OperatorConditionEnum.ISNULL == conditionEnum || OperatorConditionEnum.ISNOTNULL == conditionEnum || OperatorConditionEnum.ISBLANK == conditionEnum
                || OperatorConditionEnum.ISNOTBLANK == conditionEnum || OperatorConditionEnum.ISEMPTY == conditionEnum || OperatorConditionEnum.ISNOTEMPTY == conditionEnum) {
            return sb.toString();
        }
        sb.append(" ").append(this.value2.getExpression());
        return sb.toString();
    }
}
