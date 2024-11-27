package com.wakedata.dw.open.interceptor;

import com.wakedata.dw.open.interceptor.annotation.CreateBy;
import com.wakedata.dw.open.interceptor.annotation.CreateTime;
import com.wakedata.dw.open.interceptor.annotation.UpdateTime;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * 自定义 Mybatis 插件，自动设置 createTime 和 updateTime 的值。
 * 拦截 update 操作（添加和修改）
 * @author Administrator
 **/

@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class DateInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL 命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object parameter = invocation.getArgs()[1];
        if (Objects.isNull(parameter)){
            return invocation.proceed();
        }

        // 获取私有成员变量
        Field[] declaredFields = parameter.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            // insert 语句插入 createTime
            if (field.getAnnotation(CreateTime.class) != null) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameter, new Date());
                }
            }

            // update 语句插入 updateTime
            if (field.getAnnotation(UpdateTime.class) != null) {
                if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                    field.setAccessible(true);
                    field.set(parameter, new Date());
                }
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}