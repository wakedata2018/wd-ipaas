package com.wakedata.dw.open.utils.jsqlparser;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataSourceTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.impl.NutDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class JSqlParserUpdataHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Column> columns = new ArrayList<>();

    private Table fromTable;

    private ItemsList itemsList =  new ExpressionList();
    List<Expression>  expression = new ArrayList<Expression>();
    private List<Expression> where = new ArrayList<>();

    private static final String SQL_SEPARATION = ".";
    /**
     * 兼容逻辑，给个默认值
     */
    private DataSourceTypeEnum type = DataSourceTypeEnum.mysql;


    public void buildColumnsAndExpression (JSONObject updateConditions, NutDao nutDao ,Map<String, String> paramType) {
        for (Map.Entry<String, Object> mapEntry : updateConditions.entrySet()) {
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
        Update update =  new Update();
        List<Table> tables =  new ArrayList<>();
        tables.add(this.fromTable);
        update.setTables(tables);
        update.setColumns(this.columns);
        if (this.expression == null || this.expression.size() <= 0) {
            log.error("修改操作不能没有过滤条件，请检查。");
            return null;
        }
        update.setExpressions(this.expression);
        // 查询sql的where
        if (!CollectionUtils.isEmpty(this.where)) {
            update.setWhere(new MultiAndExpression(this.where));
        }
        return update.toString();
    }
}
