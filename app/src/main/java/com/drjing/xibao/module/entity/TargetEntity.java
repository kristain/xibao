package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 设定目标
 * Created by kristain on 16/1/16.
 */
public class TargetEntity implements Serializable, NoObfuscateInterface {

    private int id;
    private String month;
    private String categoryId;
    private String cateName;
    private String roleName;
    private String username;
    private String storeId;
    private String amount;
    /**
     * 用户id
     */
    private String uid;

    /**
     * 1:生美数据;2医美;3产品;4消耗;不传获取所有
     */
    private String type;

    private String account;

    private String target_health_beauty;

    private String target_medical_beauty;
    private String target_project;
    private String target_consume;
    private String action_health_beauty;
    private String action_medical_beauty;
    private String action_project;
    private String action_consume;

    private String sale_health_beauty;
    private String sale_medical_beauty;
    private String sale_project;
    private String sale_consume;



    public TargetEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), TargetEntity.class);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTarget_health_beauty() {
        return target_health_beauty;
    }

    public void setTarget_health_beauty(String target_health_beauty) {
        this.target_health_beauty = target_health_beauty;
    }

    public String getTarget_medical_beauty() {
        return target_medical_beauty;
    }

    public void setTarget_medical_beauty(String target_medical_beauty) {
        this.target_medical_beauty = target_medical_beauty;
    }

    public String getTarget_project() {
        return target_project;
    }

    public void setTarget_project(String target_project) {
        this.target_project = target_project;
    }

    public String getTarget_consume() {
        return target_consume;
    }

    public void setTarget_consume(String target_consume) {
        this.target_consume = target_consume;
    }

    public String getAction_health_beauty() {
        return action_health_beauty;
    }

    public void setAction_health_beauty(String action_health_beauty) {
        this.action_health_beauty = action_health_beauty;
    }

    public String getAction_medical_beauty() {
        return action_medical_beauty;
    }

    public void setAction_medical_beauty(String action_medical_beauty) {
        this.action_medical_beauty = action_medical_beauty;
    }

    public String getAction_project() {
        return action_project;
    }

    public void setAction_project(String action_project) {
        this.action_project = action_project;
    }

    public String getAction_consume() {
        return action_consume;
    }

    public void setAction_consume(String action_consume) {
        this.action_consume = action_consume;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getSale_health_beauty() {
        return sale_health_beauty;
    }

    public void setSale_health_beauty(String sale_health_beauty) {
        this.sale_health_beauty = sale_health_beauty;
    }

    public String getSale_medical_beauty() {
        return sale_medical_beauty;
    }

    public void setSale_medical_beauty(String sale_medical_beauty) {
        this.sale_medical_beauty = sale_medical_beauty;
    }

    public String getSale_project() {
        return sale_project;
    }

    public void setSale_project(String sale_project) {
        this.sale_project = sale_project;
    }

    public String getSale_consume() {
        return sale_consume;
    }

    public void setSale_consume(String sale_consume) {
        this.sale_consume = sale_consume;
    }
}
