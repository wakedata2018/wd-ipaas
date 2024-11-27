package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.constants.DeleteFlagEnum;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.connector.ConnectorGroupMapper;
import com.wakedata.dw.open.model.connector.ConnectorGroupPo;
import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorGroupService;
import com.wakedata.dw.open.service.connector.ConnectorService;
import com.wakedata.dw.open.service.connector.dto.ConnectorGroupDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.ColumnTypeConvertUtil;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 14:09
 */
@Service
public class ConnectorGroupServiceImpl extends BaseServiceImpl<ConnectorGroupPo, ConnectorGroupMapper> implements ConnectorGroupService {

    private final Integer SORT_FIELD_CONSTANT = 1;

    @Resource
    private ConnectorService connectorService;

    @Autowired
    @Override
    protected void init(CurdService<ConnectorGroupPo> curdService, ConnectorGroupMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<Long> addOfModifyConnectorGroup(ConnectorGroupDTO connectorGroupDTO) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        ConnectorGroupPo connectorGroupPo = new ConnectorGroupPo();
        connectorGroupPo.setGroupName(connectorGroupDTO.getGroupName());
        Date date = new Date();
        //存在主键id就更新
        if (ObjectUtil.isNotEmpty(connectorGroupDTO.getId())) {
            connectorGroupPo = BeanUtil.copy(connectorGroupDTO, ConnectorGroupPo.class);
            connectorGroupPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
            connectorGroupPo.setUpdateTime(date);
            return ResultDTO.success((long)getMapper().updateByPrimaryKeySelective(connectorGroupPo));
        }
        //目前不做租户隔离，平台分类名称不能重复
        if (getMapper().selectCount(connectorGroupPo) > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_group_name_duplicate);
        }
        connectorGroupPo = BeanUtil.copy(connectorGroupDTO, ConnectorGroupPo.class);
        connectorGroupPo.setLesseeId(ipaasUserInfo.getLesseeId());
        connectorGroupPo.setCreateBy(ipaasUserInfo.getUserIdentification());
        connectorGroupPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
        connectorGroupPo.setCreateTime(date);
        connectorGroupPo.setUpdateTime(date);
        //查询当前parentId下最大的排序字段值，不为空就加1，若是空则使用常量1
        connectorGroupPo.setSortField(SORT_FIELD_CONSTANT);
        Integer sortFieldMax = getMapper().queryParentIdMax(connectorGroupPo.getParentId());
        if (ObjectUtil.isNotEmpty(sortFieldMax)) {
            connectorGroupPo.setSortField(sortFieldMax + SORT_FIELD_CONSTANT);
        }
        return ResultDTO.success((long)getMapper().insert(connectorGroupPo));
    }

    @Override
    public ResultDTO<Boolean> deleteConnectorGroup(Long id) {
        ConnectorGroupPo connectorGroupPo = new ConnectorGroupPo();
        connectorGroupPo.setParentId(id);
        //校验是否有子分类
        if(getMapper().selectCount(connectorGroupPo) > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_group_have_children_group);
        }
        ConnectorPo connectorPo = new ConnectorPo();
        connectorPo.setGroupId(id);
        connectorPo.setIsDelete(DeleteFlagEnum.ACTIVE.getValue());
        //校验是否已经有连接器平台关联
        if (connectorService.find(connectorPo).size() > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_group_already_connector_use);
        }
        return ResultDTO.success(getMapper().deleteByPrimaryKey(id) > 0);
    }

    @Override
    public ResultDTO<List<ConnectorGroupDTO>> queryConnectorGroup(ConnectorGroupDTO connectorGroupDTO) {
        List<ConnectorGroupDTO> connectorGroupDTOList = new ArrayList<>();
        //如果主键id不为空，则根据主键id返回单个分类的信息
        if (ObjectUtil.isNotEmpty(connectorGroupDTO.getId())) {
            connectorGroupDTO = BeanUtil.copy(getMapper().selectByPrimaryKey(connectorGroupDTO.getId()), ConnectorGroupDTO.class);
            Example example = new Example(ConnectorGroupPo.class);
            example.createCriteria().andEqualTo("id", connectorGroupDTO.getParentId());
            ConnectorGroupPo connectorGroupPo = getMapper().selectOneByExample(example);
            if (ObjectUtil.isNotEmpty(connectorGroupPo)) {
                connectorGroupDTO.setParentName(connectorGroupPo.getGroupName());
            }
            connectorGroupDTOList.add(connectorGroupDTO);
            return ResultDTO.success(connectorGroupDTOList);
        }
        List<ConnectorGroupPo> connectorGroupPos = getMapper().getAllConnectorWithChildrenByParentId(connectorGroupDTO.getParentId());
        connectorGroupDTOList = ColumnTypeConvertUtil.copyToList(connectorGroupPos, ConnectorGroupDTO.class);
        //使用排序字段排序
        sort(connectorGroupDTOList);
        return ResultDTO.success(connectorGroupDTOList);
    }

    @Override
    public ConnectorGroupDTO checkForExist(Long groupId) {
        ConnectorGroupPo connectorGroupPo = getMapper().selectByPrimaryKey(groupId);
        if (connectorGroupPo == null) {
            throw new OpenException(MsgCodeEnum.w_connector_is_not_exit);
        }
        return BeanUtil.copy(connectorGroupPo, ConnectorGroupDTO.class);
    }

    private void sort(List<ConnectorGroupDTO> connectorGroupDTOList) {
        connectorGroupDTOList.sort(Comparator.comparing(ConnectorGroupDTO::getSortField));
        connectorGroupDTOList.forEach(n->{
            if (CollectionUtils.isNotEmpty(n.getChildren())) {
                sort(n.getChildren());
            }
        });
    }
}
