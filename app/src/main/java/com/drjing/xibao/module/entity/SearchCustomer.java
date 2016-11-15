package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * 客户Entity
 * Created by kristain on 15/12/21.
 */
public class SearchCustomer implements Serializable, NoObfuscateInterface {


    public static final int SORT_GRADE = 0;  // 评分由高到低（默认按评分）

    public static final int TYPE_ALL = 0;
    public static final int TYPE_OUT = 1;
    public static final int TYPE_IN = 2;


    private String city; // 城市"beijing"
    private String custom_name;//客户姓名
    private String custom_phone;//客户联系方式
    private String beautician_name;//美容师姓名
    private String counselor_name;//顾问姓名
    private String shop_name;//店名
    private String custom_type;//客户类别

    private String id;
    private String name;
    private String mobile;
    private String vip;
    private String storeName;
    private String staffName;//返回美容师姓名
    private String adviser;//返回顾问姓名

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

    public String getBeautician_name() {
        return beautician_name;
    }

    public String getCounselor_name() {
        return counselor_name;
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

    public void setBeautician_name(String beautician_name) {
        this.beautician_name = beautician_name;
    }

    public void setCounselor_name(String counselor_name) {
        this.counselor_name = counselor_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }



    public SearchCustomer copy() {
        return JSON.parseObject(JSON.toJSONString(this), SearchCustomer.class);
    }

    public String getCustom_type() {
        return custom_type;
    }

    public void setCustom_type(String custom_type) {
        this.custom_type = custom_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getAdviser() {
        return adviser;
    }

    public void setAdviser(String adviser) {
        this.adviser = adviser;
    }
}
