package com.yonyou.dms.manage.domains.DTO.dudget;

import java.io.Serializable;

import com.yonyou.dms.framework.service.excel.ExcelColumnDefine;
import com.yonyou.dms.function.domains.DTO.DataImportDto;

public class AccruaInfoImportDto extends DataImportDto implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@ExcelColumnDefine(value = 1)
	private String accrualMonth;
	
	@ExcelColumnDefine(value = 2)
	private String functionName;
	
	@ExcelColumnDefine(value = 3)
	private String shoppingCart;
	
	@ExcelColumnDefine(value = 4)
	private String costCenter;
	
	@ExcelColumnDefine(value = 5)
	private String gl;
	
	@ExcelColumnDefine(value = 6)
	private String shoppingCartName;
	
	@ExcelColumnDefine(value = 7)
	private String io;
	
	@ExcelColumnDefine(value = 8)
	private String prAmont;
	
	@ExcelColumnDefine(value = 9)
	private String grAmont;
	
	@ExcelColumnDefine(value = 10)
	private String poGr;
	
	@ExcelColumnDefine(value = 11)
	private String accrualReason;
	
	@ExcelColumnDefine(value = 12)
	private String amount;

	public String getAccrualMonth() {
		return accrualMonth;
	}

	public void setAccrualMonth(String accrualMonth) {
		this.accrualMonth = accrualMonth;
	}

	public String getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(String shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	public String getAccrualReason() {
		return accrualReason;
	}

	public void setAccrualReason(String accrualReason) {
		this.accrualReason = accrualReason;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getGl() {
		return gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}

	public String getShoppingCartName() {
		return shoppingCartName;
	}

	public void setShoppingCartName(String shoppingCartName) {
		this.shoppingCartName = shoppingCartName;
	}

	public String getIo() {
		return io;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public String getPrAmont() {
		return prAmont;
	}

	public void setPrAmont(String prAmont) {
		this.prAmont = prAmont;
	}

	public String getGrAmont() {
		return grAmont;
	}

	public void setGrAmont(String grAmont) {
		this.grAmont = grAmont;
	}

	public String getPoGr() {
		return poGr;
	}

	public void setPoGr(String poGr) {
		this.poGr = poGr;
	}

	@Override
	public String toString() {
		return "AccruaInfoImportDto [accrualMonth=" + accrualMonth + ", functionName=" + functionName
				+ ", shoppingCart=" + shoppingCart + ", costCenter=" + costCenter + ", gl=" + gl + ", shoppingCartName="
				+ shoppingCartName + ", io=" + io + ", prAmont=" + prAmont + ", grAmont=" + grAmont + ", poGr=" + poGr
				+ ", accrualReason=" + accrualReason + ", amount=" + amount + "]";
	}

}
