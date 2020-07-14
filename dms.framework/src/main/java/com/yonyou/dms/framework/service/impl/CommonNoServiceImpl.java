
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.function
*
* @File name : CommonNoServiceImpl.java
*
* @Author : jcsi
*
* @Date : 2016年7月19日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月19日    Administrator    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.framework.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domains.PO.SystemOrderNoPo;
import com.yonyou.dms.framework.service.CommonNoService;
import com.yonyou.dms.function.common.CommonConstants;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.function.utils.common.StringUtils;


/**
*
* @author jcsi
* @date 2016年7月19日
*/
@Service
public class CommonNoServiceImpl implements CommonNoService {

   

    /**
    * @author jcsi
    * @date 2016年7月19日
    * @param orderPrefix
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.framework.service.CommonNoService#getSystemOrderNo(java.lang.String)
    */

    @Override
    public String getSystemOrderNo(String orderPrefix,String... titles) throws ServiceBizException {
        try {
            String systemOrderNo = StringUtils.EMPTY_STRING;
            String yyyyMM = StringUtils.EMPTY_STRING;
            // 获取年月
            Date data = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyMM");
            yyyyMM = sdf.format(data);
            // 查询数据
            List<Map> listPo = lock(orderPrefix, yyyyMM);
            if (!CommonUtils.isNullOrEmpty(listPo)) {
                // 如果存在数据 取出 序号 然后+1 并修改记录
                Map map=listPo.get(0);
                // 订单号前缀
                systemOrderNo += map.get("ORDER_PREFIX");
                // 年月
                systemOrderNo += map.get("YYYY_MM");
                // 序列号
                Integer sequence=Integer.parseInt(map.get("ORDER_SEQUENCE").toString());
                systemOrderNo += CommonUtils.getFourOrderNo(sequence);
                //更新数据库序列号
                SystemOrderNoPo son=SystemOrderNoPo.findById(map.get("SYSTEM_ORDER_NO_ID"));
                son.set("ORDER_SEQUENCE",sequence + 1);
                if(titles.length>0){
                    if(titles[0]!=""||titles[0]!=null){
                        
                    }  
                }else{
                    son.saveIt();
                } 
                         
                return systemOrderNo;
            } else {
                // 如果记录不存在 新增
                SystemOrderNoPo son=new SystemOrderNoPo();
                if(!StringUtils.isNullOrEmpty(orderPrefix)){
                    son.set("ORDER_PREFIX",orderPrefix);
                }
                if(!StringUtils.isNullOrEmpty(yyyyMM)){
                    son.set("YYYY_MM",yyyyMM);
                }               
                son.set("ORDER_SEQUENCE",CommonConstants.INIT_ORDER_NO + 1);
                if(titles.length>0){
                    if(titles[0]!=""||titles[0]!=null){
                        
                    } 
                }else{
                    son.saveIt();
                }
                
               
                return orderPrefix + yyyyMM + CommonUtils.getFourOrderNo(CommonConstants.INIT_ORDER_NO);
            }
        } catch (Exception e) {
            throw new ServiceBizException("erro", e);
        }
    }
    private static int sumUp(int... values) {//表示传入sumUp的整数个数不确定，values是一个长度不确定的int数组，根据传入的参数确定长度
        int sum = 0;
    for (int i = 0; i < values.length; i++) {
     sum += values[i];
    }
    return sum;
   }
    /**
    * 
    * @author jcsi
    * @date 2016年7月19日
    * @param orderPrefix
    * @param yyyyMM
    * @return
     */
    public List<Map> lock(String orderPrefix,String yyyyMM){
        StringBuilder sb=new StringBuilder("select SYSTEM_ORDER_NO_ID,DEALER_CODE,ORDER_PREFIX,YYYY_MM,ORDER_SEQUENCE,CREATED_BY,UPDATED_BY,CREATED_AT,UPDATED_AT from TC_SYSTEM_ORDER_NO where 1=1 ");
        List<Object> queryParam=new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(orderPrefix)){
            sb.append(" and ORDER_PREFIX = ? ");
            queryParam.add(orderPrefix);
        }
        if(!StringUtils.isNullOrEmpty(yyyyMM)){
            sb.append(" and YYYY_MM = ? ");
            queryParam.add(yyyyMM);
        }
        sb.append("  for update ");
        return DAOUtil.findAll(sb.toString(), queryParam);
        /*select SYSTEM_ORDER_NO_ID,DEALER_CODE,ORDER_PREFIX,YYYY_MM,ORDER_SEQUENCE,CREATE_BY,UPDATE_BY,CREATE_DATE,UPDATE_DATE
        from TC_SYSTEM_ORDER_NO where  ORDER_PREFIX=#{orderPrefix,jdbcType=VARCHAR} and YYYY_MM=#{yyyyMM,jdbcType=VARCHAR} and DEALER_CODE=#{dealerCode,jdbcType=VARCHAR}  for update*/
    }
    

}
