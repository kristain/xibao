package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 日程
 * Created by kristain on 16/1/16.
 */
public class ScheduleEntity implements Serializable, NoObfuscateInterface {

    private int id;
    /**
     * 分类id
     */
    private String categoryId;
    /**
     * 提醒内容
     */
    private String content;
    /**
     * yyyy-MM-dd
     */
    private String alert_date;
    /**
     * 提醒周期
     */
    private String remind_period;
    /**
     * 提前几天提醒
     */
    private String remind_before;


    private String categoryName;

    /**
     * 返回字段
     */
    private String alertDate;


    private String remindBefore;

    private String remindPeriod;


    public ScheduleEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), ScheduleEntity.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAlert_date() {
        return alert_date;
    }

    public void setAlert_date(String alert_date) {
        this.alert_date = alert_date;
    }

    public String getRemind_period() {
        return remind_period;
    }

    public void setRemind_period(String remind_period) {
        this.remind_period = remind_period;
    }

    public String getRemind_before() {
        return remind_before;
    }

    public void setRemind_before(String remind_before) {
        this.remind_before = remind_before;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(String alertDate) {
        this.alertDate = alertDate;
    }

    public String getRemindBefore() {
        return remindBefore;
    }

    public void setRemindBefore(String remindBefore) {
        this.remindBefore = remindBefore;
    }

    public String getRemindPeriod() {
        return remindPeriod;
    }

    public void setRemindPeriod(String remindPeriod) {
        this.remindPeriod = remindPeriod;
    }
}
