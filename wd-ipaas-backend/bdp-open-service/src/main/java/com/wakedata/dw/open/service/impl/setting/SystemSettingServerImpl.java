package com.wakedata.dw.open.service.impl.setting;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.mapper.setting.SystemSettingMapper;
import com.wakedata.dw.open.model.setting.SystemSettingDO;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.setting.SystemSettingService;
import com.wakedata.dw.open.service.setting.dto.SystemDetailInfo;
import com.wakedata.dw.open.service.setting.dto.SystemSettingDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Objects;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:41:11
 */
@Service
public class SystemSettingServerImpl extends BaseServiceImpl<SystemSettingDO, SystemSettingMapper> implements SystemSettingService {

    public static final Integer INFO_TYPE_NAME_AND_LOGO = 1;

    @Autowired
    @Override
    protected void init(CurdService<SystemSettingDO> curdService, SystemSettingMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<SystemSettingDTO> getSystemInfoOfNameAndLogo() {
        return ResultDTO.success(buildSetting());
    }

    @Override
    public ResultDTO<Boolean> buildNameAndLogo(SystemSettingDTO dto) {
        // 删除数据
        SystemSettingDTO oldData = buildSetting();
        Example example = new Example(SystemSettingDO.class);
        example.createCriteria().andEqualTo("infoType", INFO_TYPE_NAME_AND_LOGO);
        this.getMapper().deleteByExample(example);
        if (Objects.nonNull(oldData.getSystemDetailInfo()) && StringUtils.isNotBlank(oldData.getSystemDetailInfo().getLogoUrl())) {
            FileUtil.del(oldData.getSystemDetailInfo().getLogoUrl());
        }

        dto.setInfoType(INFO_TYPE_NAME_AND_LOGO);
        SystemSettingDO systemSettingDO = convertDto2Do(dto);
        this.getMapper().insert(systemSettingDO);
        return ResultDTO.success(Boolean.TRUE);
    }

    private SystemSettingDTO buildSetting() {
        Example example = new Example(SystemSettingDO.class);
        example.createCriteria().andEqualTo("infoType", INFO_TYPE_NAME_AND_LOGO);
        SystemSettingDO systemSettingDO = this.getMapper().selectOneByExample(example);
        return Objects.isNull(systemSettingDO) ? new SystemSettingDTO() : convertDo2Dto(systemSettingDO);
    }

    private SystemSettingDO convertDto2Do(SystemSettingDTO systemSettingDTO) {
        SystemSettingDO systemSettingDO = new SystemSettingDO();
        systemSettingDO.setId(systemSettingDTO.getId());
        systemSettingDO.setSettingContent(JSONObject.toJSONString(systemSettingDTO.getSystemDetailInfo()));
        systemSettingDO.setInfoType(systemSettingDTO.getInfoType());
        return systemSettingDO;
    }

    private SystemSettingDTO convertDo2Dto(SystemSettingDO systemSettingDO) {
        SystemSettingDTO systemSettingDTO = new SystemSettingDTO();
        systemSettingDTO.setId(systemSettingDO.getId());
        systemSettingDTO.setSystemDetailInfo(JSONObject.parseObject(systemSettingDO.getSettingContent(), SystemDetailInfo.class));
        systemSettingDTO.setInfoType(systemSettingDO.getInfoType());
        return systemSettingDTO;
    }
}
