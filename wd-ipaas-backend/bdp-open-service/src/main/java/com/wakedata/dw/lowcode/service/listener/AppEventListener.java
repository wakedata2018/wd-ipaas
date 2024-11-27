package com.wakedata.dw.lowcode.service.listener;

import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.lowcode.service.LowCodeAccountService;
import com.wakedata.dw.lowcode.service.event.AppCreatedEvent;
import com.wakedata.dw.lowcode.service.event.AppDeletedEvent;
import com.wakedata.dw.lowcode.service.event.AppUpdatedEvent;
import com.wakedata.dw.open.enums.DataAssetEnums.AppType;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.service.utils.AuthUtils;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author wanghu@wakedata.com
 * @title 应用事件监听器
 * @date 2021/11/25
 * @since v1.0.0
 */
@Component
public class AppEventListener {

    @Resource
    private LowCodeAccountService lowCodeAccountService;

    @EventListener
    public void onAppCreated(AppCreatedEvent event) {
        //不是低代码应用
        AppAccessPo appAccessPo = event.getAppAccessPo();
        if (Objects.isNull(appAccessPo.getAppType()) || (appAccessPo.getAppType() != AppType.LOW_CODE_APP)) {
            return;
        }
        //账号和密码都为空，则不进行添加
        LowCodeAccountPo accountPo = appAccessPo.getLowCodeAccountPo();
        if (Objects.isNull(accountPo) ||
            (StringUtils.isBlank(accountPo.getUserName())
                && StringUtils.isBlank(accountPo.getPwd()))) {
            return;
        }

        accountPo.setAppId(appAccessPo.getDataAccessAppId());
        accountPo.setEpId(AuthUtils.currentAppId().intValue());
        lowCodeAccountService.add(accountPo);
    }

    @EventListener
    public void onAppUpdated(AppUpdatedEvent event) {
        //不是低代码应用
        AppAccessPo appAccessPo = event.getAppAccessPo();
        if (Objects.isNull(appAccessPo.getAppType()) || (appAccessPo.getAppType() != AppType.LOW_CODE_APP)) {
            return;
        }

        LowCodeAccountPo query = new LowCodeAccountPo();
        query.setAppId(appAccessPo.getDataAccessAppId());
        List<LowCodeAccountPo> accountPos = lowCodeAccountService.find(query);

        //创建账号
        if (CollectionUtils.isEmpty(accountPos)) {
            onAppCreated(new AppCreatedEvent(event.getSource(), event.getAppAccessPo()));
        } else {
            LowCodeAccountPo oldAccountPo = appAccessPo.getLowCodeAccountPo();
            oldAccountPo.setId(accountPos.get(0).getId());
            lowCodeAccountService.update(oldAccountPo);
        }
    }

    @EventListener
    public void onAppDeleted(AppDeletedEvent event) {
        if (Objects.isNull(event.getAppId())) {
            return;
        }
        LowCodeAccountPo param = new LowCodeAccountPo();
        param.setAppId(param.getAppId());
        lowCodeAccountService.delete(param);
    }
}
