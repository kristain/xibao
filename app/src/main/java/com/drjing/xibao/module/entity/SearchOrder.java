package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * 订单Entity
 * Created by kristain on 15/12/21.
 */
public class SearchOrder implements Serializable, NoObfuscateInterface {


    public static final int SORT_GRADE = 0;  // 评分由高到低（默认按评分）

    public static final int TYPE_ALL = 0;
    public static final int TYPE_OUT = 1;
    public static final int TYPE_IN = 2;


    private String city; // 城市"beijing"
    private String order_no;//订单号
    private String custom_name;//客户姓名
    private String custom_phone;//客户联系方式
    private String order_status;//订单状态
    private String service_type;//服务类型
    private String order_date;//订单时间
    private String shop_name;//店名
    private String custom_type;//客户类别

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCustom_name() {
        return custom_name;
    }

    public String getCustom_phone() {
        return custom_phone;
    }


    public String getShop_name() {
        return shop_name;
    }

    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }

    public void setCustom_phone(String custom_phone) {
        this.custom_phone = custom_phone;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public SearchOrder copy() {
        return JSON.parseObject(JSON.toJSONString(this), SearchOrder.class);
    }

    public String getCustom_type() {
        return custom_type;
    }

    public void setCustom_type(String custom_type) {
        this.custom_type = custom_type;
    }
}
