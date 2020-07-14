
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : UserCtrlServiceImpl.java
*
* @Author : yll
*
* @Date : 2016年8月19日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年8月19日    yll    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.manage.service.basedata.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.manage.domains.PO.basedata.user.UserCtrlPO;

/**
* 用户受控权限实现类
* @author yll
* @date 2016年8月19日
*/
@Service
public class UserCtrlServiceImpl implements UserCtrlService{

	/**
	 * 新增用户受控权限
	* @author yll
	* @date 2016年8月31日
	* @param UserID
	* @param ctrlCode
	* @return
	* @throws ServiceBizException
	* (non-Javadoc)
	* @see com.yonyou.dms.manage.service.basedata.user.UserCtrlService#addUserCtrl(java.lang.String, java.lang.String)
	 */
	@Override
	public Long addUserCtrl(String UserID, String ctrlCode,Integer type) throws ServiceBizException {
		if(StringUtils.isNullOrEmpty(UserID)){
	        throw new ServiceBizException("用户id不能为空");
	    }
		if(StringUtils.isNullOrEmpty(ctrlCode)){
	        throw new ServiceBizException("控制代码不能为空");
	    }
		
		UserCtrlPO userCtrlPO=new UserCtrlPO();
		userCtrlPO.setLong("USER_ID",Long.parseLong( UserID));
		userCtrlPO.setString("CTRL_CODE", ctrlCode);
		userCtrlPO.setInteger("TYPE", type);
		userCtrlPO.saveIt();
		return  userCtrlPO.getLongId();
	}

	/**
	 * 根据用户id删除菜单信息
	* @author yll
	* @date 2016年8月31日
	* @param UserID
	* @throws ServiceBizException
	* (non-Javadoc)
	* @see com.yonyou.dms.manage.service.basedata.user.UserCtrlService#deleteMenuByUserId(java.lang.String)
	 */
	@Override
	public void deleteMenuByUserId(String UserID) throws ServiceBizException {
		StringBuilder sqlSb = new StringBuilder("select USER_CTRL_ID,DEALER_CODE  from tm_user_CTRL where USER_ID='");
		sqlSb.append(Long.parseLong(UserID));
		sqlSb.append("'");

		List<Object> params = new ArrayList<>();
		List<Map> list=DAOUtil.findAll(sqlSb.toString(),params);
		for(int i=0;i<list.size();i++){
			Object ctrlId=list.get(i).get("USER_CTRL_ID");
			UserCtrlPO aa=UserCtrlPO.findById(ctrlId);
			aa.delete();
		}
	}

	/**
	 * 加载用户受控权限
	* @author yll
	* @date 2016年8月31日
	* @param roletId
	* @return
	* @throws ServiceBizException
	* (non-Javadoc)
	* @see com.yonyou.dms.manage.service.basedata.user.UserCtrlService#queryMenuCtrl(java.lang.String)
	 */
	@Override
	public Map<String, String> queryMenuCtrl(String userId) throws ServiceBizException {
		 String maintain=null;
		 String accessories=null;
		 String vehicleWarehouse=null;//整车仓库
		 String accessoriesWarehouse=null;//配件仓库
		 String favorableModels=null;
		StringBuilder sqlSb = new StringBuilder(
				"select USER_CTRL_ID,DEALER_CODE,USER_ID,CTRL_CODE,TYPE from tm_user_ctrl where 1=1");
		List<Object> params = new ArrayList<>();
		if(!StringUtils.isNullOrEmpty(userId)){
			String[] idList=userId.split(",");
			sqlSb.append(" and USER_ID in (");
			for(int i=0;i<idList.length-1;i++){
				sqlSb.append("?,");
			}
			sqlSb.append("?");
			sqlSb.append(")");
			for(int i=0;i<idList.length;i++){
				params.add(Long.parseLong(idList[i]));
			}
		//	sqlSb.append(" and USER_ID in ("+userId+")");

		}
		List<Map> list=DAOUtil.findAll(sqlSb.toString(),params);
	    Map<String,String> basicresult = new HashMap<>();
	    for(int i=0;i<list.size();i++){
			String ctrlCode=(String) list.get(i).get("CTRL_CODE");
			Integer type=(Integer) list.get(i).get("TYPE");
			//String tcCode=ctrlCode.substring(0, 4);
//			if(type.equals(DictCodeConstants.MAINTAIN)){
//				if(maintain==null){
//					maintain=ctrlCode;
//				}else{
//					maintain+=",";
//					maintain+=ctrlCode;
//				}
//			}
//			if(type.equals(DictCodeConstants.ACCESSORIES)){
//				
//				if(accessories==null){
//					accessories=ctrlCode;
//				}else{
//					accessories+=",";
//					accessories+=ctrlCode;
//				}
//			}
//			if(type.equals(DictCodeConstants.VEHICLE_WAREHOUSE)){
//				if(vehicleWarehouse==null){
//					vehicleWarehouse=ctrlCode;
//				}else{
//					vehicleWarehouse+=",";
//					vehicleWarehouse+=ctrlCode;
//				}
//			}
//			if(type.equals(DictCodeConstants.ACCESSORIES_WAREHOUSE)){
//				if(accessoriesWarehouse==null){
//					accessoriesWarehouse=ctrlCode;
//				}else{
//					accessoriesWarehouse+=",";
//					accessoriesWarehouse+=ctrlCode;
//				}
//			}
//			if(type.equals(DictCodeConstants.FAVORABLE_MODELS)){
//				if(favorableModels==null){
//					favorableModels=ctrlCode;
//				}else{
//					favorableModels+=",";
//					favorableModels+=ctrlCode;
//				}
//			}

		}

		
		basicresult.put("maintain", maintain);
		basicresult.put("accessories", accessories);
		basicresult.put("vehicleWarehouse", vehicleWarehouse);
		basicresult.put("accessoriesWarehouse", accessoriesWarehouse);
		basicresult.put("favorableModels", favorableModels);
		
		return basicresult;
	}

}
