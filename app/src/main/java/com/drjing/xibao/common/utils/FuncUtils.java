package com.drjing.xibao.common.utils;

import android.util.Log;

import com.drjing.xibao.R;
import com.drjing.xibao.module.entity.RoleEnum;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by kristain on 16/1/21.
 */
public class FuncUtils {


    /**
     * 获取唯一标识uuid
     *
     * @return
     */
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 功能描述：金额校验，必须大于0.01
     *
     * @param money
     * @return
     */
    public static boolean isMoney(String money) {
        if (StringUtils.isEmpty(money)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{0,}[.]{0,1}[0-9]{0,2}$");
        if (!pattern.matcher(money).matches()) {
            return false;
        }
        if (Double.parseDouble(money) < 0.01) {
            return false;
        }
        return true;
    }

    public static boolean isMoneyNolimit(String money) {
        if (StringUtils.isEmpty(money)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{0,}[.]{0,1}[0-9]{0,100}$");
        if (!pattern.matcher(money).matches()) {
            return false;
        }
        return true;
    }


    /**
     * 根据产品名称获取钱包分割线色值
     * @param categoryId
     * @return
     */
    public static int toWalletColorByCategory(int categoryId){
        if(categoryId==11){
            return R.color.wallet_product_color;
        }else if (categoryId==9){
            return R.color.wallet_other_color;
        }else{
            return R.color.wallet_consumer_color;
        }
    }

    /**
     * 金额相减
     * @param amt_limit
     * @param amt 支付金额
     * @return
     */
    public static double amt_sub(String amt_limit, String amt){
        if (!isMoney(amt_limit) || !isMoney(amt_limit)) {
            return 0;
        }
        return Double.parseDouble(amt_limit) - Double.parseDouble(amt);
    }


    public static String formatMonth(String date){
        if(StringUtils.isEmpty(date)){
            return "";
        }
        String month = date.substring(date.indexOf("年")+1);
        Log.e("TAG",month);
        if(month.length()==2){
            month="0"+month;
        }
        return date.substring(0,4)+""+month.replace("月","");
    }

    /**
     * 订单列表服务时间文案
     * 1970-01-18 15:00-15:30
     * @param starttime
     * @param endtime
     * @return
     */
    public static String getOrderServerTime(String starttime,String endtime){
        if(StringUtils.isEmpty(starttime) || StringUtils.isEmpty(endtime)){
            return "";
        }
        String server_starttime = DateTimeUtils.formatDateByMill(Long.parseLong(starttime));
        String server_endtime = DateTimeUtils.formatDateByMill(Long.parseLong(endtime));
        if(!StringUtils.isEmpty(server_endtime)&&server_endtime.length()==16){
            server_endtime = server_endtime.substring(11,16);
        }
        return server_starttime+"-"+server_endtime;
    }


    /**
     * 格式化手机号码
     * 格式：135 8888 8888
     * @param phone
     * @return
     */
    public static String formatPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return "";
        }
        if(!isCellPhone(phone)){
            return phone;
        }

        return phone.substring(0, 3)+" "+phone.substring(3,7)+" "+phone.substring(7);
    }


    /**
     * 手机号码检查
     * @param str
     * @return
     */
    public static boolean isCellPhone(String str)
    {
        if (StringUtils.isEmpty(str))
            return false;
        return Pattern.matches("1[34578][0-9]{9}", str.trim());
    }

    /**
     * 获取指标完成率 action/target
     * @param target
     * @param action
     * @return
     */
    public static float getCircleRate(String target, String action){
        if(StringUtils.isEmpty(target) ||StringUtils.isEmpty(action)||!isMoney(target)||!isMoney(action)){
            return 0;
        }
        if(Float.parseFloat(target)==0 || Float.parseFloat(action)==0){
            return 0;
        }
        if(Float.parseFloat(action)-Float.parseFloat(target)>0){
            return 100;
        }
        return Float.parseFloat(action)*100/Float.parseFloat(target);
    }



    /**
     * 获取指标完成率
     * @param target
     * @param action
     * @return
     */
    public static String getPercentRate(String target, String action){
        if(StringUtils.isEmpty(target) ||StringUtils.isEmpty(action)){
            return "0";
        }
        if(Float.parseFloat(target)==0 || Float.parseFloat(action)==0){
            return "0";
        }
        target=formatWMoney2(target);
        action =formatWMoney2(action);
        double rate = (Float.parseFloat(action)/Float.parseFloat(target));
        if(!isMoneyNolimit(rate+"")){
           return "0";
        }
       // if(rate>1){
        //    return "100";
        //}
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return decimalFormat.format(rate*100);
    }

    public static String getPercentRateInt(String target, String action){
        if(StringUtils.isEmpty(target) ||StringUtils.isEmpty(action)){
            return "0";
        }
        if(Float.parseFloat(target)==0 || Float.parseFloat(action)==0){
            return "0";
        }
        target=formatWMoney(target);
        action =formatWMoney(action);
        double rate = (Float.parseFloat(action)/Float.parseFloat(target));
        if(!isMoneyNolimit(rate+"")){
            return "0";
        }
        if(rate>1){
            return "100";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0");
        decimalFormat.setMaximumFractionDigits(0);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return decimalFormat.format(rate*100);
    }

    /**
     * 格式化金额
     * @param money
     * @return
     */
    public static String formatMoney(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(Double.parseDouble(money)/10000);
    }


    /**
     * 格式化金额 四舍五入
     * @param money
     * @return
     */
    public static String formatMoney4(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(Double.parseDouble(money)/10000);
    }




    /**
     * 格式化金额
     * @param money
     * @return
     */
    public static String formatMoney3(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        decimalFormat.setMaximumFractionDigits(1);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(Double.parseDouble(money));
    }

    /**
     * 格式化金额
     * @param money
     * @return
     */
    public static String formatMoney2(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.00";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setGroupingSize(0);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(Double.parseDouble(money)/10000);
    }

    /**
     * 格式化金额(单位为万)
     * @param money
     * @return
     */
    public static String formatWMoney(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        return decimalFormat.format(Double.parseDouble(money)/10000);
    }

    /**
     * 格式化金额(单位为万)
     * @param money
     * @return
     */
    public static String formatWMoney3(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0");
        return decimalFormat.format(Double.parseDouble(money)*10000);
    }


    /**
     * 格式化金额(单位为万) 保留2位小数
     * @param money
     * @return
     */
    public static String formatWMoney2(String money){
        if(StringUtils.isEmpty(money) || !StringUtils.isMoney(money)){
            return "0.0";
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.000");
        return decimalFormat.format(Double.parseDouble(money)/10000);
    }

    /**
     * 获取下属角色
     * @param role
     * @return
     */
    public static String getSubRole(String role){
        if(RoleEnum.CONSULTANT.getCode().equals(role)){
            return RoleEnum.STAFF.getName();
        }
        if(RoleEnum.STOREMANAGER.getCode().equals(role)){
            return RoleEnum.CONSULTANT.getName();
        }
        if(RoleEnum.AREAMANAGER.getCode().equals(role)){
            return RoleEnum.STOREMANAGER.getName();
        }
        if(RoleEnum.BOSS.getCode().equals(role)){
            return RoleEnum.AREAMANAGER.getName();
        }
        return RoleEnum.STAFF.getName();
    }


}
