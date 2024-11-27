package com.wakedata.dw.open.service.impl.datasource;

import com.wakedata.dw.open.mapper.datasource.DatabaseMapper;
import com.wakedata.dw.open.model.datasource.DatabasePo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.datasource.DatabaseService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanzhi
 * @title DatasourceServiceImpl
 * @date 2019/11/21 20:49
 * @projectName dw-open
 * @descriptor
 */
@Service
public class DatabaseServiceImpl extends BaseServiceImpl<DatabasePo, DatabaseMapper> implements DatabaseService {
    @Autowired
    @Override
    protected void init(CurdService<DatabasePo> curdService, DatabaseMapper mapper) {
        super.set(curdService, mapper);
    }

}
