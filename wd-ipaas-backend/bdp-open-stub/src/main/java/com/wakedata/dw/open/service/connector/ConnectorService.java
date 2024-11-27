package com.wakedata.dw.open.service.connector;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.model.query.ConnectorPageQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 15:44
 */
public interface ConnectorService extends BaseDbService<ConnectorPo> {

    /**
     * 新建连接器平台
     *
     * @param connectorDTO 连接器平台DTO
     * @return Long 主键id
     */
    ResultDTO<Long> create(ConnectorDTO connectorDTO);

    /**
     * 更新连接器平台
     *
     * @param connectorDTO 连接器平台DTO
     * @return Long 主键id
     */
    ResultDTO<Boolean> modify(ConnectorDTO connectorDTO);

    /**
     * 删除连接器平台
     *
     * @param id 连接器平台id
     * @return Boolean 删除结果
     */
    ResultDTO<Boolean> delete(Long id);

    /**
     * 分页查询连接器平台
     *
     * @param connectorPageQuery 连接器平台查询条件
     * @return PageResultDTO<ConnectorDTO> 查询结果
     */
    PageResultDTO<List<ConnectorDTO>> query(ConnectorPageQuery connectorPageQuery);

    /**
     * 查询连接器平台id和name列表
     *
     * @return PageResultDTO<ConnectorDTO> 查询结果
     */
    ResultDTO<List<ConnectorDTO>> queryIdAndName();

    /**
     * 根据连接器ID集合批量查询连接器信息
     *
     * @param connectorIds 连接器ID集合
     * @return 连接器信息集合
     */
    List<ConnectorDTO> queryByIds(List<Long> connectorIds);

    /**
     * 校验平台名称是否重复
     *
     * @param connectorDTO 连接器DTO
     * @return true：校验通过、false：校验不通过
     */
    Boolean checkConnectorNameExist(ConnectorDTO connectorDTO);
}
