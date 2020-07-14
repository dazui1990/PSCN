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

public class CommonConstants {

    public final static String  IOS_8601_DATE_FORMAT       = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public final static String  SIMPLE_DATE_FORMAT         = "yyyy-MM-dd";
    public final static String  SIMPLE_DATE_TIME_FORMAT    = "yyyy-MM-dd HH:mm";
    public final static String  FULL_DATE_TIME_FORMAT      = "yyyy-MM-dd HH:mm:ss";
    public final static String  ACCURATE_DATE_FORMAT       = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public final static String  SIMPLE_DATE_MONTH_FORMAT   = "yyyy-MM";

    /**
     * 不进行任何格式操作，没有千位符，小数点不自动补全
     */
    public final static String  Excel_NUMBER_FORMAT_SAMPLE = "#";
    
    public final static String  Excel_NUMBER_FORMAT_SAMPLE_ROUND1 = "0";
    
    public final static String  Excel_NUMBER_FORMAT_SAMPLE_ROUND2 = "0.00";
    //设置百分比样式
    public final static String  Excel_NUMBER_FORMAT_SAMPLE_PERCENT = "0.00%";
    
    // 错误信息列表最大行数
    public static final Integer IMPORT_MAX_ERRORS_ROWS     = 30;

    // 订单号初始化
    public static final Integer INIT_ORDER_NO              = 1;
    // 订单号位数
    public static final int     SYSTEM_ORDER_NO_NUMBER     = 5;

    // 车厂公共数据默认Dealer_code
    public static final String  PUBLIC_DEALER_CODE         = "-1";

    // 默认经销商代码字段名称
    public static final String  PUBLIC_DEALER_CODE_NAME    = "DEALER_CODE";
    // 默认经销商代码字段名称
    public static final String  PUBLIC_ORGANIZATION_NAME    = "ORGANIZATION_ID";

    // 价格调整单单据号前缀
    public static final String  PRICE_ADJUSTMENT_PREFIX    = "PA";

    // 调拨出库单号前缀
    public static final String  PART_ALLOCATE_OUT_PREFIX   = "DC";

    // 采购入库单号前缀
    public static final String  PART_BUY_IN_PREFIX         = "CG";

    // 销售出库单号前缀
    public static final String  PART_SALES_OUT_PREFIX      = "SO";

    // 配件报损单号前缀
    public static final String  PART_LOSS_NO               = "SF";

    // 内部领用单号前缀
    public static final String  PART_INNER_NO              = "SA";

    // 调拨入库单号前缀
    public static final String  PART_ALLOCATE_IN_PREFIX    = "DR";
    // 车主编码前缀
    public static final String  OWNER_PREFIX               = "OW";
    // 客户投诉 TS
    public static final String  COMPLAINTNO_PREFIX         = "TS";

    // 配件报溢单号前缀
    public static final String  PART_Profit_PREFIX         = "SK";
    // 客户编码前缀
    public static final String  CUSTOMER_PREFIX            = "CO";
    // 潜客编码前缀
    public static final String  POTENTIAL_CUSTOMER_PREFIX  = "PU";
    // 销售单号前缀
    public static final String  SO_NO_PREFIX               = "SO";
    // 整车出库单号前缀
    public static final String  SD_NO                      = "VD";

    // 减免单号前缀
    public static final String  DERATE_NO                  = "BA";
    
    //预约单号前缀
    public static final String BO_NO="YO"; 
    
    //文件上传文件分隔符
    public static final String FILEUPLOADID_SPLIT_STR=",;";
    //文件上传文件分隔符---已经存在文件与新上传文件
    public static final String FILEUPLOADID_SPLIT_STR_TYPE="##@";
    
    //对接销售的接口URL，也统一使用此常量
    public static final String DataURL="http://172.19.0.47:9080/WDHAC_TEST/";
    public static final String StockURL="http://172.19.0.47:9080/SALES_TEST/";
}
