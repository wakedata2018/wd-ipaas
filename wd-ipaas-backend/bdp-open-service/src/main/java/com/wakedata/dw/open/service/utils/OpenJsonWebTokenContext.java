package com.wakedata.dw.open.service.utils;

import com.wakedata.dw.platform.tools.jwt.context.JsonWebTokenContext;
import lombok.Data;

/**
 * @author ZhangXueJun
 * @title OpenJsonWebTokenContext
 * @date 2021/7/7 14:42
 * @projectName dw-open
 * @description
 */
@Data
public class OpenJsonWebTokenContext extends JsonWebTokenContext {

    private String apiPath;

}
