/*
 * Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 *
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : cmol.common.function
 *
 * @File name : CommonConstants.java
 *
 * @Author : zhangxianchao
 *
 * @Date : 2016年2月24日
 *
----------------------------------------------------------------------------------
 *     Date       Who       Version     Comments
 * 1. 2016年2月24日    zhangxianchao    1.0
 *
 *
 *
 *
----------------------------------------------------------------------------------
 */

package com.yonyou.dms.function.common;

/*
 *
 * @author zhangxianchao
 * CommonConstants
 * @date 2016年2月24日
 */

public class DictCodeConstants {

    // 有效状态
    public static final int STATUS_IS_VALID                   = 10011001;
    // 无效状态
    public static final int STATUS_NOT_VALID                  = 10011002;

    // 性别
    public static final int GENDER                            = 1002;
    // 性别男
    public static final int GENDER_MALE                       = 10021001;
    // 性别女
    public static final int GENDER_FEMALE                     = 10021002;
    // 性别未知
    public static final int GENDER_NOT_DEFINE                 = 10021003;

    // 是否
    public static final int YESORNO                           = 1004;
    // 是
    public static final int STATUS_IS_YES                     = 10041001;
    // 否
    public static final int STATUS_IS_NOT                     = 10041002;

    // 启用状态
    public static final int STATUS_QY                         = 10031001;
    // 禁用状态
    public static final int STATUS_JY                         = 10031002;

    // 系统参数 10131001
    public static final int SYSTEM_PARAM_TYPE_JSJEYZFS        = 10131001;

    // 业务类型---配件销售
    public static final int BUSINESS_TYPE_PJXS                = 14031001;
    // 业务类型---采购入库
    public static final int BUSINESS_TYPE_CGRK                = 14031002;
    // 业务类型---调拨入库
    public static final int BUSINESS_TYPE_DBRK                = 14031003;
    // 业务类型---例外入库
    public static final int BUSINESS_TYPE_LWRK                = 14031004;
    // 业务类型---维修领料
    public static final int BUSINESS_TYPE_WXLL                = 14031005;
    // 业务类型---调拨出库
    public static final int BUSINESS_TYPE_DBCK                = 14031006;
    // 业务类型---例外出库
    public static final int BUSINESS_TYPE_LWCK                = 14031007;

    // 配件种类---纯正配件
    public static final int PARTS_TYPE_CZPJ                   = 14011001;
    // 配件种类---德科配件
    public static final int PARTS_TYPE_DKPJ                   = 14011002;
    // 配件种类---其他配件
    public static final int PARTS_TYPE_QTPJ                   = 14011003;

    // 菜单分类 -PC 端菜单
    public static final int ENTRY_MENU_CATEGORY_PC            = 10171001;
    // 菜单分类 -APP 端菜单
    public static final int ENTRY_MENU_CATEGORY_APP           = 10171002;

    // 用户类型-Yonyou
    public static final int EMPLOYEE_TYPE_Yonyou              = 10501001;
    // 用户类型-车工坊
    public static final int EMPLOYEE_TYPE_CGF                 = 10501002;

    // 岗位类型-技师
    public static final int POSITION_TYPE_JS                  = 11031001;

    // 省市区县-省份
    public static final int REGION_TYPE_PROVINCE              = 10001001;
    // 省市区县-城市
    public static final int REGION_TYPE_CITY                  = 10001002;
    // 省市区县-区县
    public static final int REGION_TYPE_COUNTY                = 10001003;

    // 员工在职状态-在职
    public static final int EMPLOYEE_ISJOB                    = 10081001;
    // 员工在职状态-离职
    public static final int EMPLOYEE_NOJOB                    = 10081002;

    // 出库 单据状态-未出库
    public static final int OUT_ORDER_NOSTATUS                = 13031001;
    // 出库 单据状态-已出库
    public static final int OUT_ORDER_ISSTATUS                = 13031002;

    // 入库 单据状态-未入库
    public static final int IN_ORDER_NOSTATUS                 = 13041001;
    // 入库 单据状态-已入库
    public static final int IN_ORDER_ISSTATUS                 = 13041002;
    // 入库类型
    public static final int ENTRY_TYPE_SENDBACK               = 13171003;
    // 费用类型
    public static final int INVOICE_CHARGE_TYPE_BUYCAR        = 14211001;
    // 库存状态13081001库存大于零
    public static final int INVENTORY_GREATER                 = 13081001;

    // 库存状态13081002库存等于零
    public static final int INVENTORY_EQUAL                   = 13081002;

    // 库存状态13081003库存小于零
    public static final int INVENTORY_LESS                    = 13081003;

    // 库存状态(在途)
    public static final int DISPATCHED_STATUS_ONPASSAGE       = 14131001;
    // 库存状态(在库)
    public static final int DISPATCHED_STATUS_INSTOCK         = 14131002;
    // 库存状态(出库)
    public static final int DISPATCHED_STATUS_OUTSTOCK        = 14131003;
    // 出入库类型-销售出库
    public static final int SALES_OUT_TYPE                    = 13151005;

    // 出入库类型-维修领料出库
    public static final int REPAIR_ORDER_TYPE                 = 13151004;

    // 出入库类型-配件报损
    public static final int PART_LOSS_TYPE                    = 13151006;

    // 出入库类型-内部领用
    public static final int PART_INNER_TYPE                   = 13151008;

    // 出入库类型-调拨出库
    public static final int PART_ALLOCATE_OUT                 = 13151003;
    // 出入库类型-采购入库
    public static final int PART_BUY_IN                       = 13151001;
    // 出入库类型-配件报溢
    public static final int PART_PROFIT                       = 13151007;
    // 出入库类型-调拨入库
    public static final int PART_ALLOCATE_IN                  = 13151002;
    // 收费区分-索赔
    public static final int CHARGE_DIF_COMPENSATE             = 12141001;

    // 收费区分-赠送
    public static final int CHARGE_DIF_GIVE                   = 12141002;
    // 日志分类：系统管理
    public static final int LOG_SYSTEM_MANAGEMENT             = 10091001;
    // 日志分类：客户管理
    public static final int LOG_CUSTOMER_MANAGEMENT           = 10091006;
    // 日志分类：零售管理
    public static final int LOG_RETAIL_MANAGEMENT             = 10091007;
    // 日志分类：维修管理
    public static final int LOG_REPAIR_MANAGEMENT             = 10091002;
    // 配件管理
    public static final int LOG_SYSTEM_PART                   = 10091005;

    // 客户投诉已结案
    public static final int COMPLAINT_DEAL_STATUS_04          = 15051004;
    // 客户投诉未处理
    public static final int COMPLAINT_DEAL_STATUS_01          = 15051001;
    // 客户投诉处理中
    public static final int COMPLAINT_DEAL_STATUS_02          = 15051002;
    // 客户投诉结案申请中
    public static final int COMPLAINT_DEAL_STATUS_03          = 15051003;
    // 客户投诉来源
    public static final int COMPLAINT_ORIGIN_01               = 15001001;
    // 严重性(投诉性质) 严重
    public static final int COMPLAINT_SERIOUS_01              = 15031001;
    // 来访方式(邀约)
    public static final int COMPLAINT_VISIT_TYPE_03           = 15181003;
    // 来访方式(到店)
    public static final int COMPLAINT_VISIT_TYPE_02           = 15181002;
    // 是否手动操作结案ID
    public static final int COMPLAINT_OPE_TYPE_ID             = 10391001;
    // 来源方式 手工
    public static final int SOURCE_TYPE_MANUAL                = 13051002;
    // 来源方式 发运单
    public static final int SOURCE_TYPE_ORDER                 = 13051001;
    public static final int CUS_SOURCE_03                     = 15121003;
    /*
     * zhongsw：2016-08-09
     */
    // 配件价格调整：销售价＜成本价
    public static final int SALES_COST                        = 10131001;
    // 配件价格调整：销售价＜建议销售价
    public static final int SALES_ADVICE                      = 10131001;
    // 配件价格调整：销售价＞销售限价
    public static final int SALES_LIMIT                       = 10131001;

    // 13111001销售价
    public static final int SALES_PRICE                       = 13111001;
    // 13111002建议销售价
    public static final int ADVICE_SALE_PRICE                 = 13111002;
    // 13111003索赔价
    public static final int CLAIM_PRICE                       = 13111003;
    // 13111004销售限价
    public static final int LIMIT_PRICE                       = 13111004;
    // 13111005成本单价
    public static final int COST_PRICE                        = 13111005;

    // 12151001工单状态--新建
    public static final int NEW_CREATE_STATUS                 = 12151001;

    // 12151003工单状态--派工
    public static final int ASSIGN_CREATE_STATUS              = 12151002;
    // 12151003工单状态--已竣工
    public static final int COM_CREATE_STATUS                 = 12151003;
    // 12151008工单状态--取消竣工
    public static final int COM_CANCEL_STATUS                 = 12151008;

    // 12151004工单状态--已提交结算
    public static final int SUB_CREATE_STATUS                 = 12151004;

    // 12151005工单状态--已结算
    public static final int SET_CREATE_STATUS                 = 12151005;

    // 12151006工单状态--收款
    public static final int SET_CREATE_COLLECTION             = 12151006;
    
    // 12151007工单状态--交车
    public static final int SET_CREATE_CAR                    = 12151007;
    
    // 12131001进厂油量
    public static final int OIL_LEVEL_ZERO                    = 12131001;

    // 12131002进厂油量
    public static final int OIL_LEVEL_QUARTER                 = 12131002;

    // 12131003进厂油量
    public static final int OIL_LEVEL_HALF                    = 12131003;

    // 12131004进厂油量
    public static final int OIL_LEVEL_FOUR                    = 12131004;

    // 12131005进厂油量
    public static final int OIL_LEVEL_ALL                     = 12131005;

    // 客户类型
    public static final int CUSTOMER_CONTACT                  = 10381001; // 往来客户
    public static final int CUSTOMER_OWNER                    = 10381002; // 车主
    // 配件类别
    public static final int GROUPCODE                         = 1300;
    // 九大类
    public static final int MAINTYPE                          = 1301;
    public static final int TRACKING_RESULT_NOT               = 15171003; // 未跟进
    public static final int TRACKING_RESULT_GOON              = 15171004; // 继续跟进
    public static final int COMPLAINT_TRACKING_RESULT_6       = 15171005; // 战败申请
    // 维修
    public static final int MAINTAIN                          = 1211;
    // 配件
    public static final int ACCESSORIES                       = 1313;
    // 整车仓库
    public static final int VEHICLE_WAREHOUSE                 = 1319;
    // 配件仓库
    public static final int ACCESSORIES_WAREHOUSE             = 1320;
    // 工时打折
    public static final int FAVORABLE_MODELS                  = 1400;
    // 员工属性- 销售顾问
    public static final int SALES_CONSULTANT                  = 10061001;
    // 员工属性- 销售经理
    public static final int SALES_AUDIT                       = 10061013;
    // 员工属性- 财务经理
    public static final int FINANCE_AUDIT                     = 10061014;

    // 意向级别 -H级
    public static final int H_LEVEL                           = 15191001;
    // 意向级别 -A级
    public static final int A_LEVEL                           = 15191002;
    // 意向级别 -B级
    public static final int B_LEVEL                           = 15191003;
    // 意向级别 -C级
    public static final int C_LEVEL                           = 15191004;
    // 意向级别 -D级
    public static final int D_LEVEL                           = 15191005;
    // 意向级别 -O级
    public static final int O_LEVEL                           = 15191006;
    // 意向级别 -N级
    public static final int N_LEVEL                           = 15191007;
    // 意向级别 -FO级
    public static final int FO_LEVEL                          = 15191008;
    // 意向级别 -F级
    public static final int F_LEVEL                           = 15191009;

    // 仓库类别 -配件
    public static final int PART_STORAGE                      = 13021002;

    // 仓库类别 -整车
    public static final int VEHICLE_STORAGE                   = 13021001;

    // 业务类型 -一般销售
    public static final int GENERAL_SALES_TYPE                = 14031001;
    // 业务类型 -委托交车
    public static final int COMMISSIONED_TYPE                 = 14031002;
    // 业务类型 -销售退货
    public static final int SALES_RETURN_TYPE                 = 14031003;

    // 销售订单状态 -未提交
    public static final int NO_SUBMITTED                      = 14041001;
    // 销售订单状态 -经理审核中
    public static final int MANAGER_IN_REVIEW                 = 14041002;
    // 销售订单状态 -经理审核通过
    public static final int MANAGER_APPROVED                  = 14041003;
    // 销售订单状态 -经理审核驳回
    public static final int MANAGER_REJECTED                  = 14041004;
    // 销售订单状态 -财务审核中
    public static final int FINANCE_IN_REVIEW                 = 14041005;
    // 销售订单状态 -财务审核通过
    public static final int FINANCE_APPROVED                  = 14041006;
    // 销售订单状态 -财务审核驳回
    public static final int FINANCE_REJECTED                  = 14041007;
    // 销售订单状态 -交车确认中
    public static final int CAR_IN_CONFIRMATION               = 14041008;
    // 销售订单状态 -已交车确认
    public static final int CAR_CONFIRMED                     = 14041009;
    // 销售订单状态 -已完成
    public static final int ORDER_COMPLETED                   = 14041010;
    // 销售订单状态 -已取消
    public static final int ORDER_CANCEL                      = 14041011;
    // 销售订单状态 -已退回
    public static final int ORDER_BACK                        = 14041012;
    // 销售订单状态 -等待退回入库
    public static final int ORDER_BACK_INWAREHOUSE            = 14041013;
    // 整车基本参数
    public static final int VEHICLE_BASIC_CODE                = 1020;
    // 整车基本参数
    public static final int BASICOM_TAXRATE                   = 1019;
    // PDI检查类型（车辆入库）
    public static final int STOCK_IN_PDI                      = 14121008;
    // PDI检查类型（库存检查）
    public static final int STOCK_KC_PDI                      = 14121010;

    // 自动结清
    public static final int AUTOMATICALLY_LIQUIDATE           = 12091001;

    // 出库类型(销售出库)
    public static final int SALES_DELIVERY_TYPE               = 13181001;

    // 出库类型(调拨出库)
    public static final int ALLOT_DELIVERY_TYPE               = 13181002;

    // 出库类型(采购退回出库)
    public static final int BAKE_DELIVERY_TYPE                = 13181003;

    // 不自动结清
    public static final int DO_NOT_AUTOMATICALLY_LIQUIDATE    = 12091002;

    // PDI检查类型(出库检查)
    public static final int STOCK_OUT_PDI                     = 14121009;
    // 出库 出入库状态-未出库
    public static final int SD_NO_NOSTATUS                    = 14151001;
    // 出库 出入库状态-已出库
    public static final int SD_NO_ISSTATUS                    = 14151002;

    /**
     * 工单状态
     */
    // 已结算
    public static final int ORDER_SETTLED                     = 12151005;

    /**
     * 业务类型
     */
    // 维修
    public static final int RO_TYPE_REPAIR                    = 12161001;

    // 销售
    public static final int RO_TYPE_SALES                     = 12161002;

    // 调拨
    public static final int RO_TYPE_TRANSFERS                 = 12161003;

    // 是否结清(已结清)
    public static final int REPAIR_IS_SETTLE                  = 12231001;
    // 未结清
    public static final int REPAIR_NO_SETTLE                  = 12231002;

    // 往来客户
    public static final int CUSTOMER_TYPE                     = 10381001;

    // 车主
    public static final int VEHICLE_TYPE                      = 10381002;

    // 销售订单审核结果-审核通过
    public static final int AUDIT_PASS                        = 14161001;
    // 销售订单审核结果-审核驳回
    public static final int AUDIT_BACK                        = 14161002;

    // 销售订单审核类型-经理审核
    public static final int AUDIT_MANAGE                      = 14171002;
    // 销售订单审核类型-财务审核
    public static final int AUDIT_FINANCE                     = 14171001;

    // 未收账款 （>0 , =0）
    public static final int GREATER_ZERO                      = 12241001;
    public static final int EQUAL_ZERO                        = 12241002;
    // 统计方式(按部门)
    public static final int STATISTICAL_MODE_01               = 10411001;
    // 统计方式(按班组)
    public static final int STATISTICAL_MODE_02               = 10411002;
    // 统计方式(按工位)
    public static final int STATISTICAL_MODE_03               = 10411003;
    // 配车状态-已配车
    public static final int ALREADY_DISPATCHED_STATUS         = 14141002;
    // 配车状态-未配车
    public static final int NOT_DISPATCHED_STATUS             = 14141001;
    // 配车状态-已交车
    public static final int ALREADY_SO_STATUS_08              = 14141004;
    // 交车确认中
    public static final int SO_STATUS_08                      = 14041008;

    /**
     * 预约单状态
     */
    // 未进厂
    public static final int BOOKING_ORDER_NOT                 = 12261001;
    // 提前进厂
    public static final int BOOKING_ORDER_ADVANCE             = 12261002;
    // 按时进厂
    public static final int BOOKING_ORDER_TIME                = 12261003;
    // 延迟进厂
    public static final int BOOKING_ORDER_DELAY               = 12261004;
    // 取消进厂
    public static final int BOOKING_ORDER_CANCEL              = 12261005;

    /**
     * 文件单据类型--1042
     */

    public static final int FILE_TYPE_USER_INFO               = 10421001; // 测试类型
    public static final int FILE_TYPE_USER_INFO_ADDRESS       = 10421002; // 测试类型
    public static final int FILE_TYPE_CUSTOMER_INFO_COMPLAINT = 10421003; // 客户投诉
    public static final int FILE_TYPE_CAR_INFO_VEHICLEIN      = 10421004; // 车辆入库
    public static final int FILE_TYPE_HALL_IMG                = 10421005; // 车展图片
    public static final int FILE_TYPE_PLAN_WORK               = 10421006; // 工作计划
    public static final int FILE_TYPE_MEETING                 = 10421007; // 会议附件
    /**
     * 派工状态
     */
    // 已派工
    public static final int ASSIGN_ORDER_ALL                  = 12271001;
    // 部分派工
    public static final int ASSIGN_ORDER_SOME                 = 12271002;
    // 未派工
    public static final int ASSIGN_ORDER_NO                   = 12271003;
    // 竣工
    public static final int ASSIGN_ORDER_COMPLETE             = 12271004;
    // 待信
    public static final int ASSIGN_ORDER_INFO                 = 12271005;
    // 待料
    public static final int ASSIGN_ORDER_PART                 = 12271006;

    /**
     * 质损状态
     */
    // 正常
    public static final int TRAFFIC_MAR_STATUS_YES            = 10401001;
    /**
     * 验收结果
     */
    // 验收成功
    public static final int INSPECTION_RESULT_SUCCESS         = 10391002;
    /**
     * 入库类型???
     */
    // 采购入库
    public static final int ENTRY_TYPE_CGRK                   = 13171001;
    // 调拨入库
    public static final int ENTRY_TYPE_DBRK                   = 13171002;
    // 销售退回
    public static final int ENTRY_TYPE_XSTH                   = 13171003;
    /*
     * 维修类型
     */
    //维修
    public static final int REPAIR_TYPE_REPAIR                = 12121001;
    //返工
    public static final int REPAIR_TYPE_REWORK                = 12121002;
    //索赔
    public static final int REPAIR_TYPE_CLAIM                 = 12121003;
    
    
	//评价表状态 
    public static final Integer EVALUATE_FLAG_01 = 92011001; //评价未完成
    public static final Integer EVALUATE_FLAG_02 = 92011002; //评价完成
    public static final Integer EVALUATE_FLAG_03 = 92011003; //评价结束
    
    //整改计划来源
    public static final Integer EVALUATE_FROM_01 = 92021001; //评价表
    public static final Integer EVALUATE_FROM_02 = 92021002; //人工
    
    //评价结果
    public static final Integer EVALUATE_STATUS_OK = 92031001; //OK
    public static final Integer EVALUATE_STATUS_NG = 92031002; //NG

	//整改计划项状态
    public static final Integer CORRECT_STATUS_01 = 92041001; //未审批
    public static final Integer CORRECT_STATUS_02 = 92041002; //店端已更新
    public static final Integer CORRECT_STATUS_03 = 92041003; //驳回
    public static final Integer CORRECT_STATUS_04 = 92041004; //通过
    public static final Integer CORRECT_STATUS_05 = 92041005; //关闭
    
    //巡回报告状态
    public static final Integer REPORT_STATUS_SAVE = 92051001; //已保存
    public static final Integer REPORT_STATUS_SUBMIT = 92051002; //已提交
}
