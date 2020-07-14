
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
public class WeakStatisticsServiceImpl implements WeakStatisticsService {

	@Resource
	private DealerService dealerService;

	@Resource
	private EmployeeService employeeService;

	/**
	 * 整改结果统计
	 */
	@Override
	public PageInfoDto findWeakStatistics(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		// 登录工号所管辖所有督导
		List<Map> empNoList = employeeService.findEmployeeNoByLogin();
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder("");
		String  flag = (String) map.get("flag");
		if (flag.equals("10041002")) {
			sql.append(
					" SELECT   A.EMPLOYEE_NAME,A.GLDE_NUM ,A.employee_no AS evaluate_by ,B.*,round(B.CORRECT_NUM/A.GLDE_NUM,2) AS GS_NUM,round(B.DDTF_NUM/B.OVERDUE_UNFINISH_NUM*100,2) AS TG_LV FROM \n");
			sql.append(" (SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM  \n");
			sql.append(" FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
			sql.append(" WHERE v.employee_id=tme.employee_id\n");
			sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A \n");
			sql.append(" LEFT  JOIN ( \n");
			sql.append(
					" SELECT  V.evaluate_by AS evaluate_by1,SUM(V.CORRECT_NUM) AS CORRECT_NUM,SUM(V.OVERDUE_UNFINISH_NUM) AS OVERDUE_UNFINISH_NUM,SUM(V.FK_NUM) AS FK_NUM, \n");
			sql.append(" SUM(V.DDTF_NUM) AS DDTF_NUM, SUM(V.DDCLOSE_NUM) AS DDCLOSE_NUM  FROM ( \n");
			sql.append(" SELECT T.evaluate_by, \n");
			sql.append(
					" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) AS CORRECT_NUM, \n");
			sql.append(
					" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001  AND P.CORRECT_STATUS = 92041001   \n");
			sql.append(
					"  AND date_format(P.PLAN_FINISH_DATE,'%Y-%m-%d') < date_format(now(),'%Y-%m-%d') AND P.PLAN_BASE_ID = T.ID) AS OVERDUE_UNFINISH_NUM, \n");
			sql.append(
					" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
			sql.append("  AND P.real_finish_date is not NULL AND P.PLAN_BASE_ID = T.ID) AS FK_NUM,\n");
			sql.append(
					" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
			sql.append(" AND P.CORRECT_STATUS = 92041004  AND P.PLAN_BASE_ID = T.ID) AS DDTF_NUM,\n");
			sql.append(
					" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n ");
			sql.append(" AND P.CORRECT_STATUS = 92041005  AND P.PLAN_BASE_ID = T.ID) AS DDCLOSE_NUM \n");
			sql.append(" FROM tt_correct_plan_base T \n");
			sql.append(" WHERE T.STATUS = 10011001 AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
			sql.append(" WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
			// 权限控制
			if (null != map.get("Begin")) {
				sql.append("and t.created_at>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and t.created_at<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append(" ) V GROUP BY V.evaluate_by \n");
			sql.append(" )B ON B.evaluate_by1=A.employee_no \n");
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("WHERE A.employee_no ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" WHERE A.employee_no IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}
			sql.append(" order by A.EMPLOYEE_NAME DESC \n");
		} else {
			sql.append(" SELECT (@i:=@i+1) AS RN,V.*,V.employee_no AS evaluate_by  FROM (\n");
			sql.append(
					"SELECT A.*,B.CORRECT_NUM,C.OVERDUE_UNFINISH_NUM,D.FK_NUM,E.DDTF_NUM,F.DDCLOSE_NUM,round(B.CORRECT_NUM/A.GLDE_NUM,2) AS GS_NUM,round(E.DDTF_NUM/C.OVERDUE_UNFINISH_NUM*100,2) AS TG_LV   FROM(\n");
			sql.append(" SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM  \n");
			sql.append("FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
			sql.append(" WHERE v.employee_id=tme.employee_id\n");
			sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A\n");
			sql.append(" LEFT JOIN (\n");
			sql.append("SELECT COUNT(1) AS CORRECT_NUM,evaluate_by  \n");
			sql.append("FROM tt_correct_plan_base T \n");
			sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
			sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
			sql.append("AND T.STATUS = 10011001 AND P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
			sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
			sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) \n");
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" AND t.evaluate_by IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}
			
			if (null != map.get("Begin")) {
				sql.append("and t.created_at>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and t.created_at<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append("GROUP BY  t.evaluate_by)B ON B.evaluate_by=A.employee_no\n");
			sql.append("LEFT JOIN (\n");
			sql.append("SELECT COUNT(1) AS OVERDUE_UNFINISH_NUM,evaluate_by  \n");
			sql.append("FROM tt_correct_plan_base T \n");
			sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
			sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
			sql.append("AND T.STATUS = 10011001 AND \n");
			sql.append(" P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001  AND P.CORRECT_STATUS = 92041001 \n");
			sql.append("  AND date_format(P.PLAN_FINISH_DATE,'%Y-%m-%d') < date_format(now(),'%Y-%m-%d')\n");
			sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
			sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" AND t.evaluate_by IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}

			if (null != map.get("Begin")) {
				sql.append("and P.plan_finish_date>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and P.plan_finish_date<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append("GROUP BY  t.evaluate_by)C ON C.evaluate_by=A.employee_no\n");
			sql.append("LEFT JOIN (\n");
			sql.append("SELECT COUNT(1) AS FK_NUM,evaluate_by  \n");
			sql.append("FROM tt_correct_plan_base T \n");
			sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
			sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
			sql.append("AND T.STATUS = 10011001 AND \n");
			sql.append("P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.real_finish_date is not NULL \n");
			sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
			sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" AND t.evaluate_by IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}

			if (null != map.get("Begin")) {
				sql.append("and P.real_finish_date>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and P.real_finish_date<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append("GROUP BY  t.evaluate_by) D ON d.evaluate_by =A.employee_no\n");
			sql.append("LEFT JOIN (\n");
			sql.append("SELECT COUNT(1) AS DDTF_NUM,evaluate_by  \n");
			sql.append("FROM tt_correct_plan_base T \n");
			sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
			sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
			sql.append("AND T.STATUS = 10011001 AND \n");
			sql.append("P.STATUS = 10011001 AND P.CORRECT_STATUS = 92041004\n");
			sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
			sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) \n");

			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" AND t.evaluate_by IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}

			if (null != map.get("Begin")) {
				sql.append("and P.audit_date>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and P.audit_date<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append("GROUP BY  t.evaluate_by) E ON E.evaluate_by=A.employee_no \n");
			sql.append("LEFT JOIN ( \n");
			sql.append("SELECT COUNT(1) AS DDCLOSE_NUM,evaluate_by   \n");
			sql.append("FROM tt_correct_plan_base T  \n");
			sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID \n");
			sql.append("WHERE  P.PLAN_BASE_ID = T.ID  \n");
			sql.append("AND T.STATUS = 10011001 AND  \n");
			sql.append("P.STATUS = 10011001   AND P.CORRECT_STATUS = 92041005   \n");
			sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P  \n");
			sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)   \n");
			
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" AND t.evaluate_by IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}
			if (null != map.get("Begin")) {
				sql.append("and P.close_date>='" + map.get("Begin") + " 00:00:00' \n");
			}
			if (null != map.get("End")) {
				sql.append("and P.close_date<='" + map.get("End") + " 23:59:59' \n");
			}
			sql.append("GROUP BY  t.evaluate_by)F ON F.evaluate_by=A.employee_no\n");
			if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
				sql.append("WHERE A.employee_no ='" + map.get("duDaoId") + "' \n");
			} else {
				if (!CollectionUtils.isEmpty(empNoList)) {
					sql.append(" WHERE A.employee_no IN (");
					for (int i = 0; i < empNoList.size(); i++) {
						sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
						if (i != empNoList.size() - 1) {
							sql.append("',");
						}
					}
					sql.append("')");
				} else {
					// 异常情况 查不到登录工号对应督导 则不查询任何数据
					sql.append(" AND 1=0");
				}
			}
			sql.append(" order by A.EMPLOYEE_NAME DESC\n");
			sql.append("	) V,(SELECT @i:=0)t \n");
		}
		return DAOUtil.pageQuery(sql.toString(), params);
	}

	/**
	 * 巡回计划分析-报表
	 */
	@Override
	public List<Map> findWeakStatisticsList(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		// 登录工号所管辖所有督导
				List<Map> empNoList = employeeService.findEmployeeNoByLogin();
				List<Object> params = new ArrayList<>();
				StringBuilder sql = new StringBuilder("");
				String  flag = (String) map.get("flag");
				if (flag.equals("10041002")) {
					sql.append(
							" SELECT   A.EMPLOYEE_NAME,A.GLDE_NUM ,A.employee_no AS evaluate_by,B.*,round(B.CORRECT_NUM/A.GLDE_NUM,2) AS GS_NUM,round(B.DDTF_NUM/B.OVERDUE_UNFINISH_NUM*100,2) AS TG_LV FROM \n");
					sql.append(" (SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM  \n");
					sql.append(" FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
					sql.append(" WHERE v.employee_id=tme.employee_id\n");
					sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A \n");
					sql.append(" LEFT  JOIN ( \n");
					sql.append(
							" SELECT  V.evaluate_by AS evaluate_by1,SUM(V.CORRECT_NUM) AS CORRECT_NUM,SUM(V.OVERDUE_UNFINISH_NUM) AS OVERDUE_UNFINISH_NUM,SUM(V.FK_NUM) AS FK_NUM, \n");
					sql.append(" SUM(V.DDTF_NUM) AS DDTF_NUM, SUM(V.DDCLOSE_NUM) AS DDCLOSE_NUM  FROM ( \n");
					sql.append(" SELECT T.evaluate_by, \n");
					sql.append(
							" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) AS CORRECT_NUM, \n");
					sql.append(
							" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001  AND P.CORRECT_STATUS = 92041001   \n");
					sql.append(
							"  AND date_format(P.PLAN_FINISH_DATE,'%Y-%m-%d') < date_format(now(),'%Y-%m-%d') AND P.PLAN_BASE_ID = T.ID) AS OVERDUE_UNFINISH_NUM, \n");
					sql.append(
							" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
					sql.append("  AND P.real_finish_date is not NULL AND P.PLAN_BASE_ID = T.ID) AS FK_NUM,\n");
					sql.append(
							" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
					sql.append(" AND P.CORRECT_STATUS = 92041004  AND P.PLAN_BASE_ID = T.ID) AS DDTF_NUM,\n");
					sql.append(
							" (SELECT COUNT(1) FROM tt_correct_plan P WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n ");
					sql.append(" AND P.CORRECT_STATUS = 92041005  AND P.PLAN_BASE_ID = T.ID) AS DDCLOSE_NUM \n");
					sql.append(" FROM tt_correct_plan_base T \n");
					sql.append(" WHERE T.STATUS = 10011001 AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
					sql.append(" WHERE P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
					// 权限控制
					if (null != map.get("Begin")) {
						sql.append("and t.created_at>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and t.created_at<='" + map.get("End") + " 23:59:59' \n");
					}
					if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
						sql.append("AND t.evaluate_by ='" + map.get("duDaoId") + "' \n");
					} else {
						if (!CollectionUtils.isEmpty(empNoList)) {
							sql.append(" AND t.evaluate_by IN (");
							for (int i = 0; i < empNoList.size(); i++) {
								sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
								if (i != empNoList.size() - 1) {
									sql.append("',");
								}
							}
							sql.append("')");
						} else {
							// 异常情况 查不到登录工号对应督导 则不查询任何数据
							sql.append(" AND 1=0");
						}
					}
					sql.append(" ) V GROUP BY V.evaluate_by \n");
					sql.append(" )B ON B.evaluate_by1=A.employee_no \n");
					if (null != map.get("duDaoId") && !"".equals(map.get("duDaoId"))) {
						sql.append("WHERE WHERE A.employee_no ='" + map.get("duDaoId") + "' \n");
					} else {
						if (!CollectionUtils.isEmpty(empNoList)) {
							sql.append(" WHERE A.employee_no IN (");
							for (int i = 0; i < empNoList.size(); i++) {
								sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
								if (i != empNoList.size() - 1) {
									sql.append("',");
								}
							}
							sql.append("')");
						} else {
							// 异常情况 查不到登录工号对应督导 则不查询任何数据
							sql.append(" AND 1=0");
						}
					}
					sql.append(" order by A.EMPLOYEE_NAME DESC \n");
				} else {
					sql.append(" SELECT (@i:=@i+1) AS RN,V.*,V.employee_no AS evaluate_by  FROM (\n");
					sql.append(
							"SELECT A.*,B.CORRECT_NUM,C.OVERDUE_UNFINISH_NUM,D.FK_NUM,E.DDTF_NUM,F.DDCLOSE_NUM,round(B.CORRECT_NUM/A.GLDE_NUM,2) AS GS_NUM,round(E.DDTF_NUM/C.OVERDUE_UNFINISH_NUM*100,2) AS TG_LV   FROM(\n");
					sql.append(" SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM  \n");
					sql.append("FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
					sql.append(" WHERE v.employee_id=tme.employee_id\n");
					sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A\n");
					sql.append(" LEFT JOIN (\n");
					sql.append("SELECT COUNT(1) AS CORRECT_NUM,evaluate_by  \n");
					sql.append("FROM tt_correct_plan_base T \n");
					sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
					sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
					sql.append("AND T.STATUS = 10011001 AND P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 \n");
					sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
					sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) \n");
					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" AND t.evaluate_by IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}
					if (null != map.get("Begin")) {
						sql.append("and t.created_at>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and t.created_at<='" + map.get("End") + " 23:59:59' \n");
					}
					sql.append("GROUP BY  t.evaluate_by)B ON B.evaluate_by=A.employee_no\n");
					sql.append("LEFT JOIN (\n");
					sql.append("SELECT COUNT(1) AS OVERDUE_UNFINISH_NUM,evaluate_by  \n");
					sql.append("FROM tt_correct_plan_base T \n");
					sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
					sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
					sql.append("AND T.STATUS = 10011001 AND \n");
					sql.append(" P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001  AND P.CORRECT_STATUS = 92041001 \n");
					sql.append("  AND date_format(P.PLAN_FINISH_DATE,'%Y-%m-%d') < date_format(now(),'%Y-%m-%d')\n");
					sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
					sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" AND t.evaluate_by IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}

					if (null != map.get("Begin")) {
						sql.append("and P.plan_finish_date>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and P.plan_finish_date<='" + map.get("End") + " 23:59:59' \n");
					}
					sql.append("GROUP BY  t.evaluate_by)C ON C.evaluate_by=A.employee_no\n");
					sql.append("LEFT JOIN (\n");
					sql.append("SELECT COUNT(1) AS FK_NUM,evaluate_by  \n");
					sql.append("FROM tt_correct_plan_base T \n");
					sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
					sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
					sql.append("AND T.STATUS = 10011001 AND \n");
					sql.append("P.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.real_finish_date is not NULL \n");
					sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
					sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)  \n");
					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" AND t.evaluate_by IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}

					if (null != map.get("Begin")) {
						sql.append("and P.real_finish_date>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and P.real_finish_date<='" + map.get("End") + " 23:59:59' \n");
					}
					sql.append("GROUP BY  t.evaluate_by) D ON d.evaluate_by =A.employee_no\n");
					sql.append("LEFT JOIN (\n");
					sql.append("SELECT COUNT(1) AS DDTF_NUM,evaluate_by  \n");
					sql.append("FROM tt_correct_plan_base T \n");
					sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID\n");
					sql.append("WHERE  P.PLAN_BASE_ID = T.ID \n");
					sql.append("AND T.STATUS = 10011001 AND \n");
					sql.append("P.STATUS = 10011001 AND P.CORRECT_STATUS = 92041004\n");
					sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P \n");
					sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID) \n");

					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" AND t.evaluate_by IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}

					if (null != map.get("Begin")) {
						sql.append("and P.audit_date>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and P.audit_date<='" + map.get("End") + " 23:59:59' \n");
					}
					sql.append("GROUP BY  t.evaluate_by) E ON E.evaluate_by=A.employee_no \n");
					sql.append("LEFT JOIN ( \n");
					sql.append("SELECT COUNT(1) AS DDCLOSE_NUM,evaluate_by   \n");
					sql.append("FROM tt_correct_plan_base T  \n");
					sql.append("LEFT JOIN  tt_correct_plan P ON P.PLAN_BASE_ID = T.ID \n");
					sql.append("WHERE  P.PLAN_BASE_ID = T.ID  \n");
					sql.append("AND T.STATUS = 10011001 AND  \n");
					sql.append("P.STATUS = 10011001   AND P.CORRECT_STATUS = 92041005   \n");
					sql.append("AND EXISTS (SELECT 1 FROM tt_correct_plan P  \n");
					sql.append("WHERE p.STATUS = 10011001 AND P.IS_WEAK_PROJECT = 10041001 AND P.PLAN_BASE_ID = T.ID)   \n");

					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" AND t.evaluate_by IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}

					if (null != map.get("Begin")) {
						sql.append("and P.close_date>='" + map.get("Begin") + " 00:00:00' \n");
					}
					if (null != map.get("End")) {
						sql.append("and P.close_date<='" + map.get("End") + " 23:59:59' \n");
					}
					sql.append("GROUP BY  t.evaluate_by)F ON F.evaluate_by=A.employee_no\n");
					if (!CollectionUtils.isEmpty(empNoList)) {
						sql.append(" WHERE A.employee_no IN (");
						for (int i = 0; i < empNoList.size(); i++) {
							sql.append("'" + empNoList.get(i).get("EMPLOYEE_NO"));
							if (i != empNoList.size() - 1) {
								sql.append("',");
							}
						}
						sql.append("')");
					} else {
						// 异常情况 查不到登录工号对应督导 则不查询任何数据
						sql.append(" AND 1=0");
					}
					sql.append(" order by A.EMPLOYEE_NAME DESC\n");
					sql.append("	) V,(SELECT @i:=0)t \n");
				}
		return DAOUtil.findAll(sql.toString(), params);
	}
}
