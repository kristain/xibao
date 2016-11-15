package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 门店
 * Created by kristain on 16/1/16.
 */
public class StoreEntity implements Serializable, NoObfuscateInterface {

    /**
     * 门店ID
     */
    private int id;

    /**
     * 门店名称
     */
    private String name;


    private String companyid;

    /**
     *
     */
    private String shopowner;



    public StoreEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), StoreEntity.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getShopowner() {
        return shopowner;
    }

    public void setShopowner(String shopowner) {
        this.shopowner = shopowner;
    }
}
