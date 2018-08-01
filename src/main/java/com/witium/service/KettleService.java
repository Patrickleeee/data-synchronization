package com.witium.service;

/**
 * Desciption kettle接口
 * Create By  li.bo
 * CreateTime 2018/7/26 15:13
 * UpdateTime 2018/7/26 15:13
 */
public interface KettleService {
    String getKettleJobs();

    String sendJobsResult(String job, String key);

    void sendHeartBeat();

    void executeJobs(String jobs, String lastJobs);

    String uploadFile(String fileName);
}
