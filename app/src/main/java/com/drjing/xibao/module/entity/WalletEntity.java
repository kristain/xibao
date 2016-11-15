package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 *
 * Created by kristain on 16/1/16.
 */
public class WalletEntity implements Serializable, NoObfuscateInterface {

    /**
     * 金额
     */
    private double amount;
    /**
     * 项目
     */
    private String projectName;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 日期
     */
    private long editTime;
    /**
     * 提成点数
     */
    private double percent;
    /**
     * 主键
     */
    private int id;
    /**
     * 分类id
     */
    private int categoryId;
    /**
     *
     */
    private String beginEditTime;
    private String endEditTime;
    /**
     * 页码
     */
    private int page;

    private String accountname;


    private String Addtime;


    public WalletEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), WalletEntity.class);
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getBeginEditTime() {
        return beginEditTime;
    }

    public void setBeginEditTime(String beginEditTime) {
        this.beginEditTime = beginEditTime;
    }

    public String getEndEditTime() {
        return endEditTime;
    }

    public void setEndEditTime(String endEditTime) {
        this.endEditTime = endEditTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAddtime() {
        return Addtime;
    }

    public void setAddtime(String addtime) {
        Addtime = addtime;
    }
}
