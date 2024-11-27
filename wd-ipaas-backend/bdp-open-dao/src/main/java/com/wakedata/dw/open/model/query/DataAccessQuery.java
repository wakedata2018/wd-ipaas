package com.wakedata.dw.open.model.query;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author yiyufeng
 * @title DataAccessQuery
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
public class DataAccessQuery {

    private Map<String, String> reqField;

    private Set<String> respField;
}
