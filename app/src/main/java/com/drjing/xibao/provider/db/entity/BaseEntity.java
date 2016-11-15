package com.drjing.xibao.provider.db.entity;

import com.android.library.third.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * 实体基类
 * Created by kristain on 15/12/19.
 */
public  abstract class BaseEntity implements Serializable {


    /**
     */
    private static final long serialVersionUID = 6337104618534280060L;

    /**
     * 主键ID
     */
    @DatabaseField(id=true)
    protected String id;

    /**
     * 备注
     */
    @DatabaseField
    protected String remark;

    /**
     * 版本号
     */
    @DatabaseField(defaultValue="1")
    protected Integer version;

    /**
     * 是否有效
     */
    @DatabaseField(defaultValue="true")
    protected Boolean valid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
