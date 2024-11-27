package com.wakedata.dw.open.service.impl.api.captcha.constants;

/***
 * @author： wangchensheng
 * @date： 2022-05-12 13:55
 * @version 1.0
 */
public class CaptchaConstants {

    /**
     * 默认图片宽度(验证码图片显示位置)
     */
    public static final Integer CAPTCHA_DEFAULT_IMAGE_WIDTH = 303;

    /**
     * 默认图片高度(验证码图片显示位置)
     */
    public static final Integer CAPTCHA_DEFAULT_IMAGE_HEIGHT = 150;

    /**
     * permission登录验证码key
     */
    public static final String CAPTCHA_REDIS_KEY_LOGIN = "captcha_login_";

    /**
     * redisKey过期时间
     */
    public static final int CAPTCHA_REDIS_KEY_LOGIN_EXPIRE_TIME = 60 * 3;

    /**
     * IO异常
     */
    public static final String IO_EXCEPTION = "IO异常";

    /**
     * 模板图片文件
     */
    public static final String TEMPLATE_PATH = "/template.png";

    /**
     * 模板边框图片文件名
     */
    public static final String BORDER_PATH = "/border.png";

    /**
     * 源图路径前缀(根据文件位置自定义且前缀为classpath)
     */
    public static final String PRE_ORIGIN_IMAGE_PATH = "classpath:static/targets";

    /**
     * 模板图路径前缀(根据文件位置自定义且前缀为classpath)
     */
    public static final String PRE_TEMPLATE_IMAGE_PATH = "classpath:static/templates";

}
