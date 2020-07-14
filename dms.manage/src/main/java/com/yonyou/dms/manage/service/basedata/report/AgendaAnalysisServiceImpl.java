
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
import com.yonyou.dms.manage.service.basedata.dealer.DealerService;
import com.yonyou.dms.manage.service.basedata.employee.EmployeeService;

@Service
public class AgendaAnalysisServiceImpl implements AgendaAnalysisService {
    
    @Resource
    private DealerService dealerService;
    
    @Resource
    private EmployeeService employeeService;

    /**
     * 巡回计划分析-报表
     */
	@Override
	public PageInfoDto findAgendaAnalysis(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		//登录工号所管辖所有督导
		List<Map> empNoList = employeeService.findEmployeeNoByLogin();
		
		StringBuilder sql = new StringBuilder("");
		List<Object> params = new ArrayList<>();
		
		sql.append("SELECT V.DEALER_CODE,V.DEALER_SHORTNAME,V.ORG_NAME AS BIG_ORG_NAME, \n");
		sql.append(" V.EMPLOYEE_NAME AS SMALL_ORG_NAME,DATE_FORMAT(T.START_DATE,'%Y-%m') AS YEARMONTH, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_agenda_calendar_relation c WHERE C.AGENDA_ID = T.AGENDA_ID)*0.5 AS AGENDA_DAYS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001 WHERE SE.AGENDA_ID = T.AGENDA_ID) AS SCENE_EVALUATE_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371002 WHERE SE.AGENDA_ID = T.AGENDA_ID) AS OTHER_EVALUATE_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001 WHERE SE.IS_CORRECT = 10041001 AND SE.AGENDA_ID = T.AGENDA_ID) AS CORRECT_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001");
		sql.append(" INNER JOIN tt_correct_plan P ON P.SCENE_EVALUATE_ID = SE.ID AND P.IS_WEAK_PROJECT = 10041001 WHERE SE.IS_CORRECT = 10041001 AND SE.AGENDA_ID = T.AGENDA_ID) AS CORRECT_ITEM_NUMS,");
		sql.append(" (SELECT COUNT(1) FROM tm_nl_base N WHERE N.AGENDA_ID = T.AGENDA_ID) AS NL_NUMS \n");
		sql.append(" FROM tt_supervision_agenda T \n");
		sql.append(" INNER JOIN tt_supervision_agenda_detail TT ON T.AGENDA_ID = TT.AGENDA_ID AND TT.CHANGE_STATE = 90031001 AND TT.EVENT_TYPE = 90011002  \n");
		sql.append(" LEFT JOIN vm_dealer_org_dudao_province V ON V.DEALER_ID = T.DEALER_ID \n");
		sql.append(" WHERE T.DEALER_ID IS NOT NULL");
		// 权限控制
		if (!CollectionUtils.isEmpty(empNoList)) {
			sql.append(" AND T.USER_ID IN (");
			for (int i = 0; i < empNoList.size(); i++) {
				sql.append(empNoList.get(i).get("EMPLOYEE_ID"));
				if (i != empNoList.size() - 1) {
					sql.append(",");
				}
			}
			sql.append(")");
		} else {
			// 异常情况 查不到登录工号对应督导 则不查询任何数据
			sql.append(" AND 1=0");
		}
		//查询条件
		if(map.get("dealer_id") != null) {
			sql.append(" AND T.DEALER_ID = ?");
			params.add(map.get("dealer_id"));
		}
		if(map.get("bigOrg") != null) {
			sql.append(" AND V.ORG_ID = ?");
			params.add(map.get("bigOrg"));
		}
		if(map.get("smallOrg") != null) {
			sql.append(" AND V.EMPLOYEE_ID = ?");
			params.add(map.get("smallOrg"));
		}
		if(map.get("yearMonthBegin") != null) {
			sql.append(" AND DATE_FORMAT(T.START_DATE,'%Y-%m') >= '"+map.get("yearMonthBegin")+"'");
		}
		if(map.get("yearMonthEnd") != null) {
			sql.append(" AND DATE_FORMAT(T.START_DATE,'%Y-%m') <= '"+map.get("yearMonthEnd")+"'");
		}
		sql.append(" GROUP BY DATE_FORMAT(T.START_DATE,'%Y-%m'),T.DEALER_ID \n");
		sql.append(" ORDER BY DATE_FORMAT(T.START_DATE,'%Y-%m'),V.ORG_ID,V.EMPLOYEE_ID,T.DEALER_ID");
		
		return DAOUtil.pageQuery(sql.toString(), params);
	}
	
    /**
     * 巡回计划分析-报表
     */
	@Override
	public List<Map> findAgendaAnalysisList(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		//登录工号所管辖所有督导
		List<Map> empNoList = employeeService.findEmployeeNoByLogin();
		
		StringBuilder sql = new StringBuilder("");
		List<Object> params = new ArrayList<>();
		
		sql.append("SELECT V.DEALER_CODE,V.DEALER_SHORTNAME,V.ORG_NAME AS BIG_ORG_NAME, \n");
		sql.append(" V.EMPLOYEE_NAME AS SMALL_ORG_NAME,DATE_FORMAT(T.START_DATE,'%Y-%m') AS YEARMONTH, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_agenda_calendar_relation c WHERE C.AGENDA_ID = T.AGENDA_ID)*0.5 AS AGENDA_DAYS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001 WHERE SE.AGENDA_ID = T.AGENDA_ID) AS SCENE_EVALUATE_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371002 WHERE SE.AGENDA_ID = T.AGENDA_ID) AS OTHER_EVALUATE_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001 WHERE SE.IS_CORRECT = 10041001 AND SE.AGENDA_ID = T.AGENDA_ID) AS CORRECT_NUMS, \n");
		sql.append(" (SELECT COUNT(1) FROM tt_scene_evaluate SE INNER JOIN tt_evaluate E ON SE.TTEVALUATE_ID = E.ID AND E.PROPERTY_ID = 10371001");
		sql.append(" INNER JOIN tt_correct_plan P ON P.SCENE_EVALUATE_ID = SE.ID AND P.IS_WEAK_PROJECT = 10041001 WHERE SE.IS_CORRECT = 10041001 AND SE.AGENDA_ID = T.AGENDA_ID) AS CORRECT_ITEM_NUMS,");
		sql.append(" (SELECT COUNT(1) FROM tm_nl_analysis_entry N WHERE N.AGENDA_ID = T.AGENDA_ID AND N.ISFINISH = '1') AS NL_NUMS \n");
		sql.append(" FROM tt_supervision_agenda T \n");
		sql.append(" LEFT JOIN vm_dealer_org_dudao_province V ON V.DEALER_ID = T.DEALER_ID \n");
		sql.append(" WHERE T.DEALER_ID IS NOT NULL");
		//权限控制 
		if(!CollectionUtils.isEmpty(empNoList)) {
			sql.append(" AND T.USER_ID IN (");
			for (int i = 0; i < empNoList.size(); i++) {
				sql.append(empNoList.get(i).get("EMPLOYEE_ID"));
				if(i != empNoList.size()-1) {
					sql.append(",");
				}
			}
			sql.append(")");
		}else {
			//异常情况 查不到登录工号对应督导 则不查询任何数据
			sql.append(" AND 1=0");
		}
		//查询条件
		if(map.get("dealer_id") != null && !"".equals(map.get("dealer_id").toString())) {
			sql.append(" AND T.DEALER_ID = ?");
			params.add(map.get("dealer_id"));
		}
		if(map.get("bigOrg") != null && !"".equals(map.get("bigOrg").toString())) {
			sql.append(" AND V.ORG_ID = ?");
			params.add(map.get("bigOrg"));
		}
		if(map.get("smallOrg") != null && !"".equals(map.get("smallOrg").toString())) {
			sql.append(" AND V.EMPLOYEE_ID = ?");
			params.add(map.get("smallOrg"));
		}
		if(map.get("yearMonthBegin") != null && !"".equals(map.get("yearMonthBegin").toString())) {
			sql.append(" AND DATE_FORMAT(T.START_DATE,'%Y-%m') >= '"+map.get("yearMonthBegin")+"'");
		}
		if(map.get("yearMonthEnd") != null && !"".equals(map.get("yearMonthEnd").toString())) {
			sql.append(" AND DATE_FORMAT(T.START_DATE,'%Y-%m') <= '"+map.get("yearMonthEnd")+"'");
		}
		sql.append(" GROUP BY DATE_FORMAT(T.START_DATE,'%Y-%m'),T.DEALER_ID \n");
		sql.append(" ORDER BY DATE_FORMAT(T.START_DATE,'%Y-%m'),V.ORG_ID,V.EMPLOYEE_ID,T.DEALER_ID");		
		
		return DAOUtil.findAll(sql.toString(), params);
	}
}
