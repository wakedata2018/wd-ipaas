package com.wakedata.dw.open.datasource.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author yiyufeng
 * @title DatasourceTypeEnum
 * @projectName bdp-open
 * @date
 * @description
 */
@Getter
public enum DatasourceTypeEnum implements BaseDbEnum<Integer> {

    /**
     * mysql数据源类型
     */
    MYSQL(1, "MYSQL"),
    ORACLE(2, "ORACLE"),
    HBASE(3, "HBASE"),
    HIVE(4, "HIVE"),
    POSTGRESQL(5, "POSTGRESQL"),
    ELASTICSEARCH(6, "ELASTICSEARCH"),
    H2(7, "H2"),
    KYLIN(8, "KYLIN"),
    SQLSERVER(9,"SQLSERVER"),
    PHOENIX(10,"PHOENIX"),

    /**
     * dremio 数据源
     */
    DREMIO(11,"DREMIO"),

    /**
     * HTTP 数据源
     */
    HTTP(12, "HTTP")
    ;

    private Integer value;
    private String description;

    DatasourceTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }


}
