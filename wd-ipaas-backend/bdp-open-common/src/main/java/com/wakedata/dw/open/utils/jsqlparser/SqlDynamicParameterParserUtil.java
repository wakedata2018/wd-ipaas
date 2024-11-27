package com.wakedata.dw.open.utils.jsqlparser;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.utils.ColumnTypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Sqls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql动态参数解析工具类
 * sql支持参数类型type可为integer,long,double,string,array,boolean
 * ColumnTypeConvertUtil.convertParamType(type)
 * @author luomeng
 * @date 2023/2/15 10:01
 */
@Slf4j
public class SqlDynamicParameterParserUtil {

    /**
     * sql参数解析规则：格式为 ${param:type},如 ${tenantId:Long}，
     *  此处解析出一个参数为tenantId，Long类型，不可为空参数
     */
    public static final Pattern PATTERN_SQL_PARAM = Pattern.compile("\\$\\{\\w+:[a-zA-Z]+}");

    /**
     * sql动态参数解析规则：格式为 $param:type[sql condition]，
     * 如 1、$tenantId:Long[ and tenant_id = ?] 或者 $tenantId:Long[ and tenant_id = ${tenantId:Long}] ，
     *      参数共用时可省略，用?作为占位符，此处解析出一个参数为tenantId，Long类型，可为空参数
     *
     *   2、$tenantId:Long[ and tenant_id = ${tid:Long}] ，
     *     参数不同时可分别定义，此处解析出两个参数为1）tenantId，Long类型，可为空参数，2）tid，Long类型，不可为空参数
     */
    public static final Pattern PATTERN_SQL_DYNAMIC_PARAM = Pattern.compile("\\$\\w+:[a-zA-Z]+\\[.+]");

    private static final Pattern PATTERN_SQL_DYNAMIC_PARAM_PART = Pattern.compile("\\$\\w+:[a-zA-Z]+\\[");

    /**
     * sql分析时的默认参数值
     */
    private static final CharSequence ANALYSIS_DEFAULT_PARAM_VALUE = "1";

    /**
     * 参数占位符
     */
    private static final String QUESTION_MARK = "?";

    /**
     * sql value填充时获取指定对象属性的key
     */
    public static final String PARAM_VALUE_KEY = "value";

    /**
     * sql value 类型
     */
    public static final String PARAM_VALUE_TYPE_KEY = "type";
    /**
     * sql 判断为空时是否需要填充值时获取指定对象属性的key
     */
    public static final String PARAM_VALUE_IS_NULL_KEY = "isNull";

    /**
     * sql预处理，去掉sql格式化时添加的\t\n\r，以及:之后添加的空格
     * @param sql
     * @return
     */
    public static String prepareSql(String sql){
        return sql.replaceAll("[\\t\\n\\r]"," ").replaceAll(":\\s",":");
    }

    /**
     * 匹配sql中设置的参数
     * @param sql sql文本
     * @return 参数列表
     */
    public static List<SqlParameter> matchParam(String sql){
        sql = prepareSql(sql);
        List<SqlParameter> parameterList = matcherDynamicParam(sql,new ArrayList<>());
        return matchParam(sql, parameterList);
    }

    /**
     * 匹配sql动态参数
     * @param sql
     * @param parameterList
     * @return
     */
    private static List<SqlParameter> matcherDynamicParam(String sql,List<SqlParameter> parameterList){
        Matcher matcher = PATTERN_SQL_DYNAMIC_PARAM_PART.matcher(sql);
        while (matcher.find()) {
            String paramContent = matcher.group();
            String[] group = paramContent.replaceAll("(\\$)|(\\[)","").split(":");
            parameterList.add(new SqlParameter(group[0], ColumnTypeConvertUtil.convertParamType(group[1]),Boolean.TRUE,paramContent));
        }
        return parameterList;
    }

    /**
     * 匹配sql参数
     * @param sql
     * @param parameterList
     */
    private static List<SqlParameter> matchParam(String sql, List<SqlParameter> parameterList) {
        Matcher matcher = PATTERN_SQL_PARAM.matcher(sql);
        while (matcher.find()) {
            String paramContent = matcher.group();
            String[] group = paramContent.replaceAll("(\\$\\{)|(})","").split(":");
            parameterList.add(new SqlParameter(group[0], ColumnTypeConvertUtil.convertParamType(group[1]),Boolean.FALSE,paramContent));
        }
        return parameterList;
    }

    /**
     * 转换sql，去掉规则表达式
     * @param sql sql脚本
     * @param isAnalysisSql 是否是分析sql
     * @param paramValue 参数值
     * @return
     */
    public static String transformSql(String sql,Boolean isAnalysisSql, JSONObject paramValue){
        sql = prepareSql(sql);
        List<SqlParameter> parameterList = matchParam(sql,new ArrayList<>());
        if(CollUtil.isNotEmpty(parameterList)){
            for (SqlParameter param : parameterList) {
                sql = sql.replace(param.getContent(),isAnalysisSql ? ANALYSIS_DEFAULT_PARAM_VALUE : getSqlParamValue(param.getParam(),paramValue));
            }
        }
        return transformSqlDynamic(sql,isAnalysisSql,paramValue);
    }


    /**
     * 获取sql参数值
     * @param param
     * @param value
     * @return
     */
    private static CharSequence getSqlParamValue(String param,JSONObject value){
        JSONObject paramValue = value.getJSONObject(param);
        if(ObjectUtil.isNull(paramValue)){
            return "";
        }
        Object val = paramValue.get(PARAM_VALUE_KEY);
        if(val == null){
            return "";
        }
        String res;
        if (val instanceof Number || val instanceof Boolean) {
            res = String.valueOf(val);
        } else if (val instanceof Collection) {
            res = Sqls.escapeSqlFieldValue(JSONObject.toJSONString(val).replaceAll("[\\[\\]]","")).toString()
                    .replace("\"","'");
        } else {
            String paramType = paramValue.getString(PARAM_VALUE_TYPE_KEY);
            if(StrUtil.isNotEmpty(paramType) && paramType.contains(DataTypeEnum.ARRAY.getType())){
                res = Sqls.escapeSqlFieldValue(String.valueOf(val).replaceAll("[\\[\\]]","")).toString()
                        .replace("\"","'");
            }else {
                res = String.format("'%s'", Sqls.escapeSqlFieldValue((String) val));
            }
        }
        return res;
    }


    /**
     * 获取sql参数条件，是否允许为空参数
     * @param param
     * @param value
     * @return
     */
    private static Boolean getSqlParamValueIsNull(String param, JSONObject value){
        JSONObject paramValue = value.getJSONObject(param);
        if(ObjectUtil.isNull(paramValue)){
            return Boolean.FALSE;
        }
        return paramValue.getBoolean(PARAM_VALUE_IS_NULL_KEY);
    }

    /**
     * 转换sql动态参数
     * @param sql
     * @param isAnalysisSql
     * @param paramValue
     * @return
     */
    private static String transformSqlDynamic(String sql,Boolean isAnalysisSql, JSONObject paramValue){
        Matcher matcher = PATTERN_SQL_DYNAMIC_PARAM.matcher(sql);
        List<SqlParameter> parameterList;
        while (matcher.find()) {
            String content = matcher.group();
            int first = content.indexOf("]");
            String param = content.substring(0,first + 1);
            parameterList = matcherDynamicParam(param, new ArrayList<>());
            for(SqlParameter parameter : parameterList){
                String replaceParam = "";
                if (!isAnalysisSql) {
                    Boolean isNull = getSqlParamValueIsNull(parameter.getParam(),paramValue);
                    CharSequence sqlParamValue = getSqlParamValue(parameter.getParam(), paramValue);
                    //允许为空且参数值未填，去掉该参数
                    if(isNull && ObjectUtil.isEmpty(sqlParamValue)){
                        replaceParam = "";
                    }else{
                        replaceParam = getReplace(param, parameter, sqlParamValue);
                    }
                } else {
                    replaceParam = getReplace(param, parameter, ANALYSIS_DEFAULT_PARAM_VALUE);
                }
                sql = sql.replace(param,replaceParam);
            }
            sql = transformSqlDynamic(sql,isAnalysisSql,paramValue);
        }
        return sql;
    }

    private static String getReplace(String param, SqlParameter parameter, CharSequence sqlParamValue) {
        return param.replace(parameter.getContent(), "").replace("]", "").replace(QUESTION_MARK, sqlParamValue);
    }

    public static void main(String[] args) {
//        String sql = "select id $tenantId:long[,abc] from a where test = ${test:string} $abc:string[and t=? and abc=${abc:string}] and b = ${t2:long}";

        String sql = "select\n" +
                "  id,\n" +
                "  business_domain_id,\n" +
                "  dict_name,\n" +
                "  dict_value,\n" +
                "  dict_description,\n" +
                "  is_extend,\n" +
                "  is_deleted,\n" +
                "  create_by,\n" +
                "  create_time,\n" +
                "  update_by,\n" +
                "  update_time\n" +
                "from\n" +
                "  wd_general.wd_general_dictionary wgd\n" +
                "where\n" +
                "  id = ${id: Integer}\n" +
                "  $tenantId: Long[\n" +
                "    and test = ${test: Long}\n" +
                "  ]\n" +
                "  and $tenantId2: Long[\n" +
                "    and test2 = ?\n" +
                "  ]";

        log.info("paramList：{}", JSONUtil.toJsonPrettyStr(matchParam(sql)));
        log.info("transformSql：{}",transformSql(sql,Boolean.TRUE,null));

        JSONObject sqlParam = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_IS_NULL_KEY,true);
        value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_KEY, "32432re");
//        value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_KEY, Arrays.asList("te1","te2","te3"));
        value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_TYPE_KEY,"");
        sqlParam.put("test",value);
        log.info("sql value :{}",getSqlParamValue("test",sqlParam));

        String sql2 = "select id,test from d where test = ${test:string}";
        log.info("transformSql：{}",transformSql(sql2,Boolean.FALSE,sqlParam));
    }



}
