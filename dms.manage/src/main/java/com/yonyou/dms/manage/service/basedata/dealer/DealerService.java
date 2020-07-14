
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : DealerBasicinfoService1.java
*
* @Author : ZhengHe
*
* @Date : 2016年7月13日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月13日    ZhengHe    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.manage.service.basedata.dealer;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.function.exception.ServiceBizException;

/**
 * 特约店管理Service
 * @author Jiangxy
 * @date 2018年5月24日
 */
public interface DealerService {
    public List<Map> queryDealers(Long userId,String employeeNo) throws ServiceBizException;
    public List<Map> queryDealerIds(Long userId,String employeeNo) throws ServiceBizException;
}
