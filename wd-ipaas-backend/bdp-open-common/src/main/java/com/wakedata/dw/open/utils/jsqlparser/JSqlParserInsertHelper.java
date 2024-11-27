package com.wakedata.dw.open.utils.jsqlparser;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataSourceTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.impl.NutDao;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kuangjing
 * @title JSqlParserInsertHelper
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
@Slf4j
public class JSqlParserInsertHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Column> columns = new ArrayList<>();

    private Table fromTable;

    private ItemsList itemsList =  new ExpressionList();
    List<Expression>  expression = new ArrayList<Expression>();

    private static final String SQL_SEPARATION = ".";
    /**
     * 兼容逻辑，给个默认值
     */
    private DataSourceTypeEnum type = DataSourceTypeEnum.mysql;


    public void buildColumnsAndExpression(JSONObject conditions,NutDao nutDao,Map<String, String> paramType) {
        for (Map.Entry<String, Object> mapEntry : conditions.entrySet()) {
            String value = String.valueOf(mapEntry.getValue());
            String key = mapEntry.getKey();
            if (StringUtils.isNotBlank(key)) {
                if (paramType.get(key) != null && paramType.get(key).equals("int")) {
                    expression.add(new LongValue(value));
                } else {
                    expression.add(new StringValue(value));
                }
                columns.add(new Column(wrapSqlKeyWord(key,nutDao)));
            }
        }
    }

    protected String wrapSqlKeyWord(String keyword, NutDao nutDao) {
        if (keyword.contains(SQL_SEPARATION)) {
            String[] keyArgs = keyword.split("\\.");
            return Arrays.stream(keyArgs).map(s -> nutDao.getJdbcExpert().wrapKeywork(s, true))
                    .collect(Collectors.joining(SQL_SEPARATION));
        }
        return nutDao.getJdbcExpert().wrapKeywork(keyword, true);
    }

    /**~
     * @return
     */
    public String toSql() {
        Insert insert = new Insert();
        insert.setTable(this.fromTable);
        insert.setColumns(this.columns);
        insert.setItemsList(this.itemsList);
        insert.getItemsList().accept(new ItemsListVisitor() {
            @Override
            public void visit(SubSelect subSelect) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            public void visit(ExpressionList expressionList) {
                expressionList.setExpressions(expression);
            }
            @Override
            public void visit(NamedExpressionList namedExpressionList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            public void visit(MultiExpressionList multiExprList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        return insert.toString();
    }

}
