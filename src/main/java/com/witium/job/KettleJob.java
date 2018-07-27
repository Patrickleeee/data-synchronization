package com.witium.job;

import com.alibaba.fastjson.JSONObject;
import com.witium.model.TaskJob;
import com.witium.service.KettleService;
import com.witium.util.JobUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Desciption 定时任务
 * Create By  li.bo
 * CreateTime 2018/7/26 15:41
 * UpdateTime 2018/7/26 15:41
 */
@Component
@EnableScheduling
public class KettleJob {

    private static Logger LOGGER = LoggerFactory.getLogger(KettleJob.class.getName());

    private String request = "";

    @Autowired
    private KettleService kettleService;

    @Scheduled(cron = "0 04 17 * * ?")
    public void runTask() throws Exception {
        LOGGER.info("【kettle定时任务运行开始】");
        String lastJobs = this.request;
        String jobs = kettleService.getKettleJobs();
        // 两次结果未发生变化，不做处理
        if (StringUtils.equals(lastJobs, jobs)) {
            return;
        }
        LOGGER.info("【接口任务有变化】");
        this.request = jobs;

        // TODO 有变化时，关闭相同的任务，开启新的任务


        LOGGER.info("【kettle定时任务运行结束】");
    }
}
