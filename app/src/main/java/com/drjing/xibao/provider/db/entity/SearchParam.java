package com.drjing.xibao.provider.db.entity;

import com.android.library.third.ormlite.field.DatabaseField;
import com.android.library.third.ormlite.table.DatabaseTable;

/**
 * 查询字段
 * Created by kristain on 16/1/23.
 */
@DatabaseTable(tableName = "Search")
public class SearchParam extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1270221627651225758L;

    /**
     * mobile
     */
    @DatabaseField
    private String mobile;

    /**
     * name
     */
    @DatabaseField
    private String name;


    /**
     * type
     */
    @DatabaseField
    private String type;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
}
