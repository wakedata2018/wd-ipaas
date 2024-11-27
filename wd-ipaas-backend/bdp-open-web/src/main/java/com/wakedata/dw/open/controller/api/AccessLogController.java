package com.wakedata.dw.open.controller.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.service.log.AccessLogService;
import com.wakedata.dw.open.vo.ListAccessLogReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;

/**
 * @author tanzhi
 * @title AccessLogController
 * @date 2019/11/22 9:29
 * @projectName dw-open
 * @descriptor
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/access_log")
@Api(value = "日志审计", tags = "访问日志")
@Validated
public class AccessLogController extends BaseController {

    @Autowired
    private AccessLogService accessLogService;


    @GetMapping("/list")
    @ApiOperation("日志记录查询")
    public PageResultDTO<Page<AccessLogPo>> find(@Valid ListAccessLogReq listAccessLogReq) {
        Page<AccessLogPo> operatorLogPos = accessLogService.selectPageLikeOrderBy(
                new AccessLogPo(), listAccessLogReq.getPageNo(), listAccessLogReq.getPageSize(), listAccessLogReq.getKeyword(), Arrays.asList("accessMethod", "appId", "ip"),
                "CREATE_TIME", false, "createTime", listAccessLogReq.getFrom(), listAccessLogReq.getTo(), null,null);
        PageResultDTO<Page<AccessLogPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount(new Long(operatorLogPos.getTotal()).intValue());
        resultDTO.setData(operatorLogPos);
        return resultDTO;
    }

}
