package com.witium.config;

import com.witium.util.KettleUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Desciption 实现Job接口
 * Create By  li.bo
 * CreateTime 2018/7/27 14:01
 * UpdateTime 2018/7/27 14:01
 */
@Slf4j
public class SchedulerQuartzJob implements Job {

    private String file;

    // 属性的setter方法，会将JobDataMap的属性自动注入
    public void setFile(String file) {
        this.file = file;
    }

    private void before() {
        log.info("任务开始执行");
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
        log.info("任务开始执行");
    }

    /**
     * 需要执行的业务
     */
    private void task() throws Exception {
        // 执行kettle任务
        KettleUtil.callNativeTrans(this.file);
    }

}
