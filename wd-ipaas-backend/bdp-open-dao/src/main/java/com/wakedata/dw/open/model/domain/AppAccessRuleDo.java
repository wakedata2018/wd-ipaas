package com.wakedata.dw.open.model.domain;

import com.wakedata.dw.open.model.api.AppAccessRulePo;
import lombok.Data;

import java.util.List;

/**
 * @author tanzhi
 * @title DataAccessRuleDo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
public class AppAccessRuleDo extends AppAccessRulePo {

    private List<String> dataAccessRuleFieldList;
}
