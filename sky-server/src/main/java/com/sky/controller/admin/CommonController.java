package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传图片文件
     * @param file
     * @return
     */
    @PostMapping("upload")
    public Result<String> upload(MultipartFile file) {
        try {
            //获取文件的原始名
            String originalFilename = file.getOriginalFilename();
            //获取扩展名
            String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
            //重新生成文件名
            String objectName = UUID.randomUUID().toString() + extention;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("文件上传失败,{}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
