
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.common
*
* @File name : SystemUser.java
*
* @Author : yll
*
* @Date : 2016年10月8日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年10月8日    yll    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.web.service.login;

import java.util.Map;

import com.yonyou.dms.function.exception.ServiceBizException;

/**
* 
* @author yll
* @date 2016年10月8日
*/

public interface LoginUserService {
	

    public Map logCheck(String dealerCode,String userCode,String password)throws ServiceBizException;
}
