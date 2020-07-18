package com.yonyou.dms.manage.service.budgetManage;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetApplicationImportDto;

public interface BudgetApplicationImportService {

	public List<Map> queryBudgetInfo();

	public List<Map> queryPrInfo();

	public void saveOrUpdate(List<BudgetApplicationImportDto> dataList, List<Map> list, List<Map> prList,
			LoginInfoDto loginInfo);
	
	public void checkData(ImportResultDto<BudgetApplicationImportDto> importResult);
	
	public List<Map> getExcelResult(Map<String, String> queryParam);
	
	public List<Map> queryBudgetApply();
}
