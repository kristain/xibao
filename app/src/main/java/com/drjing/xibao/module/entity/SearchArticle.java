package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * 咨询文章Entity
 * Created by kristain on 15/12/21.
 */
public class SearchArticle implements Serializable, NoObfuscateInterface {


    public static final int SORT_GRADE = 0;  // 评分由高到低（默认按评分）

    public static final int TYPE_ALL = 0;
    public static final int TYPE_OUT = 1;
    public static final int TYPE_IN = 2;


    private String article_title;
    private String article_url;




    public SearchArticle copy() {
        return JSON.parseObject(JSON.toJSONString(this), SearchArticle.class);
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_url() {
        return article_url;
    }

    public void setArticle_url(String article_url) {
        this.article_url = article_url;
    }
}
