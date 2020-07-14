package com.yonyou.dms.manage.service.basedata.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.framework.domains.PO.EmployeePO;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.manage.domains.DTO.basedata.role.DealerRoleDto;
import com.yonyou.dms.manage.domains.PO.basedata.role.TtDealerRolePO;

@Service
public class DealerRegionServiceImpl implements DealerRegionService{

	@Override
	public List<Map> orgList() throws ServiceBizException {
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT ORG_ID,ORG_NAME\n");
		sql.append(" FROM tm_org \n");
		sql.append(" WHERE ORG_TYPE=10191002 \n");
		
		List<Object> queryParams = new ArrayList<>();
		System.out.println(sql.toString());
		return DAOUtil.findAll(sql.toString(),queryParams);
	}

	@Override
	public List<Map> regionList(long orgId) throws ServiceBizException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT b.REGION_ID,b.REGION_NAME \n");
		sql.append(" FROM	(	select tmo.ORG_ID AS BIG_ORG,tmo.ORG_NAME AS BIG_NAME,tog.ORG_ID AS SMALL_ID,tog.ORG_NAME AS SMALL_NAME \n");
		sql.append(" 			from tm_org tmo INNER JOIN tm_org tog ON tmo.ORG_ID = tog.PARENT_ORG_ID \n");
		sql.append(" 			WHERE tmo.ORG_TYPE=10191002 ORDER BY BIG_ORG ) AS a \n");
		sql.append("  		 INNER JOIN tm_dealer_org_relation tdor ON a.SMALL_ID = tdor.ORG_ID \n");
		sql.append(" 		 INNER JOIN tm_dealer td ON tdor.DEALER_ID = td.DEALER_ID \n");
		sql.append(" 		 INNER JOIN (SELECT trn.REGION_ID,trn.REGION_NAME,tmr.REGION_ID AS CITY_ID,tmr.REGION_NAME AS CITY \n");
		sql.append(" 					 FROM tm_region trn INNER JOIN tm_region tmr ON trn.REGION_ID=tmr.PARENT_ID \n");
		sql.append(" 					 WHERE trn.REGION_TYPE=10091002 ORDER BY trn.REGION_ID) b ON td.CITY_ID=b.CITY_ID \n");
		sql.append(" WHERE 1=1 \n");
		List<Object> queryParams = new ArrayList<>();
			if(!StringUtils.isNullOrEmpty(orgId)){
				sql.append(" AND a.BIG_ORG = ? \n");
				queryParams.add(orgId);
			}
		sql.append(" ORDER BY a.BIG_NAME,b.REGION_NAME,b.CITY,td.DEALER_CODE \n");
		System.out.println(sql.toString());
		return DAOUtil.findAll(sql.toString(),queryParams);
	}

	@Override
	public List<Map> cityList(long regionId) throws ServiceBizException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT trn.REGION_ID,trn.REGION_NAME,tmr.REGION_ID AS CITY_ID,tmr.REGION_NAME AS CITY \n");
		sql.append(" FROM tm_region trn INNER JOIN tm_region tmr ON trn.REGION_ID=tmr.PARENT_ID \n");
		sql.append(" WHERE trn.REGION_TYPE=10091002 \n");
		List<Object> queryParams = new ArrayList<>();
		if(!StringUtils.isNullOrEmpty(regionId)){
			sql.append(" AND trn.REGION_ID = ? \n");
			queryParams.add(regionId);
		}
		sql.append(" ORDER BY trn.REGION_ID \n");
		System.out.println(sql.toString());
		return DAOUtil.findAll(sql.toString(),queryParams);
	}

	@Override
	public List<Map> dealerCodeList() throws ServiceBizException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.REGION_ID,b.REGION_NAME,b.CITY_ID,b.CITY,td.DEALER_ID,td.DEALER_CODE,td.DEALER_SHORTNAME,td.`STATUS`,td.CREATE_DATE \n");
		sql.append(" FROM	 tm_dealer td  \n");
		sql.append(" INNER JOIN (SELECT trn.REGION_ID,trn.REGION_NAME,tmr.REGION_ID AS CITY_ID,tmr.REGION_NAME AS CITY \n");
		sql.append(" FROM tm_region trn INNER JOIN tm_region tmr ON trn.REGION_ID=tmr.PARENT_ID \n");
		sql.append(" WHERE trn.REGION_TYPE=10091002 \n");
		sql.append(" ORDER BY trn.REGION_ID) b ON td.CITY_ID=b.CITY_ID \n");
		sql.append(" WHERE 1=1 \n");
		List<Object> queryParams = new ArrayList<>();
		sql.append(" ORDER BY b.REGION_NAME,b.CITY,td.DEALER_CODE \n");
		System.out.println(sql.toString());
		return DAOUtil.findAll(sql.toString(),queryParams);
	}

	@Override
	public PageInfoDto dealerOrDudaoList(Map<String, String> queryParam) throws ServiceBizException {
			List<Object> param = new ArrayList<>();
			StringBuilder sql = new StringBuilder();
			/*sql.append(" SELECT a.BIG_ORG,tdor.DEALER_ID,a.BIG_NAME,b.REGION_ID,b.REGION_NAME,b.CITY_ID,b.CITY,td.DEALER_CODE,td.DEALER_SHORTNAME,td.`STATUS`,td.CREATE_DATE \n");
			sql.append(" , ttr.SUPERVISION,ttr.SERIES_ORDER \n");
			sql.append(" FROM	(	select tmo.ORG_ID AS BIG_ORG,tmo.ORG_NAME AS BIG_NAME,tog.ORG_ID AS SMALL_ID,tog.ORG_NAME AS SMALL_NAME \n");
			sql.append(" from tm_org tmo INNER JOIN tm_org tog ON tmo.ORG_ID = tog.PARENT_ORG_ID \n");
			sql.append(" WHERE 	tmo.ORG_TYPE=10191002 \n");
			sql.append(" ORDER BY BIG_ORG ) AS a INNER JOIN tm_dealer_org_relation tdor ON a.SMALL_ID = tdor.ORG_ID \n");
			sql.append(" INNER JOIN tm_dealer td ON tdor.DEALER_ID = td.DEALER_ID  \n");
			sql.append(" INNER JOIN (SELECT trn.REGION_ID,trn.REGION_NAME,tmr.REGION_ID AS CITY_ID,tmr.REGION_NAME AS CITY \n");
			sql.append(" FROM tm_region trn INNER JOIN tm_region tmr ON trn.REGION_ID=tmr.PARENT_ID \n");
			sql.append(" WHERE trn.REGION_TYPE=10091002 \n");
			sql.append(" ORDER BY trn.REGION_ID) b ON td.CITY_ID=b.CITY_ID \n");
			sql.append(" LEFT JOIN tt_dealer_role ttr ON td.DEALER_ID=ttr.DEALER_ID \n");
			sql.append(" WHERE 1=1  \n");*/
			sql.append(" select a.*,date(a.ESTABLISHMENT_DATE) DATE,b.SUPERVISION,b.SERIES_ORDER from vm_dealer_org_dudao_province a left join tt_dealer_role b on a.dealer_id=b.dealer_id where 1=1 \n");
			if(!StringUtils.isNullOrEmpty(queryParam.get("orgId"))){
				sql.append(" AND a.ORG_ID = ? \n");
				param.add(queryParam.get("orgId"));
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("regionId"))){
				sql.append(" AND a.REGION_ID = ? \n");
				param.add(queryParam.get("regionId"));
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("cityId"))){
				sql.append(" AND a.CITY_ID = ? \n");
				param.add(queryParam.get("cityId"));
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("dealerCode"))){
				String[] dealerId = queryParam.get("dealerCode").toString().split(",");
				sql.append(" AND a.DEALER_ID in ("+dealerId[0]+" \n");
				for(int i=1;i<dealerId.length;i++){
					sql.append(" ,"+dealerId[i]+" \n");
				}
				sql.append(" ) \n");
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("dealerName"))){
				sql.append(" AND a.DEALER_SHORTNAME like '%"+queryParam.get("dealerName")+"%' \n");
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("duDao"))){
				sql.append(" AND b.SUPERVISION like '%"+queryParam.get("duDao")+"%' \n");
			}
			if(!StringUtils.isNullOrEmpty(queryParam.get("employeeName"))){
				sql.append(" AND b.SERIES_ORDER like '%"+queryParam.get("employeeName")+"%' \n");
			}
			/*if(!StringUtils.isNullOrEmpty(queryParam.get("dealerName"))){
				sql.append(" AND td.DEALER_SHORTNAME = ? \n");
				param.add(queryParam.get("dealerName"));
			}*/
			sql.append(" ORDER BY a.ORG_NAME,a.REGION_NAME,a.CITY_NAME,a.DEALER_CODE \n");
			System.out.println(sql.toString());
			return DAOUtil.pageQuery(sql.toString(), param);
		}

	@Override
	public List<Map> duDaoList(String str) throws ServiceBizException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.EMPLOYEE_ID,b.EMPLOYEE_NO,b.EMPLOYEE_NAME \n");
		sql.append(" FROM tm_employee b \n");
		sql.append(" inner join tm_employee_post c on b.employee_id=c.employee_id \n");
		sql.append(" left join tm_employee_enabled d on b.employee_id=d.employee_id  \n");
		sql.append(" WHERE 1=1 and d.status=10031001 \n");
		if(str.equals("dudao")){
			sql.append(" and c.post_code=96091001 \n");
		}else if(str.equals("xiz")){
			sql.append(" and c.post_code=96091002 \n");
		}
		List<Object> queryParams = new ArrayList<>();
		System.out.println(sql.toString());
		return DAOUtil.findAll(sql.toString(),queryParams);
	}

	/**
	 * 给特约店分配督导与系长时，如果当前特约店有数据，则删除原数据，添加新数据
	 */
	@Override
	public void distribution(DealerRoleDto dealerDto,String selectRow) throws ServiceBizException {
		String duDaoId = dealerDto.getDuDaoId();
		String xiZhangId = dealerDto.getXiZhangId();
		//EmployeePo duDao = EmployeePo.findFirst("employee_id=?", duDaoId);
		EmployeePO duDao = EmployeePO.findById(duDaoId);

		EmployeePO xiZhang = EmployeePO.findById(xiZhangId);
		
		String duDaoName = "";
		String xiZhangName = "";
		if(duDao!=null){
			duDaoName = duDao.get("EMPLOYEE_NAME").toString();
			
			xiZhangName = xiZhang.get("EMPLOYEE_NAME").toString();
			List<TtDealerRolePO> dealerRoles = TtDealerRolePO.find("SUPERVISION_ID=? ", duDaoId);
			
			List<TtDealerRolePO> dealerRolePOs = TtDealerRolePO.find("SUPERVISION_ID=? and SERIES_ORDER_ID=?", duDaoId,xiZhangId);
			if(dealerRolePOs.size()==0&&dealerRoles.size()>0){
				throw new ServiceBizException("当前督导系长为"+dealerRoles.get(0).get("SERIES_ORDER")+",如要更换系长,请将该督导负责特约店的督导和系长都置为“请选择”后,再维护！！！");
			}
		}
		String[] row = selectRow.split(",");
		
		for(int i =0;i<row.length;i++){
			TtDealerRolePO.delete("DEALER_ID =?", row[i]);
			
			TtDealerRolePO po = new TtDealerRolePO();
			po.set("DEALER_ID",row[i]);
			//duDaoName督导ID 为空时是把对应特约店的督导系长清除重置
			if(duDaoId!=""&&duDaoId!=null){
				po.set("SUPERVISION",duDaoName);
				po.set("SERIES_ORDER",xiZhangName);
				po.setLong("SUPERVISION_ID", duDaoId);
				po.setLong("SERIES_ORDER_ID", xiZhangId);
				po.saveIt();
			}
		}
	}

}
