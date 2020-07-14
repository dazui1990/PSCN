package com.yonyou.dms.manage.constant;


/**
* 定义常量
* @author jiangxy
* @date 2018年6月4日
*/

public interface Constants {

	//评价表状态 
    public static final Integer EVALUATE_FLAG_01 = 92011001; //评价未完成
    public static final Integer EVALUATE_FLAG_02 = 92011002; //待整改
    public static final Integer EVALUATE_FLAG_03 = 92011003; //整改计划已发布
    public static final Integer EVALUATE_FLAG_04 = 92011004; //评价结束
    
    //整改计划来源
    public static final Integer EVALUATE_FROM_01 = 92021001; //评价表
    public static final Integer EVALUATE_FROM_02 = 92021002; //人工
    
    //评价结果
    public static final Integer EVALUATE_STATUS_OK = 92031001; //OK
    public static final Integer EVALUATE_STATUS_02 = 92031002; //NG

	//整改计划项状态
    public static final Integer CORRECT_STATUS_01 = 92041001; //未审批
    public static final Integer CORRECT_STATUS_02 = 92041002; //店端已更新
    public static final Integer CORRECT_STATUS_03 = 92041003; //驳回
    public static final Integer CORRECT_STATUS_04 = 92041004; //通过
    public static final Integer CORRECT_STATUS_05 = 92041005; //关闭
    
	//巡回报告状态
    public static final Integer REPORT_STATUS_01 = 92051001; //已保存
    public static final Integer REPORT_STATUS_02 = 92051002; //已提交
    
	//整改计划书状态
    public static final Integer PLAN_STATUS_01 = 92061001; //未关闭
    public static final Integer PLAN_STATUS_02 = 92061002; //全部关闭
}
