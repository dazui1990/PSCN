
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : UserManageController.java
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

package com.yonyou.dms.manage.controller.basedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.domains.PO.UserPO;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.function.utils.security.MD5Util;
import com.yonyou.dms.manage.constant.MD5Encrypt;
import com.yonyou.dms.manage.domains.DTO.basedata.CommonTreeDto;
import com.yonyou.dms.manage.domains.DTO.basedata.CommonTreeStateDto;
import com.yonyou.dms.manage.domains.DTO.basedata.EmployeeDto;
import com.yonyou.dms.manage.domains.DTO.basedata.role.RolePDto;
import com.yonyou.dms.manage.domains.DTO.basedata.user.UserDto;
import com.yonyou.dms.manage.service.basedata.employee.EmployeeService;
import com.yonyou.dms.manage.service.basedata.user.UserCtrlService;
import com.yonyou.dms.manage.service.basedata.user.UserMenuActionService;
import com.yonyou.dms.manage.service.basedata.user.UserMenuRangeService;
import com.yonyou.dms.manage.service.basedata.user.UserMenuService;
import com.yonyou.dms.manage.service.basedata.user.UserService;
import com.yonyou.f4.mvc.annotation.TxnConn;

/**
 * 用户权限管理控制类
 * @author yll
 * @date 2016年8月19日
 */
@Controller
@TxnConn
@RequestMapping("/basedata/users")
public class UserManageController {

	// 定义日志接口
	private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private UserMenuService userMenuService;
	@Autowired
	private UserCtrlService userCtrlService;
	@Autowired
	private UserMenuActionService userMenuActionService;
	@Autowired
	private UserMenuRangeService userMenuRangeService;
	@Autowired
	private EmployeeService employeeService;

	/**
	 * 
	 * 查询登录用户信息
	 * @author yll
	 * @date 2016年8月24日
	 * @return
	 */
	@RequestMapping(value="/loginuser",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> findByEmployeeId(){
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		String userCode=loginInfo.getUserAccount();
		Long id=userService.getEmployeeIdByUserCode(userCode);
		Map<String, Object> map=employeeService.findById(id);
		//查找员工角色表  转成字符串用逗号分隔  插入map中
		List<Map>  roles=employeeService.findEmpRolesByEmpId(id);
		String rolesStr="";        
		rolesStr=CommonUtils.listMapToString(roles,",");
		//Map<String, Object> map= empo.toMap();
		map.put("roles", rolesStr);	         
		Map<String, Object> user=employeeService.findUserByEmployeeId(id);
		if(user!=null){
			map.putAll(user);
		}
		return map;
	}
	/**
	 * 
	 * 更新登录用户信息
	 * @author yll
	 * @date 2016年8月24日
	 * @param id
	 * @param empDto
	 * @param uriCB
	 * @return
	 */
	@RequestMapping(value="/loginuser",method=RequestMethod.PUT)
	public ResponseEntity<EmployeeDto> loginedit(@RequestBody EmployeeDto empDto,UriComponentsBuilder uriCB){
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		String userCode=loginInfo.getUserAccount();
		Long id=userService.getEmployeeIdByUserCode(userCode);
		employeeService.updateEmpById(id, empDto);
		MultiValueMap<String,String> headers = new HttpHeaders();  
		headers.set("Location", uriCB.path("/basedata/users/loginuser").buildAndExpand(id).toUriString());  
		return new ResponseEntity<>(headers, HttpStatus.CREATED);  
	}
	/**
	 * 
	 * 快速修改密码
	 * @author yll
	 * @date 2016年8月24日
	 * @param brandDto
	 * @param uriCB
	 * @return
	 */
	@RequestMapping(value = "/editpassword", method = RequestMethod.PUT)
	public ResponseEntity<UserDto> editpassword(@RequestBody UserDto userDto,UriComponentsBuilder uriCB) {

		String userCode=userDto.getUserCode();
		String password=userDto.getPassword();
		String newPassword=userDto.getNewPassword();
		String passwordMD5=userService.getPasswordByUserCode(userCode);
		String oldpassword=MD5Encrypt.MD5Encode(password);
		//boolean validation=MD5Util.validPassword(password, passwordMD5);
		if(oldpassword.equals(passwordMD5)){
			String newPasswordMD5=MD5Encrypt.MD5Encode(newPassword);
			Long id=userService.getEmployeeIdByUserCode(userCode);
			userDto.setPassword(newPasswordMD5);
			userDto.setEmployeeId(id);
			userDto.setUserStatus(DictCodeConstants.STATUS_QY);//启用状态
			Long userId=userService.getUserIDByEmployeeId(id);
			userService.modifyUser(userId, userDto);
			MultiValueMap<String,String> headers = new HttpHeaders();  
			headers.set("Location", uriCB.path("/basedata/users/editpassword").buildAndExpand(id).toUriString());  
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		}else{
			throw new ServiceBizException("原密码不正确");
			//return null;
		}
	}

	/**
	 * 
	* 管理员修改密码
	* @author yll
	* @date 2016年10月14日
	* @param userDto
	* @param uriCB
	* @return
	 */
	@RequestMapping(value = "/supereditpassword", method = RequestMethod.PUT)
	public ResponseEntity<UserDto> supereditpassword(@RequestBody UserDto userDto,UriComponentsBuilder uriCB) {
		Long id=userDto.getEmployeeId();
		String userCode=userDto.getUserCode();
		String newPassword=userDto.getNewPassword();
		String newPasswordMD5=MD5Encrypt.MD5Encode(newPassword);//新的加密方式
		userDto.setPassword(newPasswordMD5);
		userDto.setUserCode(userCode);
		userDto.setUserStatus(DictCodeConstants.STATUS_QY);//启用状态??
		Long userId=userService.getUserIDByEmployeeId(id);
		userService.modifyUser(userId, userDto);
		MultiValueMap<String,String> headers = new HttpHeaders();  
		headers.set("Location", uriCB.path("/basedata/users/supereditpassword").buildAndExpand(id).toUriString());  
		return new ResponseEntity<>(headers, HttpStatus.CREATED);

	}
	/**
	 * 
	 * 角色对应的权限的新增及修改
	 * @author yll
	 * @date 2016年8月12日
	 * @param RolePDto
	 * @param uriCB
	 * @return
	 */
	@RequestMapping(value = "/permission", method = RequestMethod.PUT)
	public ResponseEntity<RolePDto> ModifyOrg(@RequestBody RolePDto RolePDto,UriComponentsBuilder uriCB) {
		String roleId=RolePDto.getNowRole();
		Long employeeId=RolePDto.getEmployeeId();//员工id，用来查下面是否有账户
		List<String> employeeRoles=RolePDto.getEmployeeRoles();//员工职位信息
		Long userId=userService.getUserIDByEmployeeId(employeeId);//账户id
		String userCode=RolePDto.getUserCode();//账号
		String employeeName=RolePDto.getEmployeeName();//姓名
		String password=RolePDto.getPassword();//密码
		String userStatus=RolePDto.getUserStatus();//有效无效
		String employeePosition=RolePDto.getEmployeePosition();//岗位
		String nowTree=RolePDto.getNowTree();
		String treeMenuAction=RolePDto.getTreeMenuAction();
		String treeMenuRange=RolePDto.getTreeMenuRange();/////
		List<String> Maintain =RolePDto.getMaintain();
		List<String> accessories=RolePDto.getAccessories();
		List<String> vehicleWarehouse=RolePDto.getVehicleWarehouse();//整车仓库
		List<String> accessoriesWarehouse=RolePDto.getAccessoriesWarehouse();//配件仓库
		List<String> favorableModels=RolePDto.getFavorableModels();

		if(StringUtils.isNullOrEmpty(nowTree)){//菜单权限不能为空
			throw new ServiceBizException("菜单权限不能为空");
		}
		//如果员工下面没有账户，新建一个账户
		if(StringUtils.isNullOrEmpty(userId)){
			employeeService.updateEmpById(employeeId, employeeRoles);
			
			//更新用户职位
			if(employeePosition!=null&&!employeePosition.equals("")){
				employeeService.updateEmpPositionById(employeeId,employeePosition);
			}
			
			/////新建账户的默认密码111111
			String passwordMD5=MD5Encrypt.MD5Encode("111111");//新的加密方式
			UserDto userDto=new UserDto();
			userDto.setEmployeeId(employeeId);
			userDto.setUserCode(userCode);
			userDto.setPassword(passwordMD5);
			userDto.setUserStatus(Integer.parseInt(userStatus));
			Long userIdtwice=userService.addUser(userDto);
			String useridtwice=Long.toString(userIdtwice);//类型转换
			//如果改变checkbox才做操作
			userPer(nowTree,userIdtwice,Maintain,accessories,vehicleWarehouse,accessoriesWarehouse,favorableModels, treeMenuAction, useridtwice,treeMenuRange);
		}
		else{//如果账户已有
			//employeeService.updateEmpById(employeeId, employeeRoles);
			if(roleId!=null&&!roleId.equals("")){
				employeeService.updateEmpByRoleId(employeeId,roleId);
			}
			//更新用户职位
			if(employeePosition!=null&&!employeePosition.equals("")){
				employeeService.updateEmpPositionById(employeeId,employeePosition);
			}
			/////
			UserDto userDto=new UserDto();
			userDto.setEmployeeId(employeeId);
			userDto.setUserCode(userCode);
			userDto.setDealerCode("2400001");
			userDto.setUserStatus(Integer.parseInt(userStatus));
			//userService.modifyUser(userId, userDto);
			//更新用户信息
			UserPO.update("USER_STATUS=?","USER_CODE=?",  userStatus,userCode);
			
			String userid=Long.toString(userId);//类型转换
			//如果改变checkbox才做操作
			userPer(nowTree,userId,Maintain,accessories,vehicleWarehouse,accessoriesWarehouse,favorableModels, treeMenuAction, userid, treeMenuRange);
		}

		MultiValueMap<String,String> headers = new HttpHeaders(); 
		return new ResponseEntity<>(headers, HttpStatus.CREATED);  
	}

	public void userPer(String nowTree,Long userId,List<String> Maintain,List<String> accessories,List<String> vehicleWarehouse,List<String> accessoriesWarehouse,List<String> favorableModels,String treeMenuAction,String userid,String treeMenuRange){
		//如果改变checkbox才做操作
		if(!StringUtils.isNullOrEmpty(nowTree)){
			userMenuService.deleteMenuByUserId(userid);
		}
		String[] sourceStrArray=nowTree.split(",");
		for(int i=0;i<sourceStrArray.length;i++)
		{
			userMenuService.addUserMenu(userid, sourceStrArray[i]);
		}
		userCtrlService.deleteMenuByUserId(userid);
		
//		if(!CommonUtils.isNullOrEmpty(Maintain)){
//		for(int i=0;i<Maintain.size();i++){
//			userCtrlService.addUserCtrl(userid, Maintain.get(i),DictCodeConstants.MAINTAIN);
//		}
//		}
//		if(!CommonUtils.isNullOrEmpty(accessories)){
//		for(int i=0;i<accessories.size();i++){
//			userCtrlService.addUserCtrl(userid, accessories.get(i),DictCodeConstants.ACCESSORIES);
//		}
//		}
//		if(!CommonUtils.isNullOrEmpty(vehicleWarehouse)){
//		for(int i=0;i<vehicleWarehouse.size();i++){
//			userCtrlService.addUserCtrl(userid, vehicleWarehouse.get(i),DictCodeConstants.VEHICLE_WAREHOUSE);
//		}
//		}
//		if(!CommonUtils.isNullOrEmpty(accessoriesWarehouse)){
//		for(int i=0;i<accessoriesWarehouse.size();i++){
//			userCtrlService.addUserCtrl(userid, accessoriesWarehouse.get(i),DictCodeConstants.ACCESSORIES_WAREHOUSE);
//		}
//		}
//		if(!CommonUtils.isNullOrEmpty(favorableModels)){
//		for(int i=0;i<favorableModels.size();i++){
//			userCtrlService.addUserCtrl(userid, favorableModels.get(i),DictCodeConstants.FAVORABLE_MODELS);
//		}
//		}

		if(treeMenuAction!=null){
			//treeMenuAction
			String[] oneAction=treeMenuAction.split(";");
			if(oneAction.length>0){
				for(int i=0;i<oneAction.length;i++)
				{
					if(!StringUtils.isNullOrEmpty(oneAction[i])){
						userMenuActionService.addOneAction(oneAction[i],userId+"");
					}
				} 
			}
		}
		//treeMenuRange
		if(treeMenuRange!=null){
			String[] oneRange=treeMenuRange.split(";");
			if(oneRange.length>0){
				for(int i=0;i<oneRange.length;i++)
				{
					if(!StringUtils.isNullOrEmpty(oneRange[i])){
						userMenuRangeService.addOneRange(oneRange[i],userId+"");
					}
				} 
			}
		}
	}

	/**
	 * 
	 * 加载角色权限信息
	 * @author yll
	 * @date 2016年8月12日
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/userData", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object>  userData(String employeeId) {
		Map<String,Object> userData=new HashMap<>();
		Long userId=userService.getUserIDByEmployeeId(Long.parseLong(employeeId));
		Integer employeeType=userService.getUserTypeByEmployeeId(Long.parseLong(employeeId));
		if(userId==null){
			List<Map> list = userService.queryMenu();
			List<CommonTreeDto> orgList = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				CommonTreeDto orgTreeOrg = new CommonTreeDto();
				orgTreeOrg.setId((String)list.get(i).get("MENU_ID").toString());
				String parent = (String)list.get(i).get("PARENT_ID").toString();
				if("0".equals(parent)){
					parent = "#";
				}
				orgTreeOrg.setParent(parent);
				orgTreeOrg.setText((String)list.get(i).get("MENU_NAME").toString());
				orgList.add(orgTreeOrg);
			}
			Map<String, String> checkbox=new HashMap<String, String>();
			String [] treeMenuActionString= new String [0] ;
			String [] treeMenuRangeString= new String [0] ;
			userData.put("treejson", orgList);
			userData.put("checkbox", checkbox);
			userData.put("treeMenuAction", treeMenuActionString);
			userData.put("treeMenuRange", treeMenuRangeString);
		}else{
			Map<String, String> checkbox=userCtrlService.queryMenuCtrl( Long.toString(userId));
			//List<Map> list = userService.queryMenu();
			//查询该用户类型的菜单
			List<Map> list = userService.queryMenuForType(employeeType);
			List<CommonTreeDto> orgList = new ArrayList<>(); 
			for(int i=0;i<list.size();i++){
				boolean state=userMenuService.findMenu(Long.toString(userId),(String)list.get(i).get("MENU_ID").toString());
				CommonTreeStateDto CommonTreeStateDto=new CommonTreeStateDto();
				if(state==true){
					CommonTreeStateDto.setChecked(state);
				}else{
					CommonTreeStateDto.setChecked(false);
				}

				CommonTreeDto orgTreeOrg = new CommonTreeDto();
				orgTreeOrg.setId((String)list.get(i).get("MENU_ID").toString());
				String parent = (String)list.get(i).get("PARENT_ID").toString();
				if("0".equals(parent)){
					parent = "#";
				}
				orgTreeOrg.setParent(parent);
				orgTreeOrg.setText((String)list.get(i).get("MENU_DESC").toString());
				orgTreeOrg.setState(CommonTreeStateDto);
				orgList.add(orgTreeOrg);
			}
			userData.put("treejson", orgList);
			userData.put("checkbox", checkbox);
			userData.put("treeMenuAction", userMenuActionService.findMenuAction(Long.toString(userId)));
			userData.put("treeMenuRange", userMenuRangeService.findMenuRange(Long.toString(userId)));
		}

		return userData;
	}

	/**
	 * 
	 * 查询用户菜单操作按钮 复选框组
	 * @author yll
	 * @date 2016年8月12日
	 * @param menuId
	 * @return
	 */
	@RequestMapping(value = "/userMenuAction/{menuId}", method = RequestMethod.GET)
	@ResponseBody
	public List<Map> actionRemoteUrl(@PathVariable(value = "menuId")Long  menuId){
		return userMenuActionService.remoteUrl(menuId);
	}

	/**
	 * 
	 * 查询角色菜单权限  复选框组
	 * @author yll
	 * @date 2016年8月12日
	 * @param menuId
	 * @return
	 */
	@RequestMapping(value = "/userMenuRange/{menuId}", method = RequestMethod.GET)
	@ResponseBody
	public List<Map> rangeRemoteUrl(@PathVariable(value = "menuId")Long  menuId){
		return userMenuRangeService.remoteUrl(menuId);
	}

	
	@RequestMapping(value = "/saveUserSeries", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> saveUserSeries(@RequestParam Map<String, Object> queryParam) {
		userMenuRangeService.saveUserSeries(queryParam);
		Map<String, Object> map = new HashMap<>();
    	map.put("msg", 1);
    	return map;
    }

}
