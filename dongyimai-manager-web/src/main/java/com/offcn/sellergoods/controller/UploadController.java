package com.offcn.sellergoods.controller;

import com.offcn.entity.Result;
import com.offcn.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController

public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        String filename=file.getOriginalFilename();
        String extName=filename.substring(filename.indexOf(".")+1);
        try {
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:fastdfs_client.conf");
            String s=fastDFSClient.uploadFile(file.getBytes(),extName,null);
            String path=FILE_SERVER_URL+"/"+s;
            return new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传文件失败");
        }
    }
}
