package com.yonyou.dms.manage.domains.DTO.dudget;

import java.io.Serializable;
import java.util.Date;

import com.yonyou.dms.framework.service.excel.ExcelColumnDefine;
import com.yonyou.dms.function.domains.DTO.DataImportDto;

public class BudgetExtendImportDto extends DataImportDto implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@ExcelColumnDefine(value = 1)
	private String purchaseOrder;
	
	@ExcelColumnDefine(value = 2)
	private String shoppingCart;
	
	@ExcelColumnDefine(value = 3)
	private String shoppingCartName;
	
	@ExcelColumnDefine(value = 4)
	private String internalOrder;
	
	@ExcelColumnDefine(value = 5)
	private Date PoPostingDate;
	
	@ExcelColumnDefine(value = 6)
	private String approvedShoppingCartValue;
	
	@ExcelColumnDefine(value = 7)
	private String netOrderValueInOrderCurrency;
	
	@ExcelColumnDefine(value = 8)
	private String netConfirmedValueInOrderCurrency;

	public String getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public String getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(String shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	public String getShoppingCartName() {
		return shoppingCartName;
	}

	public void setShoppingCartName(String shoppingCartName) {
		this.shoppingCartName = shoppingCartName;
	}

	public String getInternalOrder() {
		return internalOrder;
	}

	public void setInternalOrder(String internalOrder) {
		this.internalOrder = internalOrder;
	}
	
	public Date getPoPostingDate() {
		return PoPostingDate;
	}

	public void setPoPostingDate(Date poPostingDate) {
		PoPostingDate = poPostingDate;
	}

	public String getApprovedShoppingCartValue() {
		return approvedShoppingCartValue;
	}

	public void setApprovedShoppingCartValue(String approvedShoppingCartValue) {
		this.approvedShoppingCartValue = approvedShoppingCartValue;
	}

	public String getNetOrderValueInOrderCurrency() {
		return netOrderValueInOrderCurrency;
	}

	public void setNetOrderValueInOrderCurrency(String netOrderValueInOrderCurrency) {
		this.netOrderValueInOrderCurrency = netOrderValueInOrderCurrency;
	}

	public String getNetConfirmedValueInOrderCurrency() {
		return netConfirmedValueInOrderCurrency;
	}

	public void setNetConfirmedValueInOrderCurrency(String netConfirmedValueInOrderCurrency) {
		this.netConfirmedValueInOrderCurrency = netConfirmedValueInOrderCurrency;
	}

	@Override
	public String toString() {
		return "BudgetExtendImportDto [purchaseOrder=" + purchaseOrder + ", shoppingCart=" + shoppingCart
				+ ", shoppingCartName=" + shoppingCartName + ", internalOrder=" + internalOrder + ", PoPostingDate="
				+ PoPostingDate + ", approvedShoppingCartValue=" + approvedShoppingCartValue
				+ ", netOrderValueInOrderCurrency=" + netOrderValueInOrderCurrency
				+ ", netConfirmedValueInOrderCurrency=" + netConfirmedValueInOrderCurrency + "]";
	}

}
