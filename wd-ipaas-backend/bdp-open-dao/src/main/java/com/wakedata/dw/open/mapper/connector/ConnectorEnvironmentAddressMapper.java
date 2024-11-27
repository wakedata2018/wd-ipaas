package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 11:36
 */
@Repository
public interface ConnectorEnvironmentAddressMapper extends Mapper<ConnectorEnvironmentAddressPo>, InsertListMapper<ConnectorEnvironmentAddressPo> {

    /**
     * 校验出名称重复的环境地址信息
     */
    List<ConnectorEnvironmentAddressPo> checkEnvironmentAddressName(@Param("connectorId") Long connectorId, @Param("environmentAddressPoList") List<ConnectorEnvironmentAddressPo> environmentAddressPoList);

    /**
     * 根据平台ID查询环境地址信息
     *
     * @param connectorId 平台ID
     * @return 环境地址集合
     */
    List<ConnectorEnvironmentAddressPo> selectByConnectorId(@Param("connectorId") Long connectorId);
}
