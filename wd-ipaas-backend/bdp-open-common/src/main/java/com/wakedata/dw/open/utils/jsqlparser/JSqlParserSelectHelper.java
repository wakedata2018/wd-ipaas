package com.wakedata.dw.open.utils.jsqlparser;

import com.wakedata.dw.open.enums.DataSourceTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;
import net.sf.jsqlparser.util.cnfexpression.MultiOrExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yiyufeng tanzhi
 * @title JSqlParserSelectHelper
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
@Slf4j
public class JSqlParserSelectHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<SelectItem> selectItems = new ArrayList<>();

    private Table fromTable;

    private List<Join> joins = new ArrayList<>();

    private List<Expression> where = new ArrayList<>();

    private List<Expression> groupBy = new ArrayList<>();

    private List<Expression> having = new ArrayList<>();

    private List<OrderByElement> orderBy = new ArrayList<>();

    private boolean oracle;

    public boolean isOracle() {
        return oracle;
    }

    /**
     * 兼容逻辑，给个默认值
     */
    private DataSourceTypeEnum type = DataSourceTypeEnum.mysql;

    public void setOracle(boolean oracle) {
        this.oracle = oracle;
    }

    private Limit limit;
    private String count;

    public JSqlParserSelectHelper setCount(String countString) {
        this.count = countString;
        return this;
    }

    public JSqlParserSelectHelper selectItem(Table table, String tableColumn) {
        return selectItem(table, tableColumn, null);
    }

    public JSqlParserSelectHelper selectItem(Table table, String tableColumn, String columnNameAlias) {
        return selectItem(table, tableColumn, columnNameAlias, false);
    }

    public JSqlParserSelectHelper selectItem(Table table, String tableColumn, String columnNameAlias, boolean isColumnNameAliasUseAs) {
        Column column = new Column(table, tableColumn);
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(column);
        if (StringUtils.isNotBlank(columnNameAlias)) {
            selectExpressionItem.setAlias(new Alias("'" + columnNameAlias + "'", isColumnNameAliasUseAs));
        }
        this.selectItems.add(selectExpressionItem);
        return this;
    }

    public JSqlParserSelectHelper selectFunctionItem(String functionName, String columnNameAlias, boolean isColumnNameAliasUseAs, Expression... expressionArray) {
        Function function = new Function();
        function.setName(functionName);
        ExpressionList expressionList = new ExpressionList();
        expressionList.setExpressions(Arrays.asList(expressionArray));
        function.setParameters(expressionList);
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(function);
        if (StringUtils.isNotBlank(columnNameAlias)) {
            selectExpressionItem.setAlias(new Alias("'" + columnNameAlias + "'", isColumnNameAliasUseAs));
        }
        this.selectItems.add(selectExpressionItem);
        return this;
    }

    public JSqlParserSelectHelper selectFunctionItem(String selectString, String aliasName, boolean useAs) {
        SelectExpressionItem selectItem = new SelectExpressionItem();
        try {
            selectItem.setExpression(CCJSqlParserUtil.parseCondExpression(selectString));
            if (StringUtils.isNotEmpty(aliasName)) {
                selectItem.setAlias(new Alias(aliasName, useAs));
            }
            this.selectItems.add(selectItem);
        } catch (JSQLParserException e) {
            log.error("selectFunctionItem error", e);
        }
        return this;
    }

    public JSqlParserSelectHelper from(Table table) {
        this.fromTable = table;
        return this;
    }

    public JSqlParserSelectHelper innerJoin(Table table) {
        Join join = commonJoin(table);
        join.setInner(true);
        return this;
    }

    public JSqlParserSelectHelper leftJoin(Table table) {
        Join join = commonJoin(table);
        join.setLeft(true);
        return this;
    }

    public JSqlParserSelectHelper rightJoin(Table table) {
        Join join = commonJoin(table);
        join.setRight(true);
        return this;
    }

    public JSqlParserSelectHelper outerJoin(Table table) {
        Join join = commonJoin(table);
        join.setOuter(true);
        return this;
    }

    public JSqlParserSelectHelper on(Table leftTable, String leftField, Table rightTable, String rightField) {
        // 构造join的on条件
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(leftTable, leftField));
        equalsTo.setRightExpression(new Column(rightTable, rightField));
        Join join = joins.get(joins.size() - 1);
        Expression oldExpression = join.getOnExpression();
        if (null == oldExpression) {
            join.setOnExpression(equalsTo);
        } else {
            if (oldExpression instanceof MultiAndExpression) {
                MultiAndExpression multiAndExpression = (MultiAndExpression) oldExpression;
                multiAndExpression.addChild(0, equalsTo);
            } else {
                MultiAndExpression multiAndExpression = new MultiAndExpression(new ArrayList<>());
                multiAndExpression.addChild(0, equalsTo);
                multiAndExpression.addChild(0, oldExpression);
                join.setOnExpression(multiAndExpression);
            }
        }
        return this;
    }

    public JSqlParserSelectHelper limit(int page, int size) {
        Limit limit = new Limit();
        limit.setRowCount(new LongValue(size));
        limit.setOffset(new LongValue((page - 1) * size));
        this.limit = limit;
        return this;
    }

    public JSqlParserSelectHelper groupBy(Table table, String field) {
        Column column = new Column(table, field);
        groupBy.add(column);
        return this;
    }
    private Join commonJoin(Table table) {
        Join join = new Join();
        // 构造join的目标表
        join.setRightItem(table);
        // 加入join条件中
        this.joins.add(join);
        return join;
    }

    /**
     * @return
     */
    public String toSql() {
        PlainSelect plainSelect = new PlainSelect();
        // 查询sql的from
        plainSelect.setFromItem(this.fromTable);
        // 查询sql的join
        if (!CollectionUtils.isEmpty(joins)) {
            plainSelect.setJoins(this.joins);
        }
        // 查询sql的where
        if (!CollectionUtils.isEmpty(this.where)) {
            plainSelect.setWhere(new MultiAndExpression(this.where));
        }
        // 查询sql的groupBy
        if (!CollectionUtils.isEmpty(this.groupBy)) {
            GroupByElement groupByElement = new GroupByElement();
            groupByElement.setGroupByExpressions(this.groupBy);
            plainSelect.setGroupByElement(groupByElement);
        }
        // 查询sql的having
        if (CollectionUtils.isNotEmpty(this.having)) {
            plainSelect.setHaving(new MultiAndExpression(this.having));
        }
        //排序
        if (CollectionUtils.isNotEmpty(orderBy)) {
            plainSelect.setOrderByElements(orderBy);
        }
        // 查询sql的查询条件
        if (CollectionUtils.isEmpty(this.selectItems)) {
            if (StringUtils.isBlank(this.count)) {
                plainSelect.addSelectItems(new AllColumns());
            }
        } else {
            plainSelect.setSelectItems(this.selectItems);
        }


        // 构造select返回结果
        Select select = new Select();
        String sql = "";
        if (this.type.equals(DataSourceTypeEnum.sqlserver)) {
            if (this.limit != null) {
                select.setSelectBody(plainSelect);
                sql = select.toString();
                sql = sql + " offset "+Integer.parseInt(this.limit.getOffset().toString()) + " rows fetch next " + Integer.parseInt(this.limit.getRowCount().toString()) +" rows only";
            }else {
                select.setSelectBody(plainSelect);
                sql = select.toString();
            }
        }else if (!isOracle()) {
            if (this.limit != null) {
                plainSelect.setLimit(this.limit);
            }
            select.setSelectBody(plainSelect);
            sql = select.toString();
        }else {
            if (this.limit != null) {
                select.setSelectBody(plainSelect);
                sql = select.toString();
                int max = (Integer.parseInt(this.limit.getOffset().toString()) + Integer.parseInt(this.limit.getRowCount().toString()));

                if (!CollectionUtils.isEmpty(this.where)) {
                    if (sql.contains("GROUP BY ")) {
                        sql = sql.replace("GROUP BY", " AND  ROWNUM <= " + max + " ") + "GROUP BY ";
                    } else if (sql.contains(" ORDER BY ")) {
                        sql = sql.replace("ORDER BY", " AND  ROWNUM <= " + max + " " + "ORDER BY ");
                    } else {
                        sql = sql.replace("WHERE", " WHERE  ROWNUM <= " + max + " AND ");
                    }
                } else {
                    if (sql.contains("GROUP BY ")) {
                        sql = sql.replace("GROUP BY", " WHERE  ROWNUM <= " + max + " ") + "GROUP BY ";
                    } else if (sql.contains(" ORDER BY ")) {
                        sql = sql.replace("ORDER BY", " WHERE  ROWNUM <= " + max + " " + "ORDER BY ");
                    } else {
                        sql = sql + " WHERE  ROWNUM <= " + max;
                    }
                }

                sql = sql.replace("FROM", " ,ROWNUM ROWNO FROM ");
                sql = String.format("SELECT PAGED.* FROM ( %S ) PAGED WHERE PAGED.ROWNO > %s", sql, this.limit.getOffset().toString());

            } else {
                select.setSelectBody(plainSelect);
                sql = select.toString();
            }
        }

        //如果是查询总数
        if (StringUtils.isNotBlank(this.count)) {
            sql = sql.replace("SELECT", "SELECT " + this.count);
        }
        return sql;
    }
}
