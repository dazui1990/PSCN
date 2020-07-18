package com.yonyou.dms.manage.domains.DTO.dudget;

import java.io.Serializable;

import com.yonyou.dms.framework.service.excel.ExcelColumnDefine;
import com.yonyou.dms.function.domains.DTO.DataImportDto;

public class BudgetApplicationImportDto extends DataImportDto implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@ExcelColumnDefine(value = 1)
	private String week;
	
	@ExcelColumnDefine(value = 2)
	private String pr_no;
	
	@ExcelColumnDefine(value = 3)
	private String pr_item;
	
	@ExcelColumnDefine(value = 4)
	private String pr_amount;
	
	@ExcelColumnDefine(value = 5)
	private String cn0g_io;
	
	@ExcelColumnDefine(value = 6)
	private String cn0f_io;
	
	@ExcelColumnDefine(value = 7)
	private String remark;

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getPr_no() {
		return pr_no;
	}

	public void setPr_no(String pr_no) {
		this.pr_no = pr_no;
	}

	public String getPr_item() {
		return pr_item;
	}

	public void setPr_item(String pr_item) {
		this.pr_item = pr_item;
	}

	public String getPr_amount() {
		return pr_amount;
	}

	public void setPr_amount(String pr_amount) {
		this.pr_amount = pr_amount;
	}

	public String getCn0g_io() {
		return cn0g_io;
	}

	public void setCn0g_io(String cn0g_io) {
		this.cn0g_io = cn0g_io;
	}

	public String getCn0f_io() {
		return cn0f_io;
	}

	public void setCn0f_io(String cn0f_io) {
		this.cn0f_io = cn0f_io;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "BudgetApplicationImportDto [week=" + week + ", pr_no=" + pr_no + ", pr_item=" + pr_item + ", pr_amount="
				+ pr_amount + ", cn0g_io=" + cn0g_io + ", cn0f_io=" + cn0f_io + ", remark=" + remark + "]";
	}
	
}
