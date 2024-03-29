package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

   @Value("${reggie.path}")
    private String basePath;

    /*
    文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取后缀 .后面  suffix:后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止名字重复
        String name = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //不存在则创建目录
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath+name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(name);
    }
    /*
    文件下载
     */

    @GetMapping("/download")
    public void download (String  name , HttpServletResponse response){
        try {
        //输入流 ，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath +name));

        //输出流，将文件展示在浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ( (len =fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes ,0 , len);
                outputStream.flush();  //刷新
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
