package com.drjing.xibao.module.entity;

/**
 * Created by kristain on 16/1/21.
 */
public enum RoleEnum {

    STAFF("B", "美容师"), CONSULTANT("C", "顾问"), STOREMANAGER("S", "店长"),AREAMANAGER("R", "区域总经理"),BOSS("O","老板");

    public final String code;
    public final String name;

    RoleEnum(String code, String name)
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
        for (RoleEnum v : RoleEnum.values()) {
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
        for (RoleEnum v : RoleEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
