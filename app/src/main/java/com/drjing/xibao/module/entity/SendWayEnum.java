package com.drjing.xibao.module.entity;

/**
 * Created by kristain on 16/1/21.
 */
public enum SendWayEnum {

    WECHAT("wechat", "微信发送"), CALL("call", "打电话"), MESSAGE("message", "短信发送");

    public final String code;
    public final String name;

    SendWayEnum(String code, String name)
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
        for (SendWayEnum v : SendWayEnum.values()) {
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
        for (SendWayEnum v : SendWayEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
