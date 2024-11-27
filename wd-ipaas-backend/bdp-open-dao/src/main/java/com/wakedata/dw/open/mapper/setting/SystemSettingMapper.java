package com.wakedata.dw.open.mapper.setting;

import com.wakedata.dw.open.model.setting.SystemSettingDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author zhengqinghui@wakedata.com
 * date 2022/11/21 10:56
 */
@Repository
public interface SystemSettingMapper extends Mapper<SystemSettingDO>, InsertListMapper<SystemSettingDO> {
}
