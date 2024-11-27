package com.wakedata.dw.open.service.connector.dto;

import com.wakedata.dw.open.condition.Condition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 连接器鉴权配置参数
 * @author luomeng
 * @date 2022/11/22 15:11
 */
@Data
@ApiModel("连接器鉴权配置参数")
public class ConnectorAuthParamDTO {

//    /**
//     * {@link com.wakedata.dw.open.parammapping.HttpParamKind}
//     */
//    @ApiModelProperty(value = "参数位置 HEAD,QUERY,BODY")
//    private String position;
//
//    /**
//     * 鉴权参数
//     */
//    @ApiModelProperty(value = "鉴权参数")
//    private List<ConnectorAuthParam> params;

    @ApiModelProperty(value = "校验规则")
    private Condition condition;

    @Data
    public static class ConnectorAuthParam{

        @ApiModelProperty(value = "参数key")
        private String key;
        @ApiModelProperty(value = "参数描述")
        private String desc;

    }


}
