package com.witium.controller;

import com.aliyun.oss.OSSClient;
import com.witium.config.QuartzScheduler;
import com.witium.util.OssClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Desciption 动态任务封装API
 * Create By  li.bo
 * CreateTime 2018/7/27 14:43
 * UpdateTime 2018/7/27 14:43
 */
@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzApiController {
    @Autowired
    private QuartzScheduler quartzScheduler;

    @RequestMapping("/start")
    public void startQuartzJob() {
        try {
            quartzScheduler.startJob("https://aircompressor.oss-cn-shanghai.aliyuncs.com/file/135e43970e334806b9b7274ebba385ee.ktr", "0 2 15 * * ?", "job", "group");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/info/{name}/{group}")
    public String getQuartzJob(@PathVariable("name") String name, @PathVariable("group") String group) {
        String info = null;
        try {
            info = quartzScheduler.getJobInfo(name, group);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return info;
    }

    @RequestMapping("/modify")
    public boolean modifyQuartzJob(String name, String group, String time) {
        boolean flag = true;
        try {
            flag = quartzScheduler.modifyJob(name, group, time);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @RequestMapping(value = "/pause")
    public void pauseQuartzJob(String name, String group) {
        try {
            quartzScheduler.pauseJob(name, group);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/pauseAll")
    public void pauseAllQuartzJob() {
        try {
            quartzScheduler.pauseAllJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/delete")
    public void deleteJob(String name, String group) {
        try {
            quartzScheduler.deleteJob(name, group);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/test")
    public void test() {

        OSSClient client = OssClientUtil.getOSSClient();
        String bucketName = "aircompressor";

        //上传文件
        String flilePathName = "C:/Users/lenovo/Pictures/cat/173314_2BYo_1428332.jpg";
        File file = new File(flilePathName);
        String diskName = "test/images";
        String md5key = OssClientUtil.uploadFile(client, file, bucketName, diskName);
        log.info("上传后的文件MD5数字唯一签名:" + md5key);  //上传后的文件MD5数字唯一签名:A30B046A34EB326C4A3BBD784333B017
    }

}
