package com.yonyou.dms.manage.service.basedata.report;

import java.util.List;
import java.util.Map;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.function.exception.ServiceBizException;

public interface ItinerantEffectAnalysisService {
	
	public PageInfoDto searchdata1(Map<String, String> queryParam) throws ServiceBizException;//季度维度的查询SQL
	
	public PageInfoDto searchdata2(Map<String, String> queryParam) throws ServiceBizException;//时间段维度的查询SQL
	
	public PageInfoDto searchWeakdata1(Map<String, String> queryParam) throws ServiceBizException;//弱项分析季度维度的查询SQL
	
	public PageInfoDto searchWeakdata2(Map<String, String> queryParam) throws ServiceBizException;//弱项分析时间段维度的查询SQL
	
	public Map<String, Object> getAgendaInfo(String dealerIdStr,String dataTimeStr) throws ServiceBizException;//巡店时间信息的查询SQL

	public List<Map> searchdata1List(Map<String, String> queryParam) throws ServiceBizException;//季度维度的查询SQL
	
	public List<Map> searchdata2List(Map<String, String> queryParam) throws ServiceBizException;//时间段维度的查询SQL
	
	public List<Map> searchWeakdata1List(Map<String, String> queryParam) throws ServiceBizException;//弱项分析季度维度的查询SQL
	
	public List<Map> searchWeakdata2List(Map<String, String> queryParam) throws ServiceBizException;//弱项分析时间段维度的查询SQL

}
