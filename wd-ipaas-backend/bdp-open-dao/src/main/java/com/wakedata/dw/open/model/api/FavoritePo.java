package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title FavoritePo
 * @date 2019/11/27 17:58
 * @projectName bdp-open
 * @descriptor
 */
@Table(name = "DW_OPEN_COLLECT")
@Data
public class FavoritePo extends BasePo {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "DATA_ASSET_API_ID")
    private Integer dataAssetApiId;
    @Column(name = "DATA_ACCESS_APP_ID")
    private Integer dataAccessAppId;

}
