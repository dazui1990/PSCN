package com.yonyou.dms.manage.service.budgetManage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetExtendImportDto;
import com.yonyou.dms.manage.domains.PO.budget.BudgetExtendImportPO;
import com.yonyou.dms.manage.tool.CommonUtils;

@Service
public class BudgetExtendImportServiceImpl implements BudgetExtendImportService {

	@Override
	public void checkData(List<BudgetExtendImportDto> list, ImportResultDto<BudgetExtendImportDto> importResult) {

		List<Map> maps = queryBudgetExtendList(list);

		if (!CommonUtils.isNull(maps)) {
			ArrayList<BudgetExtendImportDto> errorList = new ArrayList<BudgetExtendImportDto>();
			for (int i = 0; i < list.size(); i++) {

				for (Map map : maps) {
					if (CommonUtils.getString(map.get("purchase_order")).equals(list.get(i).getPurchaseOrder())) {
						BudgetExtendImportDto dataImportDto = new BudgetExtendImportDto();
						dataImportDto.setRowNO(i + 1);
						dataImportDto.setErrorMsg("Purchase Order重复");
						errorList.add(dataImportDto);
					}
				}
			}
			importResult.setErrorList(errorList);
			importResult.setSucess(false);
		}

	}

	public List<Map> queryBudgetExtendList(List<BudgetExtendImportDto> list) {
		StringBuilder params = new StringBuilder("");
		StringBuilder sql = new StringBuilder("");
		if (!CommonUtils.isNull(list)) {
			for (BudgetExtendImportDto dto : list) {
				params.append("'").append(dto.getPurchaseOrder()).append("',");
			}
		}
		sql.append(" SELECT ");
		sql.append(" 	be.apply_id, ");
		sql.append(" 	be.purchase_order, ");
		sql.append(" 	be.shopping_cart, ");
		sql.append(" 	be.shopping_cart_name ");
		sql.append(" FROM ");
		sql.append(" 	tm_budget_extend be ");
		sql.append(" WHERE ");
		if (CommonUtils.isNull(params.toString())) {
			sql.append(" 	1 = 1 ");
		} else {
			sql.append(" 	be.purchase_order IN (" + params.toString().substring(0, params.toString().length() - 1)
					+ ") ");
		}
		return DAOUtil.findAll(sql.toString(), null);
	}

	@Override
	public void saveOrUpdate(List<BudgetExtendImportDto> list, List<Map> budgetApplyList) throws ParseException {
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);

		if (!CommonUtils.isNull(list)) {
			for (BudgetExtendImportDto dto : list) {

				 BudgetExtendImportPO po = new BudgetExtendImportPO();
				 po.setString("purchase_order", CommonUtils.getString(dto.getPurchaseOrder()));
				 po.setString("shopping_cart", CommonUtils.getString(dto.getShoppingCart()));
				 po.setString("shopping_cart_name", CommonUtils.getString(dto.getShoppingCartName()));
				 po.setString("internal_order", CommonUtils.getString(dto.getInternalOrder()));
				 po.setDate("po_posting_date", dto.getPoPostingDate());
				 po.setDouble("approved_shopping_cart_value",Double.valueOf(dto.getApprovedShoppingCartValue()));
				 po.setDouble("net_order_value_in_order_currency", Double.valueOf(dto.getNetOrderValueInOrderCurrency()));
				 po.setDouble("net_confirmed_value_in_order_currency", Double.valueOf(dto.getNetConfirmedValueInOrderCurrency()));
				
				 po.setString("created_by", loginInfo.getUserId());
				 po.setString("updated_by", loginInfo.getUserId());
				 po.setDate("created_date", new Date());
				 po.setDate("updated_date", new Date());

				for (Map budgetApply : budgetApplyList) {
					if (CommonUtils.getString(budgetApply.get("pr_no")).equals(dto.getShoppingCart())) {
						 po.setInteger("apply_id", Integer.valueOf(CommonUtils.getString(budgetApply.get("id"))));
					}
				}

				po.saveIt();
			}
		}

	}

}
