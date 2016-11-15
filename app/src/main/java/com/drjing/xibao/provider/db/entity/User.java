package com.drjing.xibao.provider.db.entity;

import com.android.library.third.ormlite.field.DatabaseField;
import com.android.library.third.ormlite.table.DatabaseTable;

/**
 * Created by kristain on 16/1/23.
 */
@DatabaseTable(tableName = "User")
public class User extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1270221627651225756L;



    /**
     * 登录账号
     */
    @DatabaseField
    private String accountname;

    /**
     * 密码
     */
    @DatabaseField
    private String password;

    /**
     * 电子邮箱
     */
    @DatabaseField
    private String email;


    /**
     * 角色
     */
    @DatabaseField
    private String roleKey;

    /**
     * 用户昵称
     */
    @DatabaseField
    private String username;



    /**
     * 门店名称
     */
    @DatabaseField
    private String store_name;

    /**
     * 门店ID
     */
    @DatabaseField
    private String store_id;

    /**
     * 性别
     */
    @DatabaseField
    private String sex;

    /**
     * 头像
     */
    @DatabaseField
    private String avatar;

    /**
     * cookie值
     */
    @DatabaseField
    private String sid;

    /**
     * 记住密码
     */
    @DatabaseField
    private String remberPasswd;

    /**
     * 手机号码
     */
    @DatabaseField
    private String mobile;

    /**
     * 手机号码
     */
    @DatabaseField
    private String logo;

    /**
     * 公司ID
     */
    @DatabaseField
    private String company_id;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getRemberPasswd() {
        return remberPasswd;
    }

    public void setRemberPasswd(String remberPasswd) {
        this.remberPasswd = remberPasswd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString()
    {
        return "User [id=" + id + ", accountname=" + accountname + ", roleKey=" + roleKey
                + "company_id＝"+company_id+"]";
    }
}
