package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanzhi
 * @title DataAccessRulePo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
@Table(name = "DW_OPEN_APP_ACCESS_RULE")
@Slf4j
public class AppAccessRulePo extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ACCESS_RULE_ID")
    private Integer dataAccessRuleId;

    @Column(name = "DATA_ASSET_API_ID")
    private Integer dataAssetApiId;

    @Column(name = "DATA_ACCESS_APP_ID")
    private Integer dataAccessAppId;

    @Column(name = "DATA_ASSET_FIELD_NAME")
    private String dataAssetFieldName;

    public static AppAccessRuleDo toDataAccessRuleDo(List<AppAccessRulePo> accessRules) {
        if (CollectionUtils.isEmpty(accessRules)) {
            log.error("that api no result columns!");
            throw new OpenException(MsgCodeEnum.w_no_apply_column_or_already_have_permit);
        }
        AppAccessRulePo dataAccessRule = accessRules.iterator().next();
        AppAccessRuleDo appAccessRuleDo = new AppAccessRuleDo();
        BeanUtils.copyProperties(dataAccessRule, appAccessRuleDo);
        List<String> accessRuleFields = new ArrayList<>(accessRules.size());
        for (AppAccessRulePo accessRule : accessRules) {
            accessRuleFields.add(accessRule.getDataAssetFieldName());
        }
        appAccessRuleDo.setDataAccessRuleFieldList(accessRuleFields);
        return appAccessRuleDo;
    }
}