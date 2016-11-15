package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * 薪资Entity
 * Created by kristain on 15/12/21.
 */
public class SearchSalary implements Serializable, NoObfuscateInterface {

    /**
     * 工资
     */
    private String salary;
    /**
     * 月份
     */
    private String workMonth;

    /**
     * 角色
     */
    private String roleName;


    public SearchSalary copy() {
        return JSON.parseObject(JSON.toJSONString(this), SearchSalary.class);
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getWorkMonth() {
        return workMonth;
    }

    public void setWorkMonth(String workMonth) {
        this.workMonth = workMonth;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
