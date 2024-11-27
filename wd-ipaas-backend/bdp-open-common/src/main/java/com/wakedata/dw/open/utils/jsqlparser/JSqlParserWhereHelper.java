package com.wakedata.dw.open.utils.jsqlparser;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.util.cnfexpression.MultiAndExpression;
import net.sf.jsqlparser.util.cnfexpression.MultiOrExpression;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class JSqlParserWhereHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Expression> where = new ArrayList<>();

    private List<Expression> having = new ArrayList<>();

    public <T> JSqlParserWhereHelper between(Table table, String tableColumn, T leftValue, T rightValue) {
        return between(table, tableColumn, leftValue, rightValue, false);
    }
    public <T> JSqlParserWhereHelper between(Table table, String tableColumn, T leftValue, T rightValue, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression leftExpression = toValueExpression(leftValue);
        Expression rightExpression = toValueExpression(rightValue);
        Between between = new Between();
        between.setLeftExpression(column);
        between.setBetweenExpressionStart(leftExpression);
        between.setBetweenExpressionEnd(rightExpression);
        if (ifHaving) {
            this.having.add(between);
        } else {
            this.where.add(between);
        }
        return this;
    }
    public <T> JSqlParserWhereHelper equal(Table table, String tableColumn, T value) {
        return equal(table, tableColumn, value, false);
    }
    public <T> JSqlParserWhereHelper equal(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(column);
        equalsTo.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(equalsTo);
        } else {
            this.where.add(equalsTo);
        }
        return this;
    }
    public <T> JSqlParserWhereHelper notequal(Table table, String tableColumn, T value) {
        return notequal(table, tableColumn, value, false);
    }
    public <T> JSqlParserWhereHelper notequal(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        NotEqualsTo notEqualsTo = new NotEqualsTo();
        notEqualsTo.setLeftExpression(column);
        notEqualsTo.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(notEqualsTo);
        } else {
            this.where.add(notEqualsTo);
        }
        return this;
    }
    public <T> JSqlParserWhereHelper less(Table table, String tableColumn, T value) {
        return less(table, tableColumn, value, false);
    }
    public <T> JSqlParserWhereHelper less(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        MinorThan minorThan = new MinorThan();
        minorThan.setLeftExpression(column);
        minorThan.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(minorThan);
        } else {
            this.where.add(minorThan);
        }
        return this;
    }
    public <T> JSqlParserWhereHelper lessorequal(Table table, String tableColumn, T value) {
        return lessorequal(table, tableColumn, value, false);
    }
    public <T> JSqlParserWhereHelper lessorequal(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        MinorThanEquals minorThanEquals = new MinorThanEquals();
        minorThanEquals.setLeftExpression(column);
        minorThanEquals.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(minorThanEquals);
        } else {
            this.where.add(minorThanEquals);
        }
        return this;
    }
    public JSqlParserWhereHelper isnull(Table table, String tableColumn) {
        return isnull(table, tableColumn, false);
    }

    public JSqlParserWhereHelper isnull(Table table, String tableColumn, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        IsNullExpression isNullExpression = new IsNullExpression();
        isNullExpression.setLeftExpression(column);
        if (ifHaving) {
            this.having.add(isNullExpression);
        } else {
            this.where.add(isNullExpression);
        }
        return this;
    }

    public JSqlParserWhereHelper notnull(Table table, String tableColumn) {
        return notnull(table, tableColumn, false);
    }

    public JSqlParserWhereHelper notnull(Table table, String tableColumn, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        IsNullExpression isNullExpression = new IsNullExpression();
        isNullExpression.setLeftExpression(column);
        isNullExpression.setNot(true);
        if (ifHaving) {
            this.having.add(isNullExpression);
        } else {
            this.where.add(isNullExpression);
        }
        return this;
    }

    public <T> JSqlParserWhereHelper greater(Table table, String tableColumn, T value) {
        return greater(table, tableColumn, value, false);
    }

    public <T> JSqlParserWhereHelper greater(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.setLeftExpression(column);
        greaterThan.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(greaterThan);
        } else {
            this.where.add(greaterThan);
        }
        return this;
    }

    public <T> JSqlParserWhereHelper greaterorequal(Table table, String tableColumn, T value) {
        return greaterorequal(table, tableColumn, value, false);
    }

    public <T> JSqlParserWhereHelper greaterorequal(Table table, String tableColumn, T value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        Expression valueExpression = this.toValueExpression(value);
        GreaterThanEquals greaterThanEquals = new GreaterThanEquals();
        greaterThanEquals.setLeftExpression(column);
        greaterThanEquals.setRightExpression(valueExpression);
        if (ifHaving) {
            this.having.add(greaterThanEquals);
        } else {
            this.where.add(greaterThanEquals);
        }
        return this;
    }

    public <T> JSqlParserWhereHelper in(Table table, String tableColumn, List<T> valueList) {
        return in(table, tableColumn, valueList, false);
    }

    public <T> JSqlParserWhereHelper in(Table table, String tableColumn, List<T> valueList, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        List<Expression> expressionList = new ArrayList<>();
        for (T value : valueList) {
            Expression valueExpression = this.toValueExpression(value);
            expressionList.add(valueExpression);
        }
        InExpression inExpression = new InExpression(column, new ExpressionList(expressionList));
        if (ifHaving) {
            this.having.add(inExpression);
        } else {
            this.where.add(inExpression);
        }
        return this;
    }

    public <T> JSqlParserWhereHelper notin(Table table, String tableColumn, List<T> valueList) {
        return notin(table, tableColumn, valueList, false);
    }

    public <T> JSqlParserWhereHelper notin(Table table, String tableColumn, List<T> valueList, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        List<Expression> expressionList = new ArrayList<>();
        for (T value : valueList) {
            Expression valueExpression = this.toValueExpression(value);
            expressionList.add(valueExpression);
        }
        InExpression inExpression = new InExpression(column, new ExpressionList(expressionList));
        inExpression.setNot(true);
        if (ifHaving) {
            this.having.add(inExpression);
        } else {
            this.where.add(inExpression);
        }
        return this;
    }

    public JSqlParserWhereHelper contain(Table table, String tableColumn, String value, boolean ifHaving) {
        return like(table, tableColumn, "%" + value + "%", ifHaving);
    }

    public JSqlParserWhereHelper notcontain(Table table, String tableColumn, String value, boolean ifHaving) {
        return notlike(table, tableColumn, "%" + value + "%", ifHaving);
    }

    public JSqlParserWhereHelper startwith(Table table, String tableColumn, String value, boolean ifHaving) {
        return like(table, tableColumn, value + "%", ifHaving);
    }

    public JSqlParserWhereHelper endwith(Table table, String tableColumn, String value, boolean ifHaving) {
        return like(table, tableColumn, "%" + value, ifHaving);
    }

    public JSqlParserWhereHelper like(Table table, String tableColumn, String value) {
        return like(table, tableColumn, value, false);
    }

    public JSqlParserWhereHelper like(Table table, String tableColumn, String value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        StringValue stringValue = new StringValue(value);
        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeftExpression(column);
        likeExpression.setRightExpression(stringValue);
        if (ifHaving) {
            this.having.add(likeExpression);
        } else {
            this.where.add(likeExpression);
        }
        return this;
    }

    public JSqlParserWhereHelper notlike(Table table, String tableColumn, String value) {
        return notlike(table, tableColumn, value, false);
    }

    public JSqlParserWhereHelper notlike(Table table, String tableColumn, String value, boolean ifHaving) {
        Expression column = new Column(table, tableColumn);
        StringValue stringValue = new StringValue(value);
        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeftExpression(column);
        likeExpression.setRightExpression(stringValue);
        likeExpression.setNot();
        if (ifHaving) {
            this.having.add(likeExpression);
        } else {
            this.where.add(likeExpression);
        }
        return this;
    }


    public JSqlParserWhereHelper multiAnd(List<Expression> expressionList) {
        return multiAnd(expressionList, false);
    }

    public JSqlParserWhereHelper multiAnd(List<Expression> expressionList, boolean ifHaving) {
        MultiAndExpression multiAndExpression = new MultiAndExpression(expressionList);
        if (ifHaving) {
            this.having.add(multiAndExpression);
        } else {
            this.where.add(multiAndExpression);
        }
        return this;
    }

    public JSqlParserWhereHelper multiOr(List<Expression> expressionList) {
        return multiOr(expressionList, false);
    }

    public JSqlParserWhereHelper multiOr(List<Expression> expressionList, boolean ifHaving) {
        MultiOrExpression multiOrExpression = new MultiOrExpression(expressionList);
        if (ifHaving) {
            this.having.add(multiOrExpression);
        } else {
            this.where.add(multiOrExpression);
        }
        return this;
    }

    private <T> Expression toValueExpression(T value) {
        Expression valueExpression = null;
        switch (value.getClass().getName()) {
            case "java.util.Date":
                valueExpression = new TimestampValue(sdf.format(value));
                break;
            case "java.lang.Double":
                valueExpression = new DoubleValue(value.toString());
                break;
            case "java.lang.String":
                valueExpression = new StringValue(value.toString().replace("'", "\\'"));
                break;
            default:
        }
        return valueExpression;
    }
}
