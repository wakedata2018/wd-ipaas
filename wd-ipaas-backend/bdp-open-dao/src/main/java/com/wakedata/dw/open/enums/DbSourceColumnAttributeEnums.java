package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2023/1/3 14:52
 */
public class DbSourceColumnAttributeEnums {


    /**
     * 数据库表字段属性
     */
    @Getter
    public enum DbSourceColumnAttributeEnum implements BaseDbEnum<String> {

        /**
         * 数据库名
         */
        TABLE_SCHEMA("TABLE_SCHEMA", "数据库名"),
        /**
         * 数据表名
         */
        TABLE_NAME("TABLE_NAME", "数据表名"),
        /**
         * 字段名
         */
        COLUMN_NAME("COLUMN_NAME", "字段名"),
        /**
         * 字段默认值
         */
        COLUMN_DEFAULT("TABLE_NAME", "字段默认值"),
        /**
         * 是否可以为空
         */
        IS_NULLABLE("IS_NULLABLE", "是否可以为空"),
        /**
         * 数据类型
         */
        DATA_TYPE("DATA_TYPE", "数据类型"),
        /**
         * 列类型
         */
        COLUMN_TYPE("COLUMN_TYPE", "列类型"),
        /**
         * 列描述
         */
        COLUMN_COMMENT("COLUMN_COMMENT", "列描述"),
        /**
         * 键类型（是否主键“PRI”）
         */
        COLUMN_KEY("COLUMN_KEY", "键类型"),
        /**
         * 是否自增
         */
        EXTRA("EXTRA", "是否自增");

        private final String value;
        private final String description;

        DbSourceColumnAttributeEnum(String value, String description) {
            this.value = value;
            this.description = description;
        }

    }


    /**
     * 数据库表字段属性(暂定这么多，有需要往下增加)
     */
    @Getter
    public enum DbSourceColumnTypeEnum implements BaseDbEnum<String> {

        /**
         * bigint
         */
        BIGINT("bigint"),
        /**
         * int
         */
        INT("int"),
        /**
         * varchar
         */
        VARCHAR("varchar"),
        /**
         * tinyint
         */
        TINYINT("tinyint"),
        /**
         * datetime
         */
        DATETIME("datetime"),
        /**
         * text
         */
        TEXT("text");

        private final String value;

        DbSourceColumnTypeEnum(String value) {
            this.value = value;
        }
    }

}
