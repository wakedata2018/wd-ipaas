package com.wakedata.dw.open.utils.jsqlparser.builder;


import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yiyufeng tanzhi
 * @title JSqlParserTableBuilder
 * @projectName bdp-open
 * @date
 * @description
 */
public class JSqlParserTableBuilder {

    private String table;

    private String alias;

    private boolean tableSubQuery;

    public JSqlParserTableBuilder table(String table) {
        this.table = table;
        return this;
    }

    public JSqlParserTableBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    public JSqlParserTableBuilder tableSubQuery(boolean tableSubQuery) {
        this.tableSubQuery = tableSubQuery;
        return this;
    }

    public Table build() {
        if (this.tableSubQuery) {
            this.table = "(" + this.table + ")";
        }
        Table table = new Table(this.table);
        if (!StringUtils.isEmpty(this.alias)) {
            table.setAlias(new Alias(this.alias));
        }
        return table;
    }

    public static JSqlParserTableBuilder builder() {
        return new JSqlParserTableBuilder();
    }
}
