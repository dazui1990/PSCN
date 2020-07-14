
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : UserService.java
*
* @Author : yll
*
* @Date : 2016年8月15日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年8月15日    yll    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.manage.service.basedata.user;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.framework.domains.PO.UserPO;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.basedata.user.UserDto;

/**
* 用户接口
* @author yll
* @date 2016年8月15日
*/

public interface UserService {

	public UserPO getUserById(Long id) throws ServiceBizException;
	
	public void modifyUser(Long id,UserDto userDto) throws ServiceBizException;
	
	public Long addUser(UserDto userDto) throws ServiceBizException;
	
	public List<Map> queryMenu() throws ServiceBizException;
	
	public List<Map> queryMenuForType(Object id) throws ServiceBizException;
	
	public Long getUserIDByEmployeeId(Long id) throws ServiceBizException;
	
	public Long getEmployeeIdByUserCode(String userCode) throws ServiceBizException;
	
	public String getPasswordByUserCode(String userCode) throws ServiceBizException;
	
	public Integer getUserTypeByEmployeeId(Long id) throws ServiceBizException;
	
}
