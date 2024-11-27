package com.wakedata.dw.open.model.api.internal.sql;

import com.wakedata.dw.open.model.api.internal.InternalApiAttr;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author ZhangXueJun
 * @title InternalSqlApi
 * @date 2021/3/8 14:25
 * @projectName dw-open
 * @description
 */
@Data
public class InternalSqlApiAttr extends InternalApiAttr {

    @Column(name = "API_SQL")
    private String apiSql;
}
