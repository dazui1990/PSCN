
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : OperateLogService.java
*
* @Author : rongzoujie
*
* @Date : 2016年7月14日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月14日    rongzoujie    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
package com.yonyou.dms.manage.service.basedata.report;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.function.exception.ServiceBizException;

public interface WeakStatisticsService  {
	
	public PageInfoDto findWeakStatistics(Map<String, Object> map) throws ServiceBizException; 
	public List<Map> findWeakStatisticsList(Map<String, Object> map) throws ServiceBizException; 
}
