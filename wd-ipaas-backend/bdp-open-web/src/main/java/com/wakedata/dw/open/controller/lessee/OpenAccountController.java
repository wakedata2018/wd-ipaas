package com.wakedata.dw.open.controller.lessee;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.lessee.OpenAccountService;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountDTO;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountQuery;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountSimpleDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luomeng
 * @date 2022/8/2 17:37
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/manage/account")
@Api(value = "Ipaas账号管理", tags = "Ipaas账号管理")
@Validated
@Slf4j
public class OpenAccountController extends BaseController {

    @Resource
    private OpenAccountService openAccountService;

    @PostMapping(value = "/create.account")
    @ApiOperation(value = "添加账号")
    public ResultDTO<Boolean> createAccount(@RequestBody @Validated OpenAccountDTO openAccountDTO){
//        if(ObjectUtil.isNull(openAccountDTO.getTenantId())){
//            throw new OpenException(MsgCodeEnum.w_user_not_tenantId);
//        }
        if(ObjectUtil.isEmpty(openAccountDTO.getPassword())){
            throw new OpenException(MsgCodeEnum.w_user_not_password);
        }
        return openAccountService.createAccount(openAccountDTO);
    }

    @PostMapping(value = "/update.account")
    @ApiOperation(value = "更新账号")
    public ResultDTO<Boolean> updateAccount(@RequestBody @Validated OpenAccountDTO openAccountDTO){
        if(ObjectUtil.isNull(openAccountDTO.getId())){
            throw new OpenException(MsgCodeEnum.w_user_not_id);
        }
        return openAccountService.updateAccount(openAccountDTO);
    }

    @GetMapping(value = "/get.account.info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "账号id",dataType = "Long", paramType = "query")
    })
    @ApiOperation(value = "获取账号信息")
    public ResultDTO<OpenAccountDTO> getAccountInfo(Long id){
        return openAccountService.getAccountInfo(id);
    }

    @PostMapping(value = "/delete.account")
    @ApiOperation(value = "删除账号")
    public ResultDTO<Boolean> deleteAccount(@RequestBody @Validated OpenAccountSimpleDTO openAccountSimpleDTO){

        return openAccountService.deleteAccount(openAccountSimpleDTO.getId());
    }

    @PostMapping(value = "/reset.password")
    @ApiOperation(value = "重置密码")
    public ResultDTO<Boolean> resetPassword(@RequestBody @Validated OpenAccountSimpleDTO openAccountSimpleDTO){
        if(ObjectUtil.isEmpty(openAccountSimpleDTO.getNewPassword()) || ObjectUtil.isEmpty(openAccountSimpleDTO.getConfirmPassword())){
            throw new OpenException(MsgCodeEnum.w_user_not_password);
        }
        if(!openAccountSimpleDTO.getNewPassword().equals(openAccountSimpleDTO.getConfirmPassword())){
            throw new OpenException(MsgCodeEnum.w_user_password_not_consistent);
        }
        return openAccountService.resetPassword(openAccountSimpleDTO);
    }

    @PostMapping(value = "/list")
    @ApiOperation(value = "查询ipaas账号列表")
    public PageResultDTO<List<OpenAccountDTO>> list(@RequestBody OpenAccountQuery openAccountQuery){

        return openAccountService.list(openAccountQuery);
    }


}
