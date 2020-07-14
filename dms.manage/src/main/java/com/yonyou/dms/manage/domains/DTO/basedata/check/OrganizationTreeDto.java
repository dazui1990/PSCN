
/** 
*Copyright 2017 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dmsgms.manage
*
* @File name : OrganizationTreeDto.java
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

/**
* 树状图Dto
* @author RenWeiDong
* @date 2017年5月27日
*/

public class OrganizationTreeDto implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String parent;
    private String text;
    private String code;//
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
