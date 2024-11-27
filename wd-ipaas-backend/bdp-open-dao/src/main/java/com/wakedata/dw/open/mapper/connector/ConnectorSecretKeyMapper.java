package com.wakedata.dw.open.mapper.connector;

import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import com.wakedata.dw.open.model.query.ConnectorSecretKeyPageQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author wujunqiang
 * @since 2022/11/17 18:48
 */
@Repository
public interface ConnectorSecretKeyMapper extends Mapper<ConnectorSecretKeyPo> {

    /**
     * 根据密钥唯一标识查询密钥信息
     *
     * @param secretKey 密钥唯一标识
     * @return ConnectorSecretKeyPo
     */
    ConnectorSecretKeyPo selectBySecretKey(@Param("secretKey") String secretKey);

    /**
     * 根据租户ID、平台ID查询启用状态的密钥列表
     *
     * @param lesseeId    租户id
     * @param connectorId 平台id
     * @return 密钥列表
     */
    List<ConnectorSecretKeyPo> selectByConnectorId(@Param("lesseeId") Long lesseeId, @Param("connectorId") Long connectorId);

    /**
     * 查询平台密钥列表
     *
     * @param query 查询条件
     * @return 平台密钥列表
     */
    List<ConnectorSecretKeyPo> pageQuery(@Param("query") ConnectorSecretKeyPageQuery query);

}
