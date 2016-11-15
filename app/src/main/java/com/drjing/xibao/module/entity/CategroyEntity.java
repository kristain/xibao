package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 *
 * Created by kristain on 16/1/16.
 */
public class CategroyEntity implements Serializable, NoObfuscateInterface {

    private String catetype;//文章：cms;日程分类:schedule;项目：project
    private String id;
    private String parentId;
    private String type;
    private String name;
    private String updateTime;


    public CategroyEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), CategroyEntity.class);
    }

    public String getCatetype() {
        return catetype;
    }

    public void setCatetype(String catetype) {
        this.catetype = catetype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
