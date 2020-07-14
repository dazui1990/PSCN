
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : EmployeeService.java
 *
 * @Author : jcsi
 *
 * @Date : 2016年7月8日
 *
----------------------------------------------------------------------------------
 *     Date       Who       Version     Comments
 * 1. 2016年7月8日    Administrator    1.0
 *
 *
 *
 *
----------------------------------------------------------------------------------
 */

package com.yonyou.dms.manage.service.basedata.employee;


import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.framework.domains.DTO.baseData.BasicParametersDTO;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.basedata.EmployeeDto;
import com.yonyou.dms.manage.domains.DTO.basedata.user.EmployeeRoleDto;

/**
 * 员工信息  接口
 * @author jcsi
 * @date 2016年7月29日
 */
public interface EmployeeService {


	public Long addEmployee(EmployeeDto empDto)throws ServiceBizException;

	public PageInfoDto searchEmployees(@RequestParam Map<String, String> param)throws ServiceBizException;

	public Map findById(Long id)throws ServiceBizException;

	public void updateEmpById(Long id,EmployeeDto empDto) throws ServiceBizException;

	public void deleteById(Long id)throws ServiceBizException;

	public List<Map> findEmpRolesByEmpId(Long id)throws ServiceBizException;

	public List<Map> selectEmployees(Map<String,String> queryParam) throws ServiceBizException ;

	public PageInfoDto searchUserEmployees(@RequestParam Map<String, String> param)throws ServiceBizException;

	public Map findUserByEmployeeId (Long id) throws ServiceBizException;

	public List<Map> qrySalesConsultant(String orgCode) throws ServiceBizException;
	
	public List<Map> qryPowerSalesConsultant(String menuId) throws ServiceBizException;

	public PageInfoDto qryAudit(List<BasicParametersDTO> basiDtolist) throws ServiceBizException;
	
	public List<Map> queryTechnician() throws ServiceBizException;
	
	public List<Map> queryServiceAss() throws ServiceBizException;

	public List<Map> qryFinanceAudit() throws ServiceBizException;
	
	 public List<Map> queryFinisher() throws ServiceBizException;
	 
	 public void updateEmpById(Long id,List<String> employeeRoles) throws ServiceBizException;
	 
	 public void updateEmpByRoleId(Long id,String roleIds) throws ServiceBizException;
	 
	 public List<Map> selectEmployeeByrole(Integer role)throws ServiceBizException;
	 public List<Map> selectPowerEmployeeByrole(Integer role ,String menuId)throws ServiceBizException;
	 
	 public void updateEmpPositionById(Long id,String position) throws ServiceBizException;

	public List<Map> findUserSeries(Long id) throws ServiceBizException;
	
	public PageInfoDto searchRoleNames(Map<String, String> param) throws ServiceBizException;
	
	public PageInfoDto findSupervisors() throws ServiceBizException;
	
	public List<Map> findEmployeeNoByLogin() throws ServiceBizException;

	public List<Map> returnName(String employeeNo) throws ServiceBizException;
	
	public void userSeriesSave(EmployeeRoleDto roleDto,long duDao,long xiZhang,long keZhang,long buZhang) throws ServiceBizException;
	
	public void userSeriesUpdateSave(long duDao,long xiZhang,long keZhang,long buZhang,long employeeId) throws ServiceBizException;
	
	public Map findNameId(Long id)throws ServiceBizException;
	
	public void findGender(Long id)throws ServiceBizException;
}

