
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : OperateLogController.java
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

package com.yonyou.dms.manage.controller.monitor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.dms.common.service.monitor.OperateLogService;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 操作日志的控制类
 * @author yll
 * @date 2016年7月14日
 */
@Controller
@TxnConn
@RequestMapping("/monitor")
public class OperateLogController extends BaseController{

	// 定义日志接口
	private static final Logger logger = LoggerFactory.getLogger(OperateLogController.class);
	@Autowired
	private OperateLogService operateLogService;


	/**
	 * 
	 * 根据查询条件返回对应的操作日志数据
	 * @author yll
	 * @date 2016年7月14日
	 * @param queryParam
	 * @return
	 */
	@RequestMapping( value="/operatelog" ,method = RequestMethod.GET)
	@ResponseBody
	public PageInfoDto queryOperateLog(@RequestParam Map<String, String> queryParam) {
		PageInfoDto pageInfoDto = operateLogService.queryOperateLog(queryParam);
		return pageInfoDto;
	}

}
