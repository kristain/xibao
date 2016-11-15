package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 行动计划
 * Created by kristain on 16/1/16.
 */
public class ActionPlanEntity implements Serializable, NoObfuscateInterface {


    private int id;
    private String customerId;
    private String month;
    private String userId;
    private String storeId;
    private String list;
    private String targets;
    private String arriveTime;
    private String userName;
    private String customerName;
    private String amount;
    private String categoryName;
    private String name;
    private String categoryId;
    private String projectids;

    private String medical_product_text;
    private String health_product_text;
    private String consume_product_text;
    private String project_product_text;
    private String medical_product_amount;
    private String health_product_amount;
    private String consume_product_amount;
    private String project_product_amount;




    public ActionPlanEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), ActionPlanEntity.class);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProjectids() {
        return projectids;
    }

    public void setProjectids(String projectids) {
        this.projectids = projectids;
    }


    public String getMedical_product_text() {
        return medical_product_text;
    }

    public void setMedical_product_text(String medical_product_text) {
        this.medical_product_text = medical_product_text;
    }

    public String getHealth_product_text() {
        return health_product_text;
    }

    public void setHealth_product_text(String health_product_text) {
        this.health_product_text = health_product_text;
    }

    public String getConsume_product_text() {
        return consume_product_text;
    }

    public void setConsume_product_text(String consume_product_text) {
        this.consume_product_text = consume_product_text;
    }

    public String getProject_product_text() {
        return project_product_text;
    }

    public void setProject_product_text(String project_product_text) {
        this.project_product_text = project_product_text;
    }

    public String getMedical_product_amount() {
        return medical_product_amount;
    }

    public void setMedical_product_amount(String medical_product_amount) {
        this.medical_product_amount = medical_product_amount;
    }

    public String getHealth_product_amount() {
        return health_product_amount;
    }

    public void setHealth_product_amount(String health_product_amount) {
        this.health_product_amount = health_product_amount;
    }

    public String getConsume_product_amount() {
        return consume_product_amount;
    }

    public void setConsume_product_amount(String consume_product_amount) {
        this.consume_product_amount = consume_product_amount;
    }

    public String getProject_product_amount() {
        return project_product_amount;
    }

    public void setProject_product_amount(String project_product_amount) {
        this.project_product_amount = project_product_amount;
    }
}
