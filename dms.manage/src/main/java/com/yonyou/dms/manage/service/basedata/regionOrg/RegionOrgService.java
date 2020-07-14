
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
	
package com.yonyou.dms.manage.service.basedata.regionOrg;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.function.exception.ServiceBizException;

/**
 * 大区 小区(督导)管理Service
 * @author Jiangxy
 * @date 2018年6月27日
 */
public interface RegionOrgService {
    public List<Map> queryBigOrg(Long userId,String employeeNo) throws ServiceBizException;
    public List<Map> findDudaoInfo(Long userId,String employeeNo) throws ServiceBizException;
    public List<Map> findDudaoIdInfo(Long userId,String employeeNo) throws ServiceBizException;
}
