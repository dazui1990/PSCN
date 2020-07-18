package com.yonyou.dms.manage.service.budgetManage;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.manage.domains.DTO.dudget.AccruaInfoImportDto;

public interface AccruaInfoImportService {
	
	public List<Map> getAccruaInfo(Map<String, String> queryMap);
	
	public List<Map> getAccruaInfo1(Map<String, String> queryMap);
	
	public List<Map> queryAccruaInfoIsExists(List<AccruaInfoImportDto> list);
	
	public void saveAccruaInfo(List<AccruaInfoImportDto> list);
	
	public void updateAccruaInfo(List<AccruaInfoImportDto> list) throws Exception;
}
