package com.drjing.xibao.module.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 *
 * Created by kristain on 16/1/16.
 */
public class OrderEntity implements Serializable, NoObfuscateInterface {


    /**
     * 主键
     */
    private int id;

    /**
     * 页码
     */
    private int page;
    /**
     * 订单编号
     */
    private String code;
    /**
     * 顾客姓名
     */
    private String customerName;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 会员卡号
     */
    private String cardNumber;
    /**
     * 项目id
     */
    private String project_id;
    /**
     * 服务日期 - 开始
     */
    private String server_time_begin;
    /**
     * 服务日期 - 结束
     */
    private String server_time_end;
    /**
     * 下单日期-开始
     */
    private String order_time_begin;
    /**
     * 下单日期-结束
     */
    private String order_time_end;
    /**
     * 3表示会员，其他非会员
     */
    private String pay_type;
    /**
     * 门店id
     */
    private String store_id;
    /**
     * -2取消订单审核中，-1取消订单,0未支付,1待服务（如果是上门，一定是已经支付成功了），2开始服务，3服务完成，4待评价
     */
    private String orderTime;
    /**
     * 是优惠码 传值：1；否则不传
     */
    private String coupon_id;
    /**
     * 登录用户id
     */
    private String user_id;
    /**
     * 店名
     */
    private String storeName;
    /**
     * 美容师ID
     */
    private String staffId;
    /**
     * 美容师姓名
     */
    private String staffName;
    /**
     * 服务开始时间
     */
    private String startTime;
    /**
     * 服务结束时间
     */
    private String endTime;
    /**
     * 下单时间
     */
    private String addTime;
    /**
     * 报时时间开始
     */
    private String clockTimeStart;
    /**
     * 报时时间结束
     */
    private String clockTimeEnd;
    /**
     * 优惠码
     */
    private String couponCode;
    /**
     * 优惠金额
     */
    private String price;
    /**
     * 备注
     */
    private String orderNote;
    /**
     * 0上门，1到店
     */
    private String type;

    /**
     * all:我的订单 new:新增订单 finish:服务日志 revisit:回访日志 remind:提醒日志
     */
    private String search_type;

    /**
     * 上门具体地址
     */
    private String address;
    private String fee;
    private String lastFee;
    private String judge;
    private String isFree;
    private String customerId;
    private String del;
    private String scheduleNum;
    private String couponId;
    private String payStatus;
    private String cancelReason;
    private String memberPayStatus;
    private String refundStatus;
    /**
     * -2取消订单审核中，-1取消订单,0未支付,1待服务（如果是上门，一定是已经支付成功了），2开始服务，3服务完成，4待评价
     */
    private String status;

    /**
     * 订单类型 0:新增订单 1:我的订单
     */
    private String order_type;


    private String content;


    private int pageSize;


    private String customer;


    private String adviserName;

    private String adviserId;


    private String isVip;




    public OrderEntity copy() {
        return JSON.parseObject(JSON.toJSONString(this), OrderEntity.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getClockTimeStart() {
        return clockTimeStart;
    }

    public void setClockTimeStart(String clockTimeStart) {
        this.clockTimeStart = clockTimeStart;
    }

    public String getClockTimeEnd() {
        return clockTimeEnd;
    }

    public void setClockTimeEnd(String clockTimeEnd) {
        this.clockTimeEnd = clockTimeEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getServer_time_begin() {
        return server_time_begin;
    }

    public void setServer_time_begin(String server_time_begin) {
        this.server_time_begin = server_time_begin;
    }

    public String getServer_time_end() {
        return server_time_end;
    }

    public void setServer_time_end(String server_time_end) {
        this.server_time_end = server_time_end;
    }

    public String getOrder_time_begin() {
        return order_time_begin;
    }

    public void setOrder_time_begin(String order_time_begin) {
        this.order_time_begin = order_time_begin;
    }

    public String getOrder_time_end() {
        return order_time_end;
    }

    public void setOrder_time_end(String order_time_end) {
        this.order_time_end = order_time_end;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getLastFee() {
        return lastFee;
    }

    public void setLastFee(String lastFee) {
        this.lastFee = lastFee;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getScheduleNum() {
        return scheduleNum;
    }

    public void setScheduleNum(String scheduleNum) {
        this.scheduleNum = scheduleNum;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getMemberPayStatus() {
        return memberPayStatus;
    }

    public void setMemberPayStatus(String memberPayStatus) {
        this.memberPayStatus = memberPayStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }


    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getSearch_type() {
        return search_type;
    }

    public void setSearch_type(String search_type) {
        this.search_type = search_type;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }


    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }


    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    @Override
    public String toString() {
        return "page:"+page+",pageSize:"+pageSize+",type:"+type+",code:"+code+",customerName:"+customerName+"order_type:"+order_type;
    }
}
