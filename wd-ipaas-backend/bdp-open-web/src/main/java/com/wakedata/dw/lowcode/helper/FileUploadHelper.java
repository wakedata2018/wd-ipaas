package com.wakedata.dw.lowcode.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.io.FileUtil;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.core.util.BaseEnumUtil;
import com.wakedata.common.storage.enums.BucketEnum;
import com.wakedata.common.storage.model.UploadRequest;
import com.wakedata.common.storage.model.UploadResult;
import com.wakedata.common.storage.service.FileStorageService;
import com.wakedata.dw.lowcode.enums.ImageHeadEnum;
import com.wakedata.dw.lowcode.util.FileUtils;
import com.wakedata.dw.open.Constants;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;

/**
 * 文件上传帮助类
 *
 * @author wanghu@wakedata.com
 * @date 2021/12/2
 * @since v1.0.0
 */
@Slf4j
@Component
public class FileUploadHelper {

    @Resource
    private FileStorageService storageService;

    /**
     * 上传临时文件的路径
     */
    private static final String TEMP_UPLOAD_FILE_PATH = "upload/temp";

    /**
     * 临时文件路径
     */
    private static final File TEMP_FILE_DIRECTORY = new File(TEMP_UPLOAD_FILE_PATH);

    /**
     * JSON文件后缀名
     */
    private static final String JSON_FILE_SUFFIX_NAME = ".json";

    /**
     * 支持上传的临时文件后缀名
     */
    private static final List<String> SUPPORTED_FILE_SUFFIX_OF_UPLOADS = new ArrayList<>();

    static {
        // 后续需要支持其他文件在此处添加
        SUPPORTED_FILE_SUFFIX_OF_UPLOADS.add(JSON_FILE_SUFFIX_NAME);
    }

    public ResultDTO<String> uploadImage(CommonsMultipartFile file) throws IOException {
        if (Objects.isNull(file)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        if (file.getSize() > Constants.FILE_SIZE_LIMIT_IMAGE_NEW) {
            throw new OpenException("图片最大只能上传5M！");
        }

        //是否是图片后缀
        String fileName = file.getFileItem().getName();
        ImageHeadEnum imageType = BaseEnumUtil.getEnumByValue(
                StringUtils.substringAfterLast(fileName, DwOpenConstant.JOIN_POINT), ImageHeadEnum.class);
        if (Objects.isNull(imageType)
                || (!ImageHeadEnum.existFileHead(FileUtils.getFileHeadHex(file.getInputStream())))) {
            throw new OpenException("请上传图片类型文件！");
        }
        UploadRequest uploadRequest = UploadRequest.builder().bucket(BucketEnum.MATERIAL).build();
        UploadResult upload = storageService.upload(uploadRequest, file);
        return ResultDTO.success(upload.getLocalUrl());
    }

    /**
     * 上传文件到临时路径
     *
     * @param uploadFile MultipartFile
     * @return 文件路径
     */
    public String uploadTempFile(MultipartFile uploadFile) {
        String fileName = checkUploadFile(uploadFile, Constants.TEMP_FILE_MAX_SIZE, uploadFile.getOriginalFilename());
        try {
            if (!TEMP_FILE_DIRECTORY.exists()) {
                FileUtil.mkdir(TEMP_FILE_DIRECTORY);
            }
            File tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), fileName.substring(fileName.lastIndexOf(".")), TEMP_FILE_DIRECTORY);
            uploadFile.transferTo(tempFile);
            FileUtil.touch(tempFile);
            return tempFile.getCanonicalPath();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_file_upload_error);
        }
    }

    /**
     * 上传base64
     */
    public String uploadBase64(String base64) {
        if (StringUtils.isBlank(base64)
                || base64.startsWith(Constants.HTTPS)
                || base64.startsWith(Constants.HTTP)) {
            return base64;
        }
        InputStream inputStream = FileUtils.base64ToInputStream(base64);
        UploadRequest uploadRequest = UploadRequest.builder().bucket(BucketEnum.MATERIAL).build();
        UploadResult upload = storageService.upload(uploadRequest, inputStream);
        return upload.getLocalUrl();
    }

    /**
     * 上传文件前校验
     *
     * @param uploadFile    MultipartFile
     * @param uploadMaxSize 上传文件最大限制
     * @param fileName      文件名
     */
    private String checkUploadFile(MultipartFile uploadFile, Long uploadMaxSize, String fileName) {
        if (Objects.isNull(uploadFile)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        // 校验文件大小
        if (uploadFile.getSize() > Constants.FILE_SIZE_LIMIT_IMAGE_NEW) {
            Long maxMbyte = uploadMaxSize / 1024L / 1024L;
            throw new OpenException(String.format("文件最大只能上传%sM！", maxMbyte));
        }
        // 校验文件名字、后缀
        if (org.apache.commons.lang3.StringUtils.isBlank(fileName)) {
            throw new OpenException(MsgCodeEnum.w_file_name_not_blank);
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!SUPPORTED_FILE_SUFFIX_OF_UPLOADS.contains(suffix.toLowerCase())) {
            throw new OpenException(MsgCodeEnum.w_file_name_suffix_not_supported);
        }
        return fileName;
    }

}
