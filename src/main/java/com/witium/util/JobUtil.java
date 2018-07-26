package com.witium.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    public static JSONObject getJobs(String firstString, String secondString) {

        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();

        try {
            first = JSONObject.parseArray(JSON.parseObject(firstString).getString("data"), String.class);
            second = JSONObject.parseArray(JSON.parseObject(secondString).getString("data"), String.class);
        } catch (Exception e) {
            LOGGER.info("比较数据格式错误");
            e.printStackTrace();
            return null;
        }

        if (first.isEmpty() && second.isEmpty()) {
            return null;
        }

        // 交集
        List<String> intersection = first.stream().filter(second::contains).collect(toList());

        // 待删除的任务
        List<String> deleteJobs = first.stream().filter(item -> !intersection.contains(item)).collect(toList());

        // 待新增的任务
        List<String> addJobs = second.stream().filter(item -> !intersection.contains(item)).collect(toList());

        JSONObject result = new JSONObject();
        result.put("deleteJobs", deleteJobs);
        result.put("addJobs", addJobs);
        return result;
    }
}