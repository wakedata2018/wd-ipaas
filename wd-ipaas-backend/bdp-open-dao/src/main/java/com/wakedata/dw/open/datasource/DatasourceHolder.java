package com.wakedata.dw.open.datasource;

import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author yiyufeng
 * @title DatasourceHolder
 * @projectName bdp-open
 * @date
 * @description
 */
@Repository
public class DatasourceHolder implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private StringEncryptor stringEncryptor;

    private Map<String, AbstractDatasource> dataSourceInstanceMap = new WeakHashMap<>(4);

    public AbstractDatasource getDataSource(DataSourcePo configPo) {
        AbstractDatasource datasource = dataSourceInstanceMap.get(buildDatasourceKey(configPo));
        if (null == datasource) {
            datasource = DataSourceFactory.produce(configPo, applicationContext);
            datasource.setStringEncryptor(stringEncryptor);
            if (datasource.testConnection(configPo)) {
                dataSourceInstanceMap.put(buildDatasourceKey(configPo), datasource);
            } else {
                datasource = null;
                return null;
            }
        }
        if (datasource == null) {
            throw new OpenException("数据库无法连接");
        }
        return datasource;
    }

    private String buildDatasourceKey(DataSourcePo dataSourcePo) {
        return String.format("%s Datasource", dataSourcePo.toString());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
