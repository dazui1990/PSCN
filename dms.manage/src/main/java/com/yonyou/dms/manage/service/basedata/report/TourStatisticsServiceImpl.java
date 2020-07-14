
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : OrganizatioServiceImpl.java
*
* @Author : rongzoujie
*
* @Date : 2016年7月6日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月6日    rongzoujie    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
package com.yonyou.dms.manage.service.basedata.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.service.basedata.regionOrg.RegionOrgService;

@Service
public class TourStatisticsServiceImpl implements TourStatisticsService {
	
    @Resource
    private RegionOrgService regionOrgService;
    
	@Override
	public PageInfoDto findTourStatistics(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		Long userId = loginInfo.getUserId();
		String employeeNo = loginInfo.getEmployeeNo();
		//登录工号权限对应特约店
		List<Map> dudaoIdList = regionOrgService.findDudaoInfo(userId, employeeNo);
		
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();

		sql.append("SELECT (SELECT E.EMPLOYEE_NAME FROM tm_employee E WHERE E.EMPLOYEE_ID = T.EMPLOYEE_ID) AS EMPLOYEE_NAME, \n");
		sql.append(" DATE_FORMAT(T.TOUR_DATE,'%Y-%m') AS YEARMONTH, \n");		
		//拼接1-31号
		for (int i = 1; i < 32; i++) {
			String day = "";
			if(i <= 9) {
				day = "0"+i;
			}else {
				day = ""+i;
			}
			sql.append(" MAX(CASE WHEN DATE_FORMAT(T.TOUR_DATE,'%d') = '"+day+"' AND DATE_FORMAT(T.TOUR_DATE,'%H') <= 11 THEN T.DEALER_SHORTNAME ELSE '' END) AS day_"+day+"_1, \n");
			sql.append(" MAX(CASE WHEN DATE_FORMAT(T.TOUR_DATE,'%d') = '"+day+"' AND DATE_FORMAT(T.TOUR_DATE,'%H') >= 12 THEN T.DEALER_SHORTNAME ELSE '' END) AS day_"+day+"_2, \n");
		}
		sql.append(" T.STAGE \n");
		sql.append(" FROM ( \n");
		//巡回计划
		sql.append(" SELECT T1.USER_ID AS EMPLOYEE_ID, \n");
		sql.append(" T1.START_DATE AS TOUR_DATE,T0.DEALER_SHORTNAME,S.STAGE \n");
		sql.append(" FROM tt_supervision_agenda T1  \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1  \n");
		sql.append(" LEFT JOIN tt_supervision_agenda T0 ON T1.AGENDA_ID = T0.AGENDA_ID AND S.STAGE = '巡回计划' \n");
		sql.append(" WHERE T1.STATE IN (90021005,90021006) \n");
		//巡回实际
		sql.append(" UNION ALL \n");
		sql.append(" SELECT (SELECT E.EMPLOYEE_ID FROM tm_employee E WHERE E.EMPLOYEE_NO = T2.EVALUATE_BY) AS EMPLOYEE_ID, \n");
		sql.append(" T2.ACTUALEVALUATE_DATE AS TOUR_DATE, \n");
		sql.append(" (SELECT D.DEALER_SHORTNAME FROM TM_DEALER D WHERE D.DEALER_ID = T0.DEALER_ID),S.STAGE \n");
		sql.append(" FROM tt_scene_evaluate T2 \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1 \n");
		sql.append(" LEFT JOIN tt_scene_evaluate T0 ON T2.ID = T0.ID AND S.STAGE = '巡回实际' \n");
		sql.append(" WHERE T2.STATE = 10011001 \n");
		sql.append(" AND EXISTS(SELECT 1 FROM tt_evaluate TE WHERE TE.PROPERTY_ID = 10371001 AND TE.ID = T2.TTEVALUATE_ID) \n");
		//店报发送
		sql.append(" UNION ALL \n");
		sql.append(" SELECT T3.SUBMIT_BY AS EMPLOYEE_ID, \n");
		sql.append(" T3.SUBMIT_DATE AS TOUR_DATE,T0.DEALER_SHORTNAME,S.STAGE \n");
		sql.append(" FROM tt_patrol_report T3 \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1 \n");
		sql.append(" LEFT JOIN tt_patrol_report T0 ON T3.ID = T0.ID AND S.STAGE = '店报发送' \n");
		sql.append(" WHERE T3.REPORT_STATUS = 92051002 \n");
		sql.append(" ) T \n");
		sql.append(" WHERE 1=1");
		if(map.get("yearMonthBegin") != null) {
			sql.append(" AND DATE_FORMAT(T.TOUR_DATE,'%Y-%m') >= ?");
			param.add(map.get("yearMonthBegin"));
		}
		if(map.get("yearMonthEnd") != null) {
			sql.append(" AND DATE_FORMAT(T.TOUR_DATE,'%Y-%m') <= ?");
			param.add(map.get("yearMonthEnd"));
		}
		if(map.get("duDaoId") != null) {
			sql.append(" AND T.EMPLOYEE_ID = ?");
			param.add(map.get("duDaoId"));
		}
		if(!CollectionUtils.isEmpty(dudaoIdList)) {
			sql.append(" AND T.EMPLOYEE_ID IN (");
			for (int i = 0; i < dudaoIdList.size(); i++) {
				sql.append(dudaoIdList.get(i).get("EMPLOYEE_ID"));
				if(i != dudaoIdList.size()-1) {
					sql.append(",");
				}
			}
			sql.append(")");
		}
		sql.append(" GROUP BY T.EMPLOYEE_ID,DATE_FORMAT(T.TOUR_DATE,'%Y-%m'),T.STAGE");
		sql.append(" ORDER BY DATE_FORMAT(T.TOUR_DATE,'%Y-%m'),T.EMPLOYEE_ID,FIELD(T.STAGE,'巡回计划','巡回实际','店报发送')");
		
		return DAOUtil.pageQuery(sql.toString(), param);
	}

	@Override
	public List<Map> findTourStatisticsList(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		Long userId = loginInfo.getUserId();
		String employeeNo = loginInfo.getEmployeeNo();
		//登录工号权限对应特约店
		List<Map> dudaoIdList = regionOrgService.findDudaoInfo(userId, employeeNo);
		
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();

		sql.append("SELECT (SELECT E.EMPLOYEE_NAME FROM tm_employee E WHERE E.EMPLOYEE_ID = T.EMPLOYEE_ID) AS EMPLOYEE_NAME, \n");
		sql.append(" DATE_FORMAT(T.TOUR_DATE,'%Y-%m') AS YEARMONTH, \n");		
		//拼接1-31号
		for (int i = 1; i < 32; i++) {
			String day = "";
			if(i <= 9) {
				day = "0"+i;
			}else {
				day = ""+i;
			}
			sql.append(" MAX(CASE WHEN DATE_FORMAT(T.TOUR_DATE,'%d') = '"+day+"' AND DATE_FORMAT(T.TOUR_DATE,'%H') <= 11 THEN T.DEALER_SHORTNAME ELSE '' END) AS day_"+day+"_1, \n");
			sql.append(" MAX(CASE WHEN DATE_FORMAT(T.TOUR_DATE,'%d') = '"+day+"' AND DATE_FORMAT(T.TOUR_DATE,'%H') >= 12 THEN T.DEALER_SHORTNAME ELSE '' END) AS day_"+day+"_2, \n");
		}
		sql.append(" T.STAGE \n");
		sql.append(" FROM ( \n");
		//巡回计划
		sql.append(" SELECT T1.USER_ID AS EMPLOYEE_ID, \n");
		sql.append(" T1.START_DATE AS TOUR_DATE,T0.DEALER_SHORTNAME,S.STAGE \n");
		sql.append(" FROM tt_supervision_agenda T1  \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1  \n");
		sql.append(" LEFT JOIN tt_supervision_agenda T0 ON T1.AGENDA_ID = T0.AGENDA_ID AND S.STAGE = '巡回计划' \n");
		sql.append(" WHERE T1.STATE IN (90021005,90021006) \n");
		//巡回实际
		sql.append(" UNION ALL \n");
		sql.append(" SELECT (SELECT E.EMPLOYEE_ID FROM tm_employee E WHERE E.EMPLOYEE_NO = T2.EVALUATE_BY) AS EMPLOYEE_ID, \n");
		sql.append(" T2.ACTUALEVALUATE_DATE AS TOUR_DATE, \n");
		sql.append(" (SELECT D.DEALER_SHORTNAME FROM TM_DEALER D WHERE D.DEALER_ID = T0.DEALER_ID),S.STAGE \n");
		sql.append(" FROM tt_scene_evaluate T2 \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1 \n");
		sql.append(" LEFT JOIN tt_scene_evaluate T0 ON T2.ID = T0.ID AND S.STAGE = '巡回实际' \n");
		sql.append(" WHERE T2.STATE = 10011001 \n");
		sql.append(" AND EXISTS(SELECT 1 FROM tt_evaluate TE WHERE TE.PROPERTY_ID = 10371001 AND TE.ID = T2.TTEVALUATE_ID) \n");
		//店报发送
		sql.append(" UNION ALL \n");
		sql.append(" SELECT T3.SUBMIT_BY AS EMPLOYEE_ID, \n");
		sql.append(" T3.SUBMIT_DATE AS TOUR_DATE,T0.DEALER_SHORTNAME,S.STAGE \n");
		sql.append(" FROM tt_patrol_report T3 \n");
		sql.append(" LEFT JOIN tt_tour_stage S ON 1=1 \n");
		sql.append(" LEFT JOIN tt_patrol_report T0 ON T3.ID = T0.ID AND S.STAGE = '店报发送' \n");
		sql.append(" WHERE T3.REPORT_STATUS = 92051002 \n");
		sql.append(" ) T \n");
		sql.append(" WHERE 1=1");
		if(map.get("yearMonthBegin") != null && !"".equals(map.get("yearMonthBegin"))) {
			sql.append(" AND DATE_FORMAT(T.TOUR_DATE,'%Y-%m') >= ?");
			param.add(map.get("yearMonthBegin"));
		}
		if(map.get("yearMonthEnd") != null && !"".equals(map.get("yearMonthBegin"))) {
			sql.append(" AND DATE_FORMAT(T.TOUR_DATE,'%Y-%m') <= ?");
			param.add(map.get("yearMonthEnd"));
		}
		if(map.get("duDaoId") != null && !"".equals(map.get("duDaoId"))) {
			sql.append(" AND T.EMPLOYEE_ID = ?");
			param.add(map.get("duDaoId"));
		}
		if(!CollectionUtils.isEmpty(dudaoIdList)) {
			sql.append(" AND T.EMPLOYEE_ID IN (");
			for (int i = 0; i < dudaoIdList.size(); i++) {
				sql.append(dudaoIdList.get(i).get("EMPLOYEE_ID"));
				if(i != dudaoIdList.size()-1) {
					sql.append(",");
				}
			}
			sql.append(")");
		}
		sql.append(" GROUP BY T.EMPLOYEE_ID,DATE_FORMAT(T.TOUR_DATE,'%Y-%m'),T.STAGE");
		sql.append(" ORDER BY DATE_FORMAT(T.TOUR_DATE,'%Y-%m'),T.EMPLOYEE_ID,FIELD(T.STAGE,'巡回计划','巡回实际','店报发送')");
		
		return DAOUtil.findAll(sql.toString(), param);
	}

}
