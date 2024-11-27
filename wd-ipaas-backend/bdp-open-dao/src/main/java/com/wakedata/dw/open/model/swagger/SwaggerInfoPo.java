package com.wakedata.dw.open.model.swagger;

import com.wakedata.dw.open.enums.SwaggerImportTypeEnum;
import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Swagger信息实体
 *
 * @author chenshaopeng
 * date 2021/11/1
 */
@Table(name = "dw_swagger_info")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerInfoPo extends BasePo {

    /**
     * 默认组Id
     */
    public static final int DEFAULT_GROUP_ID = 1;

    /**
     * 默认接口数
     */
    public static final int DEFAULT_API_AMOUNT = 0;


    /**
     * 主键
     */
    @ApiModelProperty("id")
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * swagger名称
     */
    @ApiModelProperty("swagger名称")
    @Size(min = 2, max = 20, message = "swagger名称长度必须在2-20之间")
    @NotBlank(message = "swagger名称不能为空")
    @Column(name = "swagger_name")
    private String swaggerName;

    /**
     * swagger描述
     */
    @ApiModelProperty("swagger描述")
    @Column(name = "description")
    private String description;

    /**
     * swagger数据获取地址
     */
    @ApiModelProperty("swagger数据获取地址")
    @NotBlank(message = "swagger Url不能为空")
    @Size(min = 2, max = 1024, message = "swagger Url长度必须在2-1024之间")
    @Column(name = "swagger_url")
    private String swaggerUrl;

    /**
     * 接口分类Id
     */
    @ApiModelProperty("接口分类Id")
    @NotNull(message = "接口分类不能为空")
    @Column(name = "api_group_id")
    private Integer apiGroupId;

    /**
     * Api映射外部域名
     */
    @ApiModelProperty("Api映射外部域名")
    @Column(name = "api_domain_name")
    private String apiDomainName;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    @Column(name = "create_user")
    private String createUser;

    /**
     * 租户ID
     */
    @ApiModelProperty("租户ID")
    @Column(name = "lessee_id")
    private Long lesseeId;

    @ApiModelProperty("执行状态")
    @Column(name = "execute_status")
    private Integer executeStatus;

    /**
     * swagger Api数量
     */
    @ApiModelProperty("swagger Api数量")
    @Column(name = "api_amount")
    private Integer apiAmount;

    /**
     * 导入方式
     */
    @ApiModelProperty("导入方式，URL：URL导入、FILE：文件导入")
    @Column(name = "import_type")
    private SwaggerImportTypeEnum importType;

    /**
     * swagger文档json数据
     */
    @ApiModelProperty("swagger文档json数据")
    @Column(name = "swagger_json")
    private String swaggerJson;

    /**
     * 响应参数映射规则 0：惟客云 1：其他
     */
    @Column(name = "resp_mapping_rule")
    private Integer respMappingRule;

    /**
     * Swagger解析时间
     */
    @Column(name = "parse_time")
    private Date parseTime;

    /**
     * swagger Api 解析成功数量
     */
    @ApiModelProperty("swagger Api解析成功数量")
    @Transient
    private Integer apiSuccessAmount;

    /**
     * 接口分类名
     */
    @ApiModelProperty("接口分类名")
    @Transient
    private String apiGroupName;

    /**
     * 已经上传的文件路径
     */
    @ApiModelProperty("已经上传的文件路径")
    @Transient
    private String tempFilePath;

}
