//
///** 
//*Copyright 2017 Yonyou Corporation Ltd. All Rights Reserved.
//* This software is published under the terms of the Yonyou Software
//* License version 1.0, a copy of which has been included with this
//* distribution in the LICENSE.txt file.
//*
//* @Project Name : dmsgms.manage
//*
//* @File name : OrganizationController.java
//*
//* @Author : RenWeiDong
//*
//* @Date : 2017年5月27日
//*
//----------------------------------------------------------------------------------
//*     Date       Who       Version     Comments
//* 1. 2017年5月27日    RenWeiDong    1.0
//*
//*
//*
//*
//----------------------------------------------------------------------------------
//*/
//
//package com.yonyou.dms.manage.controller.inquiry;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.yonyou.dms.function.utils.common.StringUtils;
//import com.yonyou.dms.manage.domains.DTO.basedata.check.OrganizationDto;
//import com.yonyou.dms.manage.domains.DTO.basedata.check.OrganizationTreeDto;
//import com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService;
//import com.yonyou.f4.mvc.annotation.TxnConn;
//import com.yonyou.f4.mvc.controller.BaseController;
//
///**
// * 部门
// * 
// * @author yll
// * @date 2017年5月27日
// */
//
//@Controller
//@TxnConn
//@RequestMapping("/basedata/orgs")
//public class OrganizationController extends BaseController {
//    
//  //private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
//    @Autowired
//    private OrganizationService organizationService;
//    
//    /**
//     * 
//    * 获取树状图
//    * @author pcl
//    * @date 2017年3月30日
//    * @return
//     */
//    @RequestMapping(method=RequestMethod.GET)
//    @ResponseBody
//    public List<OrganizationTreeDto> getOrgs(){
//        List<Map> list = organizationService.getOrganizationDms();
//        List<OrganizationTreeDto> orgList = new ArrayList<>(); 
//        
//        for(int i=0;i<list.size();i++){
//            Map orgMap = list.get(i);
//            orgList.add(getOrginizationTree(orgMap));
//        }
//        
//        return orgList;
//    }
//    
//    /**
//     * 
//    * 获取所有有效组织
//    * @author pcl
//    * @date 2017年3月30日
//    * @return
//     */
//    @RequestMapping(value="/getIsValid/Orgs", method=RequestMethod.GET)
//    @ResponseBody
//    public List<OrganizationTreeDto> getIsValidOrgs(){
//        List<Map> list = organizationService.getIsValidOrganizationDms();
//        List<OrganizationTreeDto> orgList = new ArrayList<>(); 
//        
//        for(int i=0;i<list.size();i++){
//            Map orgMap = list.get(i);
//            orgList.add(getOrginizationTree(orgMap));
//        }
//        
//        return orgList;
//    }
//    
//    /**
//     * 
//    * 组织经销商树的结构
//    * @author zhangxc
//    * @date 2017年3月21日
//    * @param orgMap
//    * @return
//     */
//    private OrganizationTreeDto getOrginizationTree(Map orgMap){
//        OrganizationTreeDto orgTreeOrg = new OrganizationTreeDto();
//         String orgDeptId = orgMap.get("ORG_ID").toString();
//         String orgCode = (String)orgMap.get("ORG_CODE");
//         orgTreeOrg.setId(orgDeptId);
//         String parent = "#";
//         if(!StringUtils.isNullOrEmpty(orgMap.get("PARENT_ORG_ID"))){
//             parent = orgMap.get("PARENT_ORG_ID").toString();
//         }
//         orgTreeOrg.setParent(parent);
//         orgTreeOrg.setData(orgCode);
//         orgTreeOrg.setText(orgMap.get("ORG_SHORT_NAME").toString());
//         return orgTreeOrg;
//    }
//     /**
//      * 
//     * 根据id查询
//     * @author pcl
//     * @date 2017年3月30日
//     * @param id
//     * @return
//      */
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @ResponseBody
//    public Map getOrgByCodeName(@PathVariable(value = "id") Long id){
//        return organizationService.getOrgById(id);
//    }
//    
//    /**
//     * 
//    * 新增部门
//    * @author pcl
//    * @date 2017年3月30日
//    * @param orgDto
//    * @param uriCB
//    * @return
//     */
//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<OrganizationDto> addOrg(@RequestBody OrganizationDto orgDto,UriComponentsBuilder uriCB) {
//        Integer  orgdeptId = organizationService.addOrg(orgDto);
//        MultiValueMap<String,String> headers = new HttpHeaders();  
//        headers.set("Location", uriCB.path("/basedata/orgs/{orgdeptId}").buildAndExpand(orgdeptId).toUriString());  
//        return new ResponseEntity<>(orgDto,headers, HttpStatus.CREATED);  
//    }
//    
//     /**
//      * 
//     * 修改部门
//     * @author pcl
//     * @date 2017年3月30日
//     * @param id
//     * @param orgDto
//     * @param uriCB
//     * @return
//      */
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    public ResponseEntity<OrganizationDto> ModifyOrg(@PathVariable("id") Long id,@RequestBody OrganizationDto orgDto,UriComponentsBuilder uriCB) {
//        organizationService.modifyOrgDms(id, orgDto);
//        MultiValueMap<String,String> headers = new HttpHeaders();  
//        headers.set("Location", uriCB.path("/basedata/orgs/{id}").buildAndExpand(id).toUriString());  
//        return new ResponseEntity<>(headers, HttpStatus.CREATED);  
//    }
//    
//    /**
//     * 删除部门
//    * @author rongzoujie
//    * @date 2016年8月3日
//    * @param id
//    * @param uriCB
//     */
//   /* @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteOrg(@PathVariable("id") Long id,UriComponentsBuilder uriCB){
//        organizationService.deleteOrgById(id);
//    }*/
//    
//      /**
//       * 
//      * 获取上级组织
//      * @author pcl
//      * @date 2017年3月30日
//      * @return
//       */
//    @RequestMapping(value = "/getParents/super", method=RequestMethod.GET)
//    @ResponseBody
//    public List<Map> getParents(){
//        return organizationService.getParents();
//    }
//   
//    @RequestMapping(value="/employee/{employeeCode}")
//    @ResponseBody
//    public Map findByOrgCode(@PathVariable String employeeCode){
//        return organizationService.findByOrgCode(employeeCode);
//    }
//    
//}
