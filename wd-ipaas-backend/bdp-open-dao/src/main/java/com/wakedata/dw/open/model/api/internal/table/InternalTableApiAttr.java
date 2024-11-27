package com.wakedata.dw.open.model.api.internal.table;

import com.wakedata.dw.open.model.api.AbstractApiAttr;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author ZhangXueJun
 * @title InternelTableApi
 * @date 2021/3/8 14:25
 * @projectName dw-open
 * @description
 */
@Data
public class InternalTableApiAttr extends AbstractApiAttr {

    @Column(name = "DATA_ASSET_NAME")
    private String dataAssetName;
}
