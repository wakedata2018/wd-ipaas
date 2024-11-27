package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.connector.ConnectorEnvironmentAddressMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorParamsMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorSecretKeyMapper;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorParamsPo;
import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import com.wakedata.dw.open.model.query.ConnectorSecretKeyPageQuery;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.connector.ConnectorSecretKeyService;
import com.wakedata.dw.open.service.connector.dto.ConnectorSecretKeyDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
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
 * @author wujunqiang
 * @since 2022/11/21 10:32
 */
@Service
public class ConnectorSecretKeyServiceImpl extends BaseServiceImpl<ConnectorSecretKeyPo, ConnectorSecretKeyMapper>
        implements ConnectorSecretKeyService {

    @Autowired
    private OpenApiDataCache openApiDataCache;

    @Autowired
    private ConnectorMapper connectorMapper;

    @Autowired
    private ConnectorEnvironmentAddressMapper connectorEnvironmentAddressMapper;

    @Resource
    private ConnectorParamsMapper connectorParamsMapper;

    @Autowired
    @Override
    protected void init(CurdService<ConnectorSecretKeyPo> curdService, ConnectorSecretKeyMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<List<ConnectorSecretKeyDTO>> findByConnectorId(Long lesseeId, Long connectorId) {
        List<ConnectorSecretKeyPo> connectorSecretKeyPos = getMapper().selectByConnectorId(lesseeId, connectorId);
        List<ConnectorSecretKeyDTO> connectorSecretKeyDtoList = BeanUtil.copyList(connectorSecretKeyPos, ConnectorSecretKeyDTO.class);
        if (CollectionUtil.isEmpty(connectorSecretKeyDtoList)) {
            return ResultDTO.success(new ArrayList<>());
        }
        List<Long> environmentIds = connectorSecretKeyDtoList.stream().map(ConnectorSecretKeyDTO::getEnvironmentId).collect(Collectors.toList());
        Example example = new Example(ConnectorEnvironmentAddressPo.class);
        example.createCriteria().andIn("id", environmentIds);
        Map<Long, ConnectorEnvironmentAddressPo> environmentMap = connectorEnvironmentAddressMapper.selectByExample(example).stream()
                .collect(Collectors.toMap(ConnectorEnvironmentAddressPo::getId, x -> x));
        ConnectorPo connectorPo = connectorMapper.selectByPrimaryKey(connectorId);

        ConnectorParamsPo connectorParamsPo = new ConnectorParamsPo();
        connectorParamsPo.setConnectorId(connectorId);
        List<ConnectorParamsPo> paramsPoList = connectorParamsMapper.select(connectorParamsPo);
        for (ConnectorSecretKeyDTO dto : connectorSecretKeyDtoList) {
            Long environmentId = dto.getEnvironmentId();
            ConnectorEnvironmentAddressPo connectorEnvironmentAddressPo = environmentMap.get(environmentId);
            if(connectorEnvironmentAddressPo != null) {
                dto.setEnvironmentAddress(connectorEnvironmentAddressPo.getEnvironmentAddress());
                dto.setEnvironmentName(connectorEnvironmentAddressPo.getAddressName());
            }
            dto.setConnectName(connectorPo.getName());
            dto.setParamsJson(paramHidden(paramsPoList,dto.getParamsJson()));
        }
        return ResultDTO.success(connectorSecretKeyDtoList);
    }

    /**
     * 参数隐藏
     * @param paramsPoList
     * @param paramsJson
     * @return
     */
    private String paramHidden(List<ConnectorParamsPo> paramsPoList, String paramsJson) {
        if(CollectionUtil.isEmpty(paramsPoList)){
            return paramsJson;
        }
        JSONObject param = JSONObject.parseObject(paramsJson);
        for(ConnectorParamsPo paramsPo : paramsPoList){
            String value = (String) param.get(paramsPo.getParamName());
            if(StrUtil.isNotBlank(value) && paramsPo.getHiddenType() == CommonConstant.ONE){
                param.put(paramsPo.getParamName(),paramHidden(value));
            }
        }
        return JSONObject.toJSONString(param);
    }

    /**
     * 参数值隐藏
     * @param secretKey
     * @return
     */
    private String paramHidden(String secretKey) {
        int front = 4;
        int end = 4;
        int total = 10;
        if (StrUtil.isBlank(secretKey)) {
            return "";
        } else if(secretKey.length() > front && secretKey.length() < total){
            return StrUtil.hide(secretKey, 0, secretKey.length() - front) ;
        }else if (front + end > secretKey.length()) {
            return StrUtil.repeat('*', secretKey.length());
        } else {
            return StrUtil.hide(secretKey, front, secretKey.length() - end) ;
        }
    }

    @Override
    public ResultDTO<Long> create(ConnectorSecretKeyDTO connectorSecretKeyDTO) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        Long lesseeId = ipaasUserInfo.getLesseeId();

        Example example = new Example(ConnectorSecretKeyPo.class);
        example.createCriteria()
                .andEqualTo("name", connectorSecretKeyDTO.getName())
                .andEqualTo("lesseeId", lesseeId);
        if (getMapper().selectCountByExample(example) > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_name_already_exist);
        }

        ConnectorSecretKeyPo secretKeyPo = com.wakedata.common.core.util.BeanUtil.copy(connectorSecretKeyDTO, ConnectorSecretKeyPo.class);
        secretKeyPo.setLesseeId(lesseeId);
        Date now = new Date();
        secretKeyPo.setCreateTime(now);
        secretKeyPo.setUpdateTime(now);
        String userIdentification = ipaasUserInfo.getUserIdentification();
        secretKeyPo.setCreateBy(userIdentification);
        secretKeyPo.setUpdateBy(userIdentification);
        secretKeyPo.setSecretKey(IdUtil.simpleUUID());
        getMapper().insert(secretKeyPo);
        return ResultDTO.success(secretKeyPo.getId());
    }

    @Override
    public ResultDTO<Boolean> update(ConnectorSecretKeyDTO connectorSecretKeyDTO) {
        if (connectorSecretKeyDTO.getId() == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_id_cannot_be_null);
        }
        Example example = new Example(ConnectorSecretKeyPo.class);
        example.createCriteria()
                .andNotEqualTo("id", connectorSecretKeyDTO.getId())
                .andEqualTo("name", connectorSecretKeyDTO.getName())
                .andEqualTo("lesseeId", IpaasUserContext.getUserInfo().getLesseeId());
        if (getMapper().selectCountByExample(example) > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_name_already_exist);
        }

        ConnectorSecretKeyPo secretKeyPo = com.wakedata.common.core.util.BeanUtil.copy(connectorSecretKeyDTO, ConnectorSecretKeyPo.class);
        secretKeyPo.setUpdateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        secretKeyPo.setUpdateTime(new Date());
        getMapper().updateByPrimaryKeySelective(secretKeyPo);
        openApiDataCache.removeConnectorSecretKeyPo(secretKeyPo.getSecretKey());
        return ResultDTO.success(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> delete(Long id) {
        if (id == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_id_cannot_be_null);
        }
        ConnectorSecretKeyPo connectorSecretKeyPo = getMapper().selectByPrimaryKey(id);
        if (connectorSecretKeyPo == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_key_does_not_exist);
        }
        if (getMapper().deleteByPrimaryKey(id) < 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_secret_key_delete_fail);
        }
        openApiDataCache.removeConnectorSecretKeyPo(connectorSecretKeyPo.getSecretKey());
        return ResultDTO.success(Boolean.TRUE);
    }

    @Override
    public PageResultDTO<List<ConnectorSecretKeyDTO>> page(ConnectorSecretKeyPageQuery pageQuery) {
        PageResultDTO<List<ConnectorSecretKeyDTO>> pageResultDTO = new PageResultDTO<>(pageQuery.getPageSize(), pageQuery.getPageNo());
        pageQuery.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        PageInfo<ConnectorSecretKeyPo> pageInfo = new PageInfo<>(getMapper().pageQuery(pageQuery));
        List<ConnectorSecretKeyDTO> secretKeyDTOList = com.wakedata.common.core.util.BeanUtil.copyList(pageInfo.getList(), ConnectorSecretKeyDTO.class);
        if (CollectionUtil.isEmpty(secretKeyDTOList)) {
            pageResultDTO.setData(new ArrayList<>());
            pageResultDTO.setTotalCount(0);
            return pageResultDTO;
        }
        // 查询平台信息
        List<Long> connectorIds = secretKeyDTOList.stream().map(ConnectorSecretKeyDTO::getConnectorId).collect(Collectors.toList());
        Example connectorExample = new Example(ConnectorPo.class);
        connectorExample.createCriteria().andIn("id", connectorIds);
        List<ConnectorPo> connectorPos = connectorMapper.selectByExample(connectorExample);
        Map<Long, String> connectorMap = connectorPos.stream().collect(Collectors.toMap(ConnectorPo::getId, ConnectorPo::getName));
        // 查询环境信息
        List<Long> environmentIds = secretKeyDTOList.stream().map(ConnectorSecretKeyDTO::getEnvironmentId).collect(Collectors.toList());
        Example environmentExample = new Example(ConnectorEnvironmentAddressPo.class);
        environmentExample.createCriteria().andIn("id", environmentIds);
        List<ConnectorEnvironmentAddressPo> environmentAddressPos = connectorEnvironmentAddressMapper.selectByExample(environmentExample);
        Map<Long, String> environmentMap = environmentAddressPos.stream().collect(Collectors.toMap(ConnectorEnvironmentAddressPo::getId, ConnectorEnvironmentAddressPo::getAddressName));
        // 填充平台、环境信息
        for (ConnectorSecretKeyDTO secretKeyDTO : secretKeyDTOList) {
            secretKeyDTO.setConnectName(connectorMap.getOrDefault(secretKeyDTO.getConnectorId(), null));
            secretKeyDTO.setEnvironmentName(environmentMap.getOrDefault(secretKeyDTO.getEnvironmentId(), null));
        }
        pageResultDTO.setTotalCount(Math.toIntExact(pageInfo.getTotal()));
        pageResultDTO.setData(secretKeyDTOList);
        return pageResultDTO;
    }

}
