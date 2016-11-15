package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 客户
 * Created by kristain on 16/1/16.
 */
public class CustomerEntity implements Serializable, NoObfuscateInterface {



    private int page;
    /**
     * 1:我服务过的; 其他：我的客户
     */
    private int type;

    /**
     * 顾客姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;


    private int id;
    /**
     * 美容师名字
     */
    private String staffName;
    /**
     * 店名
     */
    private String storeName;
    /**
     * 是否会员
     */
    private int vip;

    /**
     * 美容师ID
     */
    private String staff_id;

    private int days;
    private int persons;

    //客户id
    private int customerId;

    /**
     * 分配的客户ids
     */
    private String customerIds;

    //登录账户
    private String  accountName;


    private String order_id;


    private String roleKey;

    public CustomerEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), CustomerEntity.class);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }


    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }


    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(String customerIds) {
        this.customerIds = customerIds;
    }
}
