
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : DealerBasicinfoServiceImpl1.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.PO.basedata.EmployeePostPo;

/**
 * 
 * 大区 小区(督导)管理Service实现类
 * @author Jiangxy
 * @date 2018年5月24日
 */
@Service
public class RegionOrgServiceImpl implements RegionOrgService{

	@Override
	public List<Map> queryBigOrg(Long userId, String employeeNo) throws ServiceBizException {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ORG_ID,ORG_NAME\n");
		sql.append(" FROM tm_org \n");
		sql.append(" WHERE ORG_TYPE=10191002 \n");
	    
		List<Object> params =new ArrayList<>();
		return DAOUtil.findAll(sql.toString(), params);
	}

	@Override
	public List<Map> findDudaoInfo(Long userId, String employeeNo) throws ServiceBizException {
		// TODO Auto-generated method stub

		String sql = "";
		List<Map> resultList = new ArrayList<>();

		// 管理员登录
		if (employeeNo.indexOf("dmin") != -1) {
			sql = "SELECT a.EMPLOYEE_ID,a.EMPLOYEE_NO,a.EMPLOYEE_NAME FROM tm_employee a"
					+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
					+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
					+ " GROUP BY b.EMPLOYEE_ID";
		} else {
			// 查询登录人的岗位类别
			List<EmployeePostPo> postPos = EmployeePostPo.find("employee_id=? order by post_code desc", userId);
			if (!CollectionUtils.isEmpty(postPos)) {
				String postCode = postPos.get(0).get("post_code").toString();
				// 部长 科长
				if ("96091004".equals(postCode) || "96091003".equals(postCode)) {
					sql = "SELECT a.EMPLOYEE_ID,a.EMPLOYEE_NO,a.EMPLOYEE_NAME FROM tm_employee a"
							+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
							+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
							+ " GROUP BY a.EMPLOYEE_ID";
				}
				// 系长
				else if ("96091002".equals(postCode)) {
					sql = "SELECT a.EMPLOYEE_ID,a.EMPLOYEE_NO,a.EMPLOYEE_NAME FROM tm_employee a"
							+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
							+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
							+ " WHERE a.EMPLOYEE_ID IN (SELECT DISTINCT r.SUPERVISION_ID FROM tt_dealer_role r WHERE r.STATUS = 10011001 AND r.SERIES_ORDER_ID ="+userId+")"
							+ " GROUP BY a.EMPLOYEE_ID";
				}
				// 督导
				else if ("96091001".equals(postCode)) {
					sql = "SELECT a.EMPLOYEE_ID,a.EMPLOYEE_NO,a.EMPLOYEE_NAME FROM tm_employee a"
							+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
							+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
							+ " WHERE a.EMPLOYEE_ID = "+userId
							+ " GROUP BY a.EMPLOYEE_ID";
				} else {
					return resultList;
				}
			}
		}
		resultList = DAOUtil.findAll(sql, null);
		return resultList;
	}

	/**
	 * 业务数据  督导权限控制
	 * 登录工号只显示其属下的督导
	 * 用于SQL拼接,admin则返回空List 不做SQL控制
	 */
	@Override
	public List<Map> findDudaoIdInfo(Long userId, String employeeNo) throws ServiceBizException {
		// TODO Auto-generated method stub

		String sql = "";
		List<Map> resultList = new ArrayList<>();

		// 管理员登录
		if (employeeNo.indexOf("dmin") != -1) {
			return resultList;
		} else {
			// 查询登录人的岗位类别
			List<EmployeePostPo> postPos = EmployeePostPo.find("employee_id=? order by post_code desc", userId);
			if (!CollectionUtils.isEmpty(postPos)) {
				String postCode = postPos.get(0).get("post_code").toString();
				// 部长 科长
				if ("96091004".equals(postCode) || "96091003".equals(postCode)) {
					return resultList;
				}
				// 系长
				else if ("96091002".equals(postCode)) {
					sql = "SELECT a.EMPLOYEE_ID FROM tm_employee a"
							+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
							+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
							+ " WHERE a.EMPLOYEE_ID IN (SELECT DISTINCT r.SUPERVISION_ID FROM tt_dealer_role r WHERE r.STATUS = 10011001 AND r.SERIES_ORDER_ID ="+userId+")"
							+ " GROUP BY a.EMPLOYEE_ID";
				}
				// 督导
				else if ("96091001".equals(postCode)) {
					sql = "SELECT a.EMPLOYEE_ID FROM tm_employee a"
							+ " INNER JOIN tm_employee_enabled b ON a.EMPLOYEE_ID = b.EMPLOYEE_ID AND b.STATUS = 10031001"
							+ " INNER JOIN tm_employee_post c ON a.EMPLOYEE_ID = c.EMPLOYEE_ID AND c.post_code = 96091001"
							+ " WHERE a.EMPLOYEE_ID = "+userId
							+ " GROUP BY a.EMPLOYEE_ID";
				} else {
					return resultList;
				}
			}
		}
		resultList = DAOUtil.findAll(sql, null);
		return resultList;
	}
	
}
