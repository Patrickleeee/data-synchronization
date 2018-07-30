package com.witium.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Desciption 动态任务列表
 * Create By  li.bo
 * CreateTime 2018/7/30 15:11
 * UpdateTime 2018/7/30 15:11
 */
@NoArgsConstructor
@Accessors(chain = true)
@Setter
@Getter
@ToString
public class DynamicJob {

    private List<TaskJob> addJobs;  // 待新增任务
    private List<TaskJob> delJobs;  // 待删除任务
}
