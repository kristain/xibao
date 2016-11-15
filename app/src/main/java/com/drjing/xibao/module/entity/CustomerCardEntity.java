package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 *
 * Created by kristain on 16/1/16.
 */
public class CustomerCardEntity implements Serializable, NoObfuscateInterface {


    /**
     * 主键
     */
    private int id;

    private String cardId;
    private String mobile;
    /**
     * 卡号
     */
    private String cardNumber;
    private String status;
    private String discount;
    /**
     * 现金余额
     */
    private String cashBalance;
    private String customerId;
    /**
     * 会员年限
     */
    private String createTime;
    /**
     * 最近消费时间
     */
    private String lastPay;
    /**
     * 产品余额
     */
    private String productBalance;
    /**
     * 项目余额
     */
    private String projectBalance;
    /**
     * 疗程余额
     */
    private String courseBalance;
    /**
     * 项目折扣
     */
    private String projectDis;
    /**
     * 产品折扣
     */
    private String productDis;
    /**
     * 持卡人
     */
    private String customerName;




    public CustomerCardEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), CustomerCardEntity.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(String cashBalance) {
        this.cashBalance = cashBalance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastPay() {
        return lastPay;
    }

    public void setLastPay(String lastPay) {
        this.lastPay = lastPay;
    }

    public String getProductBalance() {
        return productBalance;
    }

    public void setProductBalance(String productBalance) {
        this.productBalance = productBalance;
    }

    public String getProjectBalance() {
        return projectBalance;
    }

    public void setProjectBalance(String projectBalance) {
        this.projectBalance = projectBalance;
    }

    public String getCourseBalance() {
        return courseBalance;
    }

    public void setCourseBalance(String courseBalance) {
        this.courseBalance = courseBalance;
    }

    public String getProjectDis() {
        return projectDis;
    }

    public void setProjectDis(String projectDis) {
        this.projectDis = projectDis;
    }

    public String getProductDis() {
        return productDis;
    }

    public void setProductDis(String productDis) {
        this.productDis = productDis;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
