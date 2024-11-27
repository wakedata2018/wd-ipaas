package com.wakedata.dw.open.service.connector;

import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.query.ConnectorApiPageQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDetailDTO;

import java.util.List;


/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 16:35
 */
public interface ConnectorApiService extends BaseDbService<ConnectorApiPo> {

    /**
     * 创建连接器平台api
     *
     * @param connectorApiDetailDTO 连接器平台api信息DTO
     * @return Long 返回创建成功的api的主键id
     */
    ResultDTO<Long> create(ConnectorApiDetailDTO connectorApiDetailDTO);

    /**
     * 更新连接器平台api
     *
     * @param connectorApiDetailDTO 连接器平台api信息DTO
     * @return Long 返回创建成功的api的主键id
     */
    ResultDTO<Long> modify(ConnectorApiDetailDTO connectorApiDetailDTO);

    /**
     * 删除连接器平台api
     *
     * @param id 连接器平台api主键id
     * @return Long 返回创建成功的api的主键id
     */
    ResultDTO<Boolean> delete(Long id);

    /**
     * 查询连接器平台api
     *
     * @param connectorApiPageQuery 连接器平台api查询条件
     * @return List<ConnectorApiDTO> 返回连接器平台api的list
     */
    PageResultDTO<List<ConnectorApiDTO>> query(ConnectorApiPageQuery connectorApiPageQuery);

    /**
     * 根据id查询连接器API详情
     *
     * @param id API ID
     * @return 第三方API详情DTO
     */
    ResultDTO<ConnectorApiDetailDTO> detail(Long id);

    /**
     * 根据连接器API ID集合组装API详情集合
     *
     * @param connectorIdList 连接器API ID集合
     * @return 连接器API详情集合
     */
    List<ConnectorApiDetailDTO> queryByConnectorIds(List<Long> connectorIdList);
}
