package com.wakedata.dw.open.model.xxljob;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlParamInfo
 * @date 2022/10/26 18:29
 */
@Getter
@Setter
@ToString
public class XxlParamInfo {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 列名称
     */
    private String assetColumns;

    /**
     * 列数据类型[如string]
     */
    private String assetDataType;

    /**
     * 响应类型[HEAD BODY]
     *
     * @see HttpParamKind
     */
    private HttpParamKind type;

    /**
     * 子返回参数
     */
    List<XxlParamInfo> childApiRespParams;

    /**
     * 响应业务类型，0：普通http响应模板，1：流程编排结果响应模板
     */
    private DataAssetEnums.ApiResponseBusinessType businessType;

    /**
     * 返回字段类型:string,integer,boolean,array,object,number
     */
    private String resultColumnType;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 响应模版字符串：jsonSchema/xml
     */
    private String responsePostData;

}
