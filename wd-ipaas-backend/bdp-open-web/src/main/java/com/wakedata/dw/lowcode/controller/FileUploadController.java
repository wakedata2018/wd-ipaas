package com.wakedata.dw.lowcode.controller;

import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.lowcode.helper.FileUploadHelper;
import com.wakedata.dw.open.annotation.AuditLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author wanghu
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/upload")
@Api(value = "文件上传", tags = "文件上传接口")
@Slf4j
public class FileUploadController {

    @Resource
    private FileUploadHelper fileUploadHelper;

    @ApiOperation("上传图片")
    @PostMapping(value = "/image")
    public ResultDTO<String> uploadImage(@RequestParam("file") CommonsMultipartFile file) throws IOException {
        return fileUploadHelper.uploadImage(file);
    }

    @ApiOperation(value = "上传临时文件，并返回文件路径信息", notes = "上传临时文件，并返回文件路径信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "需要上传的临时文件", paramType = "formData"),
    })
    @PostMapping(value = "/temp.file")
    public ResultDTO<String> uploadTempFile(@RequestParam("uploadFile") MultipartFile uploadFile) {
        return ResultDTO.success(fileUploadHelper.uploadTempFile(uploadFile));
    }

}
