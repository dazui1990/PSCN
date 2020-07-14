package com.yonyou.dms.manage.domains.DTO.basedata;

import java.util.Date;

public class ReminderContentDto {

	private Date createddate;
	
	private Date enddate;
	
	private String remindercontent;

	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getRemindercontent() {
		return remindercontent;
	}

	public void setRemindercontent(String remindercontent) {
		this.remindercontent = remindercontent;
	}
	
	
}
