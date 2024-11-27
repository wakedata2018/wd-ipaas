package com.wakedata.dw.open;

/**
 * @author yiyufeng
 * @title Constants
 * @projectName bdp-open
 * @date
 * @description
 */
public class Constants {

    public interface UserInfoRequestParam {

        String KEY_PARAM_LOGIN_NAME = "bgy_uac_user_info_loginName";
    }

    public interface UserIdentificationParam {

        String KEY_USER_IDENTIFICATION = "bdp_open_abstract_user_identification";
    }

    /**
     * 图片上传文件大小限制为5M
     */
    public static final long FILE_SIZE_LIMIT_IMAGE_NEW = 5242880L;

    /**
     * 临时文件上传大小限制为20M
     */
    public static final long TEMP_FILE_MAX_SIZE = 20971520L;

    /**
     * 文件头部长度
     */
    public static final int FILE_HEAD_LEN = 4;

    public static final String DEFAULT_SEPARATOR = ",";

    public static final String HTTPS = "https:";

    public static final String HTTP = "http:";

}
