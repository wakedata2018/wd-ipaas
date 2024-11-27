package com.wakedata.dw.open.service.impl.auth;

import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.auth.AuthInfoApiMapper;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.model.auth.AuthAuthorizationPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.auth.AuthInfoApiService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/16 14:36
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthInfoApiServiceImpl extends BaseServiceImpl<AuthAuthorizationPo, AuthInfoApiMapper> implements AuthInfoApiService {

    @Autowired
    @Override
    protected void init(CurdService<AuthAuthorizationPo> curdService, AuthInfoApiMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Boolean batchAuthorization(List<AuthAuthorizationPo> authAuthorizationPoList) {
        BasePo.setAllTime(authAuthorizationPoList);
        if (authAuthorizationPoList.size() == 0) {
            throw new OpenException("传入的授权信息不能为空");
        }
        try{
            for (AuthAuthorizationPo authAuthorizationPo : authAuthorizationPoList) {
                int i = this.getMapper().checkByApiIdAndAuthId(authAuthorizationPo.getApiId(), authAuthorizationPo.getAuthInfoId());
                if (i > 0) {
                    this.getMapper().updateByApiIdAndAuthId(authAuthorizationPo);
                }else {
                    this.getMapper().insert(authAuthorizationPo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
