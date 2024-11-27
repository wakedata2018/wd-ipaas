package com.wakedata.dw.open.model.swagger;

import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author WangChenSheng
 * date 2022/8/23 14:58
 */
@Table(name = "dw_open_api_swagger")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerApiDO extends BasePo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty("id")
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * api名称
     */
    private String apiName;

    /**
     * api类型
     */
    private Integer apiType;

    /**
     * apiPath
     */
    private String dataAssetApiMethod;

    /**
     * api描述
     */
    private String apiDescription;

    /**
     * 导入详情
     */
    private String errorDetail;

    /**
     * SwaggerApi解析结果：1 成功 2 失败
     */
    private Integer parseStatus;

    /**
     * 临时API导入到API主表的状态 0 未导入 1 导入成功 2 导入失败
     */
    private Integer importStatus;

    /**
     * swagger基础信息
     */
    private String apiInfo;

    /**
     * 接口分组id
     */
    private Integer apiGroupId;

    /**
     * swaggerId
     */
    private Integer swaggerId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * jsonSchema
     */
    private String jsonSchema;

}
