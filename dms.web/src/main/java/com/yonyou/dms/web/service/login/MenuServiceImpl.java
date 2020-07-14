/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.web
*
* @File name : MenuServiceImpl.java
*
* @Author : rongzoujie
*
* @Date : 2016年7月6日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月6日    rongzoujie    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.web.service.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.exception.ServiceBizException;

@Service
public class MenuServiceImpl implements MenuService {
	
	
    /**
	
     *查询菜单信息
    * @author zhangxc
    * @date 2016年8月29日
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.web.service.login.MenuService#queryMenu()
     */
    @SuppressWarnings("rawtypes")
	@Override
    public List<Map> queryMenu() throws ServiceBizException {
    	 LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
    	 List<Object> sqlParams = new ArrayList<>();
         Long USER_ID = loginInfo.getUserId();
         String dealerCode = loginInfo.getDealerCode();
         StringBuilder sql = new StringBuilder("SELECT  menu.MENU_ID,menu.RANK,menu.MENU_NAME,menu.MENU_DESC MENU_NAME_ZH,menu.MENU_NAME_EN,menu.MENU_URL,menu.MENU_ICON,menu.PARENT_ID,menu.MENU_TYPE FROM tc_menu menu WHERE (menu.MENU_ID IN ( ");
         sql.append(" SELECT ME.MENU_ID FROM tm_user_menu MI,tc_menu ME WHERE ME.MENU_ID = MI.MENU_ID AND (ME.MENU_TYPE = 1003 OR MI.MENU_ID =1 ) and ME.MENU_STATUS = 10011001 AND USER_ID = ? ");
         sqlParams.add(USER_ID);
         sql.append(" AND DEALER_CODE = ?");
         sqlParams.add(dealerCode);
         sql.append(" UNION SELECT PARENT_ID ID FROM tm_user_menu tum,tc_menu tm WHERE tum.MENU_ID = tm.MENU_ID AND tm.MENU_TYPE=1003 and tm.MENU_STATUS = 10011001 AND tum.USER_ID = ? ");
         sqlParams.add(USER_ID);
         sql.append(" AND tum.DEALER_CODE = ?");
         sqlParams.add(dealerCode);
         sql.append(" UNION SELECT PARENT_ID ID  FROM   tc_menu tm ,( SELECT  PARENT_ID SI FROM tm_user_menu  tum,  tc_menu tm    WHERE  tum.MENU_ID  = tm.MENU_ID AND tm.MENU_TYPE=1003 and tm.MENU_STATUS = 10011001  AND tum.USER_ID = ? ");
         sqlParams.add(USER_ID);
         sql.append(" AND tum.DEALER_CODE = ?");
         sqlParams.add(dealerCode);
         sql.append(" GROUP BY  PARENT_ID )SE  WHERE  SE.SI = tm.MENU_ID AND  tm.MENU_TYPE=1002 GROUP BY tm. PARENT_ID) )  ORDER BY menu.MENU_TYPE ASC,menu.RANK asc");
         
         List<Map> result = Base.findAll(sql.toString(),sqlParams.toArray());
         return result;
    }
    
    /**
     *查询用户操作功能
    * @author zhangxc
    * @date 2016年11月11日
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.web.service.login.MenuService#queryHandles()
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> queryHandles() throws ServiceBizException {
    	
    	 LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
         Long USER_ID = loginInfo.getUserId();
         List<Object> sqlParams = new ArrayList<>();
         StringBuilder sql = new StringBuilder("SELECT  ACTION.ACTION_CODE code,MENU.DEALER_CODE DEALER_CODE  FROM tm_user_menu MENU ,tc_menu_action ACTION ,tm_user_menu_action TUM WHERE MENU.USER_MENU_ID = TUM.USER_MENU_ID AND MENU.USER_ID=?");
         sqlParams.add(USER_ID);
         sql.append( "  AND TUM.MENU_CURING_ID= ACTION.MENU_CURING_ID AND ACTION.ACTION_CTL!=10041002 GROUP BY ACTION.ACTION_CODE,MENU.DEALER_CODE ");
    	 List<Map> result = DAOUtil.findAll(sql.toString(),sqlParams);
    	 for(int i =0 ; i<result.size();i++){
    		 String handle= (String) result.get(i).get("code");
    		 handle = handle.replaceAll("\\{[^/]+\\}", ".+");
    		 result.get(i).put("code", handle);  
    	   }
		return result;
	}

    /**
     * 确认是否可以登录
    * @author zhangxc
    * @date 2016年8月1日
    * @param funcId
    * @param requestUrl
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.web.service.login.MenuService#checkIsCanAccess(java.lang.Long, java.lang.String)
     */
    @Override
    public boolean checkIsCanAccess(Long funcId, String requestUrl) throws ServiceBizException {
      
        return false;
    }

    /**
     * 加载系统参数
    * @author zhangxc
    * @date 2016年11月24日
    * @return
    * @throws ServiceBizException
    * (non-Javadoc)
    * @see com.yonyou.dms.web.service.login.MenuService#querySystemParams()
     */
    @SuppressWarnings("rawtypes")
	@Override
	public List<Map> querySystemParams() throws ServiceBizException {
        StringBuilder sql = new StringBuilder("SELECT DEALER_CODE ,PARAM_TYPE paramType,PARAM_CODE paramCode FROM tc_system_param"); 
        List<Map> result = DAOUtil.findAll(sql.toString(), null);
		return result;
	}

	
}
