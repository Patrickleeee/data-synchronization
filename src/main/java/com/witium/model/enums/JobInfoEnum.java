package com.witium.model.enums;

/**
 * Desciption JobInfo枚举类
 * Create By  li.bo
 * CreateTime 2018/8/1 10:04
 * UpdateTime 2018/8/1 10:04
 */
public enum JobInfoEnum {

    /**
     * 无Job任务
     */
    NO_JOB("0", "无Job任务"),
    /**
     * 循环额度借款
     */
    ERROR("1", "发生异常");

    private String value = null;
    private String code = null;

    private JobInfoEnum(String _code, String _value) {
        this.value = _value;
        this.code = _code;
    }

    public static JobInfoEnum getEnumByKey(String key) {
        for (JobInfoEnum e : JobInfoEnum.values()) {
            if (e.getCode().equals(key)) {
                return e;
            }
        }
        return null;
    }

    /** 获取value */
    public String getValue() {
        return value;
    }

    /** 获取code */
    public String getCode() {
        return code;
    }
}
