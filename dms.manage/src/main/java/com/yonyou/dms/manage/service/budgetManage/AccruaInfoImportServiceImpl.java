package com.yonyou.dms.manage.service.budgetManage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.manage.domains.DTO.dudget.AccruaInfoImportDto;
import com.yonyou.dms.manage.domains.PO.budget.TmAccruaInfoPO;
import com.yonyou.dms.manage.tool.CommonUtils;

@Service
public class AccruaInfoImportServiceImpl implements AccruaInfoImportService{
	
	private static final Logger log = Logger.getLogger(AccruaInfoImportServiceImpl.class);

	@Override
	public List<Map> getAccruaInfo(Map<String, String> queryMap) {
		StringBuilder sql = new StringBuilder("");
		String queryMonth = CommonUtils.getString(queryMap.get("year")) + CommonUtils.getString(queryMap.get("month"));
		sql.append(" SELECT ");
		sql.append(" "+ queryMonth +" as accrualMonth, ");
		sql.append(" 	md.fucntion, ");
		sql.append(" 	tmp.shopping_cart, ");
		sql.append(" 	tmp.shopping_cart_name, ");
		sql.append(" 	ifnull(ba.cn0g_io, ba.cn0f_io) io, ");
		sql.append(" 	ba.pr_amount, ");
		sql.append(" 	tmp.grAmont, ");
		sql.append(" 	(ba.pr_amount - tmp.grAmont) poGr, ");
		sql.append(" 	dr.cost_center ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_apply ba ");
		sql.append(" LEFT JOIN (select be.shopping_cart, be.shopping_cart_name, sum(be.net_order_value_in_order_currency) grAmont from tm_budget_extend be group by be.shopping_cart, be.shopping_cart_name) tmp ON ba.pr_no = tmp.shopping_cart ");
		sql.append(" LEFT JOIN tm_budget_maste_data md ON ba.sn_no = md.sn_no AND md.`status` = "+DictCodeConstants.STATUS_IS_VALID+" ");
		sql.append(" LEFT JOIN tm_budget_maste_data_relation dr on ba.sn_no = dr.sn_no ");
//		sql.append(" where (ba.pr_amount - tmp.grAmont) >= 100000 ");
	
		log.info("AccruaInfoImportServiceImpl.getAccruaInfo"+ sql.toString());
		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public List<Map> getAccruaInfo1(Map<String, String> queryMap) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT ");
		sql.append(" 	ai.accrual_month, ");
		sql.append(" 	md.fucntion, ");
		sql.append(" 	tmp.shopping_cart, ");
		sql.append(" 	tmp.shopping_cart_name, ");
		sql.append(" 	ifnull(ba.cn0g_io, ba.cn0f_io) io, ");
		sql.append(" 	ba.pr_amount, ");
		sql.append(" 	tmp.grAmont, ");
		sql.append(" 	(ba.pr_amount - tmp.grAmont) poGr, ");
		sql.append(" 	dr.cost_center, ");
		sql.append(" 	ai.accrual_reason, ");
		sql.append(" 	ai.amount ");
		sql.append(" FROM ");
		sql.append(" 	tm_accrua_info ai ");
		sql.append(" LEFT JOIN tm_budget_apply ba on ai.pr_no = ba.pr_no ");
		sql.append(" LEFT JOIN (select be.shopping_cart, be.shopping_cart_name, sum(be.net_order_value_in_order_currency) grAmont from tm_budget_extend be group by be.shopping_cart, be.shopping_cart_name) tmp ON ba.pr_no = tmp.shopping_cart ");
		sql.append(" LEFT JOIN tm_budget_maste_data md ON ba.sn_no = md.sn_no AND md.`status` = "+DictCodeConstants.STATUS_IS_VALID+" ");
		sql.append(" LEFT JOIN tm_budget_maste_data_relation dr on ba.sn_no = dr.sn_no ");
		sql.append(" where 1 = 1 ");
		
		String accrualMonth = CommonUtils.getString(queryMap.get("year") )+ CommonUtils.getString(queryMap.get("month"));
		if (CommonUtils.isNull(accrualMonth)) {
			sql.append(" and ai.accrual_month = ?  ");
			params.add(accrualMonth);
		}
	
		log.info("AccruaInfoImportServiceImpl.getAccruaInfo1"+ sql.toString());
		return DAOUtil.findAll(sql.toString(), params);
	}

	@Override
	public List<Map> queryAccruaInfoIsExists(List<AccruaInfoImportDto> list) {
		StringBuilder params = new StringBuilder("");
		StringBuilder sql = new StringBuilder("");
		if (!CommonUtils.isNull(list)) {
			for (AccruaInfoImportDto dto : list) {
				params.append("('").append(dto.getAccrualMonth()).append("', ");
				params.append("'").append(dto.getShoppingCart()).append("'),");
			}
		}
		sql.append(" SELECT ");
		sql.append(" 	ai.accrual_month, ");
		sql.append(" 	ai.pr_no ");
		sql.append(" FROM ");
		sql.append(" 	tm_accrua_info ai ");
		sql.append(" WHERE ");
		if (CommonUtils.isNull(params.toString())) {
			sql.append(" 	1 = 1 ");
		} else {
			sql.append(" 	(ai.accrual_month, ai.pr_no) IN (" + params.toString().substring(0, params.toString().length() - 1)
					+ ") ");
		}
		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public void saveAccruaInfo(List<AccruaInfoImportDto> list) {
		if (!CommonUtils.isNull(list)) {
			LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
			for (AccruaInfoImportDto dto : list) {
				TmAccruaInfoPO po = new TmAccruaInfoPO();
				po.setString("pr_no", dto.getShoppingCart());
				po.setString("accrual_month", dto.getAccrualMonth());
				po.setString("accrual_reason", dto.getAccrualReason());
				if (!CommonUtils.isNull(dto.getAmount())) {					
					po.setDouble("amount", Double.valueOf(dto.getAmount()));
				}
				
				po.setString("created_by", loginInfo.getUserId());
				po.setDate("created_date", new Date());
				po.setString("updated_by", loginInfo.getUserId());
				po.setDate("updated_date", new Date());
				
				po.saveIt();
				
			}
		}
	}

	@Override
	public void updateAccruaInfo(List<AccruaInfoImportDto> list) throws Exception {
		if (!CommonUtils.isNull(list)) {
			List<Object> params = new ArrayList<Object>();
			
			StringBuilder sql = new StringBuilder("");
			sql.append(" UPDATE tm_accrua_info ai ");
			sql.append(" SET ai.accrual_reason = ?, ai.amount = ?, ai.updated_by = ?, ai.updated_date = now() ");
			sql.append(" WHERE 1 = 1 ");
			sql.append(" and ai.pr_no = ? ");
			sql.append(" AND ai.accrual_month = ? ");
			
			LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
			for (AccruaInfoImportDto dto : list) {
				Object[] strArr = new Object[5];
				
				strArr[0] = dto.getAccrualReason();
				strArr[1] = dto.getAmount();
				strArr[2] = loginInfo.getUserId();
				strArr[3] = dto.getShoppingCart();
				strArr[4] = dto.getAccrualMonth();
				params.add(strArr);
				
			}
			
			DAOUtil.saveBatchData(sql.toString(), params);
		}
	}
	
	

}
