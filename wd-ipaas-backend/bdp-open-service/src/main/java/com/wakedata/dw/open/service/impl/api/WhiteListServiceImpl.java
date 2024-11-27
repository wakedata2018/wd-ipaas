package com.wakedata.dw.open.service.impl.api;

import com.wakedata.dw.open.mapper.api.WhiteListMapper;
import com.wakedata.dw.open.model.api.WhiteListPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.WhiteListService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author tanzhi
 * @title WhiteListServiceImpl
 * @projectName bdp-open
 * @date 2019/9/17 19:44
 * @description
 */
@Service
public class WhiteListServiceImpl extends BaseServiceImpl<WhiteListPo, WhiteListMapper> implements WhiteListService {


    @Autowired
    @Override
    protected void init(CurdService<WhiteListPo> curdService, WhiteListMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Set<String> accessWhiteList(String appKey) {
        return getMapper().accessWhiteList(appKey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addToAccessList(Integer appId, String ips) {
        WhiteListPo record = new WhiteListPo();
        record.setDataAccessAppId(appId);
        getMapper().delete(record);
        if (StringUtils.isNotEmpty(ips)) {
            String[] ipArray = ips.split(",");
            List<WhiteListPo> list = new ArrayList<>(ipArray.length);
            for (String ip : ipArray) {
                WhiteListPo whiteListPo = new WhiteListPo();
                whiteListPo.setDataAccessAppId(appId);
                whiteListPo.setIp(ip);
                Date updateTime = new Date();
                whiteListPo.setUpdateTime(updateTime);
                whiteListPo.setCreateTime(updateTime);
                list.add(whiteListPo);
            }

            getMapper().insertList(list);
        }
        return true;
    }

    @Override
    public List<WhiteListPo> showList(Integer appId) {
        return getMapper().getWhiteList(appId);
    }
}
