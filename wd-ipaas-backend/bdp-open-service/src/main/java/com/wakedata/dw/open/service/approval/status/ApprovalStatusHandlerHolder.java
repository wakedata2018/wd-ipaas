package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
/**
 * @author yiyufeng
 * @title ApprovalStatusHandlerHolder
 * @projectName bdp-open
 * @date
 * @description
 */
public class ApprovalStatusHandlerHolder {

    @Autowired
    private List<AbstractApprovalStatusHandler> approvalStatusHandlerList;

    public AbstractApprovalStatusHandler getApprovalStatusHandler(ApprovalStatusEnum approvalStatusEnum) {
        for (AbstractApprovalStatusHandler abstractApprovalStatusHandler : approvalStatusHandlerList) {
            if (approvalStatusEnum == abstractApprovalStatusHandler.approvalStatus()) {
                return abstractApprovalStatusHandler;
            }
        }
        throw new OpenException(String.format("找不到状态{%s}的处理逻辑", approvalStatusEnum));
    }

}
