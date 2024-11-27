package com.wakedata.dw.open.service.log;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.UserPageActionPo;
import com.wakedata.dw.open.model.query.UserActionPageQuery;
import tk.mybatis.mapper.common.sqlserver.InsertMapper;

import java.util.List;

/**
 * @author yiyufeng
 * @title UserPageActionService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface UserPageActionService extends InsertMapper<UserPageActionPo> {
    /**
     * 操作日志查询
     * @param userActionPageQuery 查询条件
     * @return Page<UserPageActionPo>
     */
    Page<UserPageActionPo> queryReport(UserActionPageQuery userActionPageQuery);

    /**
     * 操作日志详情查询
     * @param id 操作日志Id
     * @return Page<UserPageActionPo>
     */
    UserPageActionPo queryAuditLogInformation(Long id);

    /**
     * 查询菜单列表
     * @return List<String>
     */
    List<String> queryMenu();

    /**
     * 查询ipaas所有用户的账号标识（管理员角色才能查询出数据）
     * @param userIdentification 用户唯一标识，作为模糊查询条件
     * @return List<String>
     */
    List<String> queryAllUserIdentification(String userIdentification);
}
