package com.wakedata.dw.open.service.approval.vo;

import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import lombok.Data;

import java.util.List;

@Data
/**
 * @author yiyufeng
 * @title DataAccessApprovalDetailVo
 * @projectName bdp-open
 * @date
 * @description
 */
public class DataAccessApprovalDetailVo extends ApprovalDetailVo {

    private String dataAssetName;

    private String dataAssetDescription;

    private String dataAssetApiUri;

    private List<DatasourceTableColumnDo> approvalColumnList;
}
