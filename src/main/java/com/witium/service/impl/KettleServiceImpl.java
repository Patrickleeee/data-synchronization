package com.witium.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.witium.config.QuartzScheduler;
import com.witium.model.DynamicJob;
import com.witium.model.enums.JobInfoEnum;
import com.witium.service.KettleService;
import com.witium.util.JobUtil;
import com.witium.util.Okhttp3Util;
import com.witium.util.OssClientUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Desciption kettle服务
 * Create By  li.bo
 * CreateTime 2018/7/26 15:15
 * UpdateTime 2018/7/26 15:15
 */
@Service
@Slf4j
public class KettleServiceImpl implements KettleService {

    @Autowired
    private QuartzScheduler quartzScheduler;

    // 定时任务接口地址
    @Value("${job.requestUrl}")
    private String requestUrl;

    // 任务执行结果上传地址
    @Value("${job.resultUrl}")
    private String resultUrl;

    @Value("${job.heartBeatUrl}")
    private String heartBeatUrl;

    // 服务器暂存文件地址
    @Value("${file.url}")
    private String fileUrl;

    /**
     * 接口调用-获取定时任务接口
     *
     * @return
     */
    @Override
    public String getKettleJobs() {

        JSONObject request = new JSONObject();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, request.toString());

        String result = Okhttp3Util.post(this.requestUrl, body);
        return result;
    }

    /**
     * 接口调用-反馈回调
     *
     * @param job
     * @param key
     * @return
     */
    @Override
    public String sendJobsResult(String job, String key) {
        JSONObject request = new JSONObject();
        request.put("jobName", job);
        request.put("result", key);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, request.toString());

        String result = Okhttp3Util.post(this.resultUrl, body);
        return result;
    }

    /**
     * 接口调用-发送心跳
     */
    @Override
    public void sendHeartBeat() {
        List<String> infos = new ArrayList<>();
        try {
            infos = quartzScheduler.getAllJobInfos();
            // 无任务，不发送心跳
            if (infos == null || infos.isEmpty()) {
                return ;
            }
            // 发送心跳
            infos.forEach(job -> {
                JSONObject request = new JSONObject();
                request.put("jobName", job);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, request.toString());
                String result = Okhttp3Util.post(this.requestUrl, body);
                if (!StringUtils.isEmpty(result)
                        && JSONObject.parseObject(result) != null
                        && JSONObject.parseObject(result).getString("state").equals("200")) {
                    log.info("【定时任务{}心跳发送成功】", job);
                } else {
                    log.info("【定时任务{}心跳发送报错：{}】", job, JSONObject.parseObject(result).getString("message"));
                }
            });
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 任务发生变化时，执行变更（没有发生变化的不做处理）
     *
     * @param jobs
     * @param lastJobs
     */
    @Override
    public void executeJobs(String jobs, String lastJobs) {

        DynamicJob changeJobs = JobUtil.getJobs(jobs, lastJobs);

        // 删除任务
        if (changeJobs != null && changeJobs.getDelJobs() != null && !changeJobs.getDelJobs().isEmpty()) {
            changeJobs.getDelJobs().forEach(job -> {
                try {
                    quartzScheduler.deleteJob(job.getName(), "group");
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            });
        }

        // 开启新增Job
        if (changeJobs != null && changeJobs.getAddJobs() != null && !changeJobs.getAddJobs().isEmpty()) {
            changeJobs.getAddJobs().forEach(job -> {
                try {
                    quartzScheduler.startJob(job.getFile(), job.getCorn(), job.getName(), "group");
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 上传文件至oss
     *
     * @param fileName
     * @return
     */
    @Override
    public String uploadFile(String fileName) {
        String bucketName = "aircompressor";

        //上传文件
        String flilePathName = this.fileUrl + "/" + fileName + ".xls_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File(flilePathName);
        String diskName = "";
        String url = OssClientUtil.uploadFile(OssClientUtil.getOSSClient(), file, bucketName, diskName);
        log.info("上传后的文件下载地址:" + url);  //上传后的文件MD5数字唯一签名:A30B046A34EB326C4A3BBD784333B017

        return url;
    }

}
