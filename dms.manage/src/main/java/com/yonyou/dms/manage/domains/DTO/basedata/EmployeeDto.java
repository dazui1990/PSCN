/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.manage
*
* @File name : EmployeeDto.java
*
* @Author : jcsi
*
* @Date : 2016年7月8日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年7月8日    jcsi    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/


package com.yonyou.dms.manage.domains.DTO.basedata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yonyou.dms.function.utils.validate.define.Email;
import com.yonyou.dms.function.utils.validate.define.IDNumber;
import com.yonyou.dms.function.utils.validate.define.Phone;
import com.yonyou.dms.function.utils.validate.define.Required;
import com.yonyou.dms.function.utils.validate.define.ZipCode;



/**
 * 员工信息
* @author jcsi
* @date 2016年7月6日
 */
public class EmployeeDto implements Serializable{
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Required
    private String employeeNo;  //员工编号
    
    @Required
    private String employeeName;//员工姓名
    
    //@Required  废弃
    private String orgCode;//ORG_CODE  部门
    
    @Required
    private int organizationId;  //部门
    
    private String positionCode;//POSITION_CODE 职务
    
    private Long gender;//GENDER  性别
    

    private Date birthday;//BIRTHDAY  出生日期
    
    @IDNumber
    private String certificateId;//CERTIFICATE_ID  身份证号
    
    @Required
    @Phone
    private String mobile;//MOBILE  手机
    
    private String phone;//PHONE  电话
    
    @Email
    private String eMail;//E_MAIL 
    
    private String address;//ADDRESS  //地址
    
    @ZipCode
    private String zipCode;//ZIP_CODE  邮编
    
    private Long isOnjob;//  在职状态
    
  
    private Date dimissionDate;//DIMISSION_DATE 离职日期
    
   
    private Date foundDate;//建档日期
    
    private String workerTypeCode;//WORKER_TYPE_CODE  工种
    
    private Long technicianGrade;//TECHNICIAN_GRADE 技师等级
    
    private String defaultPosition;//DEFAULT_POSITION  主维修工位
    
    private String workgroupCode;  //班组
    
    
    private List<String> employeeRoles= new ArrayList<>();

    
    
    
   


    
    
    public String getWorkgroupCode() {
        return workgroupCode;
    }



    
    public void setWorkgroupCode(String workgroupCode) {
        this.workgroupCode = workgroupCode;
    }



    public List<String> getEmployeeRoles() {
        return employeeRoles;
    }


    
    public void setEmployeeRoles(List<String> employeeRoles) {
        this.employeeRoles = employeeRoles;
    }


    public String getEmployeeNo() {
        return employeeNo;
    }

    
    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    
    public String getEmployeeName() {
        return employeeName;
    }

    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    
    public String getOrgCode() {
        return orgCode;
    }

    
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    
   

    
    public Date getBirthday() {
        return birthday;
    }

    
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    
    public String getCertificateId() {
        return certificateId;
    }

    
    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    
    public String getMobile() {
        return mobile;
    }

    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    
    public String getPhone() {
        return phone;
    }

    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    public String geteMail() {
        return eMail;
    }

    
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }

    
    public String getZipCode() {
        return zipCode;
    }

    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    
    

    
    
   


    public Date getDimissionDate() {
        return dimissionDate;
    }

    
    public void setDimissionDate(Date dimissionDate) {
        this.dimissionDate = dimissionDate;
    }

    
    public String getWorkerTypeCode() {
        return workerTypeCode;
    }

    
    public void setWorkerTypeCode(String workerTypeCode) {
        this.workerTypeCode = workerTypeCode;
    }

    
   


    
    public Date getFoundDate() {
        return foundDate;
    }


    
    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }



    
    public String getPositionCode() {
        return positionCode;
    }



    
    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }



    
    public Long getGender() {
        return gender;
    }



    
    public void setGender(Long gender) {
        this.gender = gender;
    }



    
    public Long getIsOnjob() {
        return isOnjob;
    }



    
    public void setIsOnjob(Long isOnjob) {
        this.isOnjob = isOnjob;
    }



    
    public Long getTechnicianGrade() {
        return technicianGrade;
    }



    
    public void setTechnicianGrade(Long technicianGrade) {
        this.technicianGrade = technicianGrade;
    }



    
    public String getDefaultPosition() {
        return defaultPosition;
    }



    
    public void setDefaultPosition(String defaultPosition) {
        this.defaultPosition = defaultPosition;
    }




	public int getOrganizationId() {
		return organizationId;
	}




	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}


    

    
    
    
    

}
