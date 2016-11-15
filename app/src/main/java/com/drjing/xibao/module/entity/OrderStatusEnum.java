package com.drjing.xibao.module.entity;

/**
 * 订单状态
 * Created by kristain on 16/1/21.
 */
public enum OrderStatusEnum {

    CANCELAUDIT("-2", "取消订单审核中"), CANCEL("-1", "取消订单"),
    UNPAY("0", "未支付"), UNSERVED("1", "待服务"),
    SERVED("2", "开始服务"), COMPELTE("3", "服务完成"),
    UNEVALUATE("4", "待评价");

    public final String code;
    public final String name;

    OrderStatusEnum(String code, String name)
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
        for (OrderStatusEnum v : OrderStatusEnum.values()) {
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
        for (OrderStatusEnum v : OrderStatusEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
