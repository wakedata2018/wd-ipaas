package com.wakedata.dw.lowcode.util;

import cn.hutool.core.io.IoUtil;
import com.wakedata.dw.open.Constants;
import com.wakedata.dw.open.exception.OpenException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import lombok.experimental.UtilityClass;

/**
 * @author wanghu@wakedata.com
 * @date 2021/11/30
 * @since v1.0.0
 */
@UtilityClass
public class FileUtils {

    /**
     * 获取文件头部16进制
     *
     * @param in 输入流
     * @return 16进制字符串
     */
    public String getFileHeadHex(InputStream in) {
        try {
            byte[] fileHeadBytes = new byte[Constants.FILE_HEAD_LEN];
            in.read(fileHeadBytes, 0, Constants.FILE_HEAD_LEN);

            StringBuilder fileHeadHexBuilder = new StringBuilder();
            for (byte fileHeadByte : fileHeadBytes) {
                fileHeadHexBuilder.append(Integer.toHexString(fileHeadByte & 0xFF).toUpperCase());
            }
            return fileHeadHexBuilder.toString();
        } catch (IOException ex) {
            throw new OpenException("读取文件错误");
        }
    }

    public InputStream base64ToInputStream(String base64) {
        String removeBase64Head = base64.substring(base64.indexOf(Constants.DEFAULT_SEPARATOR) + 1);
        return IoUtil.toStream(Base64.getDecoder().decode(removeBase64Head));
    }

}
