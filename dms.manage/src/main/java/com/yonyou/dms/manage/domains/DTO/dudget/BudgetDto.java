package com.yonyou.dms.manage.domains.DTO.dudget;

import java.io.Serializable;

import com.yonyou.dms.framework.service.excel.ExcelColumnDefine;
import com.yonyou.dms.function.domains.DTO.DataImportDto;

//预算导入返回值
public class BudgetDto extends DataImportDto implements Serializable {
	private static final long serialVersionUID = 1L;
	// 子部门
	@ExcelColumnDefine(value = 1)
	private String SUB_FUNCTION;

	// 部门
	@ExcelColumnDefine(value = 2)
	private String fucntion;

	// 预算项目
	@ExcelColumnDefine(value = 3)
	private String budget_item;

	// 类型
	@ExcelColumnDefine(value = 4)
	private String item_type;

	// 一月
	@ExcelColumnDefine(value = 5)
	private String jan;

	// 二月
	@ExcelColumnDefine(value = 6)
	private String feb;

	// 三月
	@ExcelColumnDefine(value = 7)
	private String mar;

	// 四月
	@ExcelColumnDefine(value = 8)
	private String apr;

	// 五月
	@ExcelColumnDefine(value = 9)
	private String may;

	// 六月
	@ExcelColumnDefine(value = 10)
	private String jun;

	// 七月
	@ExcelColumnDefine(value = 11)
	private String jul;

	// 八月
	@ExcelColumnDefine(value = 12)
	private String aug;

	// 九月
	@ExcelColumnDefine(value = 13)
	private String sep;

	// 十月
	@ExcelColumnDefine(value = 14)
	private String oct;

	// 十一月
	@ExcelColumnDefine(value = 15)
	private String nov;

	// 十二月
	@ExcelColumnDefine(value = 16)
	private String dec;

	
	
	public String getSUB_FUNCTION() {
		return SUB_FUNCTION;
	}

	public void setSUB_FUNCTION(String sUB_FUNCTION) {
		SUB_FUNCTION = sUB_FUNCTION;
	}

	public String getFucntion() {
		return fucntion;
	}

	public void setFucntion(String fucntion) {
		this.fucntion = fucntion;
	}

	public String getBudget_item() {
		return budget_item;
	}

	public void setBudget_item(String budget_item) {
		this.budget_item = budget_item;
	}

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}



	public String getJan() {
		return jan;
	}

	public void setJan(String jan) {
		this.jan = jan;
	}

	public String getFeb() {
		return feb;
	}

	public void setFeb(String feb) {
		this.feb = feb;
	}

	public String getMar() {
		return mar;
	}

	public void setMar(String mar) {
		this.mar = mar;
	}

	public String getApr() {
		return apr;
	}

	public void setApr(String apr) {
		this.apr = apr;
	}

	public String getMay() {
		return may;
	}

	public void setMay(String may) {
		this.may = may;
	}

	public String getJun() {
		return jun;
	}

	public void setJun(String jun) {
		this.jun = jun;
	}

	public String getJul() {
		return jul;
	}

	public void setJul(String jul) {
		this.jul = jul;
	}

	public String getAug() {
		return aug;
	}

	public void setAug(String aug) {
		this.aug = aug;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}

	public String getOct() {
		return oct;
	}

	public void setOct(String oct) {
		this.oct = oct;
	}

	public String getNov() {
		return nov;
	}

	public void setNov(String nov) {
		this.nov = nov;
	}

	public String getDec() {
		return dec;
	}

	public void setDec(String dec) {
		this.dec = dec;
	}
	
	
	@Override
	public String toString() {
		return "BudgetDto [SUB_FUNCTION=" + SUB_FUNCTION + ", fucntion=" + fucntion + ", budget_item=" + budget_item
				+ ", item_type=" + item_type + ", jan=" + jan + ", feb=" + feb + ", mar=" + mar + ", apr=" + apr
				+ ", may=" + may + ", jun=" + jun + ", jul=" + jul + ", aug=" + aug + ", sep=" + sep + ", oct=" + oct
				+ ", nov=" + nov + ", dec=" + dec + "]";
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
