package com.yonyou.dms.manage.service.budgetManage;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.function.exception.ServiceBizException;

public interface BudgetManageService {
	
	public List<Map> getListResult(Map<String, String> param)throws ServiceBizException;

}
