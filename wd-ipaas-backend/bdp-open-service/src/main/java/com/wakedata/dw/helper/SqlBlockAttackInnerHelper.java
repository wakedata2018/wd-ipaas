package com.wakedata.dw.helper;

import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.utils.jsqlparser.SqlDynamicParameterParserUtil;
import com.wakedata.dw.open.utils.jsqlparser.SqlParameter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 防止SQL全表更新与删除工具类，实现参考MyBatis Plus插件的BlockAttackInnerInterceptor
 *
 * @author wujunqiang
 * @since 2023/2/27 15:17
 */
@Slf4j
public class SqlBlockAttackInnerHelper {

    /**
     * SQL中的where关键字
     */
    private static final String WHERE = "where";

    /**
     * 检测是否全表更新或删除，如果存在全表更新或删除则抛异常
     *
     * @param sqlParameters 解析出来的请求参数
     * @param sql           sql语句
     * @param statement     Statement
     */
    public static void parser(Statement statement, List<SqlParameter> sqlParameters, String sql) {
        if (statement instanceof Update) {
            processUpdate((Update) statement, sqlParameters, sql);
        } else if (statement instanceof Delete) {
            processDelete((Delete) statement, sqlParameters, sql);
        }
    }

    private static void processDelete(Delete delete, List<SqlParameter> sqlParameters, String sql) {
        checkWhere(delete.getWhere(), sqlParameters, sql);
    }

    private static void processUpdate(Update update, List<SqlParameter> sqlParameters, String sql) {
        checkWhere(update.getWhere(), sqlParameters, sql);
    }

    private static void checkWhere(Expression where, List<SqlParameter> sqlParameters, String sql) {
        if (fullMatch(where)) {
            throw new OpenException(MsgCodeEnum.w_api_sql_where_must_have_one);
        }
        checkExistsWhereCondition(sqlParameters, sql);
    }

    private static boolean fullMatch(Expression where) {
        if (where == null) {
            return true;
        }

        if (where instanceof EqualsTo) {
            // example: 1=1
            EqualsTo equalsTo = (EqualsTo) where;
            return StringUtils.equals(equalsTo.getLeftExpression().toString(), equalsTo.getRightExpression().toString());
        } else if (where instanceof NotEqualsTo) {
            // example: 1 != 2
            NotEqualsTo notEqualsTo = (NotEqualsTo) where;
            return !StringUtils.equals(notEqualsTo.getLeftExpression().toString(), notEqualsTo.getRightExpression().toString());
        } else if (where instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) where;
            return fullMatch(orExpression.getLeftExpression()) || fullMatch(orExpression.getRightExpression());
        } else if (where instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) where;
            return fullMatch(andExpression.getLeftExpression()) && fullMatch(andExpression.getRightExpression());
        } else if (where instanceof Parenthesis) {
            // example: (1 = 1)
            Parenthesis parenthesis = (Parenthesis) where;
            return fullMatch(parenthesis.getExpression());
        } else if (where instanceof InExpression) {
            InExpression inExpression = (InExpression) where;
            ItemsList rightItemsList = inExpression.getRightItemsList();
            if (rightItemsList instanceof ExpressionList) {
                List<Boolean> collect = ((ExpressionList) rightItemsList).getExpressions().stream().map(SqlBlockAttackInnerHelper::fullMatch).collect(Collectors.toList());
                return collect.stream().anyMatch(Boolean.TRUE::equals);
            } else if (rightItemsList instanceof MultiExpressionList) {
                List<Boolean> collect = ((MultiExpressionList) rightItemsList).getExprList().stream()
                        .flatMap(x -> x.getExpressions().stream()).map(SqlBlockAttackInnerHelper::fullMatch).collect(Collectors.toList());
                return collect.stream().anyMatch(Boolean.TRUE::equals);
            } else if(rightItemsList instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) rightItemsList;
                SelectBody selectBody = subSelect.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    return fullMatch(((PlainSelect) selectBody).getWhere());
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 检查where条件是否存在固定参数，不存在抛异常
     *
     * @param sqlParameters 解析出来的请求参数
     * @param sql           sql语句
     */
    private static void checkExistsWhereCondition(List<SqlParameter> sqlParameters, String sql) {
        sql = SqlDynamicParameterParserUtil.prepareSql(sql);
        // 不存在where关键字，直接抛异常
        int whereIndex = sql.toLowerCase().lastIndexOf(WHERE);
        if (whereIndex == -1) {
            throw new OpenException(MsgCodeEnum.w_api_sql_where_must_have_one);
        }
        // 如果所有参数都是动态参数，直接抛异常
        List<SqlParameter> filterParameters = sqlParameters.stream().filter(x -> Boolean.FALSE.equals(x.getIsDynamic())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterParameters)) {
            throw new OpenException(MsgCodeEnum.w_api_sql_where_must_have_one);
        }
        // 检查where关键字后是否存在参数表达式
        int count = 0;
        for (SqlParameter sqlParameter : filterParameters) {
            if (whereIndex < sql.lastIndexOf(sqlParameter.getContent())) {
                count++;
            }
        }
        if (count == 0) {
            throw new OpenException(MsgCodeEnum.w_api_sql_where_must_have_one);
        }
    }

}
