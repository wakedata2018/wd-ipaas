package com.wakedata.dw.open.openapi;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.datasource.HbaseDatasource;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * api数据源处理
 *
 * @author luomeng
 * @date 2022/10/8 14:26
 */
@Component
public class OpenApiDataSourceHelper {

    @Resource
    private DataSourceService dataSourceService;

    /**
     * 参数过滤
     *
     * @param params
     * @param dataSourceId
     * @param apiConditionPoList
     */
    public void paramFilter(JSONObject params, Integer dataSourceId, List<ApiConditionPo> apiConditionPoList) {
        DataSourcePo dataSource = this.getDataSourcePo(dataSourceId);
        if (Objects.nonNull(dataSource)) {
            Set<String> assetColumns = apiConditionPoList.stream().map(ApiConditionPo::getAssetColumns).collect(Collectors.toSet());
            JSONObject jsonObject = params.getJSONObject(HttpParamKind.QUERY.name());
            if (jsonObject != null) {
                Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
                while (iterator.hasNext()) {
                    String value = iterator.next().getKey();
                    if (!assetColumns.contains(value)) {
                        if (DatasourceTypeEnum.HBASE.equals(dataSource.getDbType()) && HbaseDatasource.ROW_KEY.equals(value)) {
                            continue;
                        }
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * 获取数据源
     *
     * @param dataSourceId
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getDataSourcePo_' + #dataSourceId",unless = "#result == null")
    public DataSourcePo getDataSourcePo(Integer dataSourceId) {
        return dataSourceService.show(dataSourceId);
    }

}
