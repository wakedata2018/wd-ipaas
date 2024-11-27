package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.constants.DeleteFlagEnum;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.connector.ConnectorApiGroupMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorApiMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorMapper;
import com.wakedata.dw.open.model.connector.ConnectorApiGroupPo;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorApiGroupService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupRelevanceDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:12
 */
@Service
public class ConnectorApiGroupServiceImpl extends BaseServiceImpl<ConnectorApiGroupPo, ConnectorApiGroupMapper> implements ConnectorApiGroupService {

    @Resource
    private ConnectorApiMapper connectorApiMapper;

    @Resource
    private ConnectorMapper connectorMapper;

    @Autowired
    @Override
    protected void init(CurdService<ConnectorApiGroupPo> curdService, ConnectorApiGroupMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<Long> createOfModify(ConnectorApiGroupDTO connectorApiGroupDTO) {
        Example example = new Example(ConnectorApiGroupPo.class);
        example.createCriteria()
                .andEqualTo("connectorId", connectorApiGroupDTO.getConnectorId())
                .andEqualTo("groupName", connectorApiGroupDTO.getGroupName());
        if (getMapper().selectCountByExample(example) > 0) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_name_already_existed);
        }
        ConnectorApiGroupPo connectorApiGroupPo = BeanUtil.copy(connectorApiGroupDTO, ConnectorApiGroupPo.class);
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        Date date = new Date();
        if (ObjectUtil.isNotEmpty(connectorApiGroupDTO.getId())) {
            connectorApiGroupPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
            connectorApiGroupPo.setUpdateTime(date);
            super.update(connectorApiGroupPo);
            return ResultDTO.success(connectorApiGroupPo.getId());
        }
        connectorApiGroupPo.setLesseeId(ipaasUserInfo.getLesseeId());
        connectorApiGroupPo.setCreateBy(ipaasUserInfo.getUserIdentification());
        connectorApiGroupPo.setCreateTime(date);
        connectorApiGroupPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
        connectorApiGroupPo.setUpdateTime(date);
        getMapper().insert(connectorApiGroupPo);
        return ResultDTO.success(connectorApiGroupPo.getId());
    }

    @Override
    public ResultDTO<Boolean> delete(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_id_cannot_be_null);
        }
        Example example = new Example(ConnectorApiPo.class);
        example.createCriteria().andEqualTo("apiGroupId", id);
        if (connectorApiMapper.selectCountByExample(example) > 0) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_is_already_associated_with_api);
        }
        return ResultDTO.success(getMapper().deleteByPrimaryKey(id) > 0);
    }

    @Override
    public ResultDTO<ConnectorApiGroupDTO> queryById(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_id_cannot_be_null);
        }
        Example exampleApiGroup = new Example(ConnectorApiGroupPo.class);
        exampleApiGroup.createCriteria().andEqualTo("id", id);
        ConnectorApiGroupDTO connectorApiGroupDTO = BeanUtil.copy(getMapper().selectOneByExample(exampleApiGroup), ConnectorApiGroupDTO.class);
        if (ObjectUtil.isEmpty(connectorApiGroupDTO)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_not_exist);
        }
        Example exampleApi = new Example(ConnectorApiPo.class);
        exampleApi.createCriteria().andEqualTo("apiGroupId", connectorApiGroupDTO.getId());
        List<ConnectorApiDTO> connectorApiList = BeanUtil.copyList(connectorApiMapper.selectByExample(exampleApi), ConnectorApiDTO.class);
        // 为每一个API填充鉴权类型
        List<Long> connectorIds = connectorApiList.stream().map(ConnectorApiDTO::getConnectorId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(connectorIds)) {
            Example connectorExample = new Example(ConnectorPo.class);
            connectorExample.createCriteria().andIn("id", connectorIds);
            Map<Long, String> connectorMap = connectorMapper.selectByExample(connectorExample).stream().collect(Collectors.toMap(ConnectorPo::getId, ConnectorPo::getAuthType));
            for (ConnectorApiDTO connectorApi : connectorApiList) {
                String authTypeStr = connectorMap.getOrDefault(connectorApi.getConnectorId(), null);
                if (StringUtils.isNotBlank(authTypeStr)) {
                    connectorApi.setConnectorApiAuthType(ConnectorApiAuthTypeEnum.of(authTypeStr));
                }
            }
        }
        connectorApiGroupDTO.setApiDTOList(connectorApiList);
        return ResultDTO.success(connectorApiGroupDTO);
    }

    @Override
    public ResultDTO<List<ConnectorApiGroupRelevanceDTO>> queryByConnectorId(Long connectorId) {
        //全查询得出平台list。过滤出ids
        Example example = new Example(ConnectorPo.class);
        example.createCriteria().andEqualTo("isDelete", DeleteFlagEnum.ACTIVE.getValue());
        if (ObjectUtil.isNotEmpty(connectorId)) {
            example.clear();
            example.createCriteria()
                    .andEqualTo("isDelete", DeleteFlagEnum.ACTIVE.getValue())
                    .andEqualTo("id", connectorId);
        }
        List<ConnectorPo> connectorPoList = connectorMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(connectorPoList)) {
            return ResultDTO.success(new ArrayList<>());
        }
        List<Long> connectorIds = connectorPoList.stream().map(ConnectorPo::getId).collect(Collectors.toList());
        //根据平台ids查询连接器平台api分组list
        Example apiGroupPoExample = new Example(ConnectorApiGroupPo.class);
        apiGroupPoExample.createCriteria().andIn("connectorId", connectorIds);
        List<ConnectorApiGroupDTO> connectorApiGroupDTOList = BeanUtil.copyList(getMapper().selectByExample(apiGroupPoExample), ConnectorApiGroupDTO.class);
        //按照connectorId分组转换成map
        Map<Long, List<ConnectorApiGroupDTO>> map = connectorApiGroupDTOList.stream().collect(Collectors.groupingBy(ConnectorApiGroupDTO::getConnectorId));
        List<ConnectorApiGroupRelevanceDTO> connectorApiGroupRelevanceList = new ArrayList<>();
        connectorPoList.forEach(n -> {
            ConnectorApiGroupRelevanceDTO connectorApiGroupRelevanceDTO = new ConnectorApiGroupRelevanceDTO();
            connectorApiGroupRelevanceDTO.setConnectorId(n.getId());
            connectorApiGroupRelevanceDTO.setConnectorName(n.getName());
            if (map.containsKey(connectorApiGroupRelevanceDTO.getConnectorId())) {
                connectorApiGroupRelevanceDTO.setConnectorApiGroupDTOList(map.get(connectorApiGroupRelevanceDTO.getConnectorId()));
            }
            connectorApiGroupRelevanceList.add(connectorApiGroupRelevanceDTO);
        });
        return ResultDTO.success(connectorApiGroupRelevanceList);
    }

    @Override
    public List<ConnectorApiGroupDTO> queryByConnectorIds(List<Long> connectorIds) {
        if (CollectionUtil.isEmpty(connectorIds)) {
            return new ArrayList<>();
        }
        Example apiGroupPoExample = new Example(ConnectorApiGroupPo.class);
        apiGroupPoExample.createCriteria().andIn("connectorId", connectorIds);
        return BeanUtil.copyList(getMapper().selectByExample(apiGroupPoExample), ConnectorApiGroupDTO.class);
    }
}
