package com.witium.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Desciption 任务实体
 * Create By  li.bo
 * CreateTime 2018/7/26 16:40
 * UpdateTime 2018/7/26 16:40
 */
@NoArgsConstructor
@Accessors(chain = true)
@Setter
@Getter
@ToString
public class TaskJob {

    private String name;
    private String corn;
    private String file;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskJob) {
            TaskJob job = (TaskJob) obj;
            return this.name.equals(job.getName())
                    && this.corn.equals(job.getCorn())
                    && this.file.equals(job.getFile());
        }
        return false;
    }

}
