package com.wakedata.dw.open.model.domain;

import com.wakedata.dw.open.enums.DataAssetEnums;
import lombok.Data;

import java.util.Date;

/**
 * @author tanzhi
 * @title ApiFavoriteDo
 * @date 2019/11/29 10:14
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class ApiCollectDo {


    private Integer dataAssetApiId;
    private Integer accessAppId;

    private String apiName;

    private String apiDescription;

    private DataAssetEnums.UpdateFrequency updateFrequency;

    private DataAssetEnums.ResContentType responseContentType;

    private Date updateTime;

    private Integer collected;

    private Integer approval;

    private String dataAssetApiUrl;

    private Integer apiGroupId;

    private String groupName;

    private Integer created;

    private Integer apiType;

}
