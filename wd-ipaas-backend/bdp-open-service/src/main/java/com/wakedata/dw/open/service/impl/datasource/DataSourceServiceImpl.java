package com.wakedata.dw.open.service.impl.datasource;

import com.wakedata.dw.open.datasource.AbstractDatasource;
import com.wakedata.dw.open.datasource.DatasourceHolder;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.datasource.DataSourceMapper;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.utils.AuthUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author tanzhi
 * @title DatasourceConfigServiceImpl
 * @date 2019/11/18 16:30
 * @projectName bdp-open
 * @descriptor
 */
@Service
public class DataSourceServiceImpl extends BaseServiceImpl<DataSourcePo, DataSourceMapper> implements DataSourceService {

    @Autowired
    private DataAssetApiService dataAssetApiService;

    @Autowired
    private StringEncryptor encryptor;


    @Autowired
    private DatasourceHolder datasourceHolder;

    @Autowired
    @Override
    protected void init(CurdService<DataSourcePo> curdService, DataSourceMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public boolean test(DataSourcePo dataSourcePo) {
        dataSourcePo.setDbPassword(encryptor.encrypt(Optional.ofNullable(dataSourcePo.getDbPassword()).orElse("")));
        AbstractDatasource dataSource = datasourceHolder.getDataSource(dataSourcePo);
        if (dataSource == null) {
            return false;
        }
        return true;
    }

    @Override
    public Integer addDataSource(DataSourcePo dataSourcePo) {
        check(dataSourcePo, false);
        Date createTime = new Date();
        dataSourcePo.setCreateTime(createTime);
        dataSourcePo.setUpdateTime(createTime);
        String encrypt = encryptor.encrypt(dataSourcePo.getDbPassword());
        dataSourcePo.setDbPassword(encrypt);
        AuthUtils.setAppId(dataSourcePo);
        int insert = getCurdService().insert(dataSourcePo, getMapper());
        return insert;
    }

    @Override
    public Integer updateDataSource(DataSourcePo dataSourcePo) {
        check(dataSourcePo, true);
        dataSourcePo.setUpdateTime(new Date());
        dataSourcePo.setDbPassword(encryptor.encrypt(dataSourcePo.getDbPassword()));
        int i = getCurdService().updateByPrimaryKeySelective(dataSourcePo, getMapper());
        return i;
    }

    @Override
    public boolean deleteDataSource(Integer dataSourceId) {
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setDataSourceId(dataSourceId);
        List<DataAssetApiPo> assetApiPos = dataAssetApiService.find(dataAssetApiPo);
        if (CollectionUtils.isNotEmpty(assetApiPos)) {
            throw new OpenException(MsgCodeEnum.w_cannot_delete_datasource_cause_of_has_api);
        }
        getCurdService().deleteByPrimaryKey(dataSourceId, getMapper());
        return false;
    }

    @Override
    public List<DataSourcePo> findByPo(DataSourcePo dataSourcePo) {
        return this.getMapper().findByPo(dataSourcePo);
    }

    private void check(DataSourcePo dataSourcePo, Boolean update) {
        DataSourcePo checkDataSource = new DataSourcePo();
        checkDataSource.setConnectionName(dataSourcePo.getConnectionName());
        checkDataSource.setLesseeId(AuthUtils.currentAppId());
        List<DataSourcePo> select = getMapper().select(checkDataSource);
        if (CollectionUtils.isNotEmpty(select)) {
            if (update) {
                for (DataSourcePo sourcePo : select) {
                    if (sourcePo.getId().equals(dataSourcePo.getId())
                            && dataSourcePo.getConnectionName().equals(sourcePo.getConnectionName())) {
                        continue;
                    } else {
                        throw new OpenException(MsgCodeEnum.w_datasource_name_already_exists);
                    }
                }
            } else {
                throw new OpenException(MsgCodeEnum.w_datasource_name_already_exists);
            }
        }
    }
}
