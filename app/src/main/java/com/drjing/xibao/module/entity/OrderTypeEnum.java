package com.drjing.xibao.module.entity;

/**
 * 订单类型
 * Created by kristain on 16/1/21.
 */
public enum OrderTypeEnum {

    NEWORDER("new", "新增订单"), MYORDER("all", "我的订单"),
    SERVICEORDER("finish","服务日志"),REVISITORDER("revisit","回访日志"),
    REMINDORDER("remind","提醒日志"),UNREMARK("remark","未备注的订单"),
        SALELOG("salelog","未确认销售日志"),ACTIVATE("activate","激活日志");

    public final String code;
    public final String name;

    OrderTypeEnum(String code, String name)
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
        for (OrderTypeEnum v : OrderTypeEnum.values()) {
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
        for (OrderTypeEnum v : OrderTypeEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
