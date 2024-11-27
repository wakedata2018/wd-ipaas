package com.wakedata.dw.open.controller.log;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.model.log.OperatorLogPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.log.OperatorLogService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author tanzhi
 * @title OperatorLogController
 * @date 2019/10/12 9:40
 * @projectName bdp-open
 * @descriptor
 */
@RestController
@RequestMapping("${spring.mvc.backend.api.prefix}/business/operator_log")
@Api(value = "操作日志", tags = "操作日志")
public class OperatorLogController {


    @Autowired
    private OperatorLogService operatorLogService;

    @GetMapping("/list")
    public PageResultDTO<Page<OperatorLogPo>> find(String keyword, PageQuery pageQuery, String from, String to) {
        Page<OperatorLogPo> operatorLogPos = operatorLogService.selectPageLikeOrderBy(new OperatorLogPo(), pageQuery.getPageNo(), pageQuery.getPageSize(), keyword, Arrays.asList("userIdentification", "description"),
                "CREATE_TIME", false, "createTime", from, to, null, null);
        PageResultDTO<Page<OperatorLogPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount(new Long(operatorLogPos.getTotal()).intValue());
        resultDTO.setData(operatorLogPos);
        return resultDTO;
    }
}
