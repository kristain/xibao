package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 日报数据
 * Created by kristain on 16/1/16.
 */
public class ReportEntity implements Serializable, NoObfuscateInterface {

    private int id;
    /**
     * 到店次数
     */
    private int counttostore;
    /**
     * 上门次数
     */
    private int counttodoor;
    /**
     * 账号
     */
    private String account;
    /**
     * 日期
     */
    private String calendarDay;
    /**
     * 护理日志次数
     */
    private int tagsNursing;
    /**
     * 提醒日志次数
     */
    private int tagsRemind;
    /**
     * 回访日志次数
     */
    private int tagsRevisit;
    /**
     * 激活日志次数
     */
    private int tagsActive;
    /**
     * 产品销售金额
     */
    private double saleProject;
    /**
     *生美预收金额
     */
    private double saleHealthBeauty;
    /**
     *医美预收金额
     */
    private double saleMedicalBeauty;
    /**
     *消耗金额
     */
    private double saleConsume;
    /**
     *订单备注
     */
    private String orderNote;
    /**
     *销售日志次数
     */
    private int saleLog;
    /**
     *投诉数量
     */
    private int complaint;

    private String content;
    private String dId;
    private String tagId;

    private String tagName;
    private String updateTime;


    private String day;
    private int pageSize;
    private String storeid;

    private String haveSubmit;
    private String notSubmit;

    private String username;
    private String roleName;
    private String count;
    private String uid;



    public ReportEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), ReportEntity.class);
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getCounttostore() {
        return counttostore;
    }

    public void setCounttostore(int counttostore) {
        this.counttostore = counttostore;
    }

    public int getCounttodoor() {
        return counttodoor;
    }

    public void setCounttodoor(int counttodoor) {
        this.counttodoor = counttodoor;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(String calendarDay) {
        this.calendarDay = calendarDay;
    }

    public int getTagsNursing() {
        return tagsNursing;
    }

    public void setTagsNursing(int tagsNursing) {
        this.tagsNursing = tagsNursing;
    }

    public int getTagsRemind() {
        return tagsRemind;
    }

    public void setTagsRemind(int tagsRemind) {
        this.tagsRemind = tagsRemind;
    }

    public int getTagsRevisit() {
        return tagsRevisit;
    }

    public void setTagsRevisit(int tagsRevisit) {
        this.tagsRevisit = tagsRevisit;
    }

    public int getTagsActive() {
        return tagsActive;
    }

    public void setTagsActive(int tagsActive) {
        this.tagsActive = tagsActive;
    }

    public double getSaleProject() {
        return saleProject;
    }

    public void setSaleProject(double saleProject) {
        this.saleProject = saleProject;
    }

    public double getSaleHealthBeauty() {
        return saleHealthBeauty;
    }

    public void setSaleHealthBeauty(double saleHealthBeauty) {
        this.saleHealthBeauty = saleHealthBeauty;
    }

    public double getSaleMedicalBeauty() {
        return saleMedicalBeauty;
    }

    public void setSaleMedicalBeauty(double saleMedicalBeauty) {
        this.saleMedicalBeauty = saleMedicalBeauty;
    }

    public double getSaleConsume() {
        return saleConsume;
    }

    public void setSaleConsume(double saleConsume) {
        this.saleConsume = saleConsume;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public int getSaleLog() {
        return saleLog;
    }

    public void setSaleLog(int saleLog) {
        this.saleLog = saleLog;
    }

    public int getComplaint() {
        return complaint;
    }

    public void setComplaint(int complaint) {
        this.complaint = complaint;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getHaveSubmit() {
        return haveSubmit;
    }

    public void setHaveSubmit(String haveSubmit) {
        this.haveSubmit = haveSubmit;
    }

    public String getNotSubmit() {
        return notSubmit;
    }

    public void setNotSubmit(String notSubmit) {
        this.notSubmit = notSubmit;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


