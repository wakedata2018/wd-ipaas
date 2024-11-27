package com.wakedata.dw.open.service.impl.connector;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.constants.ActiveStatusEnum;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorAuthConfigMapper;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.connector.ConnectorAuthConfigPo;
import com.wakedata.dw.open.model.connector.ConnectorPo;
import com.wakedata.dw.open.model.query.ConnectorAuthCofigPageQuery;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.connector.ConnectorAuthConfigService;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthConfigDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthParamDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接器鉴权配置实现
 * @author luomeng
 * @date 2022/11/22 15:24
 */
@Service
public class ConnectorAuthConfigServiceImpl extends BaseServiceImpl<ConnectorAuthConfigPo, ConnectorAuthConfigMapper> implements ConnectorAuthConfigService {

    @Resource
    private AppAccessMapper appAccessMapper;


    @Autowired
    @Override
    protected void init(CurdService<ConnectorAuthConfigPo> curdService, ConnectorAuthConfigMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ConnectorAuthConfigDTO getConnectorAuthConfigById(Long connectorAuthId) {
        ConnectorAuthConfigPo authConfigPo = this.getMapper().selectDetail(connectorAuthId);
        if(ObjectUtil.isNull(authConfigPo)){
            return null;
        }
        ConnectorAuthConfigDTO authConfigDTO = BeanUtil.copy(authConfigPo, ConnectorAuthConfigDTO.class);
        authConfigDTO.setAuthConfigParam(JSONUtil.toBean(authConfigPo.getAuthConfig(), ConnectorAuthParamDTO.class));
        return authConfigDTO;
    }

    private void checkAuthConfigIdentification(String identification,Long id){
        if(StrUtil.isBlank(identification)){
            return;
        }
        Example example = new Example(ConnectorAuthConfigPo.class);
        example.createCriteria().andEqualTo("authIdentification",identification).andNotEqualTo("id",id);
        int count = this.getMapper().selectCountByExample(example);
        if(count > 0){
            throw new OpenException(MsgCodeEnum.w_connector_auth_identification_is_repeat);
        }
    }


    @Override
    public ResultDTO<Long> create(ConnectorAuthConfigDTO authConfigDTO) {
        checkAuthConfigIdentification(authConfigDTO.getAuthIdentification(),0L);
        ConnectorAuthConfigPo authConfig = BeanUtil.copy(authConfigDTO, ConnectorAuthConfigPo.class);
        authConfig.setAuthConfig(JSONUtil.toJsonStr(authConfigDTO.getAuthConfigParam()));
        authConfig.setCreateTime(new Date());
        authConfig.setUpdateTime(new Date());
        this.getCurdService().insert(authConfig,this.getMapper());
        return ResultDTO.success(authConfig.getId());
    }

    @Override
    public ResultDTO<Boolean> modify(ConnectorAuthConfigDTO authConfigDTO) {
        checkAuthConfigIdentification(authConfigDTO.getAuthIdentification(),authConfigDTO.getId());
        ConnectorAuthConfigPo authConfig = BeanUtil.copy(authConfigDTO, ConnectorAuthConfigPo.class);
        authConfig.setAuthConfig(JSONUtil.toJsonStr(authConfigDTO.getAuthConfigParam()));
        authConfig.setUpdateTime(new Date());
        return ResultDTO.success(this.getCurdService().updateByPrimaryKeySelective(authConfig,this.getMapper()) > 0);
    }

    @Override
    public ResultDTO<Boolean> deleteAuthConfig(Long lesseeId,Long id) {
        //已关联应用不允许删除
        Example example = new Example(AppAccessPo.class);
        example.createCriteria().andEqualTo("lesseeId",lesseeId).andEqualTo("connectorAuthId",id);
        int count = appAccessMapper.selectCountByExample(example);
        if(count > 0){
            throw new OpenException(MsgCodeEnum.w_connector_auth_config_is_relate);
        }
        return ResultDTO.success(this.getMapper().deleteByPrimaryKey(id) > 0);
    }

    @Override
    public PageResultDTO<List<ConnectorAuthConfigDTO>> query(ConnectorAuthCofigPageQuery connectorPageQuery) {
        PageHelper.startPage(connectorPageQuery.getPageNo(), connectorPageQuery.getPageSize());
        PageInfo<ConnectorAuthConfigPo> connectorPoList = new PageInfo<>(this.getMapper().selectList(connectorPageQuery));
        List<ConnectorAuthConfigDTO> connectorDTOList = connectorPoList.getList().stream().map(x->{
            ConnectorAuthConfigDTO authConfigDTO = BeanUtil.copy(x, ConnectorAuthConfigDTO.class);
            authConfigDTO.setAuthConfigParam(JSONUtil.toBean(x.getAuthConfig(), ConnectorAuthParamDTO.class));
            return authConfigDTO;
        }).collect(Collectors.toList());

        PageResultDTO<List<ConnectorAuthConfigDTO>> resultDTO = new PageResultDTO<>(connectorPageQuery.getPageSize(), connectorPageQuery.getPageNo());
        resultDTO.setTotalCount(Math.toIntExact(connectorPoList.getTotal()));
        resultDTO.setData(connectorDTOList);
        return resultDTO;
    }

    @Override
    public List<ConnectorAuthConfigDTO> getAllAuthConfig(Long lesseeId) {
        ConnectorAuthCofigPageQuery configPo = new ConnectorAuthCofigPageQuery();
        configPo.setLesseeId(lesseeId);
        configPo.setStatus(ActiveStatusEnum.ACTIVE.getValue());
        return BeanUtil.copyList(this.getMapper().selectList(configPo), ConnectorAuthConfigDTO.class);
    }
}
