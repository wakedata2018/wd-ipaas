package com.wakedata.dw.open.controller.datasource;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.datasource.DatabasePo;
import com.wakedata.dw.open.service.datasource.DatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanzhi
 * @title DataSourceController
 * @date 2019/11/21 20:46
 * @projectName dw-open
 * @descriptor
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/database")
@Api(value = "数据源管理",tags ="数据源管理")
public class DatabaseController extends BaseController {

    @Autowired
    private DatabaseService databaseService;


    @GetMapping("/add")
    @ApiOperation("添加数据库类型")
    @AuditLog
    public ResultDTO add(DatabasePo databasePo) {
        databaseService.add(databasePo);
        return new ResultDTO();
    }


    @GetMapping("/show")
    @ApiOperation("查询数据库类型")
    @AuditLog
    public ResultDTO<DatabasePo> show(Integer dataSourceId) {
        DatabasePo show = databaseService.show(dataSourceId);
        ResultDTO<DatabasePo> resultDTO = new ResultDTO<>();
        resultDTO.setData(show);
        return resultDTO;
    }

    @PostMapping("/update")
    @ApiOperation("修改数据库类型")
    @AuditLog
    public ResultDTO<DatabasePo> update(DatabasePo dataSourcePo) {
        databaseService.update(dataSourcePo);
        ResultDTO<DatabasePo> resultDTO = new ResultDTO<>();
        return resultDTO;
    }

    @GetMapping("/all")
    @ApiOperation("查询数据库类型列表")
    @AuditLog
    public ResultDTO<List<DatabasePo>> list(DatabasePo po) {
        List<DatabasePo> show = databaseService.find(po);
        ResultDTO<List<DatabasePo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(show);
        return resultDTO;
    }
}
