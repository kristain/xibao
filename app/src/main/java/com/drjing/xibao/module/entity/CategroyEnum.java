package com.drjing.xibao.module.entity;

/**
 * Created by kristain on 16/1/21.
 */
public enum CategroyEnum {

    CMS("cms", "文章"), SCHEDULE("schedule", "日程"), PROJECT("project", "项目");

    public final String code;
    public final String name;

    CategroyEnum(String code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }


    /**
     * 根据value值获取key
     * @param msg
     * @return
     */
    public static String getCodeByMsg(String msg){
        for (CategroyEnum v : CategroyEnum.values()) {
            if (v.getName().equals(msg)) {
                return v.getCode();
            }
        }
        return "";
    }

    /**
     * 根据key值获取value
     * @param code
     * @return
     */
    public static String getMsgByCode(String code){
        for (CategroyEnum v : CategroyEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
