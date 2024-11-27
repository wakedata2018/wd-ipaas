package com.wakedata.dw.open.service.impl.api.captcha.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.core.exception.BizException;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.service.impl.api.captcha.constants.CaptchaConstants;
import com.wakedata.dw.open.service.impl.api.captcha.enums.CaptchaEnum;
import com.wakedata.dw.open.service.impl.api.captcha.helper.CaptchaHelper;
import com.wakedata.wd.permission.captcha.api.CaptchaService;
import com.wakedata.wd.permission.captcha.dto.ImageVerificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/***
 * 滑动验证码实现类
 *
 * @author： wangchensheng
 * @date： 2022-05-12 11:44
 * @version 1.0
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${permission.support.autotest.login:false}")
    private Boolean SUPPORT_AUTO_TEST_LOGIN;


    @Resource
    private CaptchaHelper captchaHelper;

    @Override
    public ResultDTO<ImageVerificationDTO> selectImageVerificationCode(String type, Integer width, Integer height) {
        ImageVerificationDTO imageVerificationDTO = null;

        try {
            type = StringUtils.isEmpty(type) ? CaptchaEnum.SLIDE.name() : type;
            width = Objects.isNull(width) ? CaptchaConstants.CAPTCHA_DEFAULT_IMAGE_WIDTH : width;
            height = Objects.isNull(height) ? CaptchaConstants.CAPTCHA_DEFAULT_IMAGE_HEIGHT : height;
            imageVerificationDTO = captchaHelper.selectSlideCode(type,width,height);
            if(!SUPPORT_AUTO_TEST_LOGIN) {
                imageVerificationDTO.setX(0);
            }
        } catch (BizException e){
            log.error(e.getMessage(), e);
            throw new BizException("获取验证码异常");
        }

        return ResultDTO.success(imageVerificationDTO);
    }

    @Override
    public ResultDTO<Boolean> checkVerificationResult(Integer x, Integer y,String captchaKey) {

        int threshold = 5;

        try {
            String xy = RedisUtil.getInstance().get(captchaKey);
            if (StringUtils.isEmpty(xy)){
                return ResultDTO.fail("验证码已过期");
            }

            ImageVerificationDTO imageVerificationDTO = JSONObject.parseObject(JSON.parse(xy).toString(), ImageVerificationDTO.class);
            if (imageVerificationDTO != null) {
                if ((Math.abs(x - imageVerificationDTO.getX()) <= threshold) && y.equals(imageVerificationDTO.getY())) {
                    return ResultDTO.success(Boolean.TRUE);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultDTO.fail("校验验证码异常");
        }
        return ResultDTO.fail("校验验证码异常");
    }

}
