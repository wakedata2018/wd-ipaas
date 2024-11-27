package com.wakedata.dw.open.controller.business;

import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
//import com.wakedata.dw.open.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanzhi
 * @title UserInfoController
 * @date 2020/4/11 0:14
 * @projectName bdp-open
 * @descriptor
 */
@RestController
public class UserInfoController extends BaseController {

    @GetMapping("/user/admin")
    @ApiOperation("判断用户是否是管理员")
    public ResultDTO<Boolean> userHasAdminRole(){
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        //2021-11-15注释掉，说明参考 WebUtils.checkAdmin() 方法的注释
//        resultDTO.setData(WebUtils.checkAdmin());
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        return resultDTO;
    }

}
