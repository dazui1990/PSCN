
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

package com.yonyou.dms.manage.service.basedata.dealer;

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
 * 特约店Service实现类
 * @author Jiangxy
 * @date 2018年5月24日
 */
@Service
public class DealerServiceImpl implements DealerService{

	/**
	 * 特约店下拉框(权限控制)
	 */
	@Override
	public List<Map> queryDealers(Long userId, String employeeNo) throws ServiceBizException {

		String sql = "";
		List<Map> resultList = new ArrayList<>();

		// 管理员登录
		if (employeeNo.indexOf("dmin") != -1) {
			sql = "SELECT a.DEALER_ID,a.DEALER_CODE,a.DEALER_SHORTNAME FROM tm_dealer a where a.STATUS=10011001 GROUP BY a.DEALER_ID";
		} else {
			// 查询登录人的岗位类别
			List<EmployeePostPo> postPos = EmployeePostPo.find("employee_id=? order by post_code desc", userId);
			if (!CollectionUtils.isEmpty(postPos)) {
				String postCode = postPos.get(0).get("post_code").toString();
				// 部长 科长
				if ("96091004".equals(postCode) || "96091003".equals(postCode)) {
					sql = "SELECT a.DEALER_ID,a.DEALER_CODE,a.DEALER_SHORTNAME FROM tm_dealer a where a.STATUS=10011001 GROUP BY a.DEALER_ID";
				}
				// 系长
				else if ("96091002".equals(postCode)) {
					sql = "SELECT a.DEALER_ID,a.DEALER_CODE,a.DEALER_SHORTNAME FROM tm_dealer a inner join tt_dealer_role b on a.dealer_id=b.dealer_id  where a.STATUS=10011001 and b.SERIES_ORDER_ID="
							+ userId + " GROUP BY a.DEALER_ID";
				}
				// 督导
				else if ("96091001".equals(postCode)) {
					sql = "SELECT a.DEALER_ID,a.DEALER_CODE,a.DEALER_SHORTNAME FROM tm_dealer a inner join tt_dealer_role b on a.dealer_id=b.dealer_id  where a.STATUS=10011001 and b.SUPERVISION_ID="
							+ userId + " GROUP BY a.DEALER_ID";
				} else {
					return resultList;
				}
			}
		}
		resultList = DAOUtil.findAll(sql, null);
		return resultList;
	}
	
	/**
	 * 业务数据  特约店权限控制
	 * 登录工号只显示其管辖的特约店
	 * 用于SQL拼接,admin则返回空List 不做SQL控制
	 */
	@Override
	public List<Map> queryDealerIds(Long userId, String employeeNo) throws ServiceBizException {
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
					sql = "SELECT a.DEALER_ID FROM tm_dealer a inner join tt_dealer_role b on a.dealer_id=b.dealer_id  where a.STATUS=10011001 and b.SERIES_ORDER_ID="
							+ userId + " GROUP BY a.DEALER_ID";
				}
				// 督导
				else if ("96091001".equals(postCode)) {
					sql = "SELECT a.DEALER_ID FROM tm_dealer a inner join tt_dealer_role b on a.dealer_id=b.dealer_id  where a.STATUS=10011001 and b.SUPERVISION_ID="
							+ userId + " GROUP BY a.DEALER_ID";
				} else {
					return resultList;
				}
			}
		}
		resultList = DAOUtil.findAll(sql, null);
		return resultList;
	}
	
}
