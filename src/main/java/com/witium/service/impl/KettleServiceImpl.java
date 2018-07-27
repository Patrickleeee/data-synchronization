package com.witium.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.witium.config.QuartzScheduler;
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
     * 获取定时任务接口
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

    public void executeJobs(String jobs, String lastJobs) {

        Map<String, List<TaskJob>> changeJobs = JobUtil.getJobs(jobs, lastJobs);
        if (changeJobs.isEmpty()) {
            return;
        }

        // 删除任务
        List<TaskJob> deleteJobs = changeJobs.get("deleteJobs");
        deleteJobs.forEach(job -> {
            try {
                quartzScheduler.deleteJob(job.getName(), "group");
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

        // 新增任务
        List<TaskJob> addJobs = changeJobs.get("addJobs");

        // TODO OSS下载文件
        addJobs.forEach(job -> {
            try {
                quartzScheduler.startJob(job.getFile(), job.getCorn(), job.getName(), "group");
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
    }

}
