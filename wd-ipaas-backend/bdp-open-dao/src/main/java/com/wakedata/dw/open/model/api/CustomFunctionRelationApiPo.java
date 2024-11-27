package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author tanzhi
 * @title ApiGroupPo
 * @date 2019/11/27 10:57
 * @projectName bdp-open
 * @descriptor 自定义函数关联api记录表
 */
@Table(name = "dw_open_custom_function_relation_api")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("自定义函数关联api记录表")
public class CustomFunctionRelationApiPo extends BaseLesseePo {


    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Long id;

    @Column(name = "custom_func_name")
    @ApiModelProperty("自定义函数名称")
    private String customFuncName;

    @Column(name = "data_asset_api_id")
    @ApiModelProperty("关联apiId")
    private Integer dataAssetApiId;

    @Column(name = "create_by")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @ApiModelProperty("更新人")
    private String updateBy;

}
