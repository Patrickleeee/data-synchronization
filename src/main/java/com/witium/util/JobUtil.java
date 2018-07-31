package com.witium.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.witium.model.DynamicJob;
import com.witium.model.TaskJob;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Desciption 任务工具类
 * Create By  li.bo
 * CreateTime 2018/7/26 16:29
 * UpdateTime 2018/7/26 16:29
 */
public class JobUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(JobUtil.class.getName());

    private JobUtil() {
        throw new Error("Don't instance of " + getClass());
    }

    /**
     * 动态任务结果
     *
     * @param currentString 最新请求
     * @param lastString    上一次请求
     * @return
     */
    public static DynamicJob getJobs(String currentString, String lastString) {

        List<String> current = new ArrayList<>();
        List<String> last = new ArrayList<>();

        try {
            if (!StringUtils.isEmpty(currentString)) {
                current = JSONObject.parseArray(JSON.parseObject(currentString).getString("data"), String.class);
            }
            if (!StringUtils.isEmpty(lastString)) {
                last = JSONObject.parseArray(JSON.parseObject(lastString).getString("data"), String.class);
            }
        } catch (Exception e) {
            LOGGER.info("比较数据格式错误");
            e.printStackTrace();
            return null;
        }

        if (current.isEmpty() && last.isEmpty()) {
            return null;
        }

        List<TaskJob> delJobList = new ArrayList<>();
        List<TaskJob> addJobList = new ArrayList<>();
        // 任务全部下线
        if (current.isEmpty() && !last.isEmpty()) {
            delJobList = last.stream().map(job -> JSON.parseObject(job, TaskJob.class)).collect(toList());
            addJobList = null;
        } else if (!current.isEmpty() && last.isEmpty()) {// 首次加载，任务全部上线
            delJobList = null;
            addJobList = current.stream().map(job -> JSON.parseObject(job, TaskJob.class)).collect(toList());
        } else {
            // 交集-不做变化的任务
            List<String> intersection = current.stream().filter(last::contains).collect(toList());

            // 待删除的任务 = 上次 - 交集
            List<String> delJobs = last.stream().filter(item -> !intersection.contains(item)).collect(toList());
            delJobList = delJobs.stream().map(job -> JSON.parseObject(job, TaskJob.class)).collect(toList());

            // 待新增的任务 = 本次 - 交集
            List<String> addJobs = current.stream().filter(item -> !intersection.contains(item)).collect(toList());
            addJobList = addJobs.stream().map(job -> JSON.parseObject(job, TaskJob.class)).collect(toList());
        }

        LOGGER.info("【待删除的任务：{}】", (delJobList == null || delJobList.isEmpty()) ? "无" : delJobList.toString());
        LOGGER.info("【待新增的任务：{}】", (addJobList == null || addJobList.isEmpty()) ? "无" : addJobList.toString());

        DynamicJob result = new DynamicJob();
        result.setAddJobs(addJobList);
        result.setDelJobs(delJobList);
        return result;
    }
}
