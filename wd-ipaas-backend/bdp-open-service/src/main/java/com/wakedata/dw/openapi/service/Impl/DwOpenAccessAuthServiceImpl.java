package com.wakedata.dw.openapi.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.openapi.DwOpenAccessAuthMapper;
import com.wakedata.dw.open.model.openapi.DwOpenAccessAuthPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;
import com.wakedata.dw.openapi.service.DwOpenAccessAuthService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 应用下授权绑定的关联信息-服务接口
 * @author 佟蕊
 */
@Service
public class DwOpenAccessAuthServiceImpl extends
        BaseServiceImpl<DwOpenAccessAuthPo, DwOpenAccessAuthMapper> implements DwOpenAccessAuthService {

    @Autowired
    @Override
    protected void init(CurdService<DwOpenAccessAuthPo> curdService, DwOpenAccessAuthMapper mapper) {
            super.set(curdService, mapper);
        }

    @Override
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getWdAuthAppInfo_' + #dwOpenAccessAuthPo.lesseeId+'_'+#dwOpenAccessAuthPo.dataAccessAppId")
    public Integer updateByDataAccessAppId(DwOpenAccessAuthPo dwOpenAccessAuthPo) {
        Example example = new Example(DwOpenAccessAuthPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("dataAccessAppId",dwOpenAccessAuthPo.getDataAccessAppId());
        return getMapper().updateByExampleSelective(dwOpenAccessAuthPo,example);
    }

    @Override
    public ResultDTO<String> getAuthConfigByAppId(Integer appId) {
        ResultDTO<String> resultDTO = new ResultDTO<>();
        Long lessessId = AuthUtils.currentAppId().longValue();
        DwOpenAccessAuthPo dwOpenAccessAuthPo = new DwOpenAccessAuthPo();
        dwOpenAccessAuthPo.setDataAccessAppId(appId);
        dwOpenAccessAuthPo.setLesseeId(lessessId);
        List<DwOpenAccessAuthPo> dwOpenAccessAuthPos = this.find(dwOpenAccessAuthPo);
        if (CollectionUtils.isEmpty(dwOpenAccessAuthPos)){
            return ResultDTO.success(null);
        }
        String apiAuthConfig = dwOpenAccessAuthPos.get(0).getApiAuthConfig();
        resultDTO.setData(apiAuthConfig);
        resultDTO.isSuccess();
        return resultDTO;
    }

    @Override
    public AppAccessAuthConfig getAppAuthConfigByAppId(Long lesseeId, Integer dataAccessAppId, Integer type) {
        DwOpenAccessAuthPo dwOpenAccessAuthPo =new DwOpenAccessAuthPo();
        dwOpenAccessAuthPo.setLesseeId(lesseeId);
        dwOpenAccessAuthPo.setDataAccessAppId(dataAccessAppId);
        dwOpenAccessAuthPo.setType(type);
        DwOpenAccessAuthPo accessAuthPo = this.getMapper().selectOne(dwOpenAccessAuthPo);
        if(ObjectUtil.isNotNull(accessAuthPo) && StrUtil.isNotBlank(accessAuthPo.getApiAuthConfig())){
            return JSONUtil.toBean(accessAuthPo.getApiAuthConfig(),AppAccessAuthConfig.class);
        }
        return null;
    }
}
