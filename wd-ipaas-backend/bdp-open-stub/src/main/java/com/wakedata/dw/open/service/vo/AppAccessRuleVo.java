package com.wakedata.dw.open.service.vo;

import com.google.common.collect.Maps;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
/**
 * @author yiyufeng
 * @title DataAccessRuleVo
 * @projectName bdp-open
 * @date
 * @description
 */
public class AppAccessRuleVo {

    private Integer apiId;
    private List<AppAccessRuleDetailVo> dataAccessRuleDetailList;

    private Map<String, AppAccessRuleVo> operatorAppAccessRules = Maps.newLinkedHashMap();

    @Data
    public static class AppAccessRuleDetailVo extends DatasourceTableColumnDo {
        private Boolean authorize;
    }
}
