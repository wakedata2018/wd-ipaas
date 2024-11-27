package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * api编排执行链
 *
 * @author luomeng
 * @date 2022/10/13 17:06
 */
@Table(name = "dw_open_api_liteflow_chain")
@Data
public class ApiLiteFlowChainPo extends BasePo {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 数据apiId
     */
    @Column(name = "data_asset_api_id")
    private Long dataAssetApiId;
    /**
     * 执行链
     */
    @Column(name = "chain")
    private String chain;

}
