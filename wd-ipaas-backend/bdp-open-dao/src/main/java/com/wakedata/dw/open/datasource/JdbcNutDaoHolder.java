package com.wakedata.dw.open.datasource;

import com.wakedata.dw.open.model.datasource.DataSourcePo;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.impl.NutDao;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiyufeng
 * @title JdbcNutDaoHolder
 * @projectName bdp-open
 * @date
 * @description
 */
public class JdbcNutDaoHolder {

    private static Map<String, NutDao> nutDaoCache = new ConcurrentHashMap<>();

    public static Map<String, NutDao> getNutDaoCache() {
        return nutDaoCache;
    }


    public static String buildJdbcUrl(DataSourcePo dataSourcePo) {
        switch (dataSourcePo.getDbType()) {
            case MYSQL:
                return String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false",
                        dataSourcePo.getDbHost(), dataSourcePo.getDbPort(), dataSourcePo.getDbName()
                );
            case ORACLE:
                if(dataSourcePo.getDbName().startsWith("/")){
                    return String.format("jdbc:oracle:thin:@//%s:%s%s", dataSourcePo.getDbHost(), dataSourcePo.getDbPort(), dataSourcePo.getDbName());
                }else{
                    return String.format("jdbc:oracle:thin:@%s:%s:%s", dataSourcePo.getDbHost(), dataSourcePo.getDbPort(), dataSourcePo.getDbName());
                }
            case SQLSERVER:
                return String.format("jdbc:sqlserver://%s:%s;databaseName=%s", dataSourcePo.getDbHost(), dataSourcePo.getDbPort(),  dataSourcePo.getDbName());
            case HIVE:
                String dbHost = dataSourcePo.getDbHost();
                if (StringUtils.isBlank(dbHost)) {
                    throw new RuntimeException("hive 连接地址  不能为空");
                }
                String[] split = dbHost.split(",");
                List<String> list = Arrays.asList(split);
                Iterator<String> iterator = list.iterator();
                StringBuilder sb = new StringBuilder();
                String next = iterator.next();
                sb.append(next).append(":").append(dataSourcePo.getDbPort());
                while (iterator.hasNext()) {
                    next = iterator.next();
                    sb.append(",").append(next).append(":").append(dataSourcePo.getDbPort());
                }
                return String.format("jdbc:hive2://%s/%s;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=%s", sb.toString(), dataSourcePo.getDbName(), dataSourcePo.getZkNode());
            case PHOENIX:
                return String.format("jdbc:phoenix:%s", dataSourcePo.getUrl());
            case DREMIO:
                return String.format("jdbc:dremio:direct=%s:%d;schema=%s", dataSourcePo.getDbHost(), dataSourcePo.getDbPort(), dataSourcePo.getDbName());
            default:
                throw new RuntimeException(String.format("unExpect jdbc datasource type %s", dataSourcePo.getDbType()));
        }
    }

    public static String buildNutDaoKey(DataSourcePo po) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(po.getDbHost());
        stringBuffer.append(po.getDbPort());
        stringBuffer.append(po.getDbName());
        stringBuffer.append(po.getDbUsername());
        stringBuffer.append(po.getDbPassword());
        stringBuffer.append(po.getDbType());
        stringBuffer.append(po.getUrl());
        return stringBuffer.toString();
    }

}
