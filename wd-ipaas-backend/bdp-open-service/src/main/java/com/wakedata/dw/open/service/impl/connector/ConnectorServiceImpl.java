package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.constants.ActiveStatusEnum;
import com.wakedata.common.core.constants.DeleteFlagEnum;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.connector.*;
import com.wakedata.dw.open.model.connector.*;
import com.wakedata.dw.open.model.query.ConnectorPageQuery;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorEnvironmentAddressService;
import com.wakedata.dw.open.service.connector.ConnectorParamsService;
import com.wakedata.dw.open.service.connector.ConnectorService;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorEnvironmentAddressDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorParamsDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.UUIDUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 15:45
 */
@Service
public class ConnectorServiceImpl extends BaseServiceImpl<ConnectorPo, ConnectorMapper> implements ConnectorService {

    @Autowired
    private OpenApiDataCache openApiDataCache;

    @Resource
    private ConnectorEnvironmentAddressService connectorEnvironmentAddressService;

    @Resource
    private BatchCurdService<ConnectorEnvironmentAddressPo> environmentAddressPoBatchCurdService;

    @Resource
    private ConnectorEnvironmentAddressMapper connectorEnvironmentAddressMapper;

    @Resource
    private ConnectorParamsService connectorParamsService;

    @Resource
    private ConnectorParamsMapper connectorParamsMapper;

    @Resource
    private BatchCurdService<ConnectorParamsPo> connectorParamsPoBatchCurdService;

    @Resource
    private ConnectorGroupMapper connectorGroupMapper;

    @Resource
    private ConnectorApiMapper connectorApiMapper;

    @Resource
    private ConnectorAuthConfigMapper connectorAuthConfigMapper;

    @Autowired
    @Override
    protected void init(CurdService<ConnectorPo> curdService, ConnectorMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Long> create(ConnectorDTO connectorDTO) {
        //校验连接器平台的名称和唯一标识是否重复
        checkConnectorNameAndIdentification(connectorDTO);
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        ConnectorPo connectorPo = BeanUtil.copy(connectorDTO, ConnectorPo.class);
        Date date = new Date();
        connectorPo.setLesseeId(ipaasUserInfo.getLesseeId());
        connectorPo.setIsDelete(DeleteFlagEnum.ACTIVE.getValue());
        connectorPo.setCreateBy(ipaasUserInfo.getUserIdentification());
        connectorPo.setCreateTime(date);
        connectorPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
        connectorPo.setUpdateTime(date);
        getMapper().insertSelective(connectorPo);
        //批量插入平台环境地址
        batchInsertOfModifyConnectorEnvironmentAddress(connectorDTO.getConnectorEnvironmentAddressDTOList(), connectorPo.getId());
        //批量插入平台鉴权字段
        batchInsertOfModifyConnectorParams(connectorDTO.getConnectorParamsDTOList(), connectorPo.getId());
        //添加默认连接器鉴权配置
        insertAuthConfigPo(connectorPo);
        return ResultDTO.success(connectorPo.getId());
    }

    /**
     * 添加连接器鉴权配置
     * @param connectorPo
     */
    private void insertAuthConfigPo(ConnectorPo connectorPo) {
        ConnectorAuthConfigPo authConfig = new ConnectorAuthConfigPo();
        authConfig.setConnectorId(connectorPo.getId());
        authConfig.setAuthName(connectorPo.getName());
        authConfig.setAuthIdentification(UUIDUtils.generateSimpleUUID());
        authConfig.setLesseeId(connectorPo.getLesseeId());
        authConfig.setStatus(ActiveStatusEnum.INVALID.getValue());
        authConfig.setCreateTime(new Date());
        connectorAuthConfigMapper.insertSelective(authConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> modify(ConnectorDTO connectorDTO) {
        if (ObjectUtil.isEmpty(connectorDTO.getId())) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_id_cannot_be_null);
        }
        //校验连接器平台的名称和唯一标识是否重复
        checkConnectorNameAndIdentification(connectorDTO);
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        ConnectorPo connectorPo = BeanUtil.copy(connectorDTO, ConnectorPo.class);
        Date date = new Date();
        connectorPo.setUpdateBy(ipaasUserInfo.getUserIdentification());
        connectorPo.setUpdateTime(date);
        getMapper().updateByPrimaryKeySelective(connectorPo);
        //批量插入和更新平台环境地址
        batchInsertOfModifyConnectorEnvironmentAddress(connectorDTO.getConnectorEnvironmentAddressDTOList(), connectorPo.getId());
        batchInsertOfModifyConnectorParams(connectorDTO.getConnectorParamsDTOList(), connectorPo.getId());
        return ResultDTO.success(true);
    }

    @Override
    public ResultDTO<Boolean> delete(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_id_cannot_be_null);
        }
        ConnectorPo connectorPo = getMapper().selectByPrimaryKey(id);
        if (ObjectUtil.isEmpty(connectorPo)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_does_not_exist.getCode(),
                    String.format(OpenApiMsgCodeEnum.w_connector_does_not_exist.getDesc(), id));
        }
        connectorPo.setIsDelete(DeleteFlagEnum.DELETED.getValue());
        // 根据名称以及删除状态查询，如果数据库已经存在记录，唯一索引会导致更新状态失败，那就根据当前传入的id直接执行删除
        Example exampleConnector = new Example(ConnectorPo.class);
        exampleConnector.createCriteria()
                .andEqualTo("name", connectorPo.getName())
                .andEqualTo("isDelete", connectorPo.getIsDelete());
        if (getMapper().selectCountByExample(exampleConnector) > 0) {
            return ResultDTO.success(getMapper().deleteByPrimaryKey(id) > 0);
        }

        // 清除环境地址缓存
        Example example = new Example(ConnectorEnvironmentAddressPo.class);
        example.createCriteria().andEqualTo("connectorId", id);
        List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPos = connectorEnvironmentAddressMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(connectorEnvironmentAddressPos)) {
            connectorEnvironmentAddressPos.forEach(x -> openApiDataCache.removeConnectorEnvironmentAddressPo(x.getId()));
        }
        return ResultDTO.success(getMapper().updateByPrimaryKeySelective(connectorPo) > 0);
    }

    @Override
    public PageResultDTO<List<ConnectorDTO>> query(ConnectorPageQuery connectorPageQuery) {
        PageHelper.startPage(connectorPageQuery.getPageNo(), connectorPageQuery.getPageSize());
        PageInfo<ConnectorPo> connectorPoList = new PageInfo<>(getMapper().pageQueryConnector(connectorPageQuery));
        List<ConnectorDTO> connectorDTOList = BeanUtil.copyList(connectorPoList.getList(), ConnectorDTO.class);
        //过滤出连接器平台的id
        List<Long> ids = connectorDTOList.stream().map(ConnectorDTO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            PageResultDTO<List<ConnectorDTO>> pageResultDTO = new PageResultDTO<>(connectorPageQuery.getPageSize(), connectorPageQuery.getPageNo());
            pageResultDTO.setTotalCount(0);
            pageResultDTO.setData(new ArrayList<>());
            return pageResultDTO;
        }
        Example example = new Example(ConnectorEnvironmentAddressPo.class);
        example.createCriteria().andIn("connectorId", ids);
        //通过平台ids查询出环境地址list
        List<ConnectorEnvironmentAddressDTO> connectorEnvironmentAddressDTOList = BeanUtil.copyList(connectorEnvironmentAddressMapper.selectByExample(example), ConnectorEnvironmentAddressDTO.class);
        Map<Long, List<ConnectorEnvironmentAddressDTO>> addressDTOMap = connectorEnvironmentAddressDTOList.stream().collect(Collectors.groupingBy(ConnectorEnvironmentAddressDTO::getConnectorId));
        //通过平台ids查询出平台鉴权字段list
        List<ConnectorParamsDTO> connectorParamsDTOList = BeanUtil.copyList(connectorParamsMapper.selectByExample(example), ConnectorParamsDTO.class);
        Map<Long, List<ConnectorParamsDTO>> paramsDTOMap = connectorParamsDTOList.stream().collect(Collectors.groupingBy(ConnectorParamsDTO::getConnectorId));
        //通过平台ids查询出api数量，拼接api数量
        List<ConnectorApiPo> apiPoList = connectorApiMapper.selectByExample(example);
        Map<Long, List<ConnectorApiPo>> apiCountMap = apiPoList.stream().collect(Collectors.groupingBy(ConnectorApiPo::getConnectorId));
        //通过平台ids查询出平台分组名称，拼接分组名称
        example.clear();
        List<Long> groupIds = connectorDTOList.stream().distinct().map(ConnectorDTO::getGroupId).collect(Collectors.toList());
        example.createCriteria().andIn("id", groupIds);
        List<ConnectorGroupPo> connectorGroupPos = connectorGroupMapper.selectByExample(example);
        Map<Long, String> connectorGroupNameMap = connectorGroupPos.stream().collect(Collectors.toMap(ConnectorGroupPo::getId, ConnectorGroupPo::getGroupName));

        //将使用ids查询出来平台环境list、平台鉴权字段list、api数量、平台所属分类名称，合并到平台list中
        connectorDTOList.forEach(n -> {
            n.setConnectorEnvironmentAddressDTOList(new ArrayList<>());
            n.setConnectorParamsDTOList(new ArrayList<>());
            if (addressDTOMap.containsKey(n.getId())) {
                n.setConnectorEnvironmentAddressDTOList(addressDTOMap.get(n.getId()));
            }
            if (paramsDTOMap.containsKey(n.getId())) {
                n.setConnectorParamsDTOList(paramsDTOMap.get(n.getId()));
            }
            //if条件不通过，说明当前平台下没有api，apiCount默认赋值0
            if (apiCountMap.containsKey(n.getId())) {
                n.setApiCount(apiCountMap.get(n.getId()).size());
            } else {
                n.setApiCount(0);
            }
            if (connectorGroupNameMap.containsKey(n.getGroupId())) {
                n.setGroupName(connectorGroupNameMap.get(n.getGroupId()));
            }

        });
        PageResultDTO<List<ConnectorDTO>> connectorDTOPageResultDTO = new PageResultDTO<>(connectorPageQuery.getPageSize(), connectorPageQuery.getPageNo());
        connectorDTOPageResultDTO.setTotalCount(Math.toIntExact(connectorPoList.getTotal()));
        connectorDTOPageResultDTO.setData(connectorDTOList);
        return connectorDTOPageResultDTO;
    }

    @Override
    public ResultDTO<List<ConnectorDTO>> queryIdAndName() {
        Example example = new Example(ConnectorPo.class);
        example.createCriteria().andEqualTo("isDelete", DeleteFlagEnum.ACTIVE.getValue());
        List<ConnectorPo> connectorPoList = getMapper().selectByExample(example);
        List<ConnectorDTO> connectorDTOList = new ArrayList<>();
        connectorPoList.forEach(n -> {
            ConnectorDTO connectorDTO = new ConnectorDTO();
            connectorDTO.setId(n.getId());
            connectorDTO.setName(n.getName());
            connectorDTOList.add(connectorDTO);
        });
        return ResultDTO.success(connectorDTOList);
    }

    @Override
    public List<ConnectorDTO> queryByIds(List<Long> connectorIds) {
        if (CollectionUtil.isEmpty(connectorIds)) {
            return new ArrayList<>();
        }
        // 查询连接器基础信息
        Example connectorExample = new Example(ConnectorPo.class);
        connectorExample.createCriteria().andIn("id", connectorIds);
        List<ConnectorDTO> connectorDTOList = BeanUtil.copyList(getMapper().selectByExample(connectorExample), ConnectorDTO.class);
        // 查询连接器环境地址信息
        Map<Long, List<ConnectorEnvironmentAddressDTO>> environmentAddressMap = getEnvironmentAddressMap(connectorIds);
        // 查询连接器鉴权字段信息
        Map<Long, List<ConnectorParamsDTO>> connectorParamsMap = getConnectorParamsMap(connectorIds);
        // 组装连接器信息集合
        for (ConnectorDTO connectorDTO : connectorDTOList) {
            Long connectorId = connectorDTO.getId();
            connectorDTO.setConnectorEnvironmentAddressDTOList(environmentAddressMap.get(connectorId));
            connectorDTO.setConnectorParamsDTOList(connectorParamsMap.get(connectorId));
        }
        return connectorDTOList;
    }

    @Override
    public Boolean checkConnectorNameExist(ConnectorDTO connectorDTO) {
        try {
            checkConnectorNameAndIdentification(connectorDTO);
            return Boolean.TRUE;
        } catch (OpenException e) {
            return Boolean.FALSE;
        }
    }

    /**
     * 根据连接器id集合查询连接器环境地址信息Map
     *
     * @param connectorIds 连接器id集合
     * @return 连接器环境地址信息Map
     */
    private Map<Long, List<ConnectorEnvironmentAddressDTO>> getEnvironmentAddressMap(List<Long> connectorIds) {
        Example environmentExample = new Example(ConnectorEnvironmentAddressPo.class);
        environmentExample.createCriteria().andIn("connectorId", connectorIds);
        List<ConnectorEnvironmentAddressDTO> connectorEnvironmentAddressList = BeanUtil.copyList(connectorEnvironmentAddressMapper.selectByExample(environmentExample), ConnectorEnvironmentAddressDTO.class);
        return connectorEnvironmentAddressList.stream().collect(Collectors.groupingBy(ConnectorEnvironmentAddressDTO::getConnectorId));
    }

    /**
     * 根据连接器id集合查询连接器鉴权参数信息Map
     *
     * @param connectorIds 连接器id集合
     * @return 连接器鉴权参数信息Map
     */
    private Map<Long, List<ConnectorParamsDTO>> getConnectorParamsMap(List<Long> connectorIds) {
        Example connectorParamsExample = new Example(ConnectorParamsPo.class);
        connectorParamsExample.createCriteria().andIn("connectorId", connectorIds);
        List<ConnectorParamsDTO> connectorParamsList = BeanUtil.copyList(connectorParamsMapper.selectByExample(connectorParamsExample), ConnectorParamsDTO.class);
        return connectorParamsList.stream().collect(Collectors.groupingBy(ConnectorParamsDTO::getConnectorId));
    }

    /**
     * 批量插入平台环境地址
     *
     * @param connectorEnvironmentAddressDTOList 平台环境地址DTO列表
     * @param connectorId                        平台id
     */
    private void batchInsertOfModifyConnectorEnvironmentAddress(List<ConnectorEnvironmentAddressDTO> connectorEnvironmentAddressDTOList, Long connectorId) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        Date date = new Date();
        //环境名称 平台内不能重复
        if (CollectionUtils.isNotEmpty(connectorEnvironmentAddressDTOList)) {
            //判断是否有需要删除的数据，将数据库中存在但是更新信息中不存在的环境地址删除
            checkAndDeleteConnectorEnvironmentAddress(connectorEnvironmentAddressDTOList, connectorId);
            //校验传入的数据列表中是否有名称重复的数据
            List<String> connectorEnvironmentAddressNameList = connectorEnvironmentAddressDTOList.stream().map(ConnectorEnvironmentAddressDTO::getAddressName).collect(Collectors.toList());
            if ((connectorEnvironmentAddressNameList.stream().distinct().count()) < connectorEnvironmentAddressNameList.size()) {
                throw new OpenException(OpenApiMsgCodeEnum.w_connector_environment_address_name_already_repeat);
            }
            List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPos = BeanUtil.copyList(connectorEnvironmentAddressDTOList, ConnectorEnvironmentAddressPo.class);
            //判断connectorEnvironmentAddressPos中是否存在需要更新的数据，存在则将其过滤出来，批量更新
            if (connectorEnvironmentAddressPos.stream().anyMatch(n -> ObjectUtil.isNotEmpty(n.getId()))) {
                List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPoUpdateList = connectorEnvironmentAddressPos.stream().filter(n -> ObjectUtil.isNotEmpty(n.getId())).collect(Collectors.toList());
                connectorEnvironmentAddressPos.removeAll(connectorEnvironmentAddressPoUpdateList);
                if (CollectionUtils.isNotEmpty(connectorEnvironmentAddressPoUpdateList)) {
                    //执行批量更新
                    connectorEnvironmentAddressPoUpdateList.forEach(n -> {
                        n.setCreateBy(null);
                        n.setCreateTime(null);
                        n.setUpdateBy(ipaasUserInfo.getUserIdentification());
                        n.setUpdateTime(date);
                    });
                    environmentAddressPoBatchCurdService.updateListByPrimaryKeySelective(connectorEnvironmentAddressPoUpdateList, connectorEnvironmentAddressMapper);
                    //清除缓存
                    connectorEnvironmentAddressPoUpdateList.forEach(x -> openApiDataCache.removeConnectorEnvironmentAddressPo(x.getId()));
                }
            }
            if (CollectionUtil.isNotEmpty(connectorEnvironmentAddressPos)) {
                connectorEnvironmentAddressNameList = connectorEnvironmentAddressService.checkEnvironmentAddressName(connectorId, connectorEnvironmentAddressPos);
                if (CollectionUtils.isNotEmpty(connectorEnvironmentAddressNameList)) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_connector_connectorEnvironmentAddress_name_already_exist.getCode(),
                            String.format(OpenApiMsgCodeEnum.w_connector_connectorEnvironmentAddress_name_already_exist.getDesc(), connectorEnvironmentAddressNameList));
                }
                //执行批量插入
                connectorEnvironmentAddressPos.forEach(n -> {
                    n.setConnectorId(connectorId);
                    n.setLesseeId(ipaasUserInfo.getLesseeId());
                    n.setCreateBy(ipaasUserInfo.getUserIdentification());
                    n.setCreateTime(date);
                    n.setUpdateBy(ipaasUserInfo.getUserIdentification());
                    n.setUpdateTime(date);
                });
                environmentAddressPoBatchCurdService.insertList(connectorEnvironmentAddressPos, connectorEnvironmentAddressMapper);
            }
        }
    }

    /**
     * 批量插入平台鉴权字段
     *
     * @param connectorParamsDTOList 平台鉴权字段DTO列表
     * @param connectorId            平台id
     */
    private void batchInsertOfModifyConnectorParams(List<ConnectorParamsDTO> connectorParamsDTOList, Long connectorId) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        Date date = new Date();
        //鉴权字段 平台内不能重复
        if (CollectionUtils.isNotEmpty(connectorParamsDTOList)) {
            //判断是否有需要删除的数据，将数据库中存在但是更新信息中不存在的平台鉴权字段删除
            checkAndDeleteConnectorParams(connectorParamsDTOList, connectorId);
            //过滤出参数名称list，用于判断是否有重复
            List<String> connectorParamsNameList = connectorParamsDTOList.stream().map(ConnectorParamsDTO::getParamName).collect(Collectors.toList());
            if (connectorParamsNameList.stream().distinct().count() < connectorParamsNameList.size()) {
                throw new OpenException(OpenApiMsgCodeEnum.w_connector_params_name_already_repeat);
            }
            List<ConnectorParamsPo> connectorParamsPoList = BeanUtil.copyList(connectorParamsDTOList, ConnectorParamsPo.class);
            //判断connectorParamsDTOList中是否存在需要更新的数据，存在则将其过滤出来，批量更新
            if (connectorParamsDTOList.stream().anyMatch(n -> ObjectUtil.isNotEmpty(n.getId()))) {
                List<ConnectorParamsPo> connectorParamsPoUpdateList = connectorParamsPoList.stream().filter(n -> ObjectUtil.isNotEmpty(n.getId())).collect(Collectors.toList());
                connectorParamsPoList.removeAll(connectorParamsPoUpdateList);
                if (CollectionUtils.isNotEmpty(connectorParamsPoUpdateList)) {
                    //执行批量更新
                    connectorParamsPoUpdateList.forEach(n -> {
                        n.setCreateBy(null);
                        n.setCreateTime(null);
                        n.setUpdateBy(ipaasUserInfo.getUserIdentification());
                        n.setUpdateTime(date);
                    });
                    connectorParamsPoBatchCurdService.updateListByPrimaryKeySelective(connectorParamsPoUpdateList, connectorParamsMapper);
                }
            }
            connectorParamsPoList.forEach(n -> {
                n.setConnectorId(connectorId);
                n.setLesseeId(ipaasUserInfo.getLesseeId());
                n.setCreateBy(ipaasUserInfo.getUserIdentification());
                n.setCreateTime(date);
                n.setUpdateBy(ipaasUserInfo.getUserIdentification());
                n.setUpdateTime(date);
            });
            connectorParamsPoBatchCurdService.insertList(connectorParamsPoList, connectorParamsMapper);
        }

    }

    /**
     * 校验平台名称和平台唯一标识是否重复
     * 若是重复了直接抛出异常
     *
     * @param connectorDTO 平台信息DTO
     */
    private void checkConnectorNameAndIdentification(ConnectorDTO connectorDTO) {
        Example example = new Example(ConnectorPo.class);
        //平台名称 全局不能重复
        if (ObjectUtil.isEmpty(connectorDTO.getId())) {
            example.createCriteria()
                    .andEqualTo("name", connectorDTO.getName())
                    .andEqualTo("isDelete", DeleteFlagEnum.ACTIVE.getValue());
            if (getMapper().selectCountByExample(example) > 0) {
                throw new OpenException(OpenApiMsgCodeEnum.w_connector_name_already_exist);
            }
        } else if (ObjectUtil.isNotEmpty(connectorDTO.getId())) {
            //当主键id不为空时，说明是修改
            example.createCriteria()
                    .andNotEqualTo("id", connectorDTO.getId())
                    .andEqualTo("name", connectorDTO.getName())
                    .andEqualTo("isDelete", DeleteFlagEnum.ACTIVE.getValue());
            if (getMapper().selectCountByExample(example) > 0) {
                throw new OpenException(OpenApiMsgCodeEnum.w_connector_name_already_exist);
            }
        }


    }

    /**
     * 判断是否有需要删除的数据，将数据库中存在但是更新信息中不存在的环境地址删除
     *
     * @param connectorEnvironmentAddressDTOList 平台环境地址DTO列表
     * @param connectorId                        平台id
     */
    private void checkAndDeleteConnectorEnvironmentAddress(List<ConnectorEnvironmentAddressDTO> connectorEnvironmentAddressDTOList, Long connectorId) {
        Example example = new Example(ConnectorEnvironmentAddressPo.class);
        example.createCriteria().andEqualTo("connectorId", connectorId);
        List<ConnectorEnvironmentAddressPo> connectorEnvironmentAddressPoAllList = connectorEnvironmentAddressMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(connectorEnvironmentAddressPoAllList)) {
            //全查询得出的数据，根据id去除需要更新的数据
            List<Long> idList = connectorEnvironmentAddressDTOList.stream().map(ConnectorEnvironmentAddressDTO::getId).collect(Collectors.toList());
            connectorEnvironmentAddressPoAllList.removeIf(n -> idList.contains(n.getId()));
            //去除需要更新的数据后，剩下的就是需要删除的数据
            if (CollectionUtils.isNotEmpty(connectorEnvironmentAddressPoAllList)) {
                connectorEnvironmentAddressPoAllList.forEach(n -> connectorEnvironmentAddressMapper.deleteByPrimaryKey(n));
            }
        }
    }

    /**
     * 判断是否有需要删除的数据，将数据库中存在但是更新信息中不存在的平台鉴权字段删除
     *
     * @param connectorParamsDTOList 平台鉴权字段DTO列表
     * @param connectorId            平台id
     */
    private void checkAndDeleteConnectorParams(List<ConnectorParamsDTO> connectorParamsDTOList, Long connectorId) {
        Example example = new Example(ConnectorParamsPo.class);
        example.createCriteria().andEqualTo("connectorId", connectorId);
        List<ConnectorParamsPo> connectorParamsPoAllList = connectorParamsMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(connectorParamsPoAllList)) {
            //全查询得出的数据，根据id去除需要更新的数据
            List<Long> idList = connectorParamsDTOList.stream().map(ConnectorParamsDTO::getId).collect(Collectors.toList());
            connectorParamsPoAllList.removeIf(n -> idList.contains(n.getId()));
            //去除需要更新的数据后，剩下的就是需要删除的数据
            if (CollectionUtils.isNotEmpty(connectorParamsPoAllList)) {
                connectorParamsPoAllList.forEach(n -> connectorParamsMapper.deleteByPrimaryKey(n));
            }
        }
    }

}
