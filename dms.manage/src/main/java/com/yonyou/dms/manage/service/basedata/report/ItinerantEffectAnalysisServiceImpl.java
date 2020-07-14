package com.yonyou.dms.manage.service.basedata.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.StringUtils;
@Service
public class ItinerantEffectAnalysisServiceImpl implements ItinerantEffectAnalysisService{

	
	/**
	 * 季度维度的查询
	 */
	@Override
	public PageInfoDto searchdata1(Map<String, String> queryParam) throws ServiceBizException {
		//查询标题
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();
		param.add(queryParam.get("searhYear"));
		//非得分项
		sql.append(" SELECT CONCAT(FIRST_HEAD,SECOND_HEAD) AS item,SHOW_FORMAT AS formats FROM tt_data_display_table_head WHERE YEAR=? ");
		List<Map> list = DAOUtil.findAll(sql.toString(), param);
		//得分项
		sql = new StringBuilder();
		sql.append(" SELECT CONCAT(BRANCH_ONE,BRANCH_TWO) AS item,SHOW_FORMAT AS formats FROM tt_index_score WHERE YEAR=? ");
		List<Map> list1 = DAOUtil.findAll(sql.toString(), param);
		list.addAll(list1);
		
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		
		//全国
		sb.append(" SELECT ");
		sb.append(" '全国' AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			//百分比
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			//2位小数
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			//整数
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count WHERE REGION_TYPE=1  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" and QUARTER(DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		sb.append(" GROUP BY DATA_TIME ");
		sb.append(" UNION ALL ");
		//大区
		sb.append(" SELECT  ");
		sb.append(" MAX(b.ORG_NAME) AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a");
		sb.append(" INNER JOIN tm_org b ON a.REGION_ID = b.ORG_ID WHERE a.REGION_TYPE=2  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("orgId"));
		}		
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		sb.append(" GROUP BY a.REGION_ID,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		//督导
		sb.append(" SELECT  ");
		sb.append(" NULL AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a");
		sb.append(" INNER JOIN vm_dealer_org_dudao_province b ON a.REGION_ID = b.employee_id");
		sb.append(" WHERE a.REGION_TYPE=3  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=? ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));

		sb.append(" GROUP BY a.REGION_ID,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		//特约店
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" MAX(b.DEALER_ID) AS dealerId, ");
		sb.append(" MAX(a.DEALER_SHORTNAME) AS dealername, ");
		sb.append(" MAX(b.ORG_NAME) as orgName, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a");
		sb.append("	INNER JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND b.employee_id=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));

		sb.append(" GROUP BY a.DEALER_CODE,a.DATA_TIME ");
 		return DAOUtil.pageQuery(sb.toString(), params);
	}

	/**
	 * 时间段维度的查询
	 */
	@Override
	public PageInfoDto searchdata2(Map<String, String> queryParam) throws ServiceBizException {
		//查询标题
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();
		param.add(queryParam.get("searhYear"));
		//非得分项
		sql.append(" SELECT CONCAT(FIRST_HEAD,SECOND_HEAD) AS item,SHOW_FORMAT AS formats FROM tt_data_display_table_head WHERE YEAR=? ");
		List<Map> list = DAOUtil.findAll(sql.toString(), param);
		//得分项
		sql = new StringBuilder();
		sql.append(" SELECT CONCAT(BRANCH_ONE,BRANCH_TWO) AS item,SHOW_FORMAT AS formats FROM tt_index_score WHERE YEAR=? ");
		List<Map> list1 = DAOUtil.findAll(sql.toString(), param);
		list.addAll(list1);
		
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		
		//全国
		sb.append(" SELECT ");
		sb.append(" '全国' AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			//百分比
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			//2位小数
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			//整数
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count WHERE REGION_TYPE=1  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("month1"))){
			sb.append(" AND (DATA_MONTH IN ("+queryParam.get("month1")+") ");
			if(!StringUtils.isNullOrEmpty(queryParam.get("month2"))){
				sb.append(" OR DATA_MONTH IN ("+queryParam.get("month2")+") ");
			}
			sb.append(")");
		}
		sb.append(" GROUP BY DATA_TIME ");
		sb.append(" UNION ALL ");
		//大区
		sb.append(" SELECT  ");
		sb.append(" MAX(b.ORG_NAME) AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a");
		sb.append(" INNER JOIN tm_org b ON a.REGION_ID = b.ORG_ID WHERE a.REGION_TYPE=2  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("month1"))){
			sb.append(" AND (DATA_MONTH IN ("+queryParam.get("month1")+") ");
			if(!StringUtils.isNullOrEmpty(queryParam.get("month2"))){
				sb.append(" OR DATA_MONTH IN ("+queryParam.get("month2")+") ");
			}
			sb.append(")");
		}		
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("orgId"));
		}		
		sb.append(" GROUP BY a.REGION_ID,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		//督导
		sb.append(" SELECT  ");
		sb.append(" NULL AS dealerCode, ");
		sb.append(" NULL AS dealerId, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a");
		sb.append(" INNER JOIN vm_dealer_org_dudao_province b ON a.REGION_ID = b.employee_id");
		sb.append(" WHERE a.REGION_TYPE=3  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("month1"))){
			sb.append(" AND (DATA_MONTH IN ("+queryParam.get("month1")+") ");
			if(!StringUtils.isNullOrEmpty(queryParam.get("month2"))){
				sb.append(" OR DATA_MONTH IN ("+queryParam.get("month2")+") ");
			}
			sb.append(")");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=? ");
			params.add(queryParam.get("orgId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		//特约店
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" MAX(b.DEALER_ID) AS dealerId, ");
		sb.append(" MAX(a.DEALER_SHORTNAME) AS dealername, ");
		sb.append(" MAX(b.ORG_NAME) as orgName, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		for(int i=0; i<list.size(); i++){
			String itemName = list.get(i).get("item")+"";
			String formats = list.get(i).get("formats")+"";
			if("2".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("1".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+itemName+"' ,");
			}
			else if("0".equals(formats)) {
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a FORCE INDEX(`INDEX_3`)");
		sb.append("	INNER JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("month1"))){
			sb.append(" AND (DATA_MONTH IN ("+queryParam.get("month1")+") ");
			if(!StringUtils.isNullOrEmpty(queryParam.get("month2"))){
				sb.append(" OR DATA_MONTH IN ("+queryParam.get("month2")+") ");
			}
			sb.append(")");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND b.employee_id=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.DATA_TIME ");
 		return DAOUtil.pageQuery(sb.toString(), params);
	}

	/**
	 * 季度维度的查询
	 */
	@Override
	public PageInfoDto searchWeakdata1(Map<String, String> queryParam) throws ServiceBizException {
		
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT A.*,B.* FROM  ");
		sb.append(" (SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" MAX(b.DEALER_ID) AS dealerId, ");
		sb.append(" MAX(a.DEALER_SHORTNAME) AS dealername, ");
		sb.append(" MAX(b.ORG_NAME) as orgName, ");
		sb.append(" MAX(b.ORG_ID) AS orgID, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		sb.append(" max(CASE DATA_TYPE WHEN '进厂台次进厂达成率' THEN DATA_NUM ELSE 0 END) AS '进厂台次进厂达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养台次保养达成率' THEN DATA_NUM ELSE 0 END) AS '保养台次保养达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系二保维系率' THEN DATA_NUM ELSE 0 END) AS '二保维系二保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系保养客户留存率' THEN DATA_NUM ELSE 0 END) AS '客户维系保养客户留存率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系忠诚客户占比' THEN DATA_NUM ELSE 0 END) AS '客户维系忠诚客户占比' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系首保维系率' THEN DATA_NUM ELSE 0 END) AS '客户维系首保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系定保维系率' THEN DATA_NUM ELSE 0 END) AS '客户维系定保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系过保维系率' THEN DATA_NUM ELSE 0 END) AS '客户维系过保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系客户维系率' THEN DATA_NUM ELSE 0 END) AS '客户维系客户维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货机油订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货机油订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货轮胎订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货轮胎订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货添加剂订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货添加剂订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货电瓶订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货电瓶订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货活性炭滤芯订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货活性炭滤芯订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货空调养护订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货空调养护订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '订货单台保养件单台' THEN DATA_NUM ELSE 0 END) AS '订货单台保养件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '订货单台外观件单台' THEN DATA_NUM ELSE 0 END) AS '订货单台外观件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '订货单台用品单台金额' THEN DATA_NUM ELSE 0 END) AS '订货单台用品单台金额' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保险新保渗透率' THEN DATA_NUM ELSE 0 END) AS '保险新保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保险续保目标达成率' THEN DATA_NUM ELSE 0 END) AS '保险续保目标达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '延保延保渗透率' THEN DATA_NUM ELSE 0 END) AS '延保延保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '平板接待IMS系统使用率' THEN DATA_NUM ELSE 0 END) AS '平板接待IMS系统使用率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '在线调查综合得分' THEN DATA_NUM ELSE 0 END) AS '在线调查综合得分' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位总经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位总经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位服务经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位服务经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位零部件经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位零部件经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位客服经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位客服经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保险经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位保险经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保修鉴定员' THEN DATA_NUM ELSE 0 END) AS '关键岗位保修鉴定员' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（业务类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（业务类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（机电类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（机电类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '人员数量满足度' THEN DATA_NUM ELSE 0 END) AS '人员数量满足度' ,");
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a ");
		sb.append(" INNER JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND b.employee_id=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		
		sb.append(" GROUP BY a.DEALER_CODE,a.DATA_TIME) A ");
		sb.append(" LEFT JOIN ");
		sb.append(" (SELECT ");
		for (int i = 1; i <= 32; i++) {
			sb.append(" IFNULL(TARGET"+i+",0) TARGET"+i+",");
		}
		sb.append(" DATA_TIME,OGR_ID FROM tt_data_display_target WHERE ORG_TYPE=? ");
		params.add(queryParam.get("targetType"));
		sb.append(" ) B ");
		sb.append(" ON A.dataTime = B.DATA_TIME ");
		if("3".equals(queryParam.get("targetType"))) {
			sb.append(" AND A.orgID = B.OGR_ID ");
		}
	
 		return DAOUtil.pageQuery(sb.toString(), params);
	}

	/**
	 * 时间段维度的查询
	 */
	@Override
	public PageInfoDto searchWeakdata2(Map<String, String> queryParam) throws ServiceBizException {
		
		//查询标题
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ITEM_NAME AS item FROM tt_weak_item ");
		List<Map> itemList = DAOUtil.findAll(sql.toString(), null);
		
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT A.*,B.* FROM  ");
		sb.append(" (SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" MAX(b.DEALER_ID) AS dealerId, ");
		sb.append(" MAX(a.DEALER_SHORTNAME) AS dealername, ");
		sb.append(" MAX(b.ORG_NAME) as orgName, ");
		sb.append(" MAX(b.ORG_ID) AS orgID, ");
		sb.append(" MAX(b.EMPLOYEE_NAME) as empName, ");
		sb.append(" a.REGION_TYPE as regionType, ");
		if(!CollectionUtils.isEmpty(itemList)) {
			for(int i=0; i<itemList.size(); i++){
				String itemName = itemList.get(i).get("item")+"";
				sb.append(" max(CASE DATA_TYPE WHEN '"+itemName+"' THEN DATA_NUM ELSE 0 END) AS '"+itemName+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a ");
		sb.append(" INNER JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		if(!CollectionUtils.isEmpty(itemList)) {
			sb.append(" AND a.DATA_TYPE IN (");
			for(int i=0; i<itemList.size(); i++){
				String itemName = itemList.get(i).get("item")+"";
				if(i == 0) {
					sb.append("'"+itemName+"'");
				}else {
					sb.append(",'"+itemName+"'");
				}
			}
			sb.append(")");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("month1"))){
			sb.append(" AND (DATA_MONTH IN ("+queryParam.get("month1")+") ");
			if(!StringUtils.isNullOrEmpty(queryParam.get("month2"))){
				sb.append(" OR DATA_MONTH IN ("+queryParam.get("month2")+") ");
			}
			sb.append(")");
		}		
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND b.employee_id=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND b.ORG_ID=?  ");
			params.add(queryParam.get("orgId"));
		}

		sb.append(" GROUP BY a.DEALER_CODE,a.DATA_TIME) A ");
		sb.append(" LEFT JOIN ");
		sb.append(" (SELECT ");
		for (int i = 1; i <= 32; i++) {
			sb.append(" IFNULL(TARGET"+i+",0) TARGET"+i+",");
		}
		sb.append(" DATA_TIME,OGR_ID FROM tt_data_display_target WHERE ORG_TYPE=? ");
		params.add(queryParam.get("targetType"));
		sb.append(" ) B ");
		sb.append(" ON A.dataTime = B.DATA_TIME ");
		if("3".equals(queryParam.get("targetType"))) {
			sb.append(" AND A.orgID = B.OGR_ID ");
		}
		return DAOUtil.pageQuery(sb.toString(), params);
	}
	
	/**
	 * 根据巡店计划ID查询巡店信息
	 */
	@Override
	public Map<String, Object> getAgendaInfo(String dealerIdStr,String dataTimeStr) throws ServiceBizException {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT T.AGENDA_ID as agendaId,");
		sql.append(" CONCAT(DATE_FORMAT(T.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(T.START_DATE,'%Y%m%d')=DATE_FORMAT(T.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(T.END_DATE,'%d日')))) as tourtime,");
		sql.append(" T2.id,");
		sql.append(" T2.evaluate_id,");
		sql.append(" T2.dealer_shortname,");
		sql.append(" T2.evaluate_name,");
		sql.append(" T2.evaluate_date,");
		sql.append(" T2.evaluate_by,");
		sql.append(" (SELECT E.EMPLOYEE_NAME FROM TM_EMPLOYEE E WHERE E.EMPLOYEE_NO = T2.evaluate_by) AS evaluate_by_name");
		sql.append(" FROM tt_supervision_agenda T");
		sql.append(" INNER JOIN tt_supervision_agenda_detail T1");
		sql.append(" ON T.AGENDA_ID = T1.AGENDA_ID AND T1.CHANGE_STATE=90031001 AND T1.EVENT_TYPE=90011002");
		sql.append(" INNER JOIN tt_patrol_report T2");
		sql.append(" ON T.AGENDA_ID=T2.AGENDA_ID AND T2.REPORT_STATUS=92051002");
		sql.append(" WHERE T.DEALER_ID ="+dealerIdStr);
		sql.append(" AND '"+dataTimeStr+"' BETWEEN DATE_FORMAT(T.START_DATE,'%Y-%m-%d') AND DATE_FORMAT(T.END_DATE,'%Y-%m-%d')");
		sql.append(" ORDER BY T.START_DATE DESC");

		return DAOUtil.findFirst(sql.toString(), null);
	}
	

	/**
	 * 季度维度的查询(用于导出)
	 */
	@Override
	public List<Map> searchdata1List(Map<String, String> queryParam) throws ServiceBizException {
		//获取表头的年份，用来拼接行转列的SQL
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();
		sql.append(" SELECT CONCAT(FIRST_HEAD,SECOND_HEAD) AS item FROM tt_data_display_table_head WHERE YEAR=? ");
		param.add(queryParam.get("searhYear"));
		List<Map> list = DAOUtil.findAll(sql.toString(), param);
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT ");
		sb.append(" '全国' AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count WHERE REGION_TYPE=1  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" and QUARTER(DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		sb.append(" GROUP BY REGION_ID,REGION_TYPE,DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT ");
		sb.append(" '全国' AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" CASE QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime  ");
		sb.append(" FROM tt_data_display_count WHERE REGION_TYPE=1  ");
		sb.append(" AND YEAR=? ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER=? ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" GROUP BY REGION_ID,REGION_TYPE,YEAR,QUARTER ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" b.ORG_NAME AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN tm_org b ON a.REGION_ID = b.ORG_ID WHERE a.REGION_TYPE=2  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" b.ORG_NAME AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" CASE a.QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN tm_org b ON a.REGION_ID = b.ORG_ID WHERE a.REGION_TYPE=2  ");
		sb.append(" AND a.YEAR=?  ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND a.QUARTER=? ");
			params.add(queryParam.get("quarter"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.YEAR,a.QUARTER ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" NULL AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN (SELECT * FROM vm_dealer_org_dudao_province GROUP BY employee_id) b ON a.REGION_ID = b.employee_id WHERE a.REGION_TYPE=3  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))&&StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID IN (SELECT DISTINCT REGION_ID FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" NULL AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" CASE a.QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN (SELECT * FROM vm_dealer_org_dudao_province GROUP BY employee_id) b ON a.REGION_ID = b.employee_id WHERE a.REGION_TYPE=3  ");
		sb.append(" AND a.YEAR=?  ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND a.QUARTER=? ");
			params.add(queryParam.get("quarter"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))&&StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID IN (SELECT DISTINCT REGION_ID FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.YEAR,a.QUARTER ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" CONCAT(DATE_FORMAT(c.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(c.START_DATE,'%Y%m%d')=DATE_FORMAT(c.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(c.END_DATE,'%d日')))) as tourtime, ");
		sb.append(" d.evaluate_id as evaluateID, ");
		sb.append(" d.evaluate_date as evaluateDate, ");
		sb.append(" (SELECT E.EMPLOYEE_NAME FROM TM_EMPLOYEE E WHERE E.EMPLOYEE_NO = d.evaluate_by) AS EVALUATE_BY_NAME, ");
		sb.append(" c.AGENDA_ID as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" LEFT JOIN (select a.DEALER_ID,a.AGENDA_ID,a.START_DATE,a.END_DATE,b.EVENT_TYPE from tt_supervision_agenda a inner join tt_supervision_agenda_detail b on a.AGENDA_ID=b.AGENDA_ID and b.CHANGE_STATE=90031001 where b.EVENT_TYPE=90011002) c ");
		sb.append(" ON b.DEALER_ID=c.DEALER_ID AND DATE_FORMAT(a.DATA_TIME,'%Y%m')=DATE_FORMAT(c.START_DATE,'%Y%m') ");
		sb.append(" LEFT JOIN tt_patrol_report d ");
		sb.append(" ON c.AGENDA_ID=d.agenda_id AND d.report_status=92051002 ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE employee_id=?)  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" CONCAT(DATE_FORMAT(c.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(c.START_DATE,'%Y%m%d')=DATE_FORMAT(c.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(c.END_DATE,'%d日')))) as tourtime, ");
		sb.append(" d.evaluate_id as evaluateID, ");
		sb.append(" d.evaluate_date as evaluateDate, ");
		sb.append(" (SELECT E.EMPLOYEE_NAME FROM TM_EMPLOYEE E WHERE E.EMPLOYEE_NO = d.evaluate_by) AS EVALUATE_BY_NAME, ");
		sb.append(" c.AGENDA_ID as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" CASE a.QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" LEFT JOIN (select a.DEALER_ID,a.AGENDA_ID,a.START_DATE,a.END_DATE,b.EVENT_TYPE from tt_supervision_agenda a inner join tt_supervision_agenda_detail b on a.AGENDA_ID=b.AGENDA_ID and b.CHANGE_STATE=90031001 where b.EVENT_TYPE=90011002) c ");
		sb.append(" ON b.DEALER_ID=c.DEALER_ID AND DATE_FORMAT(a.DATA_TIME,'%Y%m')=DATE_FORMAT(c.START_DATE,'%Y%m') ");
		sb.append(" LEFT JOIN tt_patrol_report d ");
		sb.append(" ON c.AGENDA_ID=d.agenda_id AND d.report_status=92051002 ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		sb.append(" AND a.YEAR=?  ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND a.QUARTER=? ");
			params.add(queryParam.get("quarter"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE employee_id=?)  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.YEAR,a.QUARTER ");
 		return DAOUtil.findAll(sb.toString(), params);
	}

	/**
	 * 时间段维度的查询(导出)
	 */
	@Override
	public List<Map> searchdata2List(Map<String, String> queryParam) throws ServiceBizException {
		// TODO Auto-generated method stub

		//获取表头的年份，用来拼接行转列的SQL
		StringBuilder sql = new StringBuilder();
		List<Object> param=new ArrayList<>();
		sql.append(" SELECT CONCAT(FIRST_HEAD,SECOND_HEAD) AS item FROM tt_data_display_table_head WHERE YEAR=? ");
		param.add(queryParam.get("searhYear"));
		List<Map> list = DAOUtil.findAll(sql.toString(), param);
		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT ");
		sb.append(" '全国' AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count WHERE REGION_TYPE=1  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))){
			sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))){
			sb.append(" AND (DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
			sb.append(" OR DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth2Start")+"' AND '"+queryParam.get("yearMonth2End")+"') ");
		}
		sb.append(" GROUP BY REGION_ID,REGION_TYPE,DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" b.ORG_NAME AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" NULL as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN tm_org b ON a.REGION_ID = b.ORG_ID WHERE a.REGION_TYPE=2  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))){
			sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))){
			sb.append(" AND (DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
			sb.append(" OR DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth2Start")+"' AND '"+queryParam.get("yearMonth2End")+"') ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("orgId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" NULL AS dealerCode, ");
		sb.append(" NULL AS dealername, ");
		sb.append(" NULL as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL as evaluateID, ");
		sb.append(" NULL as evaluateDate, ");
		sb.append(" NULL as EVALUATE_BY_NAME, ");
		sb.append(" NULL as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN (SELECT * FROM vm_dealer_org_dudao_province GROUP BY employee_id) b ON a.REGION_ID = b.employee_id WHERE a.REGION_TYPE=3  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))){
			sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))){
			sb.append(" AND (DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
			sb.append(" OR DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth2Start")+"' AND '"+queryParam.get("yearMonth2End")+"') ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.REGION_ID IN (SELECT DISTINCT employee_id FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.REGION_ID=?  ");
			params.add(queryParam.get("dudaoId"));
		}
		sb.append(" GROUP BY a.REGION_ID,a.REGION_TYPE,a.DATA_TIME ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" CONCAT(DATE_FORMAT(c.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(c.START_DATE,'%Y%m%d')=DATE_FORMAT(c.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(c.END_DATE,'%d日')))) as tourtime, ");
		sb.append(" d.evaluate_id as evaluateID, ");
		sb.append(" d.evaluate_date as evaluateDate, ");
		sb.append(" (SELECT E.EMPLOYEE_NAME FROM TM_EMPLOYEE E WHERE E.EMPLOYEE_NO = d.evaluate_by) AS EVALUATE_BY_NAME, ");
		sb.append(" c.AGENDA_ID as agendaID, ");
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("item").toString().indexOf("率")==-1&&list.get(i).get("item").toString().indexOf("占比")==-1&&list.get(i).get("item").toString().indexOf("满足度")==-1&&list.get(i).get("item").toString().indexOf("额")==-1&&list.get(i).get("item").toString().indexOf("单台")==-1&&list.get(i).get("item").toString().indexOf("纯正性指数")==-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,0) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else if(list.get(i).get("item").toString().indexOf("额")>-1||list.get(i).get("item").toString().indexOf("单台")>-1||list.get(i).get("item").toString().indexOf("纯正性指数")>-1){
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN ROUND(DATA_NUM,2) ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}else{
				sb.append(" max(CASE DATA_TYPE WHEN '"+list.get(i).get("item").toString()+"' THEN CONCAT(ROUND(DATA_NUM*100,2),'%') ELSE 0 END) AS '"+list.get(i).get("item").toString()+"' ,");
			}
		}
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" LEFT JOIN (select a.DEALER_ID,a.AGENDA_ID,a.START_DATE,a.END_DATE,b.EVENT_TYPE from tt_supervision_agenda a inner join tt_supervision_agenda_detail b on a.AGENDA_ID=b.AGENDA_ID and b.CHANGE_STATE=90031001 where b.EVENT_TYPE=90011002) c ");
		sb.append(" ON b.DEALER_ID=c.DEALER_ID AND DATE_FORMAT(a.DATA_TIME,'%Y%m')=DATE_FORMAT(c.START_DATE,'%Y%m') ");
		sb.append(" LEFT JOIN tt_patrol_report d ");
		sb.append(" ON c.AGENDA_ID=d.agenda_id AND d.report_status=92051002 ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))){
			sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))){
			sb.append(" AND (DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
			sb.append(" OR DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth2Start")+"' AND '"+queryParam.get("yearMonth2End")+"') ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE employee_id=?)  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.DATA_TIME ");
 		return DAOUtil.findAll(sb.toString(), params);
	}

	@Override
	public List<Map> searchWeakdata1List(Map<String, String> queryParam) throws ServiceBizException {
		// TODO Auto-generated method stub

		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT A.*,B.* FROM  ");
		sb.append(" (SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" CONCAT(DATE_FORMAT(c.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(c.START_DATE,'%Y%m%d')=DATE_FORMAT(c.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(c.END_DATE,'%d日')))) as tourtime, ");
		sb.append(" d.evaluate_id as evaluateID, ");
		sb.append(" d.evaluate_date as evaluateDate, ");
		sb.append(" (SELECT E.EMPLOYEE_NAME FROM TM_EMPLOYEE E WHERE E.EMPLOYEE_NO = d.evaluate_by) AS EVALUATE_BY_NAME, ");
		sb.append(" c.AGENDA_ID as agendaID, ");
		sb.append(" max(CASE DATA_TYPE WHEN '进厂台次进厂达成率' THEN DATA_NUM ELSE 0 END) AS '进厂台次进厂达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养台次保养达成率' THEN DATA_NUM ELSE 0 END) AS '保养台次保养达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '二保维系二保维系率' THEN DATA_NUM ELSE 0 END) AS '二保维系二保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '上年保养留存保养客户留存率' THEN DATA_NUM ELSE 0 END) AS '上年保养留存保养客户留存率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '忠诚客户维系忠诚客户占比' THEN DATA_NUM ELSE 0 END) AS '忠诚客户维系忠诚客户占比' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率首保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率首保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率定保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率定保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率过保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率过保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率客户维系率' THEN DATA_NUM ELSE 0 END) AS '维系率客户维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货机油订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货机油订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货轮胎订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货轮胎订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货添加剂订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货添加剂订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货电瓶订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货电瓶订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货活性炭滤芯订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货活性炭滤芯订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货空调养护订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货空调养护订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件单台' THEN DATA_NUM ELSE 0 END) AS '保养件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '外观件单台' THEN DATA_NUM ELSE 0 END) AS '外观件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '用品单台金额' THEN DATA_NUM ELSE 0 END) AS '用品单台金额' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '新保新保渗透率' THEN DATA_NUM ELSE 0 END) AS '新保新保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '续保续保目标达成率' THEN DATA_NUM ELSE 0 END) AS '续保续保目标达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '延保延保渗透率' THEN DATA_NUM ELSE 0 END) AS '延保延保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN 'IMS系统使用率' THEN DATA_NUM ELSE 0 END) AS 'IMS系统使用率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '在线调查综合得分' THEN DATA_NUM ELSE 0 END) AS '在线调查综合得分' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位总经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位总经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位服务经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位服务经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位零部件经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位零部件经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位客服经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位客服经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保险经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位保险经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保修鉴定员' THEN DATA_NUM ELSE 0 END) AS '关键岗位保修鉴定员' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（业务类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（业务类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（机电类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（机电类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '人员数量满足度' THEN DATA_NUM ELSE 0 END) AS '人员数量满足度' ,");
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" LEFT JOIN (select a.DEALER_ID,a.AGENDA_ID,a.START_DATE,a.END_DATE,b.EVENT_TYPE from tt_supervision_agenda a inner join tt_supervision_agenda_detail b on a.AGENDA_ID=b.AGENDA_ID and b.CHANGE_STATE=90031001 where b.EVENT_TYPE=90011002) c ");
		sb.append(" ON b.DEALER_ID=c.DEALER_ID AND DATE_FORMAT(a.DATA_TIME,'%Y%m')=DATE_FORMAT(c.START_DATE,'%Y%m') ");
		sb.append(" LEFT JOIN tt_patrol_report d ");
		sb.append(" ON c.AGENDA_ID=d.agenda_id AND d.report_status=92051002 ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND QUARTER(a.DATA_TIME)=?  ");
			params.add(queryParam.get("quarter"));
		}
		sb.append(" AND DATE_FORMAT(a.DATA_TIME,'%Y')=? ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE employee_id=?)  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.DATA_TIME) A ");
		sb.append(" LEFT JOIN ");
		sb.append(" (SELECT IFNULL(TARGET1,0) TARGET1,IFNULL(TARGET2,0) TARGET2,IFNULL(TARGET3,0) TARGET3,IFNULL(TARGET4,0) TARGET4,IFNULL(TARGET5,0) TARGET5, ");
		sb.append(" IFNULL(TARGET6,0) TARGET6,IFNULL(TARGET7,0) TARGET7,IFNULL(TARGET8,0) TARGET8,IFNULL(TARGET9,0) TARGET9, ");
		sb.append(" IFNULL(TARGET10,0) TARGET10,IFNULL(TARGET11,0) TARGET11,IFNULL(TARGET12,0) TARGET12,IFNULL(TARGET13,0) TARGET13,IFNULL(TARGET14,0) TARGET14, ");
		sb.append(" IFNULL(TARGET15,0) TARGET15,IFNULL(TARGET16,0) TARGET16,IFNULL(TARGET17,0) TARGET17,IFNULL(TARGET18,0) TARGET18,IFNULL(TARGET19,0) TARGET19, ");
		sb.append(" IFNULL(TARGET20,0) TARGET20,IFNULL(TARGET21,0) TARGET21,IFNULL(TARGET22,0) TARGET22,IFNULL(TARGET23,0) TARGET23,IFNULL(ROUND(TARGET24),0) TARGET24, ");
		sb.append(" IFNULL(ROUND(TARGET25),0) TARGET25,IFNULL(ROUND(TARGET26),0) TARGET26,IFNULL(ROUND(TARGET27),0) TARGET27,IFNULL(ROUND(TARGET28),0) TARGET28,IFNULL(ROUND(TARGET29),0) TARGET29, ");
		sb.append(" IFNULL(ROUND(TARGET30),0) TARGET30,IFNULL(ROUND(TARGET31),0) TARGET31,IFNULL(TARGET32,0) TARGET32,DATA_TIME,OGR_ID FROM tt_data_display_target WHERE ORG_TYPE=? ");
		params.add(queryParam.get("targetType"));
		sb.append(" ) B ");
		sb.append(" ON A.dataTime=B.DATA_TIME ");
		if(queryParam.get("targetType").equals("3")) {
			sb.append(" AND A.orgID=B.OGR_ID ");
			
		}
		sb.append(" UNION ALL ");
		sb.append(" SELECT A.*,B.* FROM  ");
		sb.append(" (SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" NULL as tourtime, ");
		sb.append(" NULL  as evaluateID, ");
		sb.append(" NULL  as evaluateDate, ");
		sb.append(" NULL  as evaluateBY, ");
		sb.append(" NULL  as agendaID, ");
		sb.append(" max(CASE DATA_TYPE WHEN '进厂台次进厂达成率' THEN DATA_NUM ELSE 0 END) AS '进厂台次进厂达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养台次保养达成率' THEN DATA_NUM ELSE 0 END) AS '保养台次保养达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '二保维系二保维系率' THEN DATA_NUM ELSE 0 END) AS '二保维系二保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '上年保养留存保养客户留存率' THEN DATA_NUM ELSE 0 END) AS '上年保养留存保养客户留存率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '忠诚客户维系忠诚客户占比' THEN DATA_NUM ELSE 0 END) AS '忠诚客户维系忠诚客户占比' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率首保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率首保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率定保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率定保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率过保维系率' THEN DATA_NUM ELSE 0 END) AS '维系率过保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '维系率客户维系率' THEN DATA_NUM ELSE 0 END) AS '维系率客户维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货机油订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货机油订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货轮胎订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货轮胎订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货添加剂订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货添加剂订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货电瓶订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货电瓶订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货活性炭滤芯订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货活性炭滤芯订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货空调养护订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货空调养护订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件单台' THEN DATA_NUM ELSE 0 END) AS '保养件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '外观件单台' THEN DATA_NUM ELSE 0 END) AS '外观件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '用品单台金额' THEN DATA_NUM ELSE 0 END) AS '用品单台金额' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '新保新保渗透率' THEN DATA_NUM ELSE 0 END) AS '新保新保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '续保续保目标达成率' THEN DATA_NUM ELSE 0 END) AS '续保续保目标达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '延保延保渗透率' THEN DATA_NUM ELSE 0 END) AS '延保延保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN 'IMS系统使用率' THEN DATA_NUM ELSE 0 END) AS 'IMS系统使用率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '在线调查综合得分' THEN DATA_NUM ELSE 0 END) AS '在线调查综合得分' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位总经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位总经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位服务经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位服务经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位零部件经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位零部件经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位客服经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位客服经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保险经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位保险经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保修鉴定员' THEN DATA_NUM ELSE 0 END) AS '关键岗位保修鉴定员' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（业务类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（业务类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（机电类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（机电类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '人员数量满足度' THEN DATA_NUM ELSE 0 END) AS '人员数量满足度' ,");
		sb.append(" CASE a.QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE WHERE a.REGION_TYPE=4  ");
		sb.append(" AND a.YEAR=? AND DATA_TIME IS NULL ");
		params.add(queryParam.get("year"));
		if(!StringUtils.isNullOrEmpty(queryParam.get("quarter"))){
			sb.append(" AND a.QUARTER=? ");
			params.add(queryParam.get("quarter"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dudaoId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE employee_id=?)  ");
			params.add(queryParam.get("dudaoId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.YEAR,a.QUARTER) A ");
		sb.append(" LEFT JOIN ");
		sb.append(" (SELECT IFNULL(TARGET1,0) TARGET1,IFNULL(TARGET2,0) TARGET2,IFNULL(TARGET3,0) TARGET3,IFNULL(TARGET4,0) TARGET4,IFNULL(TARGET5,0) TARGET5, ");
		sb.append(" IFNULL(TARGET6,0) TARGET6,IFNULL(TARGET7,0) TARGET7,IFNULL(TARGET8,0) TARGET8,IFNULL(TARGET9,0) TARGET9, ");
		sb.append(" IFNULL(TARGET10,0) TARGET10,IFNULL(TARGET11,0) TARGET11,IFNULL(TARGET12,0) TARGET12,IFNULL(TARGET13,0) TARGET13,IFNULL(TARGET14,0) TARGET14, ");
		sb.append(" IFNULL(TARGET15,0) TARGET15,IFNULL(TARGET16,0) TARGET16,IFNULL(TARGET17,0) TARGET17,IFNULL(TARGET18,0) TARGET18,IFNULL(TARGET19,0) TARGET19, ");
		sb.append(" IFNULL(TARGET20,0) TARGET20,IFNULL(TARGET21,0) TARGET21,IFNULL(TARGET22,0) TARGET22,IFNULL(TARGET23,0) TARGET23,IFNULL(ROUND(TARGET24),0) TARGET24, ");
		sb.append(" IFNULL(ROUND(TARGET25),0) TARGET25,IFNULL(ROUND(TARGET26),0) TARGET26,IFNULL(ROUND(TARGET27),0) TARGET27,IFNULL(ROUND(TARGET28),0) TARGET28,IFNULL(ROUND(TARGET29),0) TARGET29, ");
		sb.append(" IFNULL(ROUND(TARGET30),0) TARGET30,IFNULL(ROUND(TARGET31),0) TARGET31,IFNULL(TARGET32,0) TARGET32,CASE QUARTER WHEN 1 THEN '第一季度' WHEN 2 THEN '第二季度' WHEN 3 THEN '第三季度' WHEN 4 THEN '第四季度' ELSE 0 END AS dataTime1,OGR_ID FROM tt_data_display_target WHERE ORG_TYPE=? AND YEAR=? AND QUARTER=?");
		params.add(queryParam.get("targetType"));
		params.add(queryParam.get("year"));
		params.add(queryParam.get("quarter"));
		sb.append(" ) B ");
		sb.append(" ON A.dataTime=B.dataTime1 ");
		if(queryParam.get("targetType").equals("3")) {
			sb.append(" AND A.orgID=B.OGR_ID ");
			
		}
		
 		return DAOUtil.findAll(sb.toString(), params);
}

	@Override
	public List<Map> searchWeakdata2List(Map<String, String> queryParam) throws ServiceBizException {
		// TODO Auto-generated method stub

		StringBuilder sb = new StringBuilder();
		List<Object> params=new ArrayList<>();
		sb.append(" SELECT  ");
		sb.append(" a.DEALER_CODE AS dealerCode, ");
		sb.append(" a.DEALER_SHORTNAME AS dealername, ");
		sb.append(" b.ORG_NAME as orgName, ");
		sb.append(" b.EMPLOYEE_NAME as empName, ");
		sb.append(" CONCAT(DATE_FORMAT(c.START_DATE,'%Y年%m月%d'),IF(DATE_FORMAT(c.START_DATE,'%Y%m%d')=DATE_FORMAT(c.END_DATE,'%Y%m%d'),'日',CONCAT('-',DATE_FORMAT(c.END_DATE,'%d日')))) as tourtime, ");
		sb.append(" d.evaluate_id as evaluateID, ");
		sb.append(" d.evaluate_date as evaluateDate, ");
		sb.append(" d.evaluate_by as evaluateBY, ");
		sb.append(" c.AGENDA_ID as agendaID, ");
		sb.append(" max(CASE DATA_TYPE WHEN '进厂台次进厂达成率' THEN DATA_NUM ELSE 0 END) AS '进厂台次进厂达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养台次保养达成率' THEN DATA_NUM ELSE 0 END) AS '保养台次保养达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '二保维系二保维系率' THEN DATA_NUM ELSE 0 END) AS '二保维系二保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '上年保养留存保养客户留存率' THEN DATA_NUM ELSE 0 END) AS '上年保养留存保养客户留存率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '忠诚客户维系忠诚客户占比' THEN DATA_NUM ELSE 0 END) AS '忠诚客户维系忠诚客户占比' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '首保维系率' THEN DATA_NUM ELSE 0 END) AS '首保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '定保维系率' THEN DATA_NUM ELSE 0 END) AS '定保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '过保维系率' THEN DATA_NUM ELSE 0 END) AS '过保维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '客户维系率' THEN DATA_NUM ELSE 0 END) AS '客户维系率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货机油订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货机油订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货轮胎订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货轮胎订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货添加剂订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货添加剂订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货电瓶订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货电瓶订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货活性炭滤芯订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货活性炭滤芯订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件订货空调养护订货成交率' THEN DATA_NUM ELSE 0 END) AS '保养件订货空调养护订货成交率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '保养件单台' THEN DATA_NUM ELSE 0 END) AS '保养件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '外观件单台' THEN DATA_NUM ELSE 0 END) AS '外观件单台' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '用品单台金额' THEN DATA_NUM ELSE 0 END) AS '用品单台金额' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '新保新保渗透率' THEN DATA_NUM ELSE 0 END) AS '新保新保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '续保续保目标达成率' THEN DATA_NUM ELSE 0 END) AS '续保续保目标达成率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '延保延保渗透率' THEN DATA_NUM ELSE 0 END) AS '延保延保渗透率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN 'IMS系统使用率' THEN DATA_NUM ELSE 0 END) AS 'IMS系统使用率' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '在线调查综合得分' THEN DATA_NUM ELSE 0 END) AS '在线调查综合得分' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位总经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位总经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位服务经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位服务经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位零部件经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位零部件经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位客服经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位客服经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保险经理' THEN DATA_NUM ELSE 0 END) AS '关键岗位保险经理' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位保修鉴定员' THEN DATA_NUM ELSE 0 END) AS '关键岗位保修鉴定员' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（业务类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（业务类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '关键岗位IDT讲师（机电类）' THEN DATA_NUM ELSE 0 END) AS '关键岗位IDT讲师（机电类）' ,");
		sb.append(" max(CASE DATA_TYPE WHEN '人员数量满足度' THEN DATA_NUM ELSE 0 END) AS '人员数量满足度' ,");
		sb.append(" DATA_TIME AS dataTime  ");
		sb.append(" FROM tt_data_display_count a LEFT JOIN vm_dealer_org_dudao_province b ON a.DEALER_CODE= b.DEALER_CODE ");
		sb.append(" LEFT JOIN (select a.DEALER_ID,a.AGENDA_ID,a.START_DATE,a.END_DATE,b.EVENT_TYPE from tt_supervision_agenda a inner join tt_supervision_agenda_detail b on a.AGENDA_ID=b.AGENDA_ID and b.CHANGE_STATE=90031001 where b.EVENT_TYPE=90011002) c ");
		sb.append(" ON b.DEALER_ID=c.DEALER_ID AND DATE_FORMAT(a.DATA_TIME,'%Y%m')=DATE_FORMAT(c.START_DATE,'%Y%m') ");
		sb.append(" LEFT JOIN tt_patrol_report d ");
		sb.append(" ON c.AGENDA_ID=d.agenda_id AND d.report_status=92051002 ");
		sb.append(" WHERE a.REGION_TYPE=4  ");
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))){
			sb.append(" AND DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth2End"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1Start"))&&!StringUtils.isNullOrEmpty(queryParam.get("yearMonth1End"))){
			sb.append(" AND (DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth1Start")+"' AND '"+queryParam.get("yearMonth1End")+"' ");
			sb.append(" OR DATE_FORMAT(DATA_TIME,'%Y-%m') BETWEEN '"+queryParam.get("yearMonth2Start")+"' AND '"+queryParam.get("yearMonth2End")+"') ");
		}
		
		if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE ORG_ID=?)  ");
			params.add(queryParam.get("orgId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("provinceId"))){
			sb.append(" AND a.DEALER_CODE IN (SELECT DISTINCT DEALER_CODE FROM vm_dealer_org_dudao_province WHERE REGION_ID=?)  ");
			params.add(queryParam.get("provinceId"));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
			sb.append(" AND a.DEALER_CODE=?  ");
			params.add(queryParam.get("dealerCode"));
		}
		sb.append(" GROUP BY a.DEALER_CODE,a.REGION_TYPE,a.DATA_TIME ");
		return DAOUtil.findAll(sb.toString(), params);
		}
}
