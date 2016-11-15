package com.drjing.xibao.module.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.drjing.xibao.module.MainActivity;
import com.drjing.xibao.module.application.activity.AboutUsActivity;
import com.drjing.xibao.module.application.activity.AccountInfoActivity;
import com.drjing.xibao.module.application.activity.ArticleListActivity;
import com.drjing.xibao.module.application.activity.MemberInfoActivity;
import com.drjing.xibao.module.application.activity.ModifyPwdActivity;
import com.drjing.xibao.module.application.activity.MyCompanyActivity;
import com.drjing.xibao.module.application.activity.ProductListActivity;
import com.drjing.xibao.module.application.activity.QuestionActivity;
import com.drjing.xibao.module.news.activity.AllCustomListActivity;
import com.drjing.xibao.module.news.activity.CustomerStoreActivity;
import com.drjing.xibao.module.news.activity.DateActivity;
import com.drjing.xibao.module.news.activity.LoginActivity;
import com.drjing.xibao.module.news.activity.UnAssignCustomListActivity;
import com.drjing.xibao.module.news.activity.UnactivatCustomListActivity;
import com.drjing.xibao.module.performance.activity.AdviserAimDetailActivity;
import com.drjing.xibao.module.performance.activity.AimDetailActivity;
import com.drjing.xibao.module.performance.activity.AimSettingActivity;
import com.drjing.xibao.module.performance.activity.AreaManagerAimDetailActivity;
import com.drjing.xibao.module.performance.activity.HouseDetailActivity;
import com.drjing.xibao.module.performance.activity.MySalaryActivity;
import com.drjing.xibao.module.performance.activity.RankActivity;
import com.drjing.xibao.module.performance.activity.StaffAimDetailActivity;
import com.drjing.xibao.module.performance.activity.StoreAimDetailActivity;
import com.drjing.xibao.module.performance.activity.WalletActivity;
import com.drjing.xibao.module.performance.activity.WalletAddActivity;
import com.drjing.xibao.module.workbench.activity.ActionPlanActivity;
import com.drjing.xibao.module.workbench.activity.ActionPlanAddActivity;
import com.drjing.xibao.module.workbench.activity.ActionPlanListActivity;
import com.drjing.xibao.module.workbench.activity.ActivatCycleActivity;
import com.drjing.xibao.module.workbench.activity.AdviserCustomerListActivity;
import com.drjing.xibao.module.workbench.activity.ConsultantListActivity;
import com.drjing.xibao.module.workbench.activity.ConsultantSingleListActivity;
import com.drjing.xibao.module.workbench.activity.ConsultantStoreSingleListActivity;
import com.drjing.xibao.module.workbench.activity.CustomerDetailActivity;
import com.drjing.xibao.module.workbench.activity.CustomerListActivity;
import com.drjing.xibao.module.workbench.activity.CustomerOrderListActivity;
import com.drjing.xibao.module.workbench.activity.CustomerSearchActivity;
import com.drjing.xibao.module.workbench.activity.LeaderActionPlanActivity;
import com.drjing.xibao.module.workbench.activity.LifeLogActivity;
import com.drjing.xibao.module.workbench.activity.MessageTemplateActivity;
import com.drjing.xibao.module.workbench.activity.MyCustomerListActivity;
import com.drjing.xibao.module.workbench.activity.MyOrderListActivity;
import com.drjing.xibao.module.workbench.activity.NurseLogActivity;
import com.drjing.xibao.module.workbench.activity.OrderDetailActivity;
import com.drjing.xibao.module.workbench.activity.OrderQueryActivity;
import com.drjing.xibao.module.workbench.activity.OrderSearchActivity;
import com.drjing.xibao.module.workbench.activity.OrdersListActivity;
import com.drjing.xibao.module.workbench.activity.ProjectListActivity;
import com.drjing.xibao.module.workbench.activity.RemindActivity;
import com.drjing.xibao.module.workbench.activity.ReportActivity;
import com.drjing.xibao.module.workbench.activity.ReportDetailActivity;
import com.drjing.xibao.module.workbench.activity.ReportListActivity;
import com.drjing.xibao.module.workbench.activity.ReportStoreActivity;
import com.drjing.xibao.module.workbench.activity.ReportViewActivity;
import com.drjing.xibao.module.workbench.activity.SaleLogActivity;
import com.drjing.xibao.module.workbench.activity.SaleLogAddActivity;
import com.drjing.xibao.module.workbench.activity.SaleLogDetailActivity;
import com.drjing.xibao.module.workbench.activity.ScheduleActivity;
import com.drjing.xibao.module.workbench.activity.ScheduleAddActivity;
import com.drjing.xibao.module.workbench.activity.ScheduleMonthActivity;
import com.drjing.xibao.module.workbench.activity.SpecialLogActivity;
import com.drjing.xibao.module.workbench.activity.StaffStoreSingleListActivity;
import com.drjing.xibao.module.workbench.activity.StoreCustomerListActivity;
import com.drjing.xibao.module.workbench.activity.SubEmployeeListActivity;
import com.drjing.xibao.module.workbench.activity.SubStaffListActivity;
import com.drjing.xibao.module.workbench.activity.TopicLogActivity;
import com.drjing.xibao.wxapi.WXEntryActivity;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * Created by kristain on 15/12/20.
 */
public class UIHelper {

    public final static String TAG = "UIHelper";

    public final static int RESULT_OK = 0x00;
    public final static int REQUEST_CODE = 0x01;

    public static void ToastMessage(Context cont, String msg) {
        if(cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        if(cont == null || msg <= 0) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        if(cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, time).show();
    }

    /**
     * 进入主页
     * @param context
     */
    public static void showHome(Activity context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入账户页面
     * @param context
     */
    public static void showAccountInfo(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AccountInfoActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入关于喜报
     * @param context
     */
    public static void showAboutUS(Activity context){
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }

    /**
     * 修改密码页面
     * @param context
     */
    public static void showModifyPwd(Activity context){
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        context.startActivity(intent);
    }


    /**
     * 进入我的公司页面
     * @param context
     */
    public static void showMyCompany(Activity context){
        Intent intent = new Intent(context, MyCompanyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进去问题反馈
     * @param context
     */
    public static void showQuestion(Activity context){
        Intent intent = new Intent(context, QuestionActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入登录页面
     * @param context
     */
    public static void showLogin(Activity context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }


    /**
     * 进入订单列表页面
     * @param context
     */
    public static void showOrderList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, OrdersListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入钱包页面
     * @param context
     */
    public static void showWallet(Activity context){
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入钱包新增页面
     * @param context
     */
    public static void showWalletAdd(Activity context){
        Intent intent = new Intent(context, WalletAddActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入订单详情页面
     * @param context
     */
    public static void showOrdersDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 进入订单详情页
     * requestCode
     * @param context
     */
    public static void showOrdersDetailForResult(Activity context,Bundle bundle, int requestCode){
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入销售日志确认页
     * requestCode
     * @param context
     */
    public static void showSaleLogDetailForResult(Activity context,Bundle bundle, int requestCode){
        Intent intent = new Intent(context, SaleLogDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入客户详情页面
     * @param context
     */
    public static void showCustomerDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, CustomerDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入客户搜索页面
     * @param context
     */
    public static void showCustomerSearch(Activity context,int requestCode){
        Intent intent = new Intent(context, CustomerSearchActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 进入顾问列表
     * @param context
     */
    public static void showConsultantList(Activity context,int code,Bundle bundle){
        Intent intent = new Intent(context, ConsultantListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }

    /**
     * 进入下级美容师列表
     * @param context
     */
    public static void showSubStaffList(Activity context,int code,Bundle bundle){
        Intent intent = new Intent(context, SubStaffListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }

    /**
     * 进入单选顾问列表
     * @param context
     */
    public static void showConsultantSingleList(Activity context,int code,Bundle bundle){
        Intent intent = new Intent(context, ConsultantSingleListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }



    /**
     * 进入单选顾问列表
     * @param context
     */
    public static void showConsultantStoreSingleList(Activity context,int code,Bundle bundle){
        Intent intent = new Intent(context, ConsultantStoreSingleListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }

    /**
     * 进入单选美容师列表
     * @param context
     */
    public static void showStaffStoreSingleList(Activity context,int code,Bundle bundle){
        Intent intent = new Intent(context, StaffStoreSingleListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }


    /**
     * 进入项目列表
     * @param context
     */
    public static void showProjectList(Activity context,int code,String cid,String projectIds){
        Intent intent = new Intent(context, ProjectListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("cid",cid);
        bundle.putString("projectIds",projectIds);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, code);
    }

    /**
     * 进入服务日志搜索选择日期页面
     * @param context
     */
    public static void showServiceLogSearchByDate(Activity context){
        Intent intent = new Intent(context, DateActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入待激活客户列表页面
     * @param context
     */
    public static void showUnActivateCustomList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, UnactivatCustomListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入未分配客户列表页面
     * @param context
     */
    public static void showUnAssignCustomList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, UnAssignCustomListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 进入待激活客户筛选页面
     * @param context
     */
    public static void showActivateCycle(Activity context){
        Intent intent = new Intent(context, ActivatCycleActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入日报页面
     * @param context
     */
    public static void showReport(Activity context){
        Intent intent = new Intent(context, ReportActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入查看员工日报列表
     * @param context
     */
    public static void showStoreReportView(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ReportViewActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入门店日报列表
     * @param context
     */
    public static void showStoreReportList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ReportListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入门店日报明细
     * @param context
     */
    public static void showStoreReportDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ReportDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入日报选择店铺页面
     * @param context
     */
    public static void showReportStore(Activity context){
        Intent intent = new Intent(context, ReportStoreActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入行动计划表
     * @param context
     */
    public static void showActionPlan(Activity context){
        Intent intent = new Intent(context, ActionPlanActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入上级行动计划表
     * @param context
     */
    public static void showLeaderActionPlan(Activity context,Bundle bundle){
        Intent intent = new Intent(context, LeaderActionPlanActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 添加行动计划表
     * @param context
     */
    public static void showAddActionPlan(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ActionPlanAddActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 添加行动计划表
     * @param context
     */
    public static void showActionPlanList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ActionPlanListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入日程页面
     * @param context
     */
    public static void showMonthSchedule(Activity context){
        Intent intent = new Intent(context, ScheduleMonthActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入日程页面
     * @param context
     */
    public static void showSchedule(Activity context){
        Intent intent = new Intent(context, ScheduleActivity.class);
        context.startActivity(intent);
    }


    /**
     * 进入日程新增页面
     * @param context
     */
    public static void showScheduleAdd(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ScheduleAddActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    /**
     * 进入选择信息模版页面
     * @param context
     */
    public static void showMessageTemplate(Activity context){
        Intent intent = new Intent(context, MessageTemplateActivity.class);
        context.startActivityForResult(intent, 1);
    }

    /**
     * 进入激活、提醒、回访日志
     * @param context
     */
    public static void showRemindLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, RemindActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入个人中心页面
     * @param context
     */
    public static void showMemberInfo(Activity context){
        Intent intent = new Intent(context, MemberInfoActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入销售日志页面
     * @param context
     */
    public static void showSaleLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, SaleLogActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入销售日志页面
     * @param context
     */
    public static void showSaleAddLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, SaleLogAddActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入我的历史工资页面
     * @param context
     */
    public static void showMySalaryList(Activity context){
        Intent intent = new Intent(context, MySalaryActivity.class);
        context.startActivity(intent);
    }
    /**
     * 进入英雄榜页面
     * @param context
     */
    public static void showRank(Activity context){
        Intent intent = new Intent(context, RankActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入目标看板页面
     * @param context
     */
    public static void showAimDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AimDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入美容师目标看板页面
     * @param context
     */
    public static void showStaffAimDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, StaffAimDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入顾问目标看板页面
     * @param context
     */
    public static void showAdviserAimDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AdviserAimDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 进入店长目标看板页面
     * @param context
     */
    public static void showStoreAimDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, StoreAimDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入区域经理目标看板页面
     * @param context
     */
    public static void showAreaManagerAimDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AreaManagerAimDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入咨询列表
     * @param context
     */
    public static void showArtcleList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, ArticleListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入文章明晰
     * @param context
     */
    public static void showArtcleDetail(Activity context,Bundle bundle){
        Intent intent = new Intent(context, WXEntryActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入目标设定
     * @param context
     */
    public static void showAimSetting(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AimSettingActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入我的订单列表
     * @param context
     */
    public static void showMyOrderList(Activity context){
        Intent intent = new Intent(context, MyOrderListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入我的订单列表
     * @param context
     */
    public static void showMyOrderList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, MyOrderListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 进入客户订单列表
     * @param context
     */
    public static void showCustomerOrderList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, CustomerOrderListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入订单筛选页面
     * requestCode 0 新增订单  1 我的订单
     * @param context
     */
    public static void showOrderSearch(Activity context, int requestCode){
        Intent intent = new Intent(context, OrderSearchActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入订单筛选页面
     * requestCode
     * @param context
     */
    public static void showOrderSearch(Activity context,Bundle bundle){
        Intent intent = new Intent(context, OrderQueryActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入订单日期筛选页面
     * requestCode 0 新增订单  1 我的订单
     * @param context
     */
    public static void showOrderDateSearch(Activity context, int requestCode){
        Intent intent = new Intent(context, DateActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入护理日志
     * @param context
     */
    public static void showNurseLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, NurseLogActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入特殊日期
     * @param context
     */
    public static void showSpecialLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, SpecialLogActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    /**
     * 进入私密日志
     * @param context
     */
    public static void showLifeLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, LifeLogActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入私密话题日志
     * @param context
     */
    public static void showTopicLog(Activity context,Bundle bundle){
        Intent intent = new Intent(context, TopicLogActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 进入美容师客户管理
     * @param context
     */
    public static void showCustomerList(Activity context){
        Intent intent = new Intent(context, CustomerListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 进入老总和区总客户管理
     * @param context
     */
    public static void showAllCustomerList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AllCustomListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入老总和区总客户管理 选择门店页面
     * @param context
     */
    public static void showCustomerStoreList(Activity context){
        Intent intent = new Intent(context, CustomerStoreActivity.class);
        context.startActivity(intent);
    }

    /**
     * 顾问角色进入客户管理
     * @param context
     */
    public static void showAdviserCustomerList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, AdviserCustomerListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 店长角色进入客户管理
     * @param context
     */
    public static void showStoreCustomerList(Activity context,Bundle bundle){
        Intent intent = new Intent(context, StoreCustomerListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 进入我的客户管理
     * @param context
     */
    public static void showMyCustomerList(Activity context, int requestCode,Bundle bundle){
        Intent intent = new Intent(context, MyCustomerListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入下级员工列表
     * @param context
     */
    public static void showSubEmployeeList(Activity context, int requestCode,Bundle bundle){
        Intent intent = new Intent(context, SubEmployeeListActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入产品信息
     * @param context
     */
    public static void showProductInfo(Activity context){
        Intent intent = new Intent(context, ProductListActivity.class);
        context.startActivity(intent);
    }


    public static void showHouseDetailActivity(Activity context){
        Intent intent = new Intent(context, HouseDetailActivity.class);
        context.startActivity(intent);
    }



}
