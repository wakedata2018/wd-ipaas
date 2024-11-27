package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.connector.*;
import com.wakedata.dw.open.model.connector.*;
import com.wakedata.dw.open.model.query.ConnectorApiPageQuery;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorApiService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDetailDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiRequestParamDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiResponseParamDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 16:37
 */
@Service
public class ConnectorApiServiceImpl extends BaseServiceImpl<ConnectorApiPo, ConnectorApiMapper> implements ConnectorApiService {

    @Resource
    private ConnectorApiRequestParamMapper connectorApiRequestParamMapper;

    @Resource
    private ConnectorApiResponseParamMapper connectorApiResponseParamMapper;

    @Resource
    private BatchCurdService<ConnectorApiRequestParamPo> requestBatchCurdService;

    @Resource
    private BatchCurdService<ConnectorApiResponseParamPo> responseBatchCurdService;

    @Resource
    private ConnectorApiGroupMapper connectorApiGroupMapper;

    @Resource
    private ConnectorMapper connectorMapper;


    @Autowired
    @Override
    protected void init(CurdService<ConnectorApiPo> curdService, ConnectorApiMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Long> create(ConnectorApiDetailDTO connectorApiDetailDTO) {
        ConnectorApiDTO connectorApi = connectorApiDetailDTO.getConnectorApi();
        Long lesseeId = connectorApi.getLesseeId();
        String createBy = connectorApi.getCreateBy();
        String updateBy = connectorApi.getUpdateBy();

        // 校验平台和接口分组是否存在
        this.checkExit(connectorApi, false);
        ConnectorApiPo connectorApiPo = BeanUtil.copy(connectorApi, ConnectorApiPo.class);
        getMapper().insertSelective(connectorApiPo);
        Long connectorApiId = connectorApiPo.getId();

        List<ConnectorApiRequestParamPo> connectorApiRequestParamPoList = convertRequestParams(connectorApiDetailDTO, lesseeId, createBy, updateBy, connectorApiId);
        requestBatchCurdService.insertList(connectorApiRequestParamPoList, connectorApiRequestParamMapper);

        List<ConnectorApiResponseParamPo> connectorApiResponseParamPoList = convertResponseParams(connectorApiDetailDTO, lesseeId, createBy, updateBy, connectorApiId);
        responseBatchCurdService.insertList(connectorApiResponseParamPoList, connectorApiResponseParamMapper);

        return ResultDTO.success(connectorApiPo.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Long> modify(ConnectorApiDetailDTO connectorApiDetailDTO) {
        ConnectorApiDTO connectorApi = connectorApiDetailDTO.getConnectorApi();
        Long lesseeId = connectorApi.getLesseeId();
        String createBy = connectorApi.getCreateBy();
        String updateBy = connectorApi.getUpdateBy();

        // 校验平台和接口分组是否存在
        this.checkExit(connectorApi, true);
        ConnectorApiPo connectorApiPo = BeanUtil.copy(connectorApi, ConnectorApiPo.class);
        super.update(connectorApiPo);
        Long connectorApiPoId = connectorApiPo.getId();

        Example requestParamExample = new Example(ConnectorApiRequestParamPo.class);
        requestParamExample.createCriteria().andEqualTo("connectorApiId", connectorApiPoId);
        connectorApiRequestParamMapper.deleteByExample(requestParamExample);
        List<ConnectorApiRequestParamPo> connectorApiRequestParamPoList = convertRequestParams(connectorApiDetailDTO, lesseeId, createBy, updateBy, connectorApi.getId());
        requestBatchCurdService.insertList(connectorApiRequestParamPoList, connectorApiRequestParamMapper);

        Example responseParamExample = new Example(ConnectorApiResponseParamPo.class);
        responseParamExample.createCriteria().andEqualTo("connectorApiId", connectorApiPoId);
        connectorApiResponseParamMapper.deleteByExample(responseParamExample);
        List<ConnectorApiResponseParamPo> connectorApiResponseParamPoList = convertResponseParams(connectorApiDetailDTO, lesseeId, createBy, updateBy, connectorApi.getId());
        responseBatchCurdService.insertList(connectorApiResponseParamPoList, connectorApiResponseParamMapper);

        return ResultDTO.success(connectorApiPoId);
    }

    @Override
    public ResultDTO<Boolean> delete(Long id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_id_cannot_be_null);
        }
        ConnectorApiPo connectorApiPo = getMapper().selectByPrimaryKey(id);
        if (ObjectUtil.isEmpty(connectorApiPo)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_not_exist);
        }
        //数据创建api默认上线，暂时没有状态管理的前端操作,暂时放开上线状态不能删除的限制
/*        if (DataAssetEnums.DataAccessPublishStatus.ON_LINE.equals(connectorApiPo.getEnableStatus())) {
            throw new OpenException(MsgCodeEnum.w_connector_api_is_published);
        }*/
        return ResultDTO.success(getMapper().deleteByPrimaryKey(id) > 0);
    }

    @Override
    public PageResultDTO<List<ConnectorApiDTO>> query(ConnectorApiPageQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<ConnectorApiPo> connectorApiPos = getMapper().query(query);
        PageResultDTO<List<ConnectorApiDTO>> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.setPageNo(query.getPageNo());
        pageResultDTO.setPageSize(query.getPageSize());
        if (CollectionUtil.isEmpty(connectorApiPos)) {
            pageResultDTO.setData(new ArrayList<>());
            pageResultDTO.setTotalCount(0L);
            return pageResultDTO;
        }
        //转换list类型
        PageInfo<ConnectorApiPo> pageInfo = new PageInfo<>(connectorApiPos);
        List<ConnectorApiDTO> connectorApiDTOList = BeanUtil.copyList(pageInfo.getList(), ConnectorApiDTO.class);
        //根据api列表，过滤出平台ids，获取平台名称转换成map
        List<Long> connectorIds = connectorApiPos.stream().distinct().map(ConnectorApiPo::getConnectorId).collect(Collectors.toList());
        Example example = new Example(ConnectorPo.class);
        example.createCriteria().andIn("id", connectorIds);
        List<ConnectorPo> connectorPoList = connectorMapper.selectByExample(example);
        Map<Long, String> connectorNameMap = connectorPoList.stream().collect(Collectors.toMap(ConnectorPo::getId, ConnectorPo::getName, (key1,key2) -> key2));
        //根据api列表，过滤出api分组ids，获取分组名称转换成map
        List<Long> apiGroupIds = connectorApiPos.stream().distinct().map(ConnectorApiPo::getApiGroupId).collect(Collectors.toList());
        Example connectorApiGroupExample = new Example(ConnectorApiGroupPo.class);
        connectorApiGroupExample.createCriteria().andIn("id", apiGroupIds);
        List<ConnectorApiGroupPo> connectorGroupPos = connectorApiGroupMapper.selectByExample(connectorApiGroupExample);
        Map<Long, String> connectorApiGroupNameMap = connectorGroupPos.stream().collect(Collectors.toMap(ConnectorApiGroupPo::getId, ConnectorApiGroupPo::getGroupName, (key1,key2) -> key2));
        //根据关联字段组装参数
        connectorApiDTOList.forEach(n->{
            if (connectorNameMap.containsKey(n.getConnectorId())) {
                n.setConnectorName(connectorNameMap.get(n.getConnectorId()));
            }
            if (connectorApiGroupNameMap.containsKey(n.getApiGroupId())) {
                n.setApiGroupName(connectorApiGroupNameMap.get(n.getApiGroupId()));
            }
        });
        pageResultDTO.setData(connectorApiDTOList);
        pageResultDTO.setTotalCount(pageInfo.getTotal());
        return pageResultDTO;
    }

    @Override
    public ResultDTO<ConnectorApiDetailDTO> detail(Long id) {
        ConnectorApiPo connectorApiPo = getMapper().selectByPrimaryKey(id);
        ConnectorApiDTO connectorApi = BeanUtil.copy(connectorApiPo, ConnectorApiDTO.class);
        Example requestParamExample = new Example(ConnectorApiRequestParamPo.class);
        requestParamExample.createCriteria().andEqualTo("connectorApiId", id);
        List<ConnectorApiRequestParamDTO> requestParamDTOList = BeanUtil.copyList(connectorApiRequestParamMapper.selectByExample(requestParamExample), ConnectorApiRequestParamDTO.class);
        Example responseParamExample = new Example(ConnectorApiResponseParamPo.class);
        responseParamExample.createCriteria().andEqualTo("connectorApiId", id);
        List<ConnectorApiResponseParamDTO> responseParamDTOList = BeanUtil.copyList(connectorApiResponseParamMapper.selectByExample(requestParamExample), ConnectorApiResponseParamDTO.class);
        ConnectorPo connectorPo = connectorMapper.selectByPrimaryKey(connectorApiPo.getConnectorId());
        if (connectorPo == null) {
            throw new OpenException(MsgCodeEnum.w_connector_is_not_exit);
        }
        connectorApi.setConnectorName(connectorPo.getName());
        ConnectorApiGroupPo connectorApiGroupPo = connectorApiGroupMapper.selectByPrimaryKey(connectorApiPo.getApiGroupId());
        if (connectorApiGroupPo == null) {
            throw new OpenException(MsgCodeEnum.w_connector_api_group_not_exist);
        }
        connectorApi.setApiGroupName(connectorApiGroupPo.getGroupName());
        ConnectorApiAuthTypeEnum apiAuthTypeEnum = ConnectorApiAuthTypeEnum.of(connectorPo.getAuthType());
        ConnectorApiDetailDTO apiDetailDTO = new ConnectorApiDetailDTO(connectorApi, requestParamDTOList, responseParamDTOList, apiAuthTypeEnum);
        return ResultDTO.success(apiDetailDTO);
    }

    @Override
    public List<ConnectorApiDetailDTO> queryByConnectorIds(List<Long> connectorIdList) {
        List<ConnectorApiDetailDTO> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(connectorIdList)) {
            return result;
        }
        // 查询API信息
        Example apiExample = new Example(ConnectorApiPo.class);
        apiExample.createCriteria().andIn("connectorId", connectorIdList);
        List<ConnectorApiPo> connectorApiList = getMapper().selectByExample(apiExample);
        if (CollectionUtil.isEmpty(connectorApiList)) {
            return result;
        }
        List<ConnectorApiDTO> connectorApiDtoList = BeanUtil.copyList(connectorApiList, ConnectorApiDTO.class);
        List<Long> connectorApiIds = connectorApiDtoList.stream().map(ConnectorApiDTO::getId).collect(Collectors.toList());
        // 查询API请求参数
        Example apiRequestParamExample = new Example(ConnectorApiRequestParamPo.class);
        apiRequestParamExample.createCriteria().andIn("connectorApiId", connectorApiIds);
        List<ConnectorApiRequestParamDTO> requestParamDtoList = BeanUtil.copyList(connectorApiRequestParamMapper.selectByExample(apiRequestParamExample), ConnectorApiRequestParamDTO.class);
        Map<Long, List<ConnectorApiRequestParamDTO>> requestParamMap = requestParamDtoList.stream().collect(Collectors.groupingBy(ConnectorApiRequestParamDTO::getConnectorApiId));
        // 查询API响应参数
        Example apiResponseParamExample = new Example(ConnectorApiResponseParamPo.class);
        apiResponseParamExample.createCriteria().andIn("connectorApiId", connectorApiIds);
        List<ConnectorApiResponseParamDTO> responseParamDtoList = BeanUtil.copyList(connectorApiResponseParamMapper.selectByExample(apiResponseParamExample), ConnectorApiResponseParamDTO.class);
        Map<Long, List<ConnectorApiResponseParamDTO>> responseParamMap = responseParamDtoList.stream().collect(Collectors.groupingBy(ConnectorApiResponseParamDTO::getConnectorApiId));
        // 组装连接器API参数集合
        for (ConnectorApiDTO connectorApiDTO : connectorApiDtoList) {
            Long apiId = connectorApiDTO.getId();
            List<ConnectorApiRequestParamDTO> requestParamList = requestParamMap.get(apiId);
            List<ConnectorApiResponseParamDTO> responseParamList = responseParamMap.get(apiId);
            result.add(new ConnectorApiDetailDTO(connectorApiDTO, requestParamList, responseParamList, null));
        }
        return result;
    }

    /**
     * 校验目标是否存在(没有租户隔离)
     */
    private void checkExit(ConnectorApiDTO connectorApiDTO, boolean update) {

        // 接口名称 接口路径不能重复
        Example example = new Example(ConnectorApiPo.class);
        example.createCriteria()
                .andEqualTo("apiName", connectorApiDTO.getApiName())
                .andEqualTo("connectorId", connectorApiDTO.getConnectorId());
        List<ConnectorApiPo> apiNameList = getMapper().selectByExample(example);
        List<ConnectorApiPo> checkAddAndUpdateApiName =
                update ? apiNameList.stream().filter(connectorApiPo -> ObjectUtil.notEqual(connectorApiPo.getId(), connectorApiDTO.getId())).collect(Collectors.toList()) : apiNameList;
        if (CollectionUtil.isNotEmpty(checkAddAndUpdateApiName)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_name_already_existed);
        }

        example = new Example(ConnectorApiPo.class);
        example.createCriteria()
                .andEqualTo("apiMethod", connectorApiDTO.getApiMethod())
                .andEqualTo("connectorId", connectorApiDTO.getConnectorId());
        List<ConnectorApiPo> apiMethodList = getMapper().selectByExample(example);
        List<ConnectorApiPo> checkAddAndUpdateApiMethod =
                update ? apiMethodList.stream().filter(connectorApiPo -> ObjectUtil.notEqual(connectorApiPo.getId(), connectorApiDTO.getId())).collect(Collectors.toList()) : apiMethodList;
        if (CollectionUtil.isNotEmpty(checkAddAndUpdateApiMethod)) {
            throw new OpenException(MsgCodeEnum.w_connector_api_method_already_existed);
        }

        ConnectorPo connectorPo = connectorMapper.selectByPrimaryKey(connectorApiDTO.getConnectorId());
        if (Objects.isNull(connectorPo)) {
            throw new OpenException(MsgCodeEnum.w_connector_is_not_exit);
        }
        ConnectorApiGroupPo connectorApiGroupPo = connectorApiGroupMapper.selectByPrimaryKey(connectorApiDTO.getApiGroupId());
        if (Objects.isNull(connectorApiGroupPo)) {
            throw new OpenException(MsgCodeEnum.w_connector_group_is_not_exit);
        }
    }


    /**
     * 解析并补充响应参数
     */
    private List<ConnectorApiResponseParamPo> convertResponseParams(ConnectorApiDetailDTO connectorApiDetailDTO, Long lesseeId, String createBy, String updateBy, Long connectorApiId) {
        List<ConnectorApiResponseParamDTO> connectorApiResponseParamDTOList = connectorApiDetailDTO.getResponseParams();
        for (ConnectorApiResponseParamDTO connectorApiResponseParamDTO : connectorApiResponseParamDTOList) {
            connectorApiResponseParamDTO.setConnectorApiId(connectorApiId);
            connectorApiResponseParamDTO.setLesseeId(lesseeId);
            connectorApiResponseParamDTO.setCreateBy(createBy);
            connectorApiResponseParamDTO.setUpdateBy(updateBy);
        }
        return BeanUtil.copyList(connectorApiDetailDTO.getResponseParams(), ConnectorApiResponseParamPo.class);
    }

    /**
     * 解析并补充请求参数
     */
    private List<ConnectorApiRequestParamPo> convertRequestParams(ConnectorApiDetailDTO connectorApiDetailDTO, Long lesseeId, String createBy, String updateBy, Long connectorApiId) {
        List<ConnectorApiRequestParamDTO> connectorApiRequestParamDTOList = connectorApiDetailDTO.getRequestParams();
        for (ConnectorApiRequestParamDTO connectorApiRequestParamDTO : connectorApiRequestParamDTOList) {
            connectorApiRequestParamDTO.setConnectorApiId(connectorApiId);
            connectorApiRequestParamDTO.setLesseeId(lesseeId);
            connectorApiRequestParamDTO.setCreateBy(createBy);
            connectorApiRequestParamDTO.setUpdateBy(updateBy);
        }
        return BeanUtil.copyList(connectorApiRequestParamDTOList, ConnectorApiRequestParamPo.class);
    }
}
