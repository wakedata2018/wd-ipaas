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
 * @descriptor 自定义函数配置表(添加租户自定义函数)
 */
@Table(name = "dw_open_custom_function")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("自定义函数配置表(添加租户自定义函数)")
public class CustomFunctionPo extends BaseLesseePo {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Long id;

    @Column(name = "func_name")
    @ApiModelProperty("函数名称(以字母开头，只能包含字母加数字，租户唯一)")
    private String funcName;

    @Column(name = "func_language")
    @ApiModelProperty("函数代码编写语言，默认groovy")
    private String funcLanguage;

    @Column(name = "func_desc")
    @ApiModelProperty("函数说明")
    private String funcDesc;

    /**
     * {@link com.wakedata.dw.open.function.custom.CustomFunctionStatus.StatusEnum}
     */
    @Column(name = "status")
    @ApiModelProperty("状态 1：草稿 2：上线 3：下线")
    private Integer status;

    @Column(name = "func_param")
    @ApiModelProperty("函数参数 ,json数组串 [{name:参数名称,type:参数类型,desc:参数说明}]")
    private String funcParam;

    @Column(name = "func_return")
    @ApiModelProperty("函数返回类型")
    private String funcReturn;

    @Column(name = "func_code")
    @ApiModelProperty("函数代码")
    private String funcCode;

    /**
     * {@link com.wakedata.dw.open.function.custom.CustomFunctionStatus.DebugStatusEnum}
     */
    @Column(name = "debug_status")
    @ApiModelProperty("测试状态 1：未测试 2：测试通过 3：测试失败 （只有测试通过的函数才能上线）")
    private Integer debugStatus;

    @Column(name = "create_by")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @ApiModelProperty("更新人")
    private String updateBy;

}
