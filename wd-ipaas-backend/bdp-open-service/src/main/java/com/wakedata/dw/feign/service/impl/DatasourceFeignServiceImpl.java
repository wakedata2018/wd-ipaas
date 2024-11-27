package com.wakedata.dw.feign.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.feign.service.DatasourceFeignService;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.service.impl.lessee.IpaasUserInfoHelper;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据源调用feign实现
 * @author luomeng
 * @date 2023/2/20 19:31
 */
@RestController
@RequestMapping(value = "/rpc/datasource")
public class DatasourceFeignServiceImpl implements DatasourceFeignService {

    @Override
    public ResultDTO<IpaasUserInfo> userAuth(String sessionId) {
        String value = RedisUtil.getInstance().get(IpaasUserInfoHelper.getIpaasUserInfoRedisKey(sessionId));
        if(ObjectUtil.isEmpty(value)){
            return ResultDTO.fail(MsgCodeEnum.s_not_login.getCode(),MsgCodeEnum.s_not_login.getDesc());
        }
        IpaasUserInfo ipaasUserInfo = JSONUtil.toBean(value,IpaasUserInfo.class);
        return ResultDTO.success(ipaasUserInfo);
    }
}
