package com.wakedata.dw.open.service.setting;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.setting.SystemSettingDO;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.setting.dto.SystemSettingDTO;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:28:28
 */
public interface SystemSettingService extends BaseDbService<SystemSettingDO> {

    /**
     * 获取系统设置的系统名称和系统logo
     *
     * @return 系统设置的系统名称和系统logo
     */
    ResultDTO<SystemSettingDTO> getSystemInfoOfNameAndLogo();

    /**
     * 设置系统名称和系统logo
     *
     * @param dto 系统名称 系统logo
     * @return true/false
     */
    ResultDTO<Boolean> buildNameAndLogo(SystemSettingDTO dto);

}
