package com.wakedata.dw.open.model.api.flow.operator.sql;

import com.wakedata.dw.open.enums.DataAssetEnums;
import lombok.Data;

import java.util.HashSet;

/**
 * sql算子内部对象
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/2/16 15:41
 */
@Data
public class SqlOperatorParam {

    /**
     * sql算子的SQL语句
     */
    private String sql;

    /**
     * 数据源id
     */
    private Integer dataSourceId;

    /**
     * 返回值格式（sql算子不需要考虑返回值格式，写死是json格式）
     */
    private DataAssetEnums.ResContentType responseContentType;

    /**
     * api有权限返回的列
     */
    protected HashSet<String> accessRuleFields;
}
