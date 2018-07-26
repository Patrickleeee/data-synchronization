package com.witium.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.witium.service.KettleService;
import com.witium.util.Okhttp3Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

}
