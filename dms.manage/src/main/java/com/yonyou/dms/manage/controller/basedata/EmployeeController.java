
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : EmployeeController.java
*
* @Author : jcsi
*
* @Date : 2016年7月7日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月7日    Administrator    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.manage.controller.basedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.framework.service.baseData.SystemParamService;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.manage.domains.DTO.basedata.EmployeeDto;
import com.yonyou.dms.manage.domains.DTO.basedata.user.EmployeeRoleDto;
import com.yonyou.dms.manage.service.basedata.employee.EmployeeService;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 经销商员工信息
* @author jcsi
* @date 2016年7月7日
*/
@Controller
@TxnConn
@RequestMapping("/basedata/employees")
public class EmployeeController extends BaseController{
    
    private static final Logger logger=LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SystemParamService  systemparamservice;
   
   
  
    	
    
    /**
    * 查询员工信息
    * @author jcsi
    * @date 2016年7月29日
    * @param param
    * @return
    */
    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    public PageInfoDto search(@RequestParam Map<String, String> param){        
        PageInfoDto pageInfoDto=employeeService.searchEmployees(param);
        return pageInfoDto;
    }
    /**
     * 
    * 员工下拉框加载
    * @author yll
    * @date 2016年7月20日
    * @param queryParam
    * @return
     */
    @RequestMapping(value="/employees/dict",method = RequestMethod.GET)
	@ResponseBody
	public List<Map> selectEmployees(@RequestParam Map<String,String> queryParam) {
		List<Map> employeelist = employeeService.selectEmployees(queryParam);
		return employeelist;
	}
    
    
    /**
    * 职位过滤员工
    * @author zhanshiwei
    * @date 2016年11月1日
    * @param role
    * @return
    */
    	
    @RequestMapping(value="/{role}/employeesdict",method = RequestMethod.GET)
    @ResponseBody
    public List<Map> selectEmployeeByrole(@PathVariable Integer role) {
        List<Map> employeelist = employeeService.selectEmployeeByrole(role);
        return employeelist;
    }
    
    /**
     * 新增
    * @author jcsi
    * @date 2016年7月7日
    * @param empDto
    * @param uriCB
     */
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Valid EmployeeDto empDto,UriComponentsBuilder uriCB){
    	empDto.setTechnicianGrade(10071001l);
        Long id=employeeService.addEmployee(empDto);  
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Location", uriCB.path("/basedata/employees/{id}").buildAndExpand(id).toUriString());
        return new ResponseEntity<>(empDto,headers, HttpStatus.CREATED);
    }
    
    
    /**
    * 根据id查找员工信息 
    * @author jcsi
    * @date 2016年7月12日
    * @param id
    * @return 
     */
    @RequestMapping(value="/{id}",method=RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> findByEmployeeId(@PathVariable Long id){
        Map<String, Object> map=employeeService.findById(id);
        //查找员工角色表  转成字符串用逗号分隔  插入map中
        List<Map>  roles=employeeService.findEmpRolesByEmpId(id);
        String rolesStr="";        
        rolesStr=CommonUtils.listMapToString(roles,",");
        map.put("roles", rolesStr);
        Map<String, Object> user=employeeService.findUserByEmployeeId(id);
        if(!CommonUtils.isNullOrEmpty(user)){
        	 map.putAll(user);
        }else{
        	map.put("USER_STATUS", DictCodeConstants.STATUS_QY);//初始值为启用
        }
        return map;
    }
    
    /**
    * 根据id删除员工信息 
    * @author jcsi
    * @date 2016年7月12日
    * @param id
     */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id){
        employeeService.deleteById(id);
    }
    
    /**
    * 更新员工信息
    * @author jcsi
    * @date 2016年7月12日
    * @param id
    * @param empDto
    * @param uriCB
    * @return
     */
    @RequestMapping(value="/{id}",method=RequestMethod.PUT,consumes = "application/json")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id,@RequestBody @Valid EmployeeDto empDto,UriComponentsBuilder uriCB){
        employeeService.updateEmpById(id, empDto);
        MultiValueMap<String,String> headers = new HttpHeaders();  
        headers.set("Location", uriCB.path("/basedata/employees/{id}").buildAndExpand(id).toUriString());  
        return new ResponseEntity<>(headers, HttpStatus.CREATED);  
    }
    
    /**
     * 
    * 查询员工信息(员工权限编辑页面)
    * @author yll
    * @date 2016年8月16日
    * @param param
    * @return
     */
    @RequestMapping(value="/permission/items",method=RequestMethod.GET)
    @ResponseBody
    public PageInfoDto searchRoleEmp(@RequestParam Map<String, String> param){        
        PageInfoDto pageInfoDto=employeeService.searchUserEmployees(param);
        return pageInfoDto;
    }
    
    /**
     * 督导/系长维护
     * @author ljc
     * @date 2018年5月17日
     */
    @RequestMapping(value="/permission/names",method=RequestMethod.GET)
    @ResponseBody
    public PageInfoDto searchRoleName(@RequestParam Map<String, String> param){        
        PageInfoDto pageInfoDto=employeeService.searchRoleNames(param);
        return pageInfoDto;
    }
    
    /**
     * 查询除自己外所有督导信息
     * @author jxy
     * @date 2018年8月4日
     */
    @RequestMapping(value="/findSupervisors",method=RequestMethod.GET)
    @ResponseBody
    public PageInfoDto findSupervisors(){   
    	
        return employeeService.findSupervisors();
    }
    
    /**
    * 查询销售顾问
    * @author xukl
    * @date 2016年9月13日
    * @param orgCode
    * @return
    */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="/{orgCode}/salesConsultant",method=RequestMethod.GET)
    @ResponseBody
    public List<Map> qrySalesConsultant(@PathVariable String orgCode){        
        List<Map> list=employeeService.qrySalesConsultant(orgCode);
        return list;
    }
    
    /**
     * 根据基本参数查询销售经理财务经理
     * @author xukl
     * @date 2016年9月13日
     * @param orgCode
     * @return
     */
//    @RequestMapping(value="/slctSOrFAudit/conductors",method=RequestMethod.GET)
//    @ResponseBody
//    public PageInfoDto qryAudit(){
//        List<BasicParametersDTO> basiDtolist = systemparamservice.queryBasicParameterByType(Long.valueOf(DictCodeConstants.VEHICLE_BASIC_CODE));
//        PageInfoDto list=employeeService.qryAudit(basiDtolist);
//        return list;
//    }
    
    /**
    *销售订单-审核选择财务经理
    * @author xukl
    * @date 2016年9月28日
    * @return
    */
    @RequestMapping(value="/slctFAudit/moneys",method=RequestMethod.GET)
    @ResponseBody
    public List<Map> qryFinanceAudit(){
    	List<Map> list=employeeService.qryFinanceAudit();
        return list;
    }
     
     /**
      * 派工技师下拉框
     * @author rongzoujie
     * @date 2016年9月26日
     * @return
      */
     @RequestMapping(value="/queryTechnician/dicts",method=RequestMethod.GET)
     @ResponseBody
     public List<Map> queryTechnician(){
         return employeeService.queryTechnician();
     }
     
     /**
      * 服务顾问下拉框
     * TODO description
     * @author rongzoujie
     * @date 2016年9月27日
     * @return
      */
     @RequestMapping(value="/queryServiceAss/dicts",method=RequestMethod.GET)
     @ResponseBody
     public List<Map> queryServiceAss(){
         return employeeService.queryServiceAss();
     }
     
     
     @RequestMapping(value="/queryFinishUser/items",method=RequestMethod.GET)
     @ResponseBody
     public List<Map> queryFinishUser(){
         return employeeService.queryFinisher();
     }
     
     /**
      * 根据id查找员工信息 
      * @author jcsi
      * @date 2016年7月12日
      * @param id
      * @return 
       */
      @RequestMapping(value="/findUserSeries/{id}",method=RequestMethod.GET)
      @ResponseBody
      public Map<String,Object> findUserSeries(@PathVariable Long id){
          Map<String, Object> map=employeeService.findById(id);
          //查找员工角色表  转成字符串用逗号分隔  插入map中
          List<Map>  roles=employeeService.findUserSeries(id);
          String rolesStr="";        
          rolesStr=CommonUtils.listMapToString(roles,",");
          map.put("seriesIds", rolesStr);
          Map<String, Object> user=employeeService.findUserByEmployeeId(id);
          if(!CommonUtils.isNullOrEmpty(user)){
          	 map.putAll(user);
          }else{
          	map.put("USER_STATUS", DictCodeConstants.STATUS_QY);//初始值为启用
          }
          return map;
      }
      
      /**
       * 职位信息维护信息保存
       * @author ljc
       * @date 2018年5月18日
       */
      @RequestMapping(value="/userSeriesSave",method=RequestMethod.POST)
      @ResponseStatus(HttpStatus.CREATED)
      public ResponseEntity<Map> userSeriesSave(@RequestBody @Valid EmployeeRoleDto roleDto){
          //   employeeService.userSeriesSave(roleDto);
    	  
    	  MultiValueMap<String, String> headers =new HttpHeaders();
		  Map<String, Object> map =new HashMap<String, Object>();
		  map.put("tvl", 1);
		  return new ResponseEntity<Map>(map,headers,HttpStatus.CREATED);
    	  
      }
      
      /**
       * 职位信息维护信息修改
       * @author ljc
       * @date 2018年5月18日
       */
      @RequestMapping(value="/userSeriesUpdateSave/{duDao}/{xiZhang}/{keZhang}/{buZhang}/{employeeId}",method=RequestMethod.POST)
      @ResponseStatus(HttpStatus.CREATED)
      public ResponseEntity<Map> userSeriesUpdateSave(@RequestBody @PathVariable Long duDao,@PathVariable Long xiZhang,@PathVariable Long keZhang,@PathVariable Long buZhang,@PathVariable Long employeeId){
    	  employeeService.userSeriesUpdateSave(duDao,xiZhang,keZhang,buZhang,employeeId);
    	  
    	  MultiValueMap<String, String> headers =new HttpHeaders();
		  Map<String, Object> map =new HashMap<String, Object>();
		  map.put("tvl", 1);
		  return new ResponseEntity<Map>(map,headers,HttpStatus.CREATED);
    	  
      }
      
      /**
       * 姓名返回值
       * @return
       */
      @RequestMapping(value="/returnName/{employeeNo}",method=RequestMethod.GET)
      @ResponseBody
      public List<Map> returnName(@PathVariable String employeeNo){
          return employeeService.returnName(employeeNo);
      }
      
      /**
       * 根据id查找员工信息 
       * @author jcsi
       * @date 2016年7月12日
       * @param id
       * @return 
        */
       @RequestMapping(value="/findName/{id}",method=RequestMethod.GET)
       @ResponseBody
       public Map<String,Object> findNameId(@PathVariable Long id){
           Map<String, Object> map=employeeService.findNameId(id);
           return map;
       }
       
       /**
        * 职位信息启用/暂停
        */
       @RequestMapping(value = "/startStop/{id}", method = RequestMethod.DELETE)
       @ResponseStatus(HttpStatus.NO_CONTENT)
	   public void deleteBrand(@PathVariable("id") Long id) {
    	   employeeService.findGender(id);
	   }
      
}
