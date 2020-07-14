
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : OperateLogServiceImpl.java
 *
 * @Author : yll
 *
 * @Date : 2016年7月14日
 *
----------------------------------------------------------------------------------
 *     Date       Who       Version     Comments
 * 1. 2016年7月14日    yll    1.0
 *
 *
 *
 *
----------------------------------------------------------------------------------
 */

package com.yonyou.dms.common.service.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yonyou.dms.common.domains.DTO.monitor.OperateLogDto;
import com.yonyou.dms.common.domains.PO.monitor.OperateLogPO;
import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.framework.util.FrameworkUtil;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.DateUtil;
import com.yonyou.dms.function.utils.common.StringUtils;

/**
 * 操作日志实现接口
 * @author yll
 * @date 2016年7月14日
 */
@Service
public class OperateLogServiceImpl implements OperateLogService{

	// 定义日志接口
	//private static final Logger logger = LoggerFactory.getLogger(OperateLogServiceImpl.class);

	/**
	 * 日志的查询方法
	 * @author yll
	 * @date 2016年8月7日
	 * @param queryParam
	 * @return
	 * @throws ServiceBizException
	 * (non-Javadoc)
	 * @see com.yonyou.dms.manage.service.basedata.OperateLogService#queryOperateLog(java.util.Map)
	 */
	@Override
	public PageInfoDto queryOperateLog(Map<String, String> queryParam) throws ServiceBizException {
		StringBuilder sqlSb = new StringBuilder("SELECT tol.LOG_ID, tol.DEALER_CODE, tol.OPERATE_DATE, tol.OPERATE_CONTENT, tol.OPERATE_TYPE, te.EMPLOYEE_NAME as OPERATOR, tol.REMARK, tol.RECORD_VERSION, tol.CREATED_BY, tol.CREATED_AT, tol.UPDATED_BY, tol.UPDATED_AT FROM tt_operate_log tol LEFT JOIN tm_employee te ON tol.OPERATOR=te.EMPLOYEE_NO AND te.DEALER_CODE=tol.DEALER_CODE WHERE 1 = 1");
		List<Object> params = new ArrayList<>();
		if(!StringUtils.isNullOrEmpty(queryParam.get("operateDateFrom"))){
			sqlSb.append(" and tol.OPERATE_DATE>=? ");
			params.add(DateUtil.parseDefaultDate(queryParam.get("operateDateFrom")));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("operateDateTo"))){
			sqlSb.append(" and tol.OPERATE_DATE<? ");
			params.add(DateUtil.addOneDay(queryParam.get("operateDateTo")));
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("operateConntent"))){
			sqlSb.append(" and tol.OPERATE_CONTENT like ?");
			params.add("%"+queryParam.get("operateConntent")+"%");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("operator"))){
			sqlSb.append(" and tol.OPERATOR like ?");
			params.add("%"+queryParam.get("operator")+"%");
		}
		if(!StringUtils.isNullOrEmpty(queryParam.get("operateType"))){
			sqlSb.append(" and tol.OPERATE_TYPE=?");
			params.add(Integer.parseInt(queryParam.get("operateType")));
		}

		PageInfoDto pageInfoDto = DAOUtil.pageQuery(sqlSb.toString(),params);
		return pageInfoDto;
	}

	/**
	 * 记录操作日志
	* @author jcsi
	* @date 2016年10月30日
	* @param operateLogPo
	* @param operateLogDto
	* @throws ServiceBizException
	* (non-Javadoc)
	* @see com.yonyou.dms.common.service.monitor.OperateLogService#writeOperateLog(com.yonyou.dms.common.domains.PO.monitor.OperateLogPO, com.yonyou.dms.common.domains.DTO.monitor.OperateLogDto)
	* @deprecated(使用参数传递，不使用对象传递)
	 */
    @Deprecated
    private void writeOperateLog(OperateLogDto operateLogDto) throws ServiceBizException {
        OperateLogPO operateLogPo=new OperateLogPO();
        operateLogPo.setTimestamp("OPERATE_DATE", new Date());
        operateLogPo.setString("OPERATE_CONTENT", operateLogDto.getOperateContent());  //操作内容
        operateLogPo.setInteger("OPERATE_TYPE", operateLogDto.getOperateType()); //模块
        operateLogPo.setString("OPERATOR", FrameworkUtil.getLoginInfo().getEmployeeNo()); 
        operateLogPo.setString("REMARK", operateLogDto.getRemark());
        operateLogPo.saveIt();
    }
    
    /**
     * 记录操作日志
    * @author jcsi
    * @date 2016年11月21日
    * @param operateContent,operateType
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.common.service.monitor.OperateLogService#writeOperateLog(com.yonyou.dms.common.domains.PO.monitor.OperateLogPO, com.yonyou.dms.common.domains.DTO.monitor.OperateLogDto)
     */
    @Override
    public void recordOperateLog(String operateContent, Integer operateType, String... param) {
         OperateLogPO operateLogPo=new OperateLogPO();
         operateLogPo.setTimestamp("OPERATE_DATE", new Date());
         operateLogPo.setString("OPERATE_CONTENT", operateContent);  //操作内容
         operateLogPo.setInteger("OPERATE_TYPE", operateType); //模块
         operateLogPo.setString("OPERATOR", FrameworkUtil.getLoginInfo().getEmployeeNo());
         if(StringUtils.isNullOrEmpty(param)){
             operateLogPo.setString("REMARK", param[0]);
         }
         operateLogPo.saveIt();
    }
}
