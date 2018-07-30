package com.witium.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.witium.config.QuartzScheduler;
import com.witium.model.DynamicJob;
import com.witium.model.TaskJob;
import com.witium.service.KettleService;
import com.witium.util.JobUtil;
import com.witium.util.Okhttp3Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

}
