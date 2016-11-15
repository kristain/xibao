package com.drjing.xibao.module.entity;

/**
 * Created by kristain on 16/1/21.
 */
public enum TargetTypeEnum {

    RECEIVE("1", "生美预收"), SALE("2", "医美预收"), PROJECT("3", "产品目标"),CONSUME("4", "消耗目标");

    public final String code;
    public final String name;

    TargetTypeEnum(String code, String name)
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
        for (TargetTypeEnum v : TargetTypeEnum.values()) {
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
        for (TargetTypeEnum v : TargetTypeEnum.values()) {
            if (v.getCode().equals(code)) {
                return v.getName();
            }
        }
        return "";
    }
}
