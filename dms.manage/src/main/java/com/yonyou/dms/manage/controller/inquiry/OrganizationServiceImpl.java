//
///** 
//*Copyright 2017 Yonyou Corporation Ltd. All Rights Reserved.
//* This software is published under the terms of the Yonyou Software
//* License version 1.0, a copy of which has been included with this
//* distribution in the LICENSE.txt file.
//*
//* @Project Name : dmsgms.manage
//*
//* @File name : OrganizationServiceImpl.java
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
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import org.javalite.activejdbc.Base;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.yonyou.dmsgms.framework.DAO.DAOUtil4DMS;
//import com.yonyou.dmsgms.framework.constants.FrameworkDictCodeConstants;
//import com.yonyou.dmsgms.framework.domain.LoginInfoDto;
//import com.yonyou.dmsgms.framework.util.FrameworkUtil;
//import com.yonyou.dmsgms.function.exception.ServiceBizException;
//import com.yonyou.dmsgms.function.utils.common.CommonUtils;
//import com.yonyou.dmsgms.function.utils.common.StringUtils;
//import com.yonyou.dmsgms.manage.constants.ManageDictCodeConstants;
//import com.yonyou.dmsgms.manage.domains.DTO.OrganizationDto;
//import com.yonyou.dmsgms.manage.domains.PO.AscInfoPO;
//import com.yonyou.dmsgms.manage.domains.PO.DealerBasicinfoPO;
//import com.yonyou.dmsgms.manage.domains.PO.DealerInfoPO;
//import com.yonyou.dmsgms.manage.domains.PO.DealerOrgRelationPO;
//import com.yonyou.dmsgms.manage.domains.PO.OrgRelationPO;
//import com.yonyou.dmsgms.manage.domains.PO.OrganizationPO;
//import com.yonyou.dmsgms.manage.domains.VO.TmAscClassVO;
//import com.yonyou.dmsgms.manage.domains.VO.TmAscInfoVO;
//import com.yonyou.dmsgms.manage.domains.VO.TmCompanyVO;
//import com.yonyou.dmsgms.manage.domains.VO.TmDealerInfoVO;
//import com.yonyou.dmsgms.manage.domains.VO.TmOrgModelVO;
//import com.yonyou.dmsgms.manage.domains.VO.TmOrgVO;
//import com.yonyou.dmsgms.manage.domains.VO.TrOrgBrandVO;
//import com.yonyou.dmsgms.manage.domains.VO.TrOrgVO;
//
//import io.netty.util.internal.StringUtil;
//
///**
//* 部门
//* @author RenWeiDong
//* @date 2017年5月27日
//*/
//@Service
//public class OrganizationServiceImpl implements OrganizationService {
//
//    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);
//    
//    /**
//     * 获取所有组织
//    * @author pcl
//    * @date 2017年3月26日
//    * @param dataType
//    * @return
//    * @throws ServiceBizException
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#getOrganization(java.lang.String)
//     */
//    @Override
//    public List<Map> getOrganization(String dataType) throws ServiceBizException{
//        List<Map>  result = null;
//       
//        return result;
//    }
//
//    
//    /**
//     * dms修改部门
//    * @author pcl
//    * @date 2017年3月27日
//    * @param id
//    * @param orgDto
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#modifyOrgDms(java.lang.Integer, com.yonyou.dmsgms.manage.domains.DTO.OrganizationDto)
//     */
//    @Override
//    public void modifyOrgDms(Long id, OrganizationDto orgDto) throws ServiceBizException{
//         if(orgDto.getDataSource()==ManageDictCodeConstants.DATA_SOURCES_BY_GROUP){
//             throw new ServiceBizException("此数据来自集团不可修改");
//         }else{
//             modifyOrg(id,orgDto);
//         }        
//    }
//
//  /**
//   * gms修改部门
//  * @author pcl
//  * @date 2017年3月27日
//  * @param id
//  * @param orgDto
//  * @throws ServiceBizException
//  * (non-Javadoc)
//  * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#modifyOrg(java.lang.Integer, com.yonyou.dmsgms.manage.domains.DTO.OrganizationDto)
//   */
//    @Override
//    public void modifyOrg(Long id, OrganizationDto orgDto) throws ServiceBizException {
//        checkOrgNameDup(orgDto,id);
//        if(orgDto.getIsValid() == ManageDictCodeConstants.STATUS_NOT_VALID){
//            checkIsValid(id,orgDto);
//            checkExistMan(id);
//        }
//        OrganizationPO orgPo=OrganizationPO.findById(id);
//        setOrg(orgPo,orgDto);
//        orgPo.saveIt();
//    }
//    /**
//     * 
//    * 确认该部门下是否存在人员
//    * @author pcl
//    * @date 2017年3月25日
//    * @param id
//     */
//    
//    private void checkExistMan(Long id) throws ServiceBizException{
//       OrganizationPO orgPo=OrganizationPO.findById(id);
//       StringBuilder sql=  new  StringBuilder("select emp,emp.EMPLOYEE_ID,emp.EMPLOYEE_NAME FROM tm_employee emp INNER JOIN tm_org org ON emp.ORG_ID = org.ORG_ID where 1=1");
//       List<Object> queryParams = new ArrayList<>();
//       sql.append(" and ORG_ID = ?");
//       queryParams.add(id);
//       DAOUtil4DMS.appendAclSql("emp", sql, queryParams, true);
//       List<Map> result = DAOUtil4DMS.findAll(sql.toString(), queryParams);
//       if(result.size()>0){
//           throw new ServiceBizException("此部门下存在员工不能设置为无效");
//       }
//    }
//
//    /**
//     * 
//    * 判断组织名称是否和其他组织名称重复
//    * @author pcl
//    * @date 2017年3月30日
//    * @param orgDto
//    * @param orgId
//     */
//    private void checkOrgNameDup(OrganizationDto orgDto,Long orgId) throws ServiceBizException{
//        List<Object> param = new ArrayList<>();
//        StringBuilder sql = new StringBuilder("select ORG_ID,ORG_NAME,DATA_SOURCE,DATA_TYPE from tm_org where 1=1");
//        sql.append(" and ORG_NAME = ?");
//        param.add(orgDto.getOrgName());
//        sql.append(" and ORG_ID != ?");
//        param.add(orgId);
//        sql.append(" and DATA_TYPE = ?");
//        param.add(orgDto.getDataType());
//        List<Map> result = DAOUtil4DMS.findAll(sql.toString(), param);
//        if(result.size() > 0){
//            throw new ServiceBizException("该组织名称已经存在");
//        }
//    }
//
//    /**
//     * 删除部门
//    * @author rongzoujie
//    * @date 2016年8月3日
//    * @param id
//    * @throws ServiceBizException
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#deleteOrgById(java.lang.Integer)
//     */
//   /* @Override
//    public void deleteOrgById(Long id) throws ServiceBizException {
//        OrganizationPO orgPo = OrganizationPO.findById(id);
//        String orgCode = (String)orgPo.getString("ORG_CODE").toString();
//        boolean child = checkChild(orgCode);
//        
//        if(!checkEmployee(id)){
//            throw new ServiceBizException("该部门下有员工不能删除");
//        }
//        
//        if(child){
//            orgPo.deleteCascadeShallow();
//            //记录删除日志
//            operateLogService.recordOperateLog("删除组织：组织代码 【"+orgCode+"】",ManageDictCodeConstants.LOG_SYSTEM_MANAGEMENT);
//        }else{
//            throw new ServiceBizException("该部门下有子部门不能删除");
//        }
//    }*/
//    
//    /*public boolean checkEmployee(Long id){
//      //1：更具id查出org_code
//        StringBuilder sqlOrg = new StringBuilder("select OWNER_CODE,ORG_CODE from tm_org where 1=1");
//        sqlOrg.append(" and ORG_ID = ?");
//        List<Object> orgParams = new ArrayList<>();
//        orgParams.add(id);
//        Map orgResult = DAOUtil4DMS.findFirst(sqlOrg.toString(), orgParams);
//        String orgCode = orgResult.get("ORG_CODE").toString();
//        
//        //2: 更具org_code查出部门下是否有员工
//        StringBuilder sqlEmployee = new StringBuilder("select OWNER_CODE,EMPLOYEE_ID from tm_employee where 1=1");
//        sqlEmployee.append(" and org_code = ?");
//        List<Object> empParams = new ArrayList<>();
//        empParams.add(orgCode);
//        List<Map> empResult = DAOUtil4DMS.findAll(sqlEmployee.toString(), empParams);
//        if(empResult.size()>0){
//            return false;
//        }else{
//            return true;
//        }
//    }*/
//    
//    /**
//     * 检查是否有字节点
//    * @author rongzoujie
//    * @date 2016年8月3日
//    * @param orgCode
//    * @return
//    * @throws ServiceBizException
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#checkChild(java.lang.String)
//     */
//  /*  public boolean checkChild(String orgCode) throws ServiceBizException{
//        StringBuilder sql = new StringBuilder("SELECT ORG_CODE,OWNER_CODE FROM tm_org WHERE 1=1");
//        List<Object> queryParams = new ArrayList<>();
//        sql.append(" AND PARENT_ORG_CODE = ?");
//        queryParams.add(orgCode);
//        List<Map> map = DAOUtil4DMS.findAll(sql.toString(),queryParams);
//        if(map.size()==0){
//            return true;
//        }
//            return false;
//    }*/
//
//    /**
//     * 获取上级组织
//    * @author rongzoujie
//    * @date 2016年8月3日
//    * @return
//    * @throws ServiceBizException
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#getParent()
//     */
//    @Override
//    public List<Map> getParents() throws ServiceBizException {
//        StringBuilder sql = new StringBuilder("SELECT ORG_ID,ORG_CODE,PARENT_ORG_ID,ORG_NAME FROM tm_org WHERE 1 = 1");
//        List<Object> param = new ArrayList<Object>();
//        DAOUtil4DMS.appendAclSql(null, sql, param, true);
//        List<Map> result = DAOUtil4DMS.findAll(sql.toString(),param);
//        return result;
//    }
//
//    /**
//     * 根据员工编号 查询部门信息
//    * @author jcsi
//    * @date 2016年8月16日
//    * @param employeeCode
//    * @return
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#findByOrgCode(java.lang.String)
//     */
//    @Override
//    public Map findByOrgCode(String employeeCode) throws ServiceBizException {
//        String str="SELECT t.ORG_CODE,t.ORG_NAME FROM tm_org t INNER JOIN tm_employee e on t.ORG_CODE=e.ORG_CODE where e.EMPLOYEE_NO=? and t.OWNER_CODE=? ";
//        List<Object> param=new ArrayList<>();
//        param.add(employeeCode);
//        param.add(FrameworkUtil.getLoginInfo().getOwnerCode());
//        return DAOUtil4DMS.findFirst(str, param);
//    }
//
//   /**
//    * dms查询有效组织信息
//   * @author pcl
//   * @date 2017年3月26日
//   * @return
//   * @throws ServiceBizException
//   * (non-Javadoc)
//   * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#getIsValidOrganizationDms()
//    */
//    @Override
//    public List<Map> getIsValidOrganizationDms() throws ServiceBizException  {
//        List<Object> queryParams = new ArrayList<>();
//        StringBuilder sqlsb = new StringBuilder("SELECT ORG_ID,PARENT_ORG_ID,ORG_CODE,ORG_NAME,DATA_SOURCE,DATA_TYPE FROM tm_org where 1=1 and IS_VALID = "+ManageDictCodeConstants.STATUS_IS_VALID);
//         sqlsb.append(" and DATA_TYPE = ?");
//         queryParams.add(ManageDictCodeConstants.DATA_TYPE_BY_OWNER);
//         DAOUtil4DMS.appendAclSql(null, sqlsb, queryParams);
//         List<Map> result = DAOUtil4DMS.findAll(sqlsb.toString(), queryParams);
//         return result;
//    }
//   
//    /**
//     * GMS端集团查询有效部门
//    * @author pcl
//    * @date 2017年3月24日
//    * @return
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#getOrganizationGMS()
//     */
//    @Override
//    public List<Map> getIsValidOrganizationGMS(String  dataType) throws ServiceBizException{
//        List<Map> result =null;
//        List<Object> queryParams = new ArrayList<>();
//        StringBuilder sqlsb = new StringBuilder("SELECT ORG_ID,PARENT_ORG_ID,ORG_CODE,ORG_NAME,PARENT_ORG_CODE FROM tm_org where 1=1 and IS_VALID = "+ManageDictCodeConstants.STATUS_IS_VALID);
//        if(Integer.parseInt(dataType)==ManageDictCodeConstants.DATA_TYPE_BY_GROUP){
//            sqlsb.append(" and DATA_TYPE = ?");
//            queryParams.add(ManageDictCodeConstants.DATA_TYPE_BY_GROUP);
//            sqlsb.append(" and DATA_SOURCE = ?");
//            queryParams.add(ManageDictCodeConstants.DATA_SOURCES_BY_GROUP);
//             result = DAOUtil4DMS.findAll(sqlsb.toString(), queryParams);
//        }else if(Integer.parseInt(dataType)==ManageDictCodeConstants.DATA_TYPE_BY_OWNER){
//            sqlsb.append(" and DATA_TYPE = ?");
//            queryParams.add(ManageDictCodeConstants.DATA_TYPE_BY_OWNER);
//            sqlsb.append(" and DATA_SOURCE = ?");
//            queryParams.add(ManageDictCodeConstants.DATA_SOURCES_BY_GROUP);
//            result = DAOUtil4DMS.findAll(sqlsb.toString(), queryParams);
//        }
//        return result;
//    }
//    
//
//    /**
//     * dms获取所有组织部门信息
//    * @author pcl
//    * @date 2017年3月26日
//    * @param dataType
//    * @return
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#getOrganizationDms(java.lang.String)
//     */
//   @Override
//   public List<Map> getOrganizationDms() throws ServiceBizException {
//      StringBuilder sql = new StringBuilder();
//      sql.append("select orgFirst.ORG_ID,orgFirst.ORG_CODE,orgFirst.ORG_SHORT_NAME,'' as PARENT_ORG_ID from tm_org orgFirst where orgFirst.ORG_ID = ? ");;
//      sql.append("union ");
//      sql.append("SELECT  org.ORG_ID,org.ORG_CODE,org.ORG_SHORT_NAME,tro.PARENT_ORG_ID ");
//      sql.append("FROM (SELECT @id AS _id,");
//      sql.append(" (SELECT @id := group_concat(DISTINCT ORG_ID) FROM tr_org WHERE find_in_set(PARENT_ORG_ID, _id)) AS child_ORG_ID, ");
//      sql.append(" @lvl := @lvl + 1 AS lvl ");
//      sql.append(" FROM tr_org, ");
//      sql.append("(SELECT @id :=?, @lvl := 0) vars ");
//      sql.append(" WHERE @id is not null ");
//      sql.append(" ) T1 ");
//      sql.append("join tm_org org on find_in_set(org.ORG_ID, t1.child_ORG_ID) ");
//      sql.append("left join tr_org tro on org.ORG_ID=tro.ORG_ID and find_in_set(tro.PARENT_ORG_ID, t1._id) ");
//      sql.append("where (org.org_type = ? or  org.org_type = ? ) ");
//      
//      List<Object> params = new ArrayList<>();
//      LoginInfoDto login = FrameworkUtil.getLoginInfo();
//      if(login != null){
//          params.add(login.getOrgId());
//          params.add(login.getOrgId());
//          params.add(FrameworkDictCodeConstants.ORG_TYPE_DEALER);
//          params.add(FrameworkDictCodeConstants.ORG_TYPE_DEALER2);
//      }
//      List<Map> result =DAOUtil4DMS.findAll(sql.toString(), params);
//      return result;
//   }
//
//    /**
//     *  插入oem 公司
//    * @author liusong
//    * @date 2017年7月10日
//    * @param tmCompanyVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrCompany(com.yonyou.dmsgms.manage.domains.vo.TmCompanyVO)
//     */
//   @Override
//    public void addExtrCompany(TmCompanyVO tmCompanyVO) {
//       //组织
//       TmOrgModelVO tmOrgVO  = tmCompanyVO.getTmOrgVO();
//       OrganizationPO organizationPO  = OrganizationPO.findById(tmOrgVO.getOrgId());
//       if(organizationPO == null){
//           organizationPO = new OrganizationPO();
//           this.setOrganization(organizationPO,tmOrgVO);
//           organizationPO.insert();
//       }else{
//           if(organizationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmOrgVO.getChangedAt()))){
//               this.setOrganization(organizationPO,tmOrgVO);
//               organizationPO.saveIt();
//           }
//       }
//       //经销商
//       DealerBasicinfoPO dealerBasicinfoPO = DealerBasicinfoPO.findById(tmCompanyVO.getCompanyId());
//       if(dealerBasicinfoPO == null){
//           dealerBasicinfoPO = new DealerBasicinfoPO();
//           this.setDealerBasicinfo(dealerBasicinfoPO,tmCompanyVO);
//           dealerBasicinfoPO.insert();
//       }else{
//           if(dealerBasicinfoPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmCompanyVO.getChangedAt()))){
//               this.setDealerBasicinfo(dealerBasicinfoPO,tmCompanyVO);
//               dealerBasicinfoPO.saveIt();
//           }
//       }
//       
//       //组织关系
//       TrOrgVO orgVO = tmCompanyVO.getTrOrgVO();
//       orgVO.setChangedAt(tmOrgVO.getChangedAt());
//       getOrgRelation(orgVO);
//    }
//
//   /**
//    * 
//   * 赋值
//   * @author liusong
//   * @date 2017年8月23日
//   * @param organizationPO
//   * @param tmOrgVO
//    */
//   private void setOrganization(OrganizationPO organizationPO, TmOrgModelVO tmOrgVO) {
//       organizationPO.setLong("ORG_ID", tmOrgVO.getOrgId());//组织ID
//       organizationPO.setLong("COMPANY_ID", tmOrgVO.getCompanyId());//本身所在company_id
//       organizationPO.setString("ORG_CODE", tmOrgVO.getOrgCode());//组织代码
//       organizationPO.setString("ORG_NAME", tmOrgVO.getOrgName());//组织名称
//       organizationPO.setString("ORG_SHORT_NAME", tmOrgVO.getOrgShortName());//组织名称缩写
//       organizationPO.setString("ORG_DESC", tmOrgVO.getOrgDesc());//组织描述
//       organizationPO.setInteger("ORG_TYPE", tmOrgVO.getOrgType());//组织类型
//       organizationPO.setInteger("IS_VALID", tmOrgVO.getIsValid());//有效状态
//       organizationPO.setTimestamp("SOURCE_CHANGED_AT", new Date(tmOrgVO.getChangedAt()));
//       organizationPO.setInteger("DATA_SOURCE", ManageDictCodeConstants.DATA_SOURCES_BY_SUPER);//数据来源
//    }
//
//
//   /**
//    * 公司赋值
//   * @author liusong
//   * @date 2017年7月10日
//   * @param dealerBasicinfoPO
//   * @param tmCompanyVO
//    */
//    private void setDealerBasicinfo(DealerBasicinfoPO dealerBasicinfoPO, TmCompanyVO tmCompanyVO) {
//        dealerBasicinfoPO.setLong("COMPANY_ID", tmCompanyVO.getCompanyId());// 主建DEALER_ID
//        dealerBasicinfoPO.setString("COMPANY_CODE", tmCompanyVO.getCompanyCode());//公司code
//        dealerBasicinfoPO.setInteger("COMPANY_TYPE", tmCompanyVO.getCompanyType());//公司类型,和ORG_TYPE使用同一个类型
//        dealerBasicinfoPO.setLong("PROVINCE_ID", tmCompanyVO.getProvinceId());//省份
//        dealerBasicinfoPO.setLong("CITY_ID", tmCompanyVO.getCityId());//城市
//        dealerBasicinfoPO.setLong("ORG_ID", tmCompanyVO.getOrgId());//此经销商对应组织表里的主键
//        dealerBasicinfoPO.setString("TENANT_ID", tmCompanyVO.getTenantId());//数据库租户ID
//        dealerBasicinfoPO.setLong("FACTORY_COMPANY_ID", tmCompanyVO.getFactoryCompanyId());//工厂
//        dealerBasicinfoPO.setString("COMPANY_NAME_CN", tmCompanyVO.getCompanyNameCn());//公司中文全称
//        dealerBasicinfoPO.setString("COMPANY_NAME_EN", tmCompanyVO.getCompanyNameEn());//公司全称英文
//        dealerBasicinfoPO.setString("COMPANY_SHORT_NAME_CN", tmCompanyVO.getCompanyShortNameCn());//公司简称中文
//        dealerBasicinfoPO.setString("COMPANY_SHORT_NAME_EN", tmCompanyVO.getCompanyShortNameEn());//公司简称英文
//        dealerBasicinfoPO.setTimestamp("SOURCE_CHANGED_AT", new Date(tmCompanyVO.getChangedAt()));
//    }
//
//
//   /**
//    * 插入oem 经销商组织
//   * @author liusong
//   * @date 2017年7月7日
//   * @param msgVO
//   * (non-Javadoc)
//   * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrDealerOrganization(com.yonyou.dmsgms.manage.domains.vo.TmDealerInfoVO)
//    */
//    @Override
//    public void addExtrDealerOrganization(TmDealerInfoVO dealerInfoVO) {
//        //经销商组织
//        TmOrgVO tmOrgVO = dealerInfoVO.getDealerOrg();
//        if(tmOrgVO != null){
//            tmOrgVO.setChangedAt(dealerInfoVO.getChangedAt());
//            OrganizationPO organizationPO  = OrganizationPO.findById(tmOrgVO.getOrgId());
//            if(organizationPO == null){
//                organizationPO = new OrganizationPO();
//                this.setOrganization(organizationPO,tmOrgVO);
//                organizationPO.insert();
//            }else{
//                if(organizationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmOrgVO.getChangedAt()))){
//                    this.setOrganization(organizationPO,tmOrgVO);
//                    organizationPO.saveIt();
//                }
//            }
//        }
//        //经销商
//        DealerInfoPO dealerInfoPO = DealerInfoPO.findById(dealerInfoVO.getDealerId());
//        if(dealerInfoPO == null){
//            dealerInfoPO = new DealerInfoPO();
//            this.setDealerInfo(dealerInfoPO,dealerInfoVO);
//            dealerInfoPO.insert();
//        }else{
//            if(dealerInfoPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(dealerInfoVO.getChangedAt()))){
//                this.setDealerInfo(dealerInfoPO,dealerInfoVO);
//                dealerInfoPO.saveIt();
//            }
//        }
//        //大区域经销商关系
//        TrOrgVO areaOrgVo = dealerInfoVO.getAreaOrgVo();
//        if(areaOrgVo != null){
//            areaOrgVo.setChangedAt(dealerInfoVO.getChangedAt());
//            getOrgRelation(areaOrgVo);
//        }
//        //所属经销商和集团公司关系
//        TrOrgVO myComVo = dealerInfoVO.getMyComVo();
//        if(myComVo != null){
//            myComVo.setChangedAt(dealerInfoVO.getChangedAt());
//            getOrgRelation(myComVo);
//        }
//        //所属经销商和经销商公司关系
//        TrOrgVO dealerComVo = dealerInfoVO.getDealerComVo();
//        if(dealerComVo != null){
//            dealerComVo.setChangedAt(dealerInfoVO.getChangedAt());
//            getOrgRelation(dealerComVo);
//        }
//        //分销商和经销商关系
//        TrOrgVO retailDeVo = dealerInfoVO.getRetailDeVo();
//        if(retailDeVo != null){
//            retailDeVo.setChangedAt(dealerInfoVO.getChangedAt());
//            getOrgRelation(retailDeVo);
//        }
//        //代理品牌
//        List<TrOrgBrandVO> brandVOs = dealerInfoVO.getOrgBrandVos();
//        if(!CommonUtils.isNullOrEmpty(brandVOs)){
//            for(TrOrgBrandVO vo : brandVOs){
//                vo.setChangedAt(dealerInfoVO.getChangedAt());
//                DealerOrgRelationPO dealerOrgRelationPO  = DealerOrgRelationPO.findFirst("SOURCE_ID = ? ", vo.getTrId());
//                if(dealerOrgRelationPO == null){
//                    dealerOrgRelationPO = new  DealerOrgRelationPO();
//                    this.setDealerOrgRelation(dealerOrgRelationPO,vo);
//                    dealerOrgRelationPO.saveIt();
//                }else{
//                    if(dealerOrgRelationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(vo.getChangedAt()))){
//                        this.setDealerOrgRelation(dealerOrgRelationPO,vo);
//                        dealerOrgRelationPO.saveIt();
//                    }
//                }
//            }
//        }
//    }
//
//    
//    /**
//     * 
//    * 组织关系
//    * @author liusong
//    * @date 2017年7月10日
//    * @param vo
//     */
//    private void getOrgRelation (TrOrgVO vo){
//        OrgRelationPO orgRelationPO  = OrgRelationPO.findFirst("SOURCE_ID= ?", vo.getId());
//        //大区域经销商关系
//         if(orgRelationPO == null){
//             orgRelationPO = new OrgRelationPO();
//             this.setOrgRelation(orgRelationPO,vo);
//             orgRelationPO.saveIt();
//         }else{
//             if(orgRelationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(vo.getChangedAt()))){
//                 this.setOrgRelation(orgRelationPO,vo);
//                 orgRelationPO.saveIt();
//             }
//         } 
//    }
//    
//    /**
//     * 组织关系赋值
//    * @author liusong
//    * @date 2017年7月10日
//    * @param orgRelationPO
//    * @param vo
//     */
//    private void setOrgRelation(OrgRelationPO orgRelationPO, TrOrgVO vo) {
//        orgRelationPO.setLong("ORG_ID", vo.getOrgId());
//        orgRelationPO.setLong("PARENT_ORG_ID", vo.getParentOrgId());
//        orgRelationPO.setLong("SOURCE_ID", vo.getId());
//        orgRelationPO.setTimestamp("SOURCE_CHANGED_AT", new Date(vo.getChangedAt()));
//    }
//
//
//    /**
//     * 经销商
//    * @author liusong
//    * @date 2017年7月8日
//    * @param dealerInfoPO
//    * @param dealerInfoVO
//     */
//    private void setDealerInfo(DealerInfoPO dealerInfoPO, TmDealerInfoVO dealerInfoVO) {
//        dealerInfoPO.setLong("DEALER_ID", dealerInfoVO.getDealerId());//主建DEALER_ID
//        dealerInfoPO.setLong("COMPANY_ID", dealerInfoVO.getCompanyId());//本身所在公司
//        dealerInfoPO.setLong("GROUP_COMPANY_ID", dealerInfoVO.getGroupCompanyId());//所属集团
//        dealerInfoPO.setLong("FACTORY_COMPANY_ID", dealerInfoVO.getFactoryCompanyId());//销售公司
//        dealerInfoPO.setLong("ORG_ID", dealerInfoVO.getOrgId());//组织
//        dealerInfoPO.setInteger("ORG_TYPE", dealerInfoVO.getOrgType());//组织类型
//        dealerInfoPO.setString("DEALER_CODE", dealerInfoVO.getDealerCode());//经销商代码
//        dealerInfoPO.setString("DEALER_NAME", dealerInfoVO.getDealerName());//经销商名称
//        dealerInfoPO.setString("DEALER_SHORTNAME", dealerInfoVO.getDealerShortname());//经销商简称
//        dealerInfoPO.setString("ADDRESS", dealerInfoVO.getAddress());//详细地址
//        dealerInfoPO.setLong("PROVINCE_ID", dealerInfoVO.getProvinceId());//省
//        dealerInfoPO.setLong("CITY_ID", dealerInfoVO.getCityId());//市
//        dealerInfoPO.setLong("COUNTY_ID", dealerInfoVO.getCountyId());//县
//        dealerInfoPO.setInteger("VALID_STATUS", dealerInfoVO.getValidStatus());//有效状态
//        
//        
//        
//        
//        dealerInfoPO.setTimestamp("SOURCE_CHANGED_AT", new Date(dealerInfoVO.getChangedAt()));
//    }
//
//
//    /**
//     * 
//    * 组织赋值
//    * @author liusong
//    * @date 2017年7月7日
//    * @param organizationPO
//    * @param tmOrgVO
//     */
//    private void setOrganization(OrganizationPO organizationPO, TmOrgVO tmOrgVO) {
//        organizationPO.setLong("ORG_ID", tmOrgVO.getOrgId());//组织ID
//        organizationPO.setLong("COMPANY_ID", tmOrgVO.getCompanyId());//本身所在company_id
//        organizationPO.setString("ORG_CODE", tmOrgVO.getOrgCode());//组织代码
//        organizationPO.setString("ORG_NAME", tmOrgVO.getOrgName());//组织名称
//        organizationPO.setString("ORG_SHORT_NAME", tmOrgVO.getOrgShortName());//组织名称缩写
//        organizationPO.setString("ORG_DESC", tmOrgVO.getOrgDesc());//组织描述
//        organizationPO.setInteger("ORG_TYPE", tmOrgVO.getOrgType());//组织类型
//        organizationPO.setInteger("IS_VALID", tmOrgVO.getValidStatus());//有效状态
//        organizationPO.setTimestamp("SOURCE_CHANGED_AT", new Date(tmOrgVO.getChangedAt()));
//        organizationPO.setInteger("DATA_SOURCE", ManageDictCodeConstants.DATA_SOURCES_BY_SUPER);//数据来源
//        //organizationPO.setInteger("DATA_TYPE", ManageDictCodeConstants.DATA_TYPE_BY_SUPER);//所属范围
//    }
//    
//    
//    /**
//     * 品牌赋值
//    * @author liusong
//    * @date 2017年7月7日
//    * @param dealerOrgRelationPO
//    * @param vo
//     */
//    private void setDealerOrgRelation(DealerOrgRelationPO dealerOrgRelationPO, TrOrgBrandVO vo) {
//        List<Object> queryParams = new ArrayList<>();
//        queryParams.add(vo.getBrandId());
//        Map map = DAOUtil4DMS.findFirst("select BRAND_ID from tm_brand where SOURCE_ID = ? ",queryParams);
//        if(!CommonUtils.isNullOrEmpty(map)){
//            dealerOrgRelationPO.setLong("BILL_ID", map.get("BRAND_ID"));//品牌
//        }
//        dealerOrgRelationPO.setLong("ORG_ID", vo.getOrgId());//组织
//        dealerOrgRelationPO.setLong("PARENT_ORG_ID", vo.getParentOrgId());//
//        dealerOrgRelationPO.setInteger("BILL_TYPE", ManageDictCodeConstants.ORG_TYPE_DEALER_BRAND);//类型
//        dealerOrgRelationPO.setLong("SOURCE_ID", vo.getTrId());
//        dealerOrgRelationPO.setTimestamp("SOURCE_CHANGED_AT", new Date(vo.getChangedAt()));
//    }
//
//    /**
//     * 插入oem 组织Organization
//    * @author liusong
//    * @date 2017年7月10日
//    * @param msgVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrOrganization(com.yonyou.dmsgms.manage.domains.vo.TmOrgVO)
//     */
//    @Override
//    public void addExtrOrganization(TmOrgVO tmOrgVO) {
//        OrganizationPO organizationPO  = OrganizationPO.findById(tmOrgVO.getOrgId());
//        if(organizationPO == null){
//            organizationPO = new OrganizationPO();
//            this.setOrganization(organizationPO,tmOrgVO);
//            organizationPO.insert();
//        }else{
//            if(organizationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmOrgVO.getChangedAt()))){
//                this.setOrganization(organizationPO,tmOrgVO);
//                organizationPO.saveIt();
//            }
//        }
//    }
//
//    /**
//     * 插入oem 组织ascinfo
//    * @author liusong
//    * @date 2017年7月10日
//    * @param tmAscInfoVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrAscInfo(com.yonyou.dmsgms.manage.domains.vo.TmAscInfoVO)
//     */
//    @Override
//    public void addExtrAscInfo(TmAscInfoVO tmAscInfoVO) {
//        //维修站所属组织
//        TmOrgVO tmOrgVO = tmAscInfoVO.getMyOrgVo();
//        if(tmOrgVO != null){
//            OrganizationPO organizationPO  = OrganizationPO.findById(tmOrgVO.getOrgId());
//            if(organizationPO == null){
//                organizationPO = new OrganizationPO();
//                this.setOrganization(organizationPO,tmOrgVO);
//                organizationPO.insert();
//            }else{
//                if(tmOrgVO.getChangedAt() != null){
//                    if(organizationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmOrgVO.getChangedAt()))){
//                        this.setOrganization(organizationPO,tmOrgVO);
//                        organizationPO.saveIt();
//                    }
//                }
//            }
//        }
//        //维修
//        AscInfoPO ascInfoPO = AscInfoPO.findById(tmAscInfoVO.getAscId());
//        if(ascInfoPO  == null){
//            ascInfoPO = new AscInfoPO();
//            this.setAscInfo(ascInfoPO, tmAscInfoVO);
//            ascInfoPO.insert();
//        }else{
//            if(ascInfoPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(tmAscInfoVO.getChangedAt()))){
//                this.setAscInfo(ascInfoPO,tmAscInfoVO);
//                ascInfoPO.saveIt();
//            }
//        }
//       
//        //小区与维修站关系
//        TrOrgVO areaOrgVo = tmAscInfoVO.getAreaOrgVo();
//        if(areaOrgVo != null){
//            this.getOrgRelation(areaOrgVo);
//        }
//        //集团和维修站公司关系
//        TrOrgVO ascComOrgVo = tmAscInfoVO.getAscComOrgVo();
//        if(ascComOrgVo != null){
//            this.getOrgRelation(ascComOrgVo);
//        }
//        //经销商公司与维修站关系
//        TrOrgVO dealerAscVo = tmAscInfoVO.getDealerAscVo();
//        if(dealerAscVo != null){
//            this.getOrgRelation(dealerAscVo);
//        }
//        //小区与维修站关系
//        TrOrgVO unStationAscVo = tmAscInfoVO.getUnStationAscVo();
//        if(unStationAscVo != null){
//            this.getOrgRelation(unStationAscVo);
//        }
//        //授权车系
//        List<TmAscClassVO> ascClassVos = tmAscInfoVO.getAscClassVos();
//        if(!CommonUtils.isNullOrEmpty(ascClassVos)){
//            for(TmAscClassVO vo: ascClassVos){
//                DealerOrgRelationPO dealerOrgRelationPO  = DealerOrgRelationPO.findFirst("SOURCE_ID = ?", vo.getAscClassId());
//                if(dealerOrgRelationPO == null){
//                    dealerOrgRelationPO = new  DealerOrgRelationPO();
//                    this.setDealerOrgRelation(dealerOrgRelationPO,vo);
//                    dealerOrgRelationPO.saveIt();
//                }else{
//                    if(dealerOrgRelationPO.getTimestamp("SOURCE_CHANGED_AT").before(new Date(vo.getChangedAt()))){
//                        this.setDealerOrgRelation(dealerOrgRelationPO,vo);
//                        dealerOrgRelationPO.saveIt();
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     *车系赋值
//    * @author liusong
//    * @date 2017年7月10日
//    * @param dealerOrgRelationPO
//    * @param vo
//     */
//    private void setDealerOrgRelation(DealerOrgRelationPO dealerOrgRelationPO, TmAscClassVO vo) {
//        List<Object> queryParams = new ArrayList<>();
//        queryParams.add(vo.getAscClassCode());
//        Map map = DAOUtil4DMS.findFirst("select SERIES_ID from tm_series where SERIES_CODE = ? ",queryParams);
//        if(!CommonUtils.isNullOrEmpty(map)){
//            dealerOrgRelationPO.setLong("BILL_ID", map.get("SERIES_ID"));//车系
//        }
//        AscInfoPO ascPo = AscInfoPO.findFirst("ASC_CODE = ? ", vo.getAscCode());
//        if(ascPo != null){
//            dealerOrgRelationPO.setLong("ORG_ID", ascPo.getLong("ORG_ID"));//组织
//        }
//        dealerOrgRelationPO.setString("ASC_CODE", vo.getAscCode());//维修站代码
//        dealerOrgRelationPO.setInteger("BILL_TYPE", ManageDictCodeConstants.ORG_TYPE_ASC_SERIES);//类型
//        dealerOrgRelationPO.setLong("SOURCE_ID", vo.getAscClassId());
//        dealerOrgRelationPO.setTimestamp("SOURCE_CHANGED_AT", new Date(vo.getChangedAt()));
//    }
//
//
//    /**
//     * 维修组织赋值
//    * @author liusong
//    * @date 2017年7月10日
//    * @param ascInfoPO
//    * @param tmAscInfoVO
//     */
//    private void setAscInfo(AscInfoPO ascInfoPO, TmAscInfoVO vo) {
//        ascInfoPO.setLong("ASC_ID", vo.getAscId());//主建
//        ascInfoPO.setLong("COMPANY_ID", vo.getCompanyId());//本身所在company_id
//        ascInfoPO.setLong("FACTORY_COMPANY_ID", vo.getFactoryCompanyId());//销售公司Company_id
//        ascInfoPO.setLong("ORG_ID", vo.getOrgId());//组织
//        ascInfoPO.setInteger("ORG_TYPE", vo.getOrgType());//组织类型
//        ascInfoPO.setString("ASC_CODE", vo.getAscCode());//维修站代码
//        ascInfoPO.setString("ASC_RANK", vo.getAscRank());//维修站类型
//        ascInfoPO.setString("ASC_NAME", vo.getAscName());//维修站名称
//        ascInfoPO.setString("ASC_SHORT_NAME", vo.getAscShortName());//维修站简称
//        ascInfoPO.setTimestamp("SOURCE_CHANGED_AT", new Date(vo.getChangedAt()));//来源日期
//    }
//
//    /**
//     * 插入oem 公司映射
//    * @author liusong
//    * @date 2017年8月8日
//    * @param msgVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrCompanyMapping(com.yonyou.dmsgms.manage.domains.vo.TmCompanyVO)
//     */
//    @Override
//    public void addExtrCompanyMapping(TmCompanyVO msgVO) {
//        //租户与经销商
//        StringBuilder sbSql = new StringBuilder();
//        sbSql.append("select TENANT_ID,TENANTMAPPING_CODE,TENANTMAPPING_NAME,RECORD_VERSION from f4_tenant_mapping where TENANTMAPPING_CODE = ? ");
//        List<Object> params = new ArrayList<Object>();
//        params.add(msgVO.getCompanyCode());
//        List<Map> listMaps = Base.findAll(sbSql.toString(), params.toArray());
//        if(!CommonUtils.isNullOrEmpty(listMaps)){
//            if(!StringUtils.isNullOrEmpty(msgVO.getTenantId())){
//                StringBuilder sqlUpd = new StringBuilder();
//                sqlUpd.append("update f4_tenant_mapping set ");
//                sqlUpd.append("TENANT_ID = ? , TENANTMAPPING_NAME = ?, UPDATED_BY = ?, UPDATED_AT = ?  ");
//                sqlUpd.append("where TENANTMAPPING_CODE = ? ");
//                params.clear();
//                params.add(msgVO.getTenantId());
//                params.add(msgVO.getCompanyNameCn());
//                params.add(-1 );
//                params.add(new Date());
//                params.add(msgVO.getCompanyCode());
//                Base.exec(sqlUpd.toString(), params.toArray());
//            }
//        }else{
//            if(!StringUtil.isNullOrEmpty(msgVO.getTenantId())){
//                StringBuilder sqlInt = new StringBuilder();
//                sqlInt.append("insert into f4_tenant_mapping (");
//                sqlInt.append("TENANT_ID,TENANTMAPPING_NAME,TENANTMAPPING_CODE,RECORD_VERSION,CREATED_BY,CREATED_AT) ");
//                sqlInt.append("value(?,?,?,?,?,?) ");
//                params.clear();
//                params.add(msgVO.getTenantId());
//                params.add(msgVO.getCompanyNameCn());
//                params.add(msgVO.getCompanyCode());
//                params.add(0);
//                params.add(-1 );
//                params.add(new Date());
//                Base.exec(sqlInt.toString(), params.toArray());
//            }
//        }
//        this.companyMapping(msgVO.getCompanyCode(), msgVO.getCompanyCode(), msgVO.getCompanyNameCn());
//    }
//    /**
//     * 租户与经销商映射
//    * @author liusong
//    * @date 2017年8月9日
//    * @param companyCode
//    * @param ownerCode
//    * @param companyName
//     */
//    private void companyMapping(String companyCode, String ownerCode,String companyName){
//        //租房与经销商
//        StringBuilder sbSql = new StringBuilder();
//        sbSql.append("select TENANTMAPPING_CODE,OWNER_CODE,OWNER_SHORTNAME,RECORD_VERSION from f4_tenant_dealer_mapping where TENANTMAPPING_CODE = ? and OWNER_CODE = ? ");
//        List<Object> params = new ArrayList<Object>();
//        params.add(companyCode);
//        params.add(ownerCode);
//        List<Map> listMaps = Base.findAll(sbSql.toString(), params.toArray());
//        if(!CommonUtils.isNullOrEmpty(listMaps)){
//            StringBuilder sqlUpd = new StringBuilder();
//            sqlUpd.append("update f4_tenant_dealer_mapping set ");
//            sqlUpd.append("OWNER_SHORTNAME = ?, UPDATED_BY = ?, UPDATED_AT = ?  ");
//            sqlUpd.append("where TENANTMAPPING_CODE = ? and OWNER_CODE = ? ");
//            params.clear();
//            params.add(companyName);
//            params.add(-1 );
//            params.add(new Date());
//            params.add(companyCode);
//            params.add(ownerCode);
//            Base.exec(sqlUpd.toString(), params.toArray());
//        }else{
//            StringBuilder sqlInt = new StringBuilder();
//            sqlInt.append("insert into f4_tenant_dealer_mapping (");
//            sqlInt.append("TENANTMAPPING_CODE,OWNER_CODE,OWNER_SHORTNAME,RECORD_VERSION,CREATED_BY,CREATED_AT) ");
//            sqlInt.append("value(?,?,?,?,?,?) ");
//            params.clear();
//            params.add(companyCode);
//            params.add(ownerCode);
//            params.add(companyName);
//            params.add(0);
//            params.add(-1 );
//            params.add(new Date());
//            Base.exec(sqlInt.toString(), params.toArray());
//        }
//    }
//
//    /**
//     * 插入oem 经销商映射
//    * @author liusong
//    * @date 2017年8月9日
//    * @param msgVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrDealerMapping(com.yonyou.dmsgms.manage.domains.vo.TmDealerInfoVO)
//     */
//    @Override
//    public void addExtrDealerMapping(TmDealerInfoVO dealerInfoVO) {
//        this.companyMapping(dealerInfoVO.getCompanyCode(), dealerInfoVO.getDealerCode(), dealerInfoVO.getDealerName()); 
//    }
//
//
//    /**
//     * 插入oem 维修站映射
//    * @author liusong
//    * @date 2017年8月10日
//    * @param msgVO
//    * (non-Javadoc)
//    * @see com.yonyou.dmsgms.manage.service.basedata.org.OrganizationService#addExtrAscMapping(com.yonyou.dmsgms.manage.domains.vo.TmAscInfoVO)
//     */
//    @Override
//    public void addExtrAscMapping(TmAscInfoVO msgVO) {
//        this.companyMapping(msgVO.getCompanyCode(), msgVO.getAscCode(), msgVO.getAscShortName()); 
//    }
//    
//}
