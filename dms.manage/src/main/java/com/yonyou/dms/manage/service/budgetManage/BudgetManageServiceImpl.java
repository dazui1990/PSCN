package com.yonyou.dms.manage.service.budgetManage;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.function.exception.ServiceBizException;

@Service
public class BudgetManageServiceImpl implements BudgetManageService {

	@Override
	public List<Map> getListResult(Map<String, String> param) throws ServiceBizException {
		List<Object> queryParam=new ArrayList<>();
		StringBuilder sb = new StringBuilder(" \n");
		sb.append("SELECT A.SN_NO,A.SUB_FUNCTION,A.FUCNTION,A.BUDGET_ITEM,A.ITEM_TYPE,  \n");
		sb.append("Round(B.JANUARY_VALUE,2)JANUARY_VALUE,Round(B.FEBRUARY_VALUE,2)FEBRUARY_VALUE, \n");
		sb.append("Round(B.MARCH_VALUE,2)MARCH_VALUE,Round(B.FIRST_QUARTER_VALUE,2)FIRST_QUARTER_VALUE, \n");
		sb.append("Round(B.APRIL_VALUE,2)APRIL_VALUE,Round(B.MAY_VALUE,2)MAY_VALUE, \n");
		sb.append("Round(B.JUNE_VALUE,2)JUNE_VALUE,Round(B.SECOND_QUARTER_VALUE,2)SECOND_QUARTER_VALUE, \n");
		sb.append("Round(B.JULY_VALUE,2)JULY_VALUE,Round(B.AUGUST_VALUE,2)AUGUST_VALUE, \n");
		sb.append("Round(B.SEPTEMBER_VALUE,2)SEPTEMBER_VALUE,Round(B.THIRD_QUARTER_VALUE,2)THIRD_QUARTER_VALUE,  \n");
		sb.append("Round(B.OCTOBER_VALUE,2)OCTOBER_VALUE,Round(B.NOVEMBER_VALUE,2)NOVEMBER_VALUE, \n");
		sb.append("Round(B.DECEMBER_VALUE,2)DECEMBER_VALUE,Round(B.FOUR_QUARTER_VALUE,2)FOUR_QUARTER_VALUE,  \n");
		sb.append("Round(B.ALL_YEAR,2)ALL_YEAR  \n");
		sb.append("FROM TM_BUDGET_MASTE_DATA A  \n");
		sb.append("INNER JOIN TM_BUDGET_DETAIL B ON A.master_id=B.master_id   \n");
		sb.append("WHERE A.YEAR='"+param.get("year")+"' and A.IMPORT_MONTH ='"+param.get("month")+"' \n");
//		sb.append("and A.VERSION='"+param.get("servion")+"' ");

		return DAOUtil.findAll(sb.toString(), queryParam);
	}

}
