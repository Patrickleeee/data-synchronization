package com.witium.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.witium.util.KettleUtil;
import com.witium.util.Okhttp3Util;
import com.witium.util.OssClientUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Desciption 实现Job接口-Job任务的执行逻辑
 * Create By  li.bo
 * CreateTime 2018/7/27 14:01
 * UpdateTime 2018/7/27 14:01
 */
@Slf4j
public class SchedulerQuartzJob implements Job {

    // 任务执行结果上传地址
    private String resultUrl;
    // 服务器暂存文件地址
    private String fileUrl;
    // kettle文件路径
    private String file;
    // 执行Job名称
    private String jobName;

    // 属性的setter方法，会将JobDataMap的属性自动注入
    public void setFile(String file) {
        this.file = file;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    private void before() {
        log.info("【任务{}执行开始】", this.jobName);
    }

    @Override
    public void execute(JobExecutionContext content) throws JobExecutionException {
        before();
        log.info("开始：" + System.currentTimeMillis());
        // 业务
        try {
            this.task();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("结束：" + System.currentTimeMillis());
        after();
    }

    private void after() {
        log.info("【任务{}执行结束】", this.jobName);
    }

    /**
     * 需要执行的业务
     */
    private void task() throws Exception {
        Long startTime = System.currentTimeMillis();

        // 执行kettle任务，将文件保存在"/tmp"目录下
        KettleUtil.callNativeTrans(this.file);
        // 将"/tmp"上传目录下的文件至OSS
        String key = this.uploadFile(this.jobName);
        if (StringUtils.isEmpty(key)) {
            log.info("【平台任务{}上传至OSS失败】", this.jobName);
            return;
        }
        log.info("【平台任务{}上传至OSS成功】", this.jobName);

        Long endTime = System.currentTimeMillis();

        // 将成功的key反馈
        String result = this.sendJobsResult(this.jobName, key, startTime, endTime);
        if (StringUtils.isNotBlank(result) && StringUtils.equals(JSON.parseObject(result).getString("state"), "200")) {
            log.info("【平台任务{}执行结果反馈至平台成功】", this.jobName);
        } else {
            log.info("【平台任务{}执行结果反馈至平台失败】", this.jobName);
        }

    }

    public String uploadFile(String fileName) {
        String bucketName = "aircompressor";

        //上传文件
//        String flilePathName =File.separator + this.fileUrl + File.separator + fileName + ".xls_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
        String filePathName =File.separator + this.fileUrl + File.separator + fileName + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
        File file = new File(filePathName);
        String diskName = "";
        String md5key = OssClientUtil.uploadFile(OssClientUtil.getOSSClient(), file, bucketName, diskName);
        log.info("上传后的文件MD5数字唯一签名:" + md5key);  //上传后的文件MD5数字唯一签名:A30B046A34EB326C4A3BBD784333B017

        return md5key;
    }

    public String sendJobsResult(String job, String key, Long startTime, Long endTime) {
        JSONObject request = new JSONObject();
        request.put("jobName", job);
        request.put("result", key);
        request.put("startTime", startTime);
        request.put("endTime", endTime);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, request.toString());

        String result = Okhttp3Util.post(this.resultUrl, body);
        return result;
    }

}
