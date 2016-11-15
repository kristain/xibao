package com.drjing.xibao.module.entity;

/**
 * 订单服务类型
 * Created by kristain on 16/1/21.
 */
public enum OrderServiceTypeEnum {

    STORETYPE("1", "到店服务"), HOMETYPE("0", "上门服务");

    public final String code;
    public final String name;

    OrderServiceTypeEnum(String code, String name)
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
        for (OrderServiceTypeEnum v : OrderServiceTypeEnum.values()) {
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
        for (OrderServiceTypeEnum v : OrderServiceTypeEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }

}
