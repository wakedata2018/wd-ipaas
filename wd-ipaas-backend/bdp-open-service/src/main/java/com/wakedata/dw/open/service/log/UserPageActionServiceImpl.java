package com.wakedata.dw.open.service.log;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.mapper.UserPageActionMapper;
import com.wakedata.dw.open.model.UserPageActionPo;
import com.wakedata.dw.open.model.query.UserActionPageQuery;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.wd.permission.enums.IpaasRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
/**
 * @author yiyufeng
 * @title UserPageActionServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
public class UserPageActionServiceImpl implements UserPageActionService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserPageActionMapper userPageActionMapper;

    @Autowired
    private CurdService<UserPageActionPo> curdService;

    @Override
    public int insert(UserPageActionPo userPageActionPo) {
        return curdService.insert(userPageActionPo, this.userPageActionMapper);
    }

    @Override
    public Page<UserPageActionPo> queryReport(UserActionPageQuery userActionPageQuery) {
        //目前规则是企业开发者角色不能看到其它账号的操作日志，只看自己账号的操作日志也没有意义，先设定企业开发者不查看操作日志
        if (IpaasRoleEnum.DEVELOPER_ROLE.getId().equals(IpaasUserContext.getUserInfo().getRelateRoleId())) {
            return new Page<>();
        }
        PageHelper.startPage(userActionPageQuery.getPageNo(), userActionPageQuery.getPageSize());
        return (Page<UserPageActionPo>) this.userPageActionMapper.queryReport(userActionPageQuery);
    }

    @Override
    public UserPageActionPo queryAuditLogInformation(Long id) {
        return this.userPageActionMapper.queryAuditLogInformation(id);
    }

    @Override
    public List<String> queryMenu() {
        return Optional.ofNullable(this.userPageActionMapper.queryMenu()).orElse(new ArrayList<>());
    }


    @Override
    public List<String> queryAllUserIdentification(String userIdentification) {
        if (!IpaasRoleEnum.SUPER_ADMIN_ROLE.getId().equals(IpaasUserContext.getUserInfo().getRelateRoleId())) {
            return new ArrayList<>();
        }
        return userPageActionMapper.selectUserIdentificationList(userIdentification);
    }
}
