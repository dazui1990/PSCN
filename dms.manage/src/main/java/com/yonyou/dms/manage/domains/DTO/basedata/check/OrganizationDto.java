
/** 
*Copyright 2017 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dmsgms.manage
*
* @File name : OrganizationDto.java
*
* @Author : RenWeiDong
*
* @Date : 2017年5月27日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2017年5月27日    RenWeiDong    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.manage.domains.DTO.basedata.check;
import java.io.Serializable;
import java.util.Date;

/**
 * 部门
 * 
 * @author RenWeiDong
 * @date 2017年5月27日
 */

public class OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer           organizationId;
    private String            dealerCode;
    private Integer           parentOrgId;
    private String            parentOrgCode;
    private String            orgCode;
    private String            orgName;
    private String            orgShortName;
    private String            orgDesc;
    private String            orgType;
    private Integer           recordVersion;
    private String            createdBy;
    private Date              createAt;
    private String            updateBy;
    private Date              updateAt;
    private Integer           isValid;
    private Integer           dataSource;
    private Integer           dataType;             // 所属范围

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public Integer getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Integer parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getParentOrgCode() {
        return parentOrgCode;
    }

    public void setParentOrgCode(String parentOrgCode) {
        this.parentOrgCode = parentOrgCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getorgShortName() {
        return orgShortName;
    }

    public void setorgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Integer getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Integer recordVersion) {
        this.recordVersion = recordVersion;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }
}
