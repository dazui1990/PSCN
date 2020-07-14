
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : BasicComServiceImpl.java
*
* @Author : zhanshiwei
*
* @Date : 2016年7月19日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月19日    zhanshiwei    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.manage.service.basedata.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.DefinedRowProcessor;
import com.yonyou.dms.framework.domains.DTO.baseData.BasicParametersDTO;
import com.yonyou.dms.framework.domains.PO.baseData.BasicParametersPO;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.basedata.BasicParametersListDTO;

/**
 * 基础参数设置
 * @author zhanshiwei
 * @date 2016年7月19日
 */
@Service
public class BasicComServiceImpl implements BasicComService {

	/**
	 * 查询基础参数信息
	* @author zhanshiwei
	* @date 2016年8月2日
	* @param queryParam
	* @return
	* (non-Javadoc)
	* @see com.yonyou.dms.manage.service.basedata.parameter.BasicComService#QueryBasicParameters(java.util.Map)
	*/
		
    @Override
	public Map<String, Map<String, Object>> queryBasicParameters(Map<String, String> queryParam) throws ServiceBizException{
		StringBuilder sb = new StringBuilder(
				"select PARAM_ID,DEALER_CODE,PARAM_CODE,PARAM_NAME,PARAM_VALUE,REMARK from tc_system_param where 1=1");
		List<String> params = new ArrayList<>();
		final Map<String, Map<String, Object>> basicresult = new HashMap<>();
	    DAOUtil.findAll(sb.toString(), params,new DefinedRowProcessor(){		     
	            @Override
	            protected void process(Map<String, Object> row) {
	                basicresult.put(row.get("PARAM_CODE").toString(), row);
	            }
	        });
		return basicresult;
	}

	
	/**
	* 设置 BasicParametersPO 属性
	* @author zhanshiwei
	* @date 2016年9月7日
	* @param bapo
	* @param basiDto
	* @throws ServiceBizException
	*/
		
	private void setBasicParametersPO(BasicParametersPO bapo, BasicParametersDTO basiDto) throws ServiceBizException{
		bapo.setString("PARAM_VALUE", basiDto.getParamValue());
	}

	
	/**
	 * 新增修改基础参数信息
	* @author zhanshiwei
	* @date 2016年8月2日
	* @param basilistdto
	* (non-Javadoc)
	* @see com.yonyou.dms.manage.service.basedata.parameter.BasicComService#modifyBasicParametersListDTO(com.yonyou.dms.manage.domains.DTO.basedata.BasicParametersListDTO)
	*/
		
	@Override
	public void modifyBasicParametersListDTO(BasicParametersListDTO basilistdto) throws ServiceBizException{

		List<BasicParametersDTO> paramList = basilistdto.getParamList();
		for (int i = 0; i < paramList.size(); i++) {
			BasicParametersPO basipo = new BasicParametersPO();
		    basipo = BasicParametersPO.findById(paramList.get(i).getParamId());
			setBasicParametersPO(basipo, paramList.get(i));
			basipo.saveIt();
		}

	}
}
