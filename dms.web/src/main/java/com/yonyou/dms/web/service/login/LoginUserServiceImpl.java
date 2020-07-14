
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.common
 *
 * @File name : SystemUserServiceImpl.java
 *
 * @Author : yll
 *
 * @Date : 2016年10月8日
 *
----------------------------------------------------------------------------------
 *     Date       Who       Version     Comments
 * 1. 2016年10月8日    yll    1.0
 *
 *
 *
 *
----------------------------------------------------------------------------------
 */

package com.yonyou.dms.web.service.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.domain.UserAccessInfoDto;
import com.yonyou.dms.framework.interceptors.ExceptionControllerAdvice;
import com.yonyou.dms.framework.util.acl.AccessUrlUtils;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.web.controller.login.MD5Encrypt;

/**
 * 
 * @author yll
 * @date 2016年10月8日
 */
@Service
public class LoginUserServiceImpl implements LoginUserService{
	
    private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
	/**
	 * 登录校验
	 * @author yll
	 * @date 2016年10月10日
	 * @param dealerCode
	 * @param userCode
	 * (non-Javadoc)
	 * @see com.yonyou.dms.web.service.login.LoginUserService#logCheck(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes" })
    @Override
	public Map logCheck(String dealerCode,String userCode,String password) {
		//判断用户是否存在
		Map information=new HashMap();
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT");
		sql.append(" tu.USER_STATUS STA,");
		sql.append(" tu.EMPLOYEE_ID,");
		sql.append(" te.EMPLOYEE_NO,");
		sql.append(" tu.USER_ID,");
		sql.append(" tu.USER_CODE,");
		sql.append(" tu.`PASSWORD`,");
		sql.append(" te.EMPLOYEE_NAME,");
		sql.append(" te.GENDER,");
		sql.append(" te.POSITION_CODE,");
		sql.append("   (CASE WHEN EXISTS(SELECT 1 FROM tm_employee_post tep WHERE tep.POST_CODE = 96091001 AND tu.EMPLOYEE_ID = tep.EMPLOYEE_ID) THEN 1 ELSE 0 END) AS IS_SUPERVISOR,");
		sql.append("   (CASE WHEN EXISTS(SELECT 1 FROM tm_employee_post tep WHERE tep.POST_CODE = 96091002 AND tu.EMPLOYEE_ID = tep.EMPLOYEE_ID) THEN 1 ELSE 0 END) AS IS_SERIES_CHIEF,");
		sql.append("   (CASE WHEN EXISTS(SELECT 1 FROM tm_employee_post tep WHERE tep.POST_CODE = 96091003 AND tu.EMPLOYEE_ID = tep.EMPLOYEE_ID) THEN 1 ELSE 0 END) AS IS_SECTION_CHIEF,");
		sql.append("   (CASE WHEN EXISTS(SELECT 1 FROM tm_employee_post tep WHERE tep.POST_CODE = 96091004 AND tu.EMPLOYEE_ID = tep.EMPLOYEE_ID) THEN 1 ELSE 0 END) AS IS_MINISTER");
		sql.append(" FROM");
		sql.append(" tm_user tu");
		sql.append(" INNER JOIN tm_employee te ON te.EMPLOYEE_ID = tu.EMPLOYEE_ID");
		sql.append(" WHERE 1=1");
		List<Object> queryParam = new ArrayList<>();
		sql.append("  AND tu.USER_CODE  = ?");
		queryParam.add(userCode);
		
		List<Map> listMap=DAOUtil.findAll(sql.toString(), queryParam);
		
		String newPassword = MD5Encrypt.MD5Encode(password);//编码后的密码
		//如果查询通过
		if(!CommonUtils.isNullOrEmpty(listMap)){
		    Map userInfo = listMap.get(0);
			// 密码验证
			 String passwordMD5=(String) userInfo.get("PASSWORD");//密码
			 Object USER_STATUS = userInfo.get("STA");//停用启用

			if(newPassword!=passwordMD5&&!newPassword.equals(passwordMD5)){
			   logger.info("登录密码错误"+passwordMD5+"-----"+newPassword);
			   throw new ServiceBizException("密码不正确");
			 }else{
			   logger.info("登录密码正确"+passwordMD5+"-----"+newPassword);
			 }
			if(USER_STATUS.toString().equals("10011002")){
				logger.info("账号"+userCode+"已停用");
				throw new ServiceBizException("账号已停用");
			}
		    //获取用户信息实体类
		    LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);  
		    
		    //获取用户url权限实体类
		    UserAccessInfoDto powerUrl = ApplicationContextHelper.getBeanByType(UserAccessInfoDto.class);
	
		    //用户id
	    	Long userId = userInfo.get("USER_ID") == null?null:Long.valueOf(userInfo.get("USER_ID")+"");
	    	if(userId != null) {
	    	    loginInfo.setUserId(userId);
	    	}
	    	
	    	//员工id
	    	Long employeeId = userInfo.get("EMPLOYEE_ID") == null?null:Long.valueOf(userInfo.get("EMPLOYEE_ID")+"");
	    	if(employeeId != null) {
	    	    loginInfo.setEmployeeId(employeeId);
	    	}
	    	
	    	//员工编号
	    	String employeeNo = userInfo.get("EMPLOYEE_NO") == null?"":userInfo.get("EMPLOYEE_NO")+"";
	    	if(!StringUtils.isNullOrEmpty(employeeNo)){
	    	    loginInfo.setEmployeeNo(employeeNo);
			}
	    	
	    	//员工姓名
	    	String employeeName = userInfo.get("EMPLOYEE_NAME") == null?"":userInfo.get("EMPLOYEE_NAME")+"";
	    	if(!StringUtils.isNullOrEmpty(employeeName)){
	    	    loginInfo.setUserName(employeeName);
			}
	    	
	    	//员工性别
	    	Integer gender = userInfo.get("GENDER") == null?null:Integer.valueOf(userInfo.get("GENDER")+"");
	    	if(gender != null) {
	    	    loginInfo.setGender(gender);
	    	}
	    		
	    	//是否为督导
	    	Integer isSupervisor = userInfo.get("IS_SUPERVISOR") == null?null:Integer.valueOf(userInfo.get("IS_SUPERVISOR")+"");
	    	if(isSupervisor != null) {
	    	    loginInfo.setIsSupervisor(isSupervisor);
	    	}
	    	//是否为系长
	    	Integer isSeriesChief = userInfo.get("IS_SERIES_CHIEF") == null?null:Integer.valueOf(userInfo.get("IS_SERIES_CHIEF")+"");
	    	if(isSeriesChief != null) {
	    	    loginInfo.setIsSeriesChief(isSeriesChief);
	    	}
	    	//是否为科长
	    	Integer isSectionChief = userInfo.get("IS_SECTION_CHIEF") == null?null:Integer.valueOf(userInfo.get("IS_SECTION_CHIEF")+"");
	    	if(isSectionChief != null) {
	    	    loginInfo.setIsSectionChief(isSectionChief);
	    	}
	    	//是否为部长
	    	Integer isMinister = userInfo.get("IS_MINISTER") == null?null:Integer.valueOf(userInfo.get("IS_MINISTER")+"");
	    	if(isMinister != null) {
	    	    loginInfo.setIsMinister(isMinister);
	    	}
	    	
	    	//最高职位(同是督导和系长 则为系长)
	    	if(isMinister == 1) {
	    		loginInfo.setPositionCode("96091004");
	    	}else if(isSectionChief == 1) {
	    		loginInfo.setPositionCode("96091003");
	    	}else if(isSeriesChief == 1) {
	    		loginInfo.setPositionCode("96091002");
	    	}else if(isSupervisor == 1) {
	    		loginInfo.setPositionCode("96091001");
	    	}
	    	
	    	if(employeeId != null) {
	    		//如果是督导 查询大区信息
	    		StringBuilder sql1 = new StringBuilder("SELECT MAX(V.ORG_ID) AS ORG_ID FROM vm_dealer_org_dudao_province V WHERE V.`STATUS` = 10011001 AND V.EMPLOYEE_ID ="+employeeId);
	    		Map map = DAOUtil.findFirst(sql1.toString(), null);
	    		if(map.get("ORG_ID") != null) {
	    			loginInfo.setOrgId(Long.valueOf(map.get("ORG_ID")+""));
	    		}
	    	}
	    	
/*	    	String dealerName=(String) userInfo.get("DEALER_NAME");//经销商名字
	    	if(!StringUtils.isNullOrEmpty(dealerName)){
	    	    loginInfo.setDealerName(dealerName);
			}
	    	String dealerShortName=(String) userInfo.get("DEALER_SHORTNAME");//经销商短名
	    	if(!StringUtils.isNullOrEmpty(dealerShortName)){
	    	    loginInfo.setDealerShortName(dealerShortName);
			}
	    	Object dealerID=userInfo.get("DEALER_ID");//经销商id
	    	if(!StringUtils.isNullOrEmpty(dealerID)){
	    	    loginInfo.setDealerId(Long.parseLong(dealerID.toString()));
			}
	        // 组织orgCode
	    	String orgCode=(String) userInfo.get("ORG_CODE");//经销商id
            if (!StringUtils.isNullOrEmpty(orgCode)) {
                loginInfo.setOrgCode(orgCode);
            }
            // 组织orgName
            String orgName=(String) userInfo.get("ORG_NAME");//经销商id
            if (!StringUtils.isNullOrEmpty(orgName)) {
                loginInfo.setOrgName(orgName);
            }
            // 组织orgName
            Object organizationId=userInfo.get("ORGDEPT_ID");//经销商id
            if (!StringUtils.isNullOrEmpty(organizationId)) {
                loginInfo.setOrgId(Long.parseLong(organizationId.toString()));
            }*/
            //设置经销商信息及账号信息
            loginInfo.setDealerCode(dealerCode);
            loginInfo.setUserAccount(userCode);
                
             Long userIdPower = Long.parseLong(userId.toString());
             
            //Map<String,Object> powerMap = getDataPower(userIdPower,dealerCode);
            //获取该用户整车仓库权限
            //loginInfo.setCarLoadDepot((String) powerMap.get("carLoadDepot")); 
            //获取该用户配件仓库权限
            //loginInfo.setPurchaseDepot((String) powerMap.get("purchaseDepot"));
            //获取该用户维修数据权限
            //loginInfo.setRepair((Map) powerMap.get("repair"));
            //获取该用户配件数据权限
            //loginInfo.setPurchase((Map) powerMap.get("purchase"));
            //获取优惠模式数据权限
            //loginInfo.setPreferentialMode( (List) powerMap.get("preferentialMode"));
            //2 获取该用户菜单和按钮权限
            powerUrl.setUserResouces(getActionUrl(userIdPower,dealerCode) );
            
        }else{
        	throw new ServiceBizException("账号不存在");
        }
		return information;
	}
	
	
	/**
	 *获取到用户的数据权限的url列表
	 * @author yll
	 * @date 2016年11月17日
	 * @param dealerCode
	 * @param userCode
	 * @see com.yonyou.dms.web.service.login.LoginUserService#getDataPower(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,Object> getDataPower(Long USER_ID,String dealerCode){
		List<Object> sqlParams = new ArrayList<>();
		StringBuilder  powerSql = new  StringBuilder("SELECT  CTRL.CTRL_CODE  code , type ,DEALER_CODE FROM  tm_user_ctrl CTRL WHERE CTRL.USER_ID =? ");
		sqlParams.add(USER_ID);
		List<Map> PowerList = DAOUtil.findAll(powerSql.toString(),sqlParams);
		Iterator<Map> powerIterator = PowerList.iterator();
		Map<String,Object> powerString = new HashMap<>();
		Map<String,List> reusltPower = new HashMap<>();
		//五种数据权限的TYPE数组
//		String[] dataUrlsList = {DictCodeConstants.VEHICLE_WAREHOUSE+"",DictCodeConstants.ACCESSORIES_WAREHOUSE+"",DictCodeConstants.MAINTAIN+"",DictCodeConstants.ACCESSORIES+"",DictCodeConstants.FAVORABLE_MODELS+""};	
//		for(String urlsList:dataUrlsList){                      
//			reusltPower.put(urlsList, new ArrayList());
//		}
//		while(powerIterator.hasNext()){
//			Map power = powerIterator.next();
//			String powerType =  power.get("type").toString();
//			for(String string:dataUrlsList ){
//				if(string.equals(powerType)){
//					reusltPower.get(powerType).add(power.get("code"));
//					break;
//				}
//			}
//		}
//		StringBuilder sb = new StringBuilder("SELECT CODE_ID FROM tc_code WHERE TYPE=?");
//		//获取配件参数权限配置下的所有类型
//		List<Map> pl = Base.findAll(sb.toString(), DictCodeConstants.ACCESSORIES);
//		//获取维修参数权限配置下的所有类型
//		List<Map> rl = Base.findAll(sb.toString(), DictCodeConstants.MAINTAIN);
//		powerString.put("carLoadDepot", AccessUrlUtils.iteratorToString(reusltPower.get(dataUrlsList[0])));
//		powerString.put("purchaseDepot", AccessUrlUtils.iteratorToString(reusltPower.get(dataUrlsList[1])));
//		powerString.put("repair", AccessUrlUtils.iteratorToMap(reusltPower.get(dataUrlsList[2]),rl));
//		powerString.put("purchase", AccessUrlUtils.iteratorToMap(reusltPower.get(dataUrlsList[3]),pl));
//		powerString.put("preferentialMode",reusltPower.get(dataUrlsList[4]));
		return powerString;
	}

	/**
	 *获取该用户的url权限
	 * @author yll
	 * @date 2016年11月17日
	 * @param dealerCode
	 * @param userCode
	 * @see com.yonyou.dms.web.service.login.LoginUserService#getActionUrl(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
    public String[]  getActionUrl(Long USER_ID ,String dealerCode){
		    List<Object> sqlParams = new ArrayList<>();
			StringBuilder actionSql = new StringBuilder("SELECT ACTION_METHOD method , MODULE model ,ACTION_CODE code ,MENU_CURING_ID FROM tc_menu_action  WHERE ACTION_CTL = 10041002 AND MENU_ID IN (");
			actionSql.append("SELECT MENU_ID  FROM  tm_user_menu WHERE  USER_ID =?");
			sqlParams.add(USER_ID);
			actionSql.append(" AND DEALER_CODE=?");
			sqlParams.add(dealerCode);
			actionSql.append(" ) ");
			actionSql.append(" UNION SELECT ACTION_METHOD method ,  ACTION.MODULE model,ACTION.ACTION_CODE code,ACTION.MENU_CURING_ID MENU_CURING_ID FROM tm_user_menu MENU ,tc_menu_action ACTION ,tm_user_menu_action TUM  ");
			actionSql.append("WHERE MENU.USER_MENU_ID = TUM.USER_MENU_ID AND MENU.USER_ID=?");
			sqlParams.add(USER_ID);
			actionSql.append(" AND DEALER_CODE=?");
			sqlParams.add(dealerCode);
			actionSql.append("  AND TUM.MENU_CURING_ID= ACTION.MENU_CURING_ID ");
			List<Map> actionUrlList = Base.findAll(actionSql.toString(),sqlParams.toArray());
			return AccessUrlUtils.iteratorToArray(actionUrlList);
	}
}
