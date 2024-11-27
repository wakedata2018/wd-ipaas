package com.wakedata.dw.open.service.approval;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import com.wakedata.dw.open.service.approval.vo.ApprovalDetailVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalHistoryVo;

import java.util.List;

/**
 * @author yiyufeng
 * @title ApprovalBusiness
 * @projectName bdp-open
 * @date
 * @description
 */
public interface ApprovalBusiness {

    /**
     * 业务类型
     *
     * @return
     */
    ApprovalBusinessTypeEnum getBusinessType();

    /**
     * 解析审批主题
     *
     * @param userIdentification
     * @param approvalBody
     * @return
     */
    List<AppApprovalPo> analyzeApprovalBody(String userIdentification, Object approvalBody);


    /**
     * 审批成功
     *
     * @param userIdentification
     * @param approval
     * @return
     */
    boolean handleApprovalSuccess(String userIdentification, AppApprovalPo approval);

    /**
     * 审批失败
     *
     * @param userIdentification
     * @param approval
     * @return
     */
    boolean handleApprovalFail(String userIdentification, AppApprovalPo approval);

    /**
     * 列出可见审批
     *
     * @param appApprovalPo
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @return
     */
    Page<AppApprovalPo> listVisibleApproval(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword);

    /**
     * 转换审批历史
     *
     * @param userIdentification
     * @param approval
     * @return
     */
    ApprovalHistoryVo transformApprovalHistory(String userIdentification, AppApprovalPo approval);

    /**
     * 转换审批详情
     *
     * @param userIdentification
     * @param approval
     * @return
     */
    ApprovalDetailVo transformApprovalDetail(String userIdentification, AppApprovalPo approval);

}
