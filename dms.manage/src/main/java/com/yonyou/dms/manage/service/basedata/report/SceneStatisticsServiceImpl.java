
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
public class SceneStatisticsServiceImpl implements SceneStatisticsService {
    
    @Resource
    private DealerService dealerService;
    
    @Resource
    private EmployeeService employeeService;

    /**
     * 巡回评价统计结果
     */
	@Override
	public PageInfoDto findSceneStatistics(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		//登录工号所管辖所有督导
		List<Map> empNoList = employeeService.findEmployeeNoByLogin();
		StringBuilder sql = new StringBuilder("");
		
		List<Object> params = new ArrayList<>();
		sql.append(" SELECT A.employee_no,A.EMPLOYEE_NAME,A.GLDE_NUM,B.jhxh_sum,C.sjxh_sum,D.xcxh_sum,E.ndfd_sum,F.hy_sum,G.qt_sum,H.nl_sum, \n");
		//查询出其他评价表模板
		StringBuilder sql1 = new StringBuilder("SELECT DISTINCT t.evaluate_name from tt_evaluate  t where t.property_id=10371002");
		List<Map> qtpjmb= DAOUtil.findAll(sql1.toString(), null);
		for (int i = 0; i < qtpjmb.size(); i++) {
			sql.append("J.qt_sum"+i+",\n");	
		}
		sql.append(" M.yfb_sum,  \n");
		sql.append(" B.jhxh_sum/A.GLDE_NUM AS jhxm_pc,D.xcxh_sum/A.GLDE_NUM AS xcph_pc,E.ndfd_sum/A.GLDE_NUM AS ndfd_pc \n");
		sql.append(" FROM ( \n");
		sql.append(" SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM \n");
		sql.append(" FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
		sql.append(" WHERE v.employee_id=tme.employee_id\n");
		sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A \n");
		sql.append(" LEFT JOIN ( \n");
		sql.append(" SELECT sc.evaluate_by,COUNT(sc.dealer_id) AS jhxh_sum FROM  tt_scene_evaluate  sc, tt_evaluate eva \n");
		sql.append(" where 1=1  and eva.id=sc.ttevaluate_id and eva.property_id=10371001 \n");
		if(null!=map.get("Begin")){
			sql.append("and sc.evaluate_date>='"+map.get("Begin")+"' \n");
		}
		if(null!=map.get("End")){
			sql.append("and sc.evaluate_date<='"+map.get("End")+"' \n");
		}
		
		sql.append(" GROUP BY sc.evaluate_by)B ON A.employee_no=B.evaluate_by \n");
		sql.append(" LEFT JOIN( \n");
		sql.append(" SELECT re.evaluate_by,COUNT(re.dealer_id) AS sjxh_sum FROM tt_patrol_report  re where 1=1 \n"); 
		// 权限控制
		if(null!=map.get("Begin")){
			sql.append("and re.submit_date>='"+map.get("Begin")+" 00:00:00' \n");
		}
		if(null!=map.get("End")){
			sql.append("and re.submit_date<='"+map.get("End")+" 23:59:59' \n");
		}
		
		sql.append(" GROUP BY re.evaluate_by) C ON A.employee_no=C.evaluate_by \n");
		sql.append(" LEFT JOIN( \n"); 
		sql.append(" SELECT sc.evaluate_by ,COUNT(sc.dealer_id) AS xcxh_sum FROM  tt_scene_evaluate  sc \n"); 
		sql.append(" where 1=1  AND (sc.evaluate_flag=92011003 or sc.evaluate_flag=92011002) \n"); 
		if(null!=map.get("Begin")){
			sql.append("and sc.evaluate_date>='"+map.get("Begin")+"' \n");
		}
		if(null!=map.get("End")){
			sql.append("and sc.evaluate_date<='"+map.get("End")+"' \n");
		}
		sql.append(" GROUP BY sc.evaluate_by) D ON D.evaluate_by=A.employee_no \n");
		sql.append(" LEFT JOIN(\n");
		sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  ndfd_sum FROM(\n");	
		sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
		sql.append(" FROM tt_supervision_agenda ag\n");
		sql.append(" LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
		sql.append(" where 1=1  and de.EVENT_TYPE=90011004  and ag.state=90021005  \n");
		  if(null!=map.get("Begin")){
			 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
					}
		 if(null!=map.get("End")){
			sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
						}
		 sql.append("   )AA \n");
		 sql.append("	GROUP BY  AA.CREATED_BY)E ON E.CREATED_BY=A.employee_no\n");
		 sql.append("	LEFT JOIN (\n");
		 sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  hy_sum FROM(\n");	
		 sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
		 sql.append("	 FROM tt_supervision_agenda ag\n");
		 sql.append("	LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
		 sql.append("		where 1=1 and de.EVENT_TYPE=90011003 and ag.state=90021005  \n");
		  if(null!=map.get("Begin")){
				 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
						}
			 if(null!=map.get("End")){
				sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
							}
			 sql.append("   )AA \n");
			 sql.append(" GROUP BY AA.CREATED_BY) F ON F.CREATED_BY=A.employee_no \n");
			 sql.append(" LEFT JOIN( \n");
			 sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  qt_sum FROM(\n");	
			 sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
			 sql.append(" FROM tt_supervision_agenda ag \n");
			 sql.append(" LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
			 sql.append(" where 1=1 AND   de.EVENT_TYPE=90011005  and ag.state=90021005 \n");
			  if(null!=map.get("Begin")){
					 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
							}
				 if(null!=map.get("End")){
					sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
								}
				 sql.append("  )AA \n");
				 sql.append("GROUP BY AA.CREATED_BY) G ON G.CREATED_BY=A.employee_no \n");
				 sql.append("LEFT JOIN ( \n");
				 sql.append("SELECT nl.CREATED_BY,COUNT(nl.dealer_code) as nl_sum from tm_nl_analysis_entry nl \n");
				 sql.append("where nl.isfinish=1  \n");
				  if(null!=map.get("Begin")){
						 sql.append(" and nl.created_at>='"+map.get("Begin")+" 00:00:00' \n");
								}
					 if(null!=map.get("End")){
						sql.append(" and nl.created_at<='"+map.get("End")+" 23:59:59'\n");
									}
					 sql.append(" GROUP BY nl.CREATED_BY) H ON H.CREATED_BY=A.employee_no\n");
					 sql.append(" LEFT JOIN (\n");
					 sql.append(" SELECT \n");
					 for (int i = 0; i < qtpjmb.size(); i++) {
					Map mapq=qtpjmb.get(i);
			  	    sql.append(" MAX(CASE A.evaluate_name WHEN '"+mapq.get("evaluate_name")+"' THEN A.qt_sum ELSE 0 END ) AS qt_sum"+i+",\n");
					}
					 sql.append(" A.evaluate_by\n");
					 sql.append(" FROM(\n");
					 sql.append(" select sc.evaluate_by,ev.evaluate_name,COUNT(sc.dealer_id) AS qt_sum FROM  tt_scene_evaluate sc\n");
					 sql.append(" INNER JOIN  tt_evaluate  ev on  sc.ttevaluate_id=ev.id and  ev.property_id=10371002\n");
					 sql.append(" WHERE  1=1 AND (sc.evaluate_flag=92011003 or sc.evaluate_flag=92011002) \n");
					  if(null!=map.get("Begin")){
							 sql.append(" and sc.evaluate_date>='"+map.get("Begin")+"' \n");
									}
						 if(null!=map.get("End")){
							sql.append(" and sc.evaluate_date<='"+map.get("End")+"' \n");
										}
					 sql.append(" GROUP BY sc.evaluate_by,ev.evaluate_name)A\n");
					 sql.append(" GROUP BY A.evaluate_by\n");
					 sql.append(" ) J ON J.evaluate_by=A.employee_no\n");
					 sql.append(" LEFT JOIN(\n");
					 sql.append(" SELECT re.evaluate_by,COUNT(re.dealer_id) AS yfb_sum FROM tt_patrol_report  re where 1=1 \n");
					 sql.append("  and re.report_status=92051002\n");
					  if(null!=map.get("Begin")){
							 sql.append(" and re.submit_date>='"+map.get("Begin")+" 00:00:00' \n");
									}
						 if(null!=map.get("End")){
							sql.append(" and re.submit_date<='"+map.get("End")+" 23:59:59' \n");
										}
					 sql.append(" GROUP BY re.evaluate_by) M ON M.evaluate_by=A.employee_no\n");
					 sql.append(" where 1=1 \n");
						if(null!=map.get("duDaoId") && !"".equals(map.get("duDaoId"))){
							sql.append("AND  A.employee_no ='"+map.get("duDaoId")+"' \n");
						}else{
							if (!CollectionUtils.isEmpty(empNoList)) {
								sql.append(" AND  A.employee_no IN (");
								for (int i = 0; i < empNoList.size(); i++) {
									sql.append("'"+empNoList.get(i).get("EMPLOYEE_NO"));
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
						sql.append(" order by A.employee_no DESC ");
		return DAOUtil.pageQuery(sql.toString(), params);
	}
	
    /**
     * 巡回计划分析-报表
     */
	@Override
	public List<Map> findSceneStatisticsList(Map<String, Object> map) throws ServiceBizException {
		// TODO Auto-generated method stub
		//登录工号所管辖所有督导
		List<Map> empNoList = employeeService.findEmployeeNoByLogin();
		StringBuilder sql = new StringBuilder("");
		List<Object> params = new ArrayList<>();
		sql.append("SELECT (@i:=@i+1) AS RN,V.* FROM ( ");
		sql.append(" SELECT A.employee_no,A.EMPLOYEE_NAME,A.GLDE_NUM,B.jhxh_sum,C.sjxh_sum,D.xcxh_sum,E.ndfd_sum,F.hy_sum,G.qt_sum,H.nl_sum, \n");
		//查询出其他评价表模板
		StringBuilder sql1 = new StringBuilder("SELECT DISTINCT t.evaluate_name from tt_evaluate  t where t.property_id=10371002");
		List<Map> qtpjmb= DAOUtil.findAll(sql1.toString(), null);
		for (int i = 0; i < qtpjmb.size(); i++) {
			sql.append("J.qt_sum"+i+",\n");	
		}
		sql.append(" M.yfb_sum,  \n");
		sql.append(" B.jhxh_sum/A.GLDE_NUM AS jhxm_pc,D.xcxh_sum/A.GLDE_NUM AS xcph_pc,E.ndfd_sum/A.GLDE_NUM AS ndfd_pc \n");
		sql.append(" FROM ( \n");
		sql.append(" SELECT  v.EMPLOYEE_NAME,tme.employee_no,COUNT(v.DEALER_CODE) AS GLDE_NUM \n");
		sql.append(" FROM  vm_dealer_org_dudao_province v,tm_employee tme \n");
		sql.append(" WHERE v.employee_id=tme.employee_id\n");
		sql.append(" GROUP BY v.employee_id,v.EMPLOYEE_NAME,tme.employee_no) A \n");
		sql.append(" LEFT JOIN ( \n");
		sql.append(" SELECT sc.evaluate_by,COUNT(sc.dealer_id) AS jhxh_sum FROM  tt_scene_evaluate  sc, tt_evaluate eva \n");
		sql.append(" where 1=1  and eva.id=sc.ttevaluate_id and eva.property_id=10371001 \n");
		if(null!=map.get("Begin")){
			sql.append("and sc.evaluate_date>='"+map.get("Begin")+"' \n");
		}
		if(null!=map.get("End")){
			sql.append("and sc.evaluate_date<='"+map.get("End")+"' \n");
		}
		
		sql.append(" GROUP BY sc.evaluate_by)B ON A.employee_no=B.evaluate_by \n");
		sql.append(" LEFT JOIN( \n");
		sql.append(" SELECT re.evaluate_by,COUNT(re.dealer_id) AS sjxh_sum FROM tt_patrol_report  re where 1=1 \n"); 
		// 权限控制
		if(null!=map.get("Begin")){
			sql.append("and re.submit_date>='"+map.get("Begin")+" 00:00:00' \n");
		}
		if(null!=map.get("End")){
			sql.append("and re.submit_date<='"+map.get("End")+" 23:59:59' \n");
		}
		
		sql.append(" GROUP BY re.evaluate_by) C ON A.employee_no=C.evaluate_by \n");
		sql.append(" LEFT JOIN( \n"); 
		sql.append(" SELECT sc.evaluate_by ,COUNT(sc.dealer_id) AS xcxh_sum FROM  tt_scene_evaluate  sc \n"); 
		sql.append(" where 1=1  AND (sc.evaluate_flag=92011003 or sc.evaluate_flag=92011002) \n"); 
		if(null!=map.get("Begin")){
			sql.append("and sc.evaluate_date>='"+map.get("Begin")+"' \n");
		}
		if(null!=map.get("End")){
			sql.append("and sc.evaluate_date<='"+map.get("End")+"' \n");
		}
		sql.append(" GROUP BY sc.evaluate_by) D ON D.evaluate_by=A.employee_no \n");
		sql.append(" LEFT JOIN(\n");
		sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  ndfd_sum FROM(\n");	
		sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
		sql.append(" FROM tt_supervision_agenda ag\n");
		sql.append(" LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
		sql.append(" where 1=1  and de.EVENT_TYPE=90011004  and ag.state=90021005  \n");
		  if(null!=map.get("Begin")){
			 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
					}
		 if(null!=map.get("End")){
			sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
						}
		 sql.append("   )AA \n");
		 sql.append("	GROUP BY  AA.CREATED_BY)E ON E.CREATED_BY=A.employee_no\n");
		 sql.append("	LEFT JOIN (\n");
		 sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  hy_sum FROM(\n");	
		 sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
		 sql.append("	 FROM tt_supervision_agenda ag\n");
		 sql.append("	LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
		 sql.append("		where 1=1 and de.EVENT_TYPE=90011003 and ag.state=90021005  \n");
		  if(null!=map.get("Begin")){
				 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
						}
			 if(null!=map.get("End")){
				sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
							}
			 sql.append("   )AA \n");
			 sql.append(" GROUP BY AA.CREATED_BY) F ON F.CREATED_BY=A.employee_no \n");
			 sql.append(" LEFT JOIN( \n");
			 sql.append(" select AA.CREATED_BY,COUNT(AA.AGENDA_ID) AS  qt_sum FROM(\n");	
			 sql.append(" select  DISTINCT ag.CREATED_BY,ag.AGENDA_ID \n");
			 sql.append(" FROM tt_supervision_agenda ag \n");
			 sql.append(" LEFT JOIN  tt_supervision_agenda_detail de on de.AGENDA_ID=ag.AGENDA_ID  \n");
			 sql.append(" where 1=1 AND   de.EVENT_TYPE=90011005  and ag.state=90021005 \n");
			  if(null!=map.get("Begin")){
					 sql.append(" and ag.START_DATE>='"+map.get("Begin")+"' \n");
							}
				 if(null!=map.get("End")){
					sql.append(" and ag.START_DATE<='"+map.get("End")+"' \n");
								}
				 sql.append("  )AA \n");
				 sql.append("GROUP BY AA.CREATED_BY) G ON G.CREATED_BY=A.employee_no \n");
				 sql.append("LEFT JOIN ( \n");
				 sql.append("SELECT nl.CREATED_BY,COUNT(nl.dealer_code) as nl_sum from tm_nl_analysis_entry nl \n");
				 sql.append("where nl.isfinish=1  \n");
				  if(null!=map.get("Begin")){
						 sql.append(" and nl.created_at>='"+map.get("Begin")+" 00:00:00' \n");
								}
					 if(null!=map.get("End")){
						sql.append(" and nl.created_at<='"+map.get("End")+" 23:59:59' \n");
									}
					 sql.append(" GROUP BY nl.CREATED_BY) H ON H.CREATED_BY=A.employee_no\n");
					 sql.append(" LEFT JOIN (\n");
					 sql.append(" SELECT \n");
					 for (int i = 0; i < qtpjmb.size(); i++) {
					Map mapq=qtpjmb.get(i);
			  	    sql.append(" MAX(CASE A.evaluate_name WHEN '"+mapq.get("evaluate_name")+"' THEN A.qt_sum ELSE 0 END ) AS qt_sum"+i+",\n");
					}
					 sql.append(" A.evaluate_by\n");
					 sql.append(" FROM(\n");
					 sql.append(" select sc.evaluate_by,ev.evaluate_name,COUNT(sc.dealer_id) AS qt_sum FROM  tt_scene_evaluate sc\n");
					 sql.append(" INNER JOIN  tt_evaluate  ev on  sc.ttevaluate_id=ev.id and  ev.property_id=10371002\n");
					 sql.append(" WHERE  sc.evaluate_flag=92011003 or sc.evaluate_flag=92011002\n");
					  if(null!=map.get("Begin")){
							 sql.append(" and sc.evaluate_date>='"+map.get("Begin")+"' \n");
									}
						 if(null!=map.get("End")){
							sql.append(" and sc.evaluate_date<='"+map.get("End")+"' \n");
										}
					 sql.append(" GROUP BY sc.evaluate_by,ev.evaluate_name)A\n");
					 sql.append(" GROUP BY A.evaluate_by\n");
					 sql.append(" ) J ON J.evaluate_by=A.employee_no\n");
					 sql.append(" LEFT JOIN(\n");
					 sql.append(" SELECT re.evaluate_by,COUNT(re.dealer_id) AS yfb_sum FROM tt_patrol_report  re where 1=1 \n");
					 sql.append("  and re.report_status=92051002\n");
					  if(null!=map.get("Begin")){
							 sql.append(" and re.submit_date>='"+map.get("Begin")+" 00:00:00' \n");
									}
						 if(null!=map.get("End")){
							sql.append(" and re.submit_date<='"+map.get("End")+" 23:59:59' \n");
										}
					 sql.append(" GROUP BY re.evaluate_by) M ON M.evaluate_by=A.employee_no\n");
					 sql.append(" where 1=1\n");
						if(null!=map.get("duDaoId") && !"".equals(map.get("duDaoId"))){
							sql.append("AND  A.employee_no ='"+map.get("duDaoId")+"' \n");
						}else{
							if (!CollectionUtils.isEmpty(empNoList)) {
								sql.append(" AND  A.employee_no IN (");
								for (int i = 0; i < empNoList.size(); i++) {
									sql.append("'"+empNoList.get(i).get("EMPLOYEE_NO"));
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
						sql.append(" order by A.employee_no DESC ");
						sql.append(" ) V,(SELECT @i:=0)t \n");
		return DAOUtil.findAll(sql.toString(), params);
	}
}
