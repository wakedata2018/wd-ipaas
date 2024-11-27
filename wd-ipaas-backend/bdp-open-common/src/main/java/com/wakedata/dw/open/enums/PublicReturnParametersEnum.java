package com.wakedata.dw.open.enums;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/8/23 12:16
 */
@Getter
@AllArgsConstructor
public enum PublicReturnParametersEnum {
    /**
     * 公共返回参数枚举
     */
    SUCCESS("success", "Boolean", "接口执行状态", DataTypeEnum.BOOLEAN.getType(), "true"),
    DATA("data", "Object", "返回的数据对象", DataTypeEnum.OBJECT.getType(), null),
    CODE("code", "Integer", "状态码", DataTypeEnum.INTEGER.getType(), "200"),
    MSG("msg", "String", "返回内容的描述", DataTypeEnum.STRING.getType(), "操作成功"),
    PAGE_SIZE("pageSize", "Integer", "分页参数，每页显示数据的条数", DataTypeEnum.INTEGER.getType(), "10"),
    PAGE_NO("pageNo", "Integer", "分页参数，当前页码", DataTypeEnum.INTEGER.getType(), "1"),
    TOTAL_COUNT("totalCount", "Long", "分页参数，返回数据总条数", DataTypeEnum.LONG.getType(), "100");


    /**
     * 属性名称
     */
    private final String attributeName;
    /**
     * 属性类型
     */
    private final String attributeType;
    /**
     * 错误描述
     */
    private final String attributeDesc;
    /**
     * 前端响应体控件类型
     */
    private final String frontType;
    /**
     * 示例值
     */
    private final String sample;

    /**
     * 将所有枚举字段封装成一个JSONObject
     */
    public static JSONObject buildPublicReturnParametersEnumJsonObject() {
        JSONObject jsonObject = new JSONObject();
        EnumSet<PublicReturnParametersEnum> publicReturnParametersEnums = EnumSet.allOf(PublicReturnParametersEnum.class);
        for (PublicReturnParametersEnum publicReturnParametersEnum : publicReturnParametersEnums) {
            jsonObject.put(publicReturnParametersEnum.getAttributeName(), null);
        }
        return jsonObject;
    }

    /**
     * 根据属性名称返回枚举
     */
    public static PublicReturnParametersEnum getEnumByAttributeName(String attributeName) {
        EnumSet<PublicReturnParametersEnum> publicReturnParametersEnums = EnumSet.allOf(PublicReturnParametersEnum.class);
        for (PublicReturnParametersEnum publicReturnParametersEnum : publicReturnParametersEnums) {
            if (publicReturnParametersEnum.getAttributeName().equals(attributeName)) {
                return publicReturnParametersEnum;
            }
        }
        return null;
    }

    /**
     * 删除分页参数（如果是JSONObject中的pageNo、pageSize、totalCount的值数据都为空，判定不需要分页参数，直接删掉这三个分页参数）
     * 如果其中一个不为空，则不做处理
     */
    public static void judgeRemovePageParam(JSONObject pageResultJson) {
        if (ObjectUtil.isEmpty(pageResultJson.get(PublicReturnParametersEnum.PAGE_NO.getAttributeName()))
                && ObjectUtil.isEmpty(pageResultJson.get(PublicReturnParametersEnum.PAGE_SIZE.getAttributeName()))
                && ObjectUtil.isEmpty(pageResultJson.get(PublicReturnParametersEnum.TOTAL_COUNT.getAttributeName()))) {
            pageResultJson.remove(PublicReturnParametersEnum.PAGE_NO.getAttributeName());
            pageResultJson.remove(PublicReturnParametersEnum.PAGE_SIZE.getAttributeName());
            pageResultJson.remove(PublicReturnParametersEnum.TOTAL_COUNT.getAttributeName());
        }
    }

}
