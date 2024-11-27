package com.wakedata.dw.open.controller.business;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.FunctionEnumUtil;
import com.wakedata.dw.open.function.FunctionVo;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.function.custom.CustomFunction;
import com.wakedata.dw.open.function.custom.CustomFunctionSupportVo;
import com.wakedata.dw.open.input.PathMapping;
import com.wakedata.dw.open.service.api.CustomFunctionService;
import com.wakedata.dw.open.service.api.dto.CustomFunctionDTO;
import com.wakedata.dw.open.service.api.dto.CustomFunctionQuery;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.open.vo.FunctionExpressCheckReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.noear.snack.ONode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


/**
 * @author luomeng
 * @date 2022/8/18 20:28
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/function")
@Api(value = "函数", tags = "函数")
public class FunctionController {

    @Resource
    private CustomFunctionService customFunctionService;

    @GetMapping("/get.all.fn")
    @ApiOperation(value = "获取所有的函数",notes = "获取支持的所有函数")
    public ResultDTO<List<FunctionVo>> getAllFn(){
        return ResultDTO.success(FunctionEnumUtil.getSupportFunctionList());
    }

    @PostMapping("/check")
    @ApiOperation(value = "校验函数表达式是否有效", notes = "校验函数表达式是否有效")
    public ResultDTO<?> checkFunctionExpress(@RequestBody FunctionExpressCheckReq req) {
        ONode ctxNode = ONode.load(new HashMap<>(16));
        JSONObject jsonObject = JSONObject.parseObject("{}");
        PathMapping.setByPath(ctxNode, IFunc.SEPARATOR_ASTERISK, jsonObject, false);
        return ResultDTO.success(FuncExecutor.getInstance().exec(ctxNode, req.getFunctionExpress(), Boolean.TRUE));
    }

    @GetMapping("/getCustomFunctionSupportList")
    @ApiOperation(value = "获取自定义函数支持列表",notes = "查询支持的自定义函数类型以及代码语言和示例代码")
    public ResultDTO<CustomFunctionSupportVo> getCustomFunctionSupportList(){
        return ResultDTO.success(FunctionEnumUtil.getCustomFunctionSupportList());
    }

    @PostMapping(value = "/getAllCustomFunctionList")
    @ApiOperation(value = "获取自定义函数列表",notes = "获取自定义函数列表")
    public PageResultDTO<List<CustomFunctionDTO>> getAllCustomFunctionList(@RequestBody CustomFunctionQuery customFunctionQuery){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        customFunctionQuery.setLesseeId(userInfo.getLesseeId());
        return customFunctionService.getAllCustomFunctionList(customFunctionQuery);
    }

    @PostMapping(value = "/addCustomFunction")
    @ApiOperation(value = "添加自定义函数",notes = "添加自定义函数")
    public ResultDTO<Boolean> addCustomFunction(@RequestBody CustomFunctionDTO customFunctionDTO){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        customFunctionDTO.setLesseeId(userInfo.getLesseeId());
        customFunctionDTO.setCreateBy(userInfo.getName());
        customFunctionDTO.setUpdateBy(userInfo.getName());
        return customFunctionService.addCustomFunction(customFunctionDTO);
    }


    @GetMapping(value = "/getCustomFunctionById")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "customerFunctionId",paramType = "query",value = "自定义函数id")
    })
    @ApiOperation(value = "根据id查询自定义函数",notes = "根据id查询自定义函数")
    public ResultDTO<CustomFunctionDTO> getCustomFunctionById(Long customerFunctionId){
        return customFunctionService.getCustomFunctionById(customerFunctionId);
    }

    @PostMapping(value = "/updateCustomFunction")
    @ApiOperation(value = "更新自定义函数",notes = "更新自定义函数")
    public ResultDTO<Boolean> updateCustomFunction(@RequestBody CustomFunctionDTO customFunctionDTO){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        customFunctionDTO.setLesseeId(userInfo.getLesseeId());
        customFunctionDTO.setUpdateBy(userInfo.getName());
        return customFunctionService.updateCustomFunction(customFunctionDTO);
    }

    @PostMapping(value = "/debugCustomFunction")
    @ApiOperation(value = "调试自定义函数",notes = "调试自定义函数，返回函数执行结果")
    public ResultDTO<Object> debugCustomFunction(@RequestBody CustomFunction customFunction){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        customFunction.setLesseeId(userInfo.getLesseeId());
        return ResultDTO.success(customFunctionService.debugCustomFunction(customFunction));
    }

    @PostMapping(value = "/updateCustomFunctionStatus")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "customerFunctionId",paramType = "query",value = "自定义函数id"),
            @ApiImplicitParam(name = "status",paramType = "query",value = "状态 1：草稿 2：上线 3：下线")
    })
    @ApiOperation(value = "更改自定义函数状态",notes = "更改自定义函数状态")
    public ResultDTO<Boolean> updateCustomFunctionStatus(Long customerFunctionId,Integer status){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        return ResultDTO.success(customFunctionService.updateCustomFunctionStatus(userInfo.getLesseeId(),customerFunctionId,status));
    }


}
