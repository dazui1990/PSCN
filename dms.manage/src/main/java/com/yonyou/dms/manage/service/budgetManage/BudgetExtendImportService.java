package com.yonyou.dms.manage.service.budgetManage;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetExtendImportDto;

public interface BudgetExtendImportService {
	
	public void checkData(List<BudgetExtendImportDto> list, ImportResultDto<BudgetExtendImportDto> importResult);
	
	public void saveOrUpdate(List<BudgetExtendImportDto>  list, List<Map> budgetApplyList) throws ParseException;
	
}
