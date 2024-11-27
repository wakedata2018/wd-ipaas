package com.wakedata.dw.open.utils.jsqlparser;

import com.wakedata.dw.open.enums.DataSourceTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;
import org.apache.commons.collections4.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kuangjing
 * @title JSqlParserInsertHelper
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
@Slf4j
public class JSqlParserDeleteHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Column> columns = new ArrayList<>();

    private Table fromTable;

    private ItemsList itemsList =  new ExpressionList();
    List<Expression>  expression = new ArrayList<Expression>();
    private List<Expression> where = new ArrayList<>();
    /**
     * 兼容逻辑，给个默认值
     */
    private DataSourceTypeEnum type = DataSourceTypeEnum.mysql;
    /**~
     * @return
     */
    public String toSql() {
        Delete delete =  new Delete();
        delete.setTable(this.fromTable);
        delete.setWhere(new MultiAndExpression(this.where));
        return delete.toString();
    }
}
