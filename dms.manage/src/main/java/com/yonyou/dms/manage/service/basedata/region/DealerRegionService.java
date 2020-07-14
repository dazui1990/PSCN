package com.yonyou.dms.manage.service.basedata.region;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.basedata.role.DealerRoleDto;

public interface DealerRegionService {
	public List<Map> orgList() throws ServiceBizException;		//区域下拉框
	
	public List<Map> regionList(long orgId) throws ServiceBizException;   //省份下拉框
	
	public List<Map> cityList(long regionId) throws ServiceBizException;   //城市下拉框
	
	public List<Map> dealerCodeList() throws ServiceBizException;   //特约店代码下拉框
	
	public PageInfoDto dealerOrDudaoList(@RequestParam Map<String, String> queryParam) throws ServiceBizException;   //查询
	
	public List<Map> duDaoList(String str) throws ServiceBizException;   //督导下拉选框
	
	public void distribution(DealerRoleDto dealerDto,String selectRow) throws ServiceBizException;   //督导下拉选框
	
	
}
