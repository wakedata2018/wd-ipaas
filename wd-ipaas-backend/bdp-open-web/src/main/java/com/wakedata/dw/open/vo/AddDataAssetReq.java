package com.wakedata.dw.open.vo;

import com.wakedata.dw.open.enums.DataAssetEnums;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author tanzhi
 * @title AddDataAssetReq
 * @date 2019/12/10 15:10
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class AddDataAssetReq {

    private Integer dataAssetApiId;

    @NotNull(message = "dataAssetApiMethod不能为空")
    @Length(max = 255)
    private String dataAssetApiMethod;

    @NotNull(message = "apiName不能为空")
    @Length(max = 32,message = "apiName最大长度32")
    private String apiName;

    @NotNull(message = "updateFrequency不能为空")
    private DataAssetEnums.UpdateFrequency updateFrequency;

    @NotNull(message = "protocol不能为空")
    private DataAssetEnums.ReqProtocol protocol;

    @NotNull(message = "secret不能为空")
    private DataAssetEnums.PublicEnums secret;

    @NotNull(message = "apiGroupId不能为空")
    private Integer apiGroupId;

//    @NotNull(message = "apiDescription不能为空")
    @Length(max = 1024,message = "apiDescription最大长度1024")
    private String apiDescription;

    @NotNull(message = "responseContentType不能为空")
    private DataAssetEnums.ResContentType responseContentType;


    @NotNull(message = "reqMethod不能为空")
    private DataAssetEnums.ReqMethod reqMethod;

    @NotNull(message = "apiType不能为空")
    private DataAssetEnums.DataApiType apiType;

    @Length(max = 2000,message = "sql的最大长度为2000")
    private String apiSql;

}
