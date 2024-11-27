package com.wakedata.dw.open.service.impl.lessee;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.lessee.OpenAccountMapper;
import com.wakedata.dw.open.model.lessee.OpenAccountPo;
import com.wakedata.dw.open.model.vo.OpenAccountVo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.utils.DecryptHelper;
import com.wakedata.dw.open.service.lessee.OpenAccountLoginService;
import com.wakedata.dw.open.service.lessee.dto.AccountLoginDTO;
import com.wakedata.dw.open.service.lessee.dto.IpaasUserInfoDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.UUIDUtils;
import com.wakedata.wd.permission.enums.IpaasRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wakedata.wd.permission.captcha.api.CaptchaService;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author luomeng
 * @date 2022/8/4 15:28
 */
@Service
@Slf4j
public class OpenAccountLoginServiceImpl extends BaseServiceImpl<OpenAccountPo, OpenAccountMapper> implements OpenAccountLoginService {


    @Resource
    private CaptchaService captchaService;

    @Autowired
    @Override
    protected void init(CurdService<OpenAccountPo> curdService, OpenAccountMapper mapper) {
        super.set(curdService,mapper);
    }

    @Override
    public ResultDTO<IpaasUserInfoDTO> login(HttpServletRequest request, HttpServletResponse response, AccountLoginDTO accountLoginDTO) {
        //优先校验滑块验证码
        checkSlideCaptcha(accountLoginDTO.getX(),accountLoginDTO.getY(),accountLoginDTO.getCaptchaKey());
        OpenAccountVo openAccountVo = this.getMapper().getAccountByUsername(accountLoginDTO.getUsername());
        if(ObjectUtil.isNull(openAccountVo)){
            // 校验完验证码后，删除原来的key，防止验证码被复用
            RedisUtil.getInstance().del(accountLoginDTO.getCaptchaKey());
            throw new OpenException(MsgCodeEnum.w_user_account_error);
        }
        String inputPwd = DecryptHelper.decrypt(accountLoginDTO.getPassword(), accountLoginDTO.getUsername());
        //校验密码
        checkPassword(inputPwd,openAccountVo.getPassword(),accountLoginDTO.getCaptchaKey());
        //设置用户信息
        IpaasUserInfo ipaasUserInfo = BeanUtil.copy(openAccountVo,IpaasUserInfo.class);
        ipaasUserInfo.setSessionId(IdUtil.fastSimpleUUID());
        setIpaasUserRole(ipaasUserInfo);

        //写缓存
        setCookie(request, response, ipaasUserInfo);
        RedisUtil.getInstance().set(IpaasUserInfoHelper.getIpaasUserInfoRedisKey(ipaasUserInfo.getSessionId())
                , JSONUtil.toJsonStr(ipaasUserInfo),DwOpenConstant.IPAAS_USERINFO_EXPIRETIME);
        // 校验完验证码后，删除原来的key，防止验证码被复用
        RedisUtil.getInstance().del(accountLoginDTO.getCaptchaKey());
        return ResultDTO.success(BeanUtil.copy(ipaasUserInfo,IpaasUserInfoDTO.class));
    }

    /**
     * 校验滑块验证码
     * (x y captchaKey为空时走的时旧登录 不为空走的是带验证码登录)
     *
     * @param x x坐标轴
     * @param y y坐标轴
     * @param captchaKey redisKey
     */
    public void checkSlideCaptcha(Integer x ,Integer y ,String captchaKey) {
        if (Objects.nonNull(x) && Objects.nonNull(y) && StringUtils.isNotEmpty(captchaKey)){
            com.wakedata.common.core.dto.ResultDTO<Boolean> resultDTO = captchaService.checkVerificationResult(x, y,captchaKey);
            // 校验完验证码后，删除原来的key，防止验证码被复用
            RedisUtil.getInstance().del(captchaKey);
            if (Objects.equals(resultDTO.isSuccess(),Boolean.FALSE)) {
                throw new OpenException(resultDTO.getMsg());
            }
        }
    }

    /**
     * 设置cookie
     * @param request
     * @param response
     * @param ipaasUserInfo
     */
    private void setCookie(HttpServletRequest request, HttpServletResponse response, IpaasUserInfo ipaasUserInfo) {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(DwOpenConstant.COOKIE_IPAAS_KEY);
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        simpleCookie.setValue(ipaasUserInfo.getSessionId());
        simpleCookie.saveTo(request, response);
    }

    /**
     * 设置角色
     * @param ipaasUserInfo
     */
    private void setIpaasUserRole(IpaasUserInfo ipaasUserInfo) {
        if(IpaasRoleEnum.SUPER_ADMIN_ROLE.getId().equals(ipaasUserInfo.getRelateRoleId())){
            ipaasUserInfo.setRoleIdentifier(IpaasRoleEnum.SUPER_ADMIN_ROLE.getIdentifier());
            ipaasUserInfo.setIsPlatformAdmin(true);
        }else{
            ipaasUserInfo.setRoleIdentifier(IpaasRoleEnum.DEVELOPER_ROLE.getIdentifier());
            ipaasUserInfo.setIsPlatformAdmin(false);
        }
    }

    /**
     * 校验密码
     * @param inputPwd
     * @param userPassword
     */
    private void checkPassword(String inputPwd,String userPassword,String captchaKey) {
        String password = DigestUtil.md5Hex(String.join(DwOpenConstant.JOIN_POINT, inputPwd, DwOpenConstant.USER_PASSWORD_SALT));
        if(!userPassword.equals(password)){
            // 校验完验证码后，删除原来的key，防止验证码被复用
            RedisUtil.getInstance().del(captchaKey);
            throw new OpenException(MsgCodeEnum.w_user_account_error);
        }
    }

    @Override
    public ResultDTO<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if(ObjectUtil.isEmpty(cookies)){
            return ResultDTO.success(true);
        }
        for(Cookie cookie : cookies){
            if(DwOpenConstant.COOKIE_IPAAS_KEY.equals(cookie.getName())){
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                //删除redis缓存
                RedisUtil.getInstance().del(IpaasUserInfoHelper.getIpaasUserInfoRedisKey(cookie.getValue()));
            }
        }
        return ResultDTO.success(true);
    }

    @Override
    public ResultDTO<IpaasUserInfoDTO> getAccountInfo(HttpServletRequest request) {
        String ipaasCookieId = IpaasUserInfoHelper.getIpaasCookieId(request);
        if(ObjectUtil.isEmpty(ipaasCookieId)){
            return ResultDTO.success(null);
        }
        String value = RedisUtil.getInstance().get(IpaasUserInfoHelper.getIpaasUserInfoRedisKey(ipaasCookieId));
        if(ObjectUtil.isEmpty(value)){
            return ResultDTO.success(null);
        }
        IpaasUserInfoDTO ipaasUserInfoDTO = JSONUtil.toBean(value, IpaasUserInfoDTO.class);
        ipaasUserInfoDTO.setLesseeIdCode(UUIDUtils.generateLesseeIdEncode(ipaasUserInfoDTO.getLesseeId()));
        return ResultDTO.success(ipaasUserInfoDTO);
    }
}
