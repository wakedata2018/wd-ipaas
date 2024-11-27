package com.wakedata.dw.open.model.api.flow.operator.sql;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sql算子组件
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/2/14 18:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SqlComponent extends AbstractComponent {
    /**
     * sql算子参数对象
     */
    private SqlOperatorParam sqlOperatorParam;

    /**
     * 算子描述
     */
    private String desc;

    /**
     * sql算子请求参数
     */
    private List<ApiConditionPo> parameters;

    /**
     * sql算子返回列（需要用于判断列授权，不能用JSONSchema格式）
     */
    private List<ApiConditionPo> results;

    /**
     * sql算子响应参数模板
     */
    private List<ApiRespParamDTO> responseParams;

    /**
     * sql算子能力类型（用于生成引用值参数模板）
     */
    private DataAssetEnums.DataApiOperationType sqlOperationType;

    /**
     * Redis锁配置
     */
    private RedisLockConfigAttr apiAttr;
}
