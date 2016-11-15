package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 问题反馈
 * Created by kristain on 16/1/16.
 */
public class QuestionEntity implements Serializable, NoObfuscateInterface {

    private int id;
    private String images;
    private String content;

    private String localPath;



    public QuestionEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), QuestionEntity.class);
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
