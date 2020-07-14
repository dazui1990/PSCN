
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.framework
*
* @File name : LoginFrameworkServiceImpl.java
*
* @Author : zhangxc
*
* @Date : 2016年8月1日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年8月1日    zhangxc    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.framework.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.service.LoginFrameworkService;
import com.yonyou.dms.framework.util.FrameworkUtil;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.exception.UtilException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.f4.mvc.annotation.TxnConn;


/**
* 登录相关功能的校验Service
* @author zhangxc
* @date 2016年8月1日
*/
@Service
@TxnConn
public class LoginFrameworkServiceImpl implements LoginFrameworkService {
    // 定义日志接口
    private static final Logger logger = LoggerFactory.getLogger(LoginFrameworkServiceImpl.class);
    
    private static Pattern isContainNumberPattern;
    private static Pattern urlModalPattern;
    
    /**
     *判断是否包含数字
     */
    static{
        isContainNumberPattern = Pattern.compile(".+/rest/\\w+/.*\\d+.*");
        urlModalPattern = Pattern.compile(".*/(.+)/rest/.*");
    }
    /**
     * 确认是否有权限登录
    * @author zhangxc
    * @date 2016年8月1日
    * @param funcId
    * @param requestUrl
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.framework.service.LoginFrameworkService#checkIsCanAccess(java.lang.Long, java.lang.String)
    */

    @Override
    public boolean checkIsCanAccess(Long funcId, String requestUrl,String requestMethod) throws ServiceBizException {
        Matcher matcher = urlModalPattern.matcher(requestUrl);
        String modalName = null;
        if(matcher.find()){
            modalName = matcher.group(1);
        }
        if(modalName ==null){
            throw new UtilException("获取模块名称失败");
        }
        
        String sql = "select ACTION_CODE,ACTION_NAME from tc_menu_action where menu_id = ? and ACTION_METHOD = ? and MODULE = ?";
        List<Object> queryParam = new ArrayList<>();
        queryParam.add(funcId);
        queryParam.add(requestMethod);
        queryParam.add(modalName);
        List<Map> result = DAOUtil.findAll(sql, queryParam,false);
        
        //判断请求的URL 中是否包含数字
        matcher = isContainNumberPattern.matcher(requestUrl);
        
        if(!CommonUtils.isNullOrEmpty(result)){
            for(Map actionMap:result){
                String actionUrl = (String) actionMap.get("ACTION_CODE");
                actionUrl = actionUrl.replaceAll("\\{[^/]+\\}", "[^/]+");
                if(FrameworkUtil.isCanAccess(requestUrl,actionUrl)){
                    return true;
                }
            }
        }
        return false;
    }

}
