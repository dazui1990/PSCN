package com.yonyou.dms.manage.service.budgetManage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.manage.constant.Constants;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetApplicationImportDto;
import com.yonyou.dms.manage.domains.PO.budget.BudgetApplicationImportPO;
import com.yonyou.dms.manage.tool.CommonUtils;

@SuppressWarnings("rawtypes")
@Service
public class BudgetApplicationImportServiceImpl implements BudgetApplicationImportService {

	@Override
	public List<Map> queryBudgetInfo() {
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT ");
		sql.append(" 	mdr.io, ");
		sql.append(" 	md.fucntion, ");
		sql.append(" 	md.sn_no, ");
		sql.append(" 	md.budget_item, ");
		sql.append(" 	bd.all_year ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_maste_data md ");
		sql.append(" LEFT JOIN tm_budget_maste_data_relation mdr ON md.sn_no = mdr.sn_no ");
		sql.append(
				" LEFT JOIN tm_budget_detail bd on bd.master_id = md.master_id and bd.data_type = '预算数据' and bd.`status` = "
						+ DictCodeConstants.STATUS_IS_VALID + " ");

		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public List<Map> queryPrInfo() {
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT ");
		sql.append(" 	ba.pr_no, ");
		sql.append(" 	ba.pr_amount ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_apply ba ");
		sql.append(" WHERE ba.`status` = " + Constants.PR_STATUS_APPROVED + " ");

		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public void saveOrUpdate(List<BudgetApplicationImportDto> dataList, List<Map> list, List<Map> prList,
			LoginInfoDto loginInfo) {
		if (!CommonUtils.isNull(dataList)) {
			for (BudgetApplicationImportDto dto : dataList) {
				BudgetApplicationImportPO po = new BudgetApplicationImportPO();

				if (!CommonUtils.isNull(list)) {
					for (Map map : list) {
						if (CommonUtils.getString(map.get("io")).equals(dto.getCn0f_io())
								|| CommonUtils.getString(map.get("io")).equals(dto.getCn0g_io())) {
							po.setString("sn_no", CommonUtils.getString(map.get("sn_no")));

							po.setDouble("annual_budget", Double.valueOf(CommonUtils.getString(map.get("all_year")))); // 年度预算

						}
					}
				}

				Double prConsumptionAmount = 0.0;
				if (!CommonUtils.isNull(prList)) {
					for (Map map : prList) {
						if (CommonUtils.getString(map.get("pr_no")).equals(dto.getPr_no())) {
							prConsumptionAmount += Double.valueOf(CommonUtils.getString(map.get("pr_amount")));
						}
					}
				}

				po.setString("week", dto.getWeek());

				po.setString("pr_no", dto.getPr_no());
				po.setString("pr_item", dto.getPr_item());
				po.setDouble("pr_amount", dto.getPr_amount());
				po.setString("cn0g_io", dto.getCn0g_io());
				po.setString("cn0f_io", dto.getCn0f_io());

				po.setDouble("pr_consumption_amount", prConsumptionAmount);
				po.setDouble("remaining_budget",
						(po.getDouble("annual_budget") == null ? 0.0 : po.getDouble("annual_budget"))
								- prConsumptionAmount
								- (po.getDouble("pr_amount") == null ? 0.0 : po.getDouble("pr_amount")));
				if (po.getDouble("annual_budget") == null || po.getDouble("annual_budget") == 0.0) {
					po.setDouble("budget_burning_rate", 0.0);
				} else {
					po.setDouble("budget_burning_rate", (po.getDouble("annual_budget")
							- (po.getDouble("remaining_budget") == null ? 0.0 : po.getDouble("remaining_budget")))
							/ po.getDouble("annual_budget"));
				}

				po.setInteger("status", Constants.PR_STATUS_UNDER_REVIEW);
				po.setString("remark", dto.getRemark());
				po.setString("created_by", loginInfo.getUserId());
				po.setDate("created_date", new Date());
				po.setString("updated_by", loginInfo.getUserId());
				po.setDate("updated_date", new Date());
				po.saveIt();
			}
		}
	}

	@Override
	public void checkData(ImportResultDto<BudgetApplicationImportDto> importResult) {
		// List<BudgetApplicationImportDto> importList = importResult.getDataList();
		// Map<String, String> keyMap = new HashMap<String, String>();
		// if (!CommonUtils.isNull(importList)) {
		// for (BudgetApplicationImportDto dto : importList) {
		// if () {
		//
		// }
		// }
		// }
	}

	@Override
	public List<Map> getExcelResult(Map<String, String> queryParam) {
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT ");
		sql.append(" 	ba.`week`, ");
		sql.append(" 	ba.pr_no, ");
		sql.append(" 	ba.pr_item, ");
		sql.append(" 	ba.cn0f_io, ");
		sql.append(" 	ba.cn0g_io, ");
		sql.append(" 	ifnull(ba.annual_budget, 0) as annual_budget, ");
		sql.append(" 	ifnull(ba.pr_consumption_amount, 0) as pr_consumption_amount, ");
		sql.append(" 	ifnull(ba.budget_burning_rate, 0) as budget_burning_rate, ");
		sql.append(" 	ifnull(ba.pr_amount, 0) as pr_amount, ");
		sql.append(" CASE ");
		sql.append(" 	WHEN ba.`status` = 80011001 THEN 'Approved' ");
		sql.append(" 	WHEN ba.`status` = 80011002 THEN 'Further Discussion' ");
		sql.append(" 	WHEN ba.`status` = 80011003 THEN 'Reject' ");
		sql.append(" END AS statusDesc, ");
		sql.append(" 	ba.remark, ");
		sql.append(" 	ifnull(ba.remaining_budget, 0) as remaining_budget, ");
		sql.append(" 	md.fucntion, ");
		sql.append(" 	md.budget_item ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_apply ba ");
		sql.append(" LEFT JOIN tm_budget_maste_data md ON md.sn_no = ba.sn_no and md.`status` = "+DictCodeConstants.STATUS_IS_VALID+" ");
	
		
	
		
		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public List<Map> queryBudgetApply() {
		StringBuilder sql = new StringBuilder("");
		sql.append(" SELECT ");
		sql.append(" 	ba.id, ");
		sql.append(" 	ba.pr_no, ");
		sql.append(" 	ba.pr_amount ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_apply ba ");

		return DAOUtil.findAll(sql.toString(), null);
	}

}
