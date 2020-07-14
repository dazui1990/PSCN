"use strict";
/**
 * 配件模块相关功能
 */
var dmsPart = function() {
	var showOutPrice = function(priceType,container){
		var price="";
		//销售价
		if(priceType=="13111001"){
			price = $("#salesPrice",container).val();
		}
		//建议销售价
		if(priceType=="13111002"){
			price = $("#adviceSalePrice",container).val();
		}
		//索赔价
		if(priceType=="13111003"){
			price = $("#claimPrice",container).val();
		}
		//销售限价
		if(priceType=="13111004"){
			price = $("#limitPrice",container).val();
		}
		//成本单价
		if(priceType=="13111005"){
			price = $("#costPrice",container).val();
		}
		$("#price",container).valChange(price);
	};
	
	var showOutPrice2 = function(priceType,container){
		var paramprice="";
		//销售价
		if(priceType=="13091002"){
			paramprice = $("#salesPrice",container).val();
			$("#paramprices",container).attr("data-fieldName","SALES_PRICE");
		}
		//索赔价
		if(priceType=="13091003"){
			paramprice = $("#claimPrice",container).val();
			$("#paramprices",container).attr("data-fieldName","CLAIM_PRICE");
		}
		//成本单价
		if(priceType=="13091001"){
			paramprice = $("#costPrice",container).val();
			$("#paramprices",container).attr("data-fieldName","COST_PRICE");
		}
		$("#paramprices",container).valChange(paramprice);
	};

	return {
		priceShow:function(priceType,container){
			return showOutPrice(priceType,container);
		},
		priceShow2:function(priceType,container){
			return showOutPrice2(priceType,container);
		}
		
	};
}();

/**
 * 维修模块相关功能
 */
var dmsRepair = function() {
	//加载维修项目下拉框数据
	var initLabourData = function(){
		var getTableRows = $("#labourList",getElementContext()).dmsTable().getTableRows();
		var repairPro = $("#labourList",getElementContext()).dmsTable().getRowDataByIndex();
		var selectData = new Array();
		for(var i=0;i<getTableRows;i++){
			if(repairPro[i].RO_LABOUR_ID!=undefined&&repairPro[i].RO_LABOUR_ID!=""){
				selectData.push({id:repairPro[i].RO_LABOUR_ID+":"+repairPro[i].labourCode,name:(i+1)+"-"+repairPro[i].labourName});
			}else if(repairPro[i].orderNum!=undefined&&repairPro[i].orderNum!=""){
				selectData.push({id:repairPro[i].orderNum+":"+repairPro[i].labourCode,name:(i+1)+"-"+repairPro[i].labourName});
			}
		}
		return selectData;
	};
	//费用结算为查询到数据不可编辑
	var addDisabled = function(container){
		$(".disDiv a",container).attr("disabled","disabled");
		//$("#remark",container).setElementReadOnly();
		$("#remark",container).attr("disabled","disabled");
		$("#discountModeCode",container).setElementReadOnly();
		return "";
	};
	
	//费用结算查询到数据后可编辑
	var removeDisabled = function(){
		//
		$(".disDiv a",getElementContext()).removeAttr("disabled");
		$("#remark",getElementContext()).removeAttr("disabled");
		$("#discountModeCode",getElementContext()).removeElementReadOnly();
	};
	//计算维修材料费
	var calRepairPartAmount=function(curPartAmount){
		//计算材料费		
		var oldDisRepairPartAmount= $("#disRepairPartAmount",getElementContext()).val(); //折后维修材料费（上次总和）
		var oldRepairPartAmount=$("#repairPartAmount",getElementContext()).val();//维修材料费 （上次总和）
		//var partAmount=parseFloat($("#salesAmountShow",container).val());    //金额
		var partAmount=parseFloat(curPartAmount);
		if(oldDisRepairPartAmount==null||oldDisRepairPartAmount==""){
			oldDisRepairPartAmount=0;				
		}
		if(oldRepairPartAmount==null||oldRepairPartAmount==""){
			oldRepairPartAmount=0;
		}
		var disRepairPartAmount=parseFloat(oldDisRepairPartAmount) +partAmount;
		var repairPartAmount=parseFloat(oldRepairPartAmount)+partAmount;
		
		var map={dis_repair_part_amount:disRepairPartAmount.toFixed(2),repair_part_amount:repairPartAmount.toFixed(2)};
		$("div[data-partAmount='true']",getElementContext()).initHtmlContent(map,false);
	};
	var appendRowInRolabour=function(repairPro,workOrderType,selectData,receiveMoney,i,labourPrice){
		var workOrderRepairProFlag = true;
		var workOrderRepairPro = {};
		if("12121003" == workOrderType){
			workOrderRepairPro.chargePartitionName ="S";
			workOrderRepairProFlag = false;
		}
		workOrderRepairPro.assignLabourHour=repairPro[i].ASSIGN_LABOUR_HOUR;
		workOrderRepairPro.repairType=repairPro[i].REPAIR_TYPE_CODE;
		workOrderRepairPro.labourName=repairPro[i].LABOUR_NAME;
		workOrderRepairPro.labourCode=repairPro[i].LABOUR_CODE;
		workOrderRepairPro.stdHour=repairPro[i].STD_LABOUR_HOUR;
		workOrderRepairPro.workType=repairPro[i].WORKER_TYPE_CODE;
		workOrderRepairPro.modeGroup=repairPro[i].MODEL_LABOUR_CODE;
		workOrderRepairPro.localLabourCode=repairPro[i].LOCAL_LABOUR_CODE;
		workOrderRepairPro.localLabourName=repairPro[i].LOCAL_LABOUR_NAME;
		workOrderRepairPro.workHourSinglePrice = labourPrice;
		workOrderRepairPro.discountRate = discountMode;
		var workHourPriceF = parseFloat(repairPro[i].STD_LABOUR_HOUR) *  parseFloat(labourPrice);
		workOrderRepairPro.workHourPrice =  workHourPriceF; //工时费
		selectData.push({id:repairPro[i].LABOUR_CODE,name:repairPro[i].LABOUR_NAME});
		var discountMoneyF = workHourPriceF * (parseFloat(1) - parseFloat(discountMode));
		workOrderRepairPro.discountMoney = discountMoneyF.toFixed(2);//优惠金额
		if(workOrderRepairProFlag){
			var receiveMoneyF = workHourPriceF - discountMoneyF
			receiveMoney = receiveMoneyF.toFixed(2);
			workOrderRepairPro.receiveMoney = receiveMoney;
		}else{
			workOrderRepairPro.receiveMoney = receiveMoney.toFixed(2);
		}
		
		var appRowTr = $("#labourList",getElementContext()).dmsTable().appendRow(workOrderRepairPro);
		var tableRow = $("#labourList",getElementContext()).dmsTable().getTableRows();
		if("12121003" == workOrderType){
			
		}else{
			var roTypeSet=$("#roTypeSet",getElementContext()).val();
			if(roTypeSet=='10131001'){
				dmsDict.refreshSelectByFunction($("select[id^=chargePartitionName]",appRowTr),function(selectObj){
					$(selectObj).find("option[value='S']").remove();
				});
			}
		}
		$("select[id^=chargePartitionName]",appRowTr).bindChange(function(obj){
			
			
			var index=$(obj).attr("id").substr($(obj).attr("id").length-1);
			if($(obj).val()=="S"||$(obj).val()=="Z"){
					 $("input[name^=receiveMoney]",$(obj).closest("tr")).valChange(0);
					 $("span",$("input[name^=receiveMoney]",$(obj).closest("tr")).parent("td")).text(0);
			}
		});
		
		
		//赠送索赔下拉框
		if(workOrderRepairPro.chargePartitionName == "S"){
			$("#chargePartitionName"+(tableRow-1),getElementContext()).attr("disabled",true);
		}else{
			$("#chargePartitionName"+(tableRow-1),getElementContext()).removeAttr("disabled");
			$("#chargePartitionName"+(tableRow-1)+" option[value='S']",getElementContext()).remove();
		}
		if(discountMode > 0){
			var minRate = $("#minRate",container).val();
			$("#discountRate"+(tableRow-1),getElementContext()).attr("min",minRate);
		}
	};
	/**
	 * 结算（结算圆整方式）
	 * param val 值
	 *       settlementType 结算圆整方式 （四舍五入、只入不舍、只舍不如）
	 *       precision  结算精度 （元、角、分）
	 */
	var settlementPre = function(val){
		var settlementType=$("#settlementTypeHidden",getElementContext()).val();
		var precision=$("#accuracy",getElementContext()).val();
		var resultVal="";
		val=parseFloat(val);
		resultVal=val;
		if(precision=="10121001"){  //元
			if(settlementType=="10141001"){  //四舍五入 （整数）
				resultVal=parseFloat(val).toFixed(0);
			}else if(settlementType=="10141002"){ //只舍不如 (整数)
				resultVal=parseInt(val);
			}else if(settlementType=="10141003"){ //只入不舍 (保留整数位)
				resultVal=Math.ceil(val);
			}
		}else if(precision=="10121002"){ //角
			if(settlementType=="10141001"){  //四舍五入 
				resultVal=val.toFixed(1);
			}else if(settlementType=="10141002"){ //只舍不如 
				var tag='.';
				var str=val+"";
				if(str.indexOf(tag)==-1){
					resultVal=val.toFixed(1);
				}else{
					resultVal=parseFloat(str.substring(0,str.lastIndexOf('.')+2));
				}
			}else if(settlementType=="10141003"){ //只入不舍 
				
				 var bb = val+"";  
				 var dian = bb.indexOf('.');  
				 if(dian == -1){
					 resultVal =  val.toFixed(1);  
				 }else{  
				    var cc = bb.substring(dian+1,bb.length);  
				    if(cc.length >=2){  
				    	bb=bb.substring(0,dian+2);
				    	resultVal=parseFloat(bb)+parseFloat(0.1);  
				    }else{  
				    	resultVal =  val.toFixed(1);  
				    }  
				 }  
			}
		}else if(precision=="10121003"){ //分
			resultVal=val.toFixed(2);
		}else{
			resultVal=val.toFixed(2);
		}
		return resultVal;
	};
	//维修项目费用计算
	var countDisAmount=function(){
		var tab=$("#labourList tbody",getElementContext());
		var rows=$("tr",tab).length;
		var labourAmount=0;
		var disLabourAmount=0;
		var workHourPriceTrueNum=$("#labourList",getElementContext()).dmsTable().getVisiableCellNumber(10);
		var receiveMoneyTrueNum=$("#labourList",getElementContext()).dmsTable().getVisiableCellNumber(13);
		for(var i=0;i<rows;i++){
			if($("tr:eq(0) td:eq(0)",tab).text()!="没有找到匹配的记录"){
				if(!$("tr:eq("+i+") input[id^=isNorepair]",tab).is(':checked')){
					labourAmount=parseFloat(labourAmount)+parseFloat($("tr:eq("+i+") td:eq("+workHourPriceTrueNum+") input[id^=workHourPrice]",tab).val());
					disLabourAmount=parseFloat(disLabourAmount)+parseFloat($("tr:eq("+i+") td:eq("+receiveMoneyTrueNum+") input[id^=receiveMoney]",tab).val());
				}
			}
		}
		var map = {labour_amount:labourAmount.toFixed(2),dis_labour_amount:disLabourAmount.toFixed(2)};
		$("div[data-repairAmount='true']",getElementContext()).initHtmlContent(map,false);
	};
	//维修材料费用计算
	var countDisPartAmount=function(){
		 var tab=$("#roPartDtos tbody",getElementContext());
			var rows=$("tr",tab).length;
			var partSalesAmount=0;
			var disRepairPartAmount=0;
			if(rows==1&&$("tr:eq(0) td:eq(0)",tab).text()==1){
				for(var i=0;i<rows;i++){
					if($("tr:eq("+i+") input[id^=isNorepair]",tab).val()!="10131001"){
						partSalesAmount=parseFloat(partSalesAmount)+parseFloat($("tr:eq("+i+") td:eq(8) input[id^=partSalesAmount]",tab).val());
						disRepairPartAmount=parseFloat(disRepairPartAmount)+parseFloat($("tr:eq("+i+") td:eq(11) input[id^=afterDiscountAmount]",tab).val());
					}
				}
			}else{
				if($("tr:eq(0) td:eq(0)",tab).text()!="没有找到匹配的记录"){
					for(var i=0;i<rows;i++){
						if($("tr:eq("+i+") input[id^=isNorepair]",tab).val()!="10131001"){
							partSalesAmount=parseFloat(partSalesAmount)+parseFloat($("tr:eq("+i+") td:eq(8) input[id^=partSalesAmount]",tab).val());
							disRepairPartAmount=parseFloat(disRepairPartAmount)+parseFloat($("tr:eq("+i+") td:eq(11) input[id^=afterDiscountAmount]",tab).val());
						}
					}
				}
			}
			var map={dis_repair_part_amount:disRepairPartAmount.toFixed(2),repair_part_amount:partSalesAmount.toFixed(2)};
			$("div[data-partAmount='true']",getElementContext()).initHtmlContent(map,false); 
	};
	var changeChargePartitionId=function(appRowTr,orderNum,sId,zId){
		$("select[id^=chargePartitionId]",appRowTr).bindChange(function(obj){
			if($(obj).val()==sId||$(obj).val()==zId){
				 $("input[name^=receiveMoney]",$(obj).closest("tr")).valChange(0);
				 $("input[name^=technician]",$(obj).closest("tr")).valChange(0);
				 $("span",$("input[name^=receiveMoney]",$(obj).closest("tr")).parent("td")).text(0);
				 dmsRepair.countDisAmount();
			}else{
				$("input[name^=technician]",$(obj).closest("tr")).valChange(1);
			}
			var changeLabourCode=$("input[name^=labourCode]",$(obj).closest("tr")).val();
			var bpoNum=$("#roPartDtos",getElementContext()).dmsTable().getTableRows();
	
			for(var j=0;j<bpoNum;j++){
				if(orderNum==$("input[id^=orderNum]",$("#roPartDtos tbody tr:eq("+j+") td:eq(17)")).val()){
				 	$("select[id^=chargePartitionId]",$("#roPartDtos tbody tr:eq("+j+") td:eq(2)")).setDmsValue($(obj).val());
				}
			}
		});
		$("input[id^=isNorepair]",appRowTr).bindChange(function(obj){
			 dmsRepair.countDisAmount();
			 var bpoNum=$("#roPartDtos",getElementContext()).dmsTable().getTableRows();
				
			for(var j=0;j<bpoNum;j++){
				if(orderNum==$("input[id^=orderNum]",$("#roPartDtos tbody tr:eq("+j+") td:eq(17)")).val()){
					if($(obj).is(':checked')){
					 	$("input[id^=isNorepair]",$("#roPartDtos tbody tr:eq("+j+") td:eq(17)")).setDmsValue("10131001");
					 	dmsRepair.countDisPartAmount();
					}else{
						$("input[id^=isNorepair]",$("#roPartDtos tbody tr:eq("+j+") td:eq(17)")).setDmsValue("");
					 	dmsRepair.countDisPartAmount();
					}
				}
			}
		});
	};
	var changeChargePartitionIdOnPart=function(appRowTr,sId,zId){
		$("select[id^=chargePartitionId]",appRowTr).bindChange(function(obj){
			if($(obj).val()==sId||$(obj).val()==zId){
					 $("input[name^=afterDiscountAmount]",$(obj).closest("tr")).valChange(0);
					 $("span",$("input[name^=afterDiscountAmount]",$(obj).closest("tr")).parent("td")).text(0);
					 $("input[name^=discountHidden]",$(obj).closest("tr")).valChange(0);
					 dmsRepair.countDisPartAmount();
			}else{
				$("input[name^=discountHidden]",$(obj).closest("tr")).valChange(1);
				dmsRepair.countDisPartAmount();
			}
		});
	};
	var changeLabourCodeOnPart=function(obj){
		if($(obj).val()!=null){	
			//获取维修项目数据
			var repairPro = $("#labourList",getElementContext()).dmsTable().getRowDataByIndex();
	 		var labourCode=$(obj).val().split(":")[1];
			var labourName=$('option:selected',$(obj)).text();
			var orderNumer=labourName.split("-")[0];
			var orderNum=repairPro[orderNumer-1].orderNum;
			var labourCodeL=repairPro[orderNumer-1].labourCode;
			var roLabourId=repairPro[orderNumer-1].RO_LABOUR_ID;
			var tab=$("#roPartDtos tbody");
			var rows=$("tr",tab).length;
			var thisLength=$(obj).closest("tr").attr("data-index");
			if(thisLength<rows){
				for(var i=0;i<rows;i++){
					if(i!=thisLength){
						if($("input[name^=orderNum]",$("tr:eq("+i+")",tab)).val()!=undefined){
							if($("input[name^=orderNum]",$("tr:eq("+i+")",tab)).val()==orderNum&&$("input[name^=partNo]",$("tr:eq("+i+")",tab)).val()==$("input[name^=partNo]",$(obj).closest("tr")).val()&&$("select[name^=storageCode]",$("tr:eq("+i+")",tab)).val()==$("select[name^=storageCode]",$(obj).closest("tr")).val()){
								return false;
							}
						}
						if($("input[name^=roLabourId]",$("tr:eq("+i+")",tab)).val()!=undefined){
							if($("input[name^=roLabourId]",$("tr:eq("+i+")",tab)).val()==roLabourId&&$("input[name^=partNo]",$("tr:eq("+i+")",tab)).val()==$("input[name^=partNo]",$(obj).closest("tr")).val()&&$("select[name^=storageCode]",$("tr:eq("+i+")",tab)).val()==$("select[name^=storageCode]",$(obj).closest("tr")).val()){
								return false;
							}
						}
					}
				}
			}

			if(labourCode==labourCodeL){
				$("select[id^=chargePartitionId]",$(obj).closest("tr")).setDmsValue($("select[id^=chargePartitionId]",$("#labourList tbody tr:eq("+(orderNumer-1)+") td:eq(3)")).val());
				$("input[name^=orderNum]",$(obj).closest("tr")).setDmsValue(orderNum);
				$("input[name^=roLabourId]",$(obj).closest("tr")).setDmsValue(roLabourId);
				 var isNorepair=$("tr:eq("+(orderNumer-1)+")  input[id^=isNorepair]","#labourList tbody").is(':checked');
				 if(isNorepair){
					 $("input[name^=isNorepair]",$(obj).closest("tr")).setDmsValue("10131001");
				 }else{
					 $("input[name^=isNorepair]",$(obj).closest("tr")).setDmsValue("");
				 }
			}
			return true;
		}
	};
	var addAppendRowByCharge=function(roId,roType,selectRow,dmsTable){
		dmsCommon.ajaxRestRequest({
    		url : dmsCommon.getDmsPath()["dms_repair"] + "/balance/balanceAccounts/getReceiveMoneyCustomer/"+roId,
			type : 'GET',
			data : {roType:roType},
			sucessCallBack : function(modelData) {
				for(var i=0;i<modelData.length;i++){
					if(i!=0){
						selectRow.DIS_AMOUNT=0;
					}
					if(roType==12161001&&selectRow.OWNER_NO!=modelData[0].OWNER_NO){
						var map={OWNER_NO:selectRow.OWNER_NO,RO_TYPE:12161001,DELIVERER:selectRow.DELIVERER,DIS_AMOUNT:dmsRepair.settlementPre(selectRow.DIS_AMOUNT),SUB_OBB_AMOUNT:0,RECEIVABLE_AMOUNT:selectRow.DIS_AMOUNT,NOT_RECEIVED_AMOUNT:selectRow.DIS_AMOUNT,RECEIVED_AMOUNT:0,CUSTOMER_BASICINFO_ID:selectRow.CUSTOMER_BASICINFO_ID};
						dmsTable.appendRow(map);
					}
					if(roType==12161001){
						var map={OWNER_NO:modelData[i].OWNER_NO,RO_TYPE:12161001,DELIVERER:modelData[i].DELIVERER,SUB_OBB_AMOUNT:0,DIS_AMOUNT:dmsRepair.settlementPre(selectRow.DIS_AMOUNT),RECEIVED_AMOUNT:modelData[i].RECEIVE_AMOUNT,RECEIVABLE_AMOUNT:dmsRepair.settlementPre(selectRow.DIS_AMOUNT),NOT_RECEIVED_AMOUNT:(dmsRepair.settlementPre(selectRow.DIS_AMOUNT)-modelData[i].RECEIVE_AMOUNT),CUSTOMER_TYPE:modelData[i].PAYMENT_OBJECT_TYPE,CUSTOMER_BASICINFO_ID:modelData[i].CUSTOMER_BASICINFO_ID};
						dmsTable.appendRow(map);
					}
					if(roType==12161002){
						var map={OWNER_NO:modelData[i].OWNER_NO,RO_TYPE:12161002,DELIVERER:modelData[i].DELIVERER,SUB_OBB_AMOUNT:0,DIS_AMOUNT:selectRow.disAmount,RECEIVED_AMOUNT:modelData[i].RECEIVE_AMOUNT,RECEIVABLE_AMOUNT:dmsRepair.settlementPre(selectRow.disAmount),NOT_RECEIVED_AMOUNT:(selectRow.disAmount-modelData[i].RECEIVE_AMOUNT),CUSTOMER_TYPE:modelData[i].PAYMENT_OBJECT_TYPE,CUSTOMER_BASICINFO_ID:modelData[i].CUSTOMER_BASICINFO_ID};
						dmsTable.appendRow(map);
					}
					if(roType==12161003){
						var map={OWNER_NO:modelData[i].OWNER_NO,RO_TYPE:12161003,DELIVERER:modelData[i].DELIVERER,SUB_OBB_AMOUNT:0,DIS_AMOUNT:selectRow.partAmount,RECEIVED_AMOUNT:modelData[i].RECEIVE_AMOUNT,RECEIVABLE_AMOUNT:dmsRepair.settlementPre(selectRow.partAmount),NOT_RECEIVED_AMOUNT:(selectRow.partAmount-modelData[i].RECEIVE_AMOUNT),CUSTOMER_TYPE:modelData[i].PAYMENT_OBJECT_TYPE,CUSTOMER_BASICINFO_ID:modelData[i].CUSTOMER_BASICINFO_ID};
						dmsTable.appendRow(map);
					}
				
				}
				if(roType==12161001&&modelData.length<1){
					var map={OWNER_NO:selectRow.OWNER_NO,RO_TYPE:12161001,DELIVERER:selectRow.DELIVERER,DIS_AMOUNT:dmsRepair.settlementPre(selectRow.DIS_AMOUNT),SUB_OBB_AMOUNT:0,RECEIVABLE_AMOUNT:selectRow.DIS_AMOUNT,NOT_RECEIVED_AMOUNT:selectRow.DIS_AMOUNT,RECEIVED_AMOUNT:0,CUSTOMER_TYPE:selectRow.CUSTOMER_FROM_TYPE,CUSTOMER_BASICINFO_ID:selectRow.CUSTOMER_BASICINFO_ID};
					dmsTable.appendRow(map);
				}
				if(roType==12161002&&modelData.length<1){
					var map={OWNER_NO:selectRow.CUSTOMER_CODE,RO_TYPE:12161002,DELIVERER:selectRow.CUSTOMER_NAME,DIS_AMOUNT:selectRow.disAmount,SUB_OBB_AMOUNT:0,RECEIVABLE_AMOUNT:selectRow.disAmount,NOT_RECEIVED_AMOUNT:selectRow.disAmount,RECEIVED_AMOUNT:0,CUSTOMER_TYPE:selectRow.CUSTOMER_FROM_TYPE,CUSTOMER_BASICINFO_ID:selectRow.CUSTOMER_BASICINFO_ID};
					dmsTable.appendRow(map);
				}
				if(roType==12161003&&modelData.length<1){
					 var map={OWNER_NO:selectRow.CUSTOMER_CODE,RO_TYPE:12161003,DELIVERER:selectRow.CUSTOMER_NAME,DIS_AMOUNT:selectRow.partAmount,SUB_OBB_AMOUNT:0,RECEIVABLE_AMOUNT:selectRow.partAmount,NOT_RECEIVED_AMOUNT:selectRow.partAmount,RECEIVED_AMOUNT:0,CUSTOMER_TYPE:selectRow.CUSTOMER_FROM_TYPE,CUSTOMER_BASICINFO_ID:selectRow.CUSTOMER_BASICINFO_ID};
					 dmsTable.appendRow(map);
				}
				$("#discountModeCode",getElementContext()).setDmsValue($("#discountModeCode",getElementContext()).val());
			}
		});
	};
	return {
		initLabourData:function(){
			return initLabourData();
		},
		addDisabled:function(container){
			return addDisabled(container);
		},
		removeDisabled:function(){
			removeDisabled();
		},
		calRepairPartAmount:function(curPartAmount){
			calRepairPartAmount(curPartAmount);
		},
		appendRowInRolabour:function(repairPro,workOrderType,selectData,receiveMoney,i,labourPrice){
			appendRowInRolabour(repairPro,workOrderType,selectData,receiveMoney,i,labourPrice);
		},
		settlementPre:function(val){
			return settlementPre(val);
		},
		countDisAmount:function(){
			return countDisAmount();
		},countDisPartAmount:function(){
			return countDisPartAmount();
		},changeChargePartitionId:function(appRowTr,orderNum,sId,zId){
			return changeChargePartitionId(appRowTr,orderNum,sId,zId);
		},changeChargePartitionIdOnPart:function(appRowTr,sId,zId){
			return changeChargePartitionIdOnPart(appRowTr,sId,zId);
		},changeLabourCodeOnPart:function(obj){
			return changeLabourCodeOnPart(obj);
		},addAppendRowByCharge:function(roId,roType,selectRow,dmsTable){
			return addAppendRowByCharge(roId,roType,selectRow,dmsTable);
		},
	};
	
	
	
}();


/**
 * 客户模块模块相关功能
 */
var dmsCustomer = function() {
	
}();

/**
 * 管理模块相关功能
 */
var dmsManager = function() {
	var sample = function(){
		
	};
	
	return {
		sample:function(){
			sample();
		}
	};
}();

/**
 * 零售模块相关功能
 */
var dmsRetail = function() {
	var saleDatabindChange= function (tableObject,container){
		 var tab=$("#soDecrodateList tbody",container);
		 var tab2=$("#soDecrodatePartList tbody",container);
		 var tab3=$("#soServicesList tbody",container);
		 $("select[id^=accountMode]",tab2).each(function() {
			 if($(this).val() =="14061003"||$(this).val() =="14061004"){
				 $("input[id^=discount]",$(this).closest("tr")).attr("disabled","true");
			 }else{
				 $("input[id^=discount]",$(this).closest("tr")).removeAttr("disabled");
			 }
		 });
		   /***装潢材料******/
			//改变折扣率重置结算方式
			var calDecrodateDiscount = function(obj){
				var inputValue=$(obj).val();
				if(inputValue == 0 || inputValue == ""){
					
				}else{
					//$("select[id^=accountMode]",$(obj).closest("tr")).setDmsValue("");
				}
			}
			//通过下拉框数据实时变更左侧数值
			var calDecrodatePartItemAmount = function(obj){
				var selectValue = $(obj).val();
				if(selectValue =="14061003"||selectValue =="14061004"){
					$("input[id^=discount]",$(obj).closest("tr")).valChange("0");
					/*$("input[id^=afterDiscountAmountPartList]",$(obj).closest("tr")).valChange("0");
					$("input[id^=afterDiscountAmountPartList]",$(obj).closest("tr")).parents("td").find("span").valChange("0");*/
					$("input[id^=discount]",$(obj).closest("tr")).attr("disabled","true");
				}else{
					$("input[id^=discount]",$(obj).closest("tr")).valChange(1);
					$("input[id^=discount]",$(obj).closest("tr")).removeAttr("disabled");
				}
				//dmsRetail.moneyCalculate(container);
			}
			//填写序列号数值表格变成1并且不可编辑
			var calDecrodatePartItem = function(obj){
				var inputValue=$(obj).val();
				if(inputValue =="" || inputValue ==undefined){
				$("input[id^=partQuantity]",$(obj).closest("tr")).valChange("1");
				$("input[id^=partQuantity]",$(obj).closest("tr")).removeAttr("disabled");
				}else{
					$("input[id^=partQuantity]",$(obj).closest("tr")).valChange("1");
					$("input[id^=partQuantity]",$(obj).closest("tr")).attr("disabled","true");
				}
			}
			//绑定select事件--	结算方式	
			$("select[id^=accountMode]",tab2).bindChange(function(obj){
				//计算服务项目实收金额
				calDecrodatePartItemAmount(obj);
			});
			
			$("input[id^=partSequence]",tab2).bindChange(function(obj){
				//计算服务项目实收金额
				calDecrodatePartItem(obj);
			});
			
			$("input[id^=discount]",tab2).bindChange(function(obj){
				//计算服务项目实收金额
				calDecrodateDiscount(obj);
			});
			
			/***装潢项目******/
			//通过下拉框数据实时变更左侧数值
			var calDecrodateAmount = function(obj){
				var selectValue = $(obj).val();
				if(selectValue =="14061003"||selectValue =="14061004"){
					$("input[id^=discount]",$(obj).closest("tr")).valChange("0");
				}
			}
			//填写序列号数值表格变成1并且不可编辑
			var calDecrodateItem = function(obj){
				$("input[id^=partQuantity]",$(obj).closest("tr")).valChange("1");
				$("input[id^=partQuantity]",$(obj).closest("tr")).attr("disabled","true");
			}
			//改变加价率重置select值
			var calDecrodateDiscount2 = function(obj){
				var inputValue = $(obj).val();
				if(inputValue == 0 || inputValue == ""){
					
				}else{
					$("select[id^=accountMode]",$(obj).closest("tr")).setDmsValue("");
				}
			}
			
			var calServiceItemAmountList = function(obj){
				var inputValue=$(obj).val();
				if(inputValue == 0 || inputValue == ""){
					
				}else{
					$("select[id^=accountMode]",$(obj).closest("tr")).setDmsValue("14061002");
				}
				 dmsRetail.moneyCalculate(container);
			}
			
			//通过下拉框数据实时变更左侧数值
			var calServiceItemAmount = function(obj){
				var selectValue = $(obj).val();
				if(selectValue =="14061003"||selectValue =="14061004"){
					$("input[id^=afterDiscountAmountServicesList]",$(obj).closest("tr")).valChange("0");
				}
				 dmsRetail.moneyCalculate(container);
			}
			//绑定select事件--	结算方式	
			$("select[id^=accountModeList]",tab3).bindChange(function(obj){
				//计算服务项目实收金额
				calServiceItemAmount(obj);
			});
			
			$("input[id^=afterDiscountAmountServicesList]",tab3).bindChange(function(obj){
				//计算服务项目实收金额
				calServiceItemAmountList(obj);
			});
			
			
	}
	var moneyCalculate = function(container){
		 var tab=$("#soDecrodateList tbody",container);
		 var tab2=$("#soDecrodatePartList tbody",container);
		 var tab3=$("#soServicesList tbody",container);
		 var rows=$("tr",tab).length;
		 var rows2=$("tr",tab2).length;
		 var rows3=$("tr",tab3).length;
		 var afterDiscountAmount=parseFloat(0);
		 var afterDiscountAmountPartList=parseFloat(0);
		 var afterDiscountAmountServicesList=parseFloat(0);
		 var partSalesAmount=parseFloat(0);
		 var directivePrice=parseFloat(0);
		 var DiscountAmountPrice =parseFloat(0);
		 var sumDiscountAmountPrice =parseFloat(0);
		 var sumPresentPrice = parseFloat(0);
		 var sumAfterDiscountAmount=parseFloat(0);
		 var sumAfterDiscountAmountPartList=parseFloat(0);
		 var sumAfterDiscountAmountServicesList=parseFloat(0);
		 var sumPartSalesAmount=parseFloat(0);
		 var sumDirectivePrice=parseFloat(0);
		 var sumPartPresentAmount=parseFloat(0);
		 //装潢项目计算
		 if(rows>0){
			 for(var i=0;i<rows;i++){
				afterDiscountAmount = $("tr:eq("+i+") td:eq(7) input[id^=afterDiscountAmount]",tab).val();//装潢项目实收金额
				if(afterDiscountAmount =="" || afterDiscountAmount ==undefined){
				    var afterDiscountAmount=parseFloat(0);
				    sumAfterDiscountAmount=parseFloat(sumAfterDiscountAmount)+parseFloat(afterDiscountAmount);//装潢项目实收金额总和
				}else{
					sumAfterDiscountAmount=parseFloat(sumAfterDiscountAmount)+parseFloat(afterDiscountAmount);//装潢项目实收金额总和
				} 
			 }
		 }else{
			  var sumAfterDiscountAmount=parseFloat(0);
		  }
		// console.log("状态状态状态很好"+sumAfterDiscountAmount+"价格:");
		 
		   //装潢材料
			if(rows2>0){
				 for(var i=0;i<rows2;i++){
					 afterDiscountAmountPartList = $("tr:eq("+i+") td:eq(11) input[id^=afterDiscountAmountPartList]",tab2).val();//装潢材料实收金额
					 partSalesPrice=  $("tr:eq("+i+") td:eq(7) input[id^=partSalesPrice]",tab2).val();//装潢材料销售价格
					 partQuantity=  $("tr:eq("+i+") td:eq(8) input[id^=partQuantity]",tab2).val();//装潢材料销售数量
					 partSalesAmount = $("tr:eq("+i+") td:eq(9) input[id^=partSalesAmount]",tab2).val();//装潢材料销售金额
					// discount2= $("tr:eq("+i+") td:eq(10) input[id^=discount]",tab2).val();//折扣率
					 accountMode = $("tr:eq("+i+") td:eq(12) select[id^=accountMode]",tab2).val();//结算方式
					 if(typeof(afterDiscountAmountPartList) == "undefined"||afterDiscountAmountPartList==''){
						 afterDiscountAmountPartList=parseFloat(0).toFixed(2);
					 }
					 
					 if(typeof(partSalesPrice) == "undefined"||partSalesPrice==''){
						 partSalesPrice=parseFloat(0).toFixed(2);
					 }
					 
					 if(typeof(partQuantity) == "undefined"||partQuantity==''){
						 partQuantity=parseFloat(0).toFixed(2);
					 }
					 
					/* if(accountMode !="14061003"&&accountMode !="14061004"){
						 afterDiscountAmountPartList=(partSalesPrice*partQuantity*discount2).toFixed(2)
						 $("tr:eq("+i+") td:eq(11) input[id^=afterDiscountAmountPartList]",tab2).valChange(afterDiscountAmountPartList);
						 $("tr:eq("+i+") td:eq(11) span",tab2).valChange(afterDiscountAmountPartList);
					 }*/
					 if(typeof(partSalesAmount) == "undefined"||partSalesAmount==''){
						 partSalesAmount=parseFloat(partSalesPrice)*parseFloat(partQuantity);
						 var item =$("tr:eq("+i+") td:eq(9)>span",tab2);
						 var digits = $(item).attr("data-autoValueDigits");
						 if(digits){
							 partSalesAmount = partSalesAmount.toFixed(parseInt(digits));
						 }
					   $(item).valChange(partSalesAmount);
							//如果item 不是input 属性
						 if(!$(item).is(":input")){
							 $("input",$(item).parent()).valChange(partSalesAmount);
						 }
					 }

					 
					
					
					 if(accountMode=="14061004"){
						 sumPartPresentAmount=parseFloat(sumPartPresentAmount)+parseFloat(partSalesAmount);//状态为赠送时的金额总和(销售金额)
					 }
					 if(partSalesAmount =="" || partSalesAmount ==undefined){
						 var afterDiscountAmountPartList =parseFloat(0);
						 var partSalesAmount =parseFloat(0);
						 sumPartSalesAmount=parseFloat(sumPartSalesAmount)+parseFloat(partSalesAmount);//销售金额总和
						 sumAfterDiscountAmountPartList=parseFloat(sumAfterDiscountAmountPartList)+parseFloat(afterDiscountAmountPartList); //装潢材料实收金额总和
					 }else{
						 sumPartSalesAmount=parseFloat(sumPartSalesAmount)+parseFloat(partSalesAmount);//销售金额总和
						 sumAfterDiscountAmountPartList=parseFloat(sumAfterDiscountAmountPartList)+parseFloat(afterDiscountAmountPartList); //装潢材料实收金额总和
					 }
	  				   

				 }
			}else{
				var afterDiscountAmountPartList =parseFloat(0);
				var partSalesAmount =parseFloat(0);
				var DiscountAmountPrice =parseFloat(0);
				var sumDiscountAmountPrice =parseFloat(0);
				var sumPartSalesAmount =parseFloat(0);
				var sumAfterDiscountAmountPartList =parseFloat(0);
			}
			
			   
	   		//服务项目
			 if(rows3>0){
				 for(var i=0;i<rows3;i++){
					 afterDiscountAmountServicesList = $("tr:eq("+i+") td:eq(6) input[id^=afterDiscountAmountServicesList]",tab3).val();//服务项目实收金额
					 directivePrice=$("tr:eq("+i+") td:eq(5) input[id^=directivePrice]",tab3).val();
					 accountModeList = $("tr:eq("+i+") td:eq(7) select[id^=accountModeList]",tab3).val();
					 if(accountModeList=="14061004"){
						 sumDirectivePrice=parseFloat(sumDirectivePrice)+parseFloat(directivePrice);
					 }
					 if(afterDiscountAmountServicesList=="" || afterDiscountAmountServicesList ==undefined){
						 var afterDiscountAmountServicesList=parseFloat(0);
						 sumAfterDiscountAmountServicesList=parseFloat(sumAfterDiscountAmountServicesList)+parseFloat(afterDiscountAmountServicesList);//服务项目实收金额总和
					 }else{
						 sumAfterDiscountAmountServicesList=parseFloat(sumAfterDiscountAmountServicesList)+parseFloat(afterDiscountAmountServicesList);//服务项目实收金额总和
					 }	
				 }
			 }else{
				var afterDiscountAmountServicesList=parseFloat(0);
				var presentPrice=parseFloat(0);
				var directivePrice=parseFloat(0);
				var presentPrice=parseFloat(0);
				var sumPresentPrice=parseFloat(0);
				var sumAfterDiscountAmountServicesList=parseFloat(0);
			}
			
			 if(sumAfterDiscountAmount=="" && sumAfterDiscountAmountPartList !=""){
				 $("#upholsterSum",container).setDmsValue(parseFloat(sumAfterDiscountAmountPartList).toFixed(2));
				 $("#upholsterWorkSum",container).setDmsValue(parseFloat(sumAfterDiscountAmount).toFixed(2));//设置装潢工时金额
				 $("#upholsterPartSum",container).setDmsValue(parseFloat(sumAfterDiscountAmountPartList).toFixed(2)-parseFloat(sumAfterDiscountAmount).toFixed(2));//设置装潢配件金额
			 }else if(sumAfterDiscountAmountPartList=="" && sumAfterDiscountAmount !=""){
				 $("#upholsterSum",container).setDmsValue(parseFloat(sumAfterDiscountAmount).toFixed(2));
				 $("#upholsterWorkSum",container).setDmsValue(parseFloat(sumAfterDiscountAmount).toFixed(2));//设置装潢工时金额
				 $("#upholsterPartSum",container).setDmsValue(parseFloat(0));//设置装潢配件金额
			 }else if(sumAfterDiscountAmountPartList=="" && sumAfterDiscountAmount==""){
				 $("#upholsterSum",container).setDmsValue(parseFloat(0));
				 $("#upholsterWorkSum",container).setDmsValue(parseFloat(0));//设置装潢工时金额
				 $("#upholsterPartSum",container).setDmsValue(parseFloat(0));//设置装潢配件金额
			 }else{
			 	$("#upholsterSum",container).setDmsValue((parseFloat(sumAfterDiscountAmount)+parseFloat(sumAfterDiscountAmountPartList)).toFixed(2));//装潢金额总和	
			 	$("#upholsterWorkSum",container).setDmsValue(parseFloat(sumAfterDiscountAmount).toFixed(2));//设置装潢工时金额
			 	$("#upholsterPartSum",container).setDmsValue(parseFloat(sumAfterDiscountAmountPartList).toFixed(2));//设置装潢配件金额
			 }
			//计算赠送金额
		    if(accountModeList=="14061004" && accountMode !="14061004"){
					$("#presentSum",container).setDmsValue(parseFloat(sumDirectivePrice).toFixed(2));
			 }else if(accountMode=="14061004" && accountModeList !="14061004"){
				 $("#presentSum",container).setDmsValue(parseFloat(sumPartPresentAmount).toFixed(2));
			 }else if(accountModeList=="" && accountMode==""){
				 $("#presentSum",container).setDmsValue(parseFloat(0));
			 }else{
				 $("#presentSum",container).setDmsValue((parseFloat(sumDirectivePrice)+parseFloat(sumPartPresentAmount)).toFixed(2));//赠送金额总和
			 }
			 
			 $("#serviceSum",container).setDmsValue(parseFloat(sumAfterDiscountAmountServicesList).toFixed(2));//服务项目金额总和
			
	};
	var moneyVehicle = function(container){
		 var tab4=$("#productList tbody",container);
		 var rows4=$("tr",tab4).length;
		 var afterDiscountAmountvehicle=parseFloat(0);//
		 var sumAfterDiscountAmountvehicle=parseFloat(0);//
		 //车辆总额计算
		 if(rows4>0){
			 for(var i=0;i<rows4;i++){
				afterDiscountAmountvehicle = $("tr:eq("+i+") td:eq(11) input[id^=sumAmount]",tab4).val();	
				afterDiscountAmountvehicle = $.parseNumber(afterDiscountAmountvehicle, {format:"#,##0.00",locale:"us"});
				if(afterDiscountAmountvehicle =="" || afterDiscountAmountvehicle ==undefined){
				    afterDiscountAmountvehicle=parseFloat(0);
				    sumAfterDiscountAmountvehicle=parseFloat(sumAfterDiscountAmountvehicle)+parseFloat(afterDiscountAmountvehicle);//车辆金额总和
				}else{
					afterDiscountAmountvehicle=parseFloat(afterDiscountAmountvehicle);
					sumAfterDiscountAmountvehicle=parseFloat(sumAfterDiscountAmountvehicle)+parseFloat(afterDiscountAmountvehicle);//车辆金额总和
				} 
			 }
		 }else{
			    sumAfterDiscountAmountvehicle=parseFloat(0);
		  }
		 $("#vehiclePrice",container).setDmsValue(parseFloat(sumAfterDiscountAmountvehicle).toFixed(2));//车辆总额
	}
	//计算服务项目的各种金额
	var moneyServiceItems = function(container){
		 var tab4=$("#serviceProjectList tbody",container);
		 var rows4=$("tr",tab4).length;
		 //开始计算金额,服务项目tab页里面包含了 保险金额  insuranceAmount 牌照金额 licenceAmount 信贷金额 creditAmount 税费金额 taxAmount 其他服务金额 otherServiceSum
		 //抵扣金额offsetAmount 代办应收agencyReceivable  赠送金额presentSum    精品金额boutiqueAmount
		 var insuranceAmount=parseFloat(0);//保险金额
		 var licenceAmount=parseFloat(0);//牌照金额
		 var creditAmount=parseFloat(0);//信贷金额
		 var taxAmount=parseFloat(0);// 税费金额
		 var otherServiceSum=parseFloat(0);//其他服务金额
		// var offsetAmount=parseFloat(0);//抵扣金额
		 var agencyReceivable=parseFloat(0);//代办应收
		 var presentAmount=parseFloat(0);//赠送金额
		// var boutiqueAmount=parseFloat(0);// 精品金额
		 
		 
		 var afterDiscountAmountvehicle=parseFloat(0);//
		 var sumAfterDiscountAmountvehicle=parseFloat(0);//
		 //总额计算
		 if(rows4>0){
			 for(var i=0;i<rows4;i++){
				 var s = $("tr:eq("+i+") td:eq(7) ",tab4);
				 var accountMode = $("tr:eq("+i+") td:eq(7) select[id^=accountModeList]",tab4).val();	//结算方式
				 var serviceType = $("tr:eq("+i+") td:eq(5) input[id^=serviceType]",tab4).val();	//服务类型
				 var actualSellPrice = $("tr:eq("+i+") td:eq(6) input[id^=actualSellPrice]",tab4).val();	//实际销售价
				 var advanceAmount = $("tr:eq("+i+") td:eq(9) input[id^=advanceAmount]",tab4).val();	//代办预收金额
				 var actualIncurredAmount = $("tr:eq("+i+") td:eq(8) input[id^=actualIncurredAmount]",tab4).val();	//实际发生金额
				 //格式化
				 actualSellPrice = $.parseNumber(actualSellPrice, {format:"#,##0.00",locale:"us"});
				 advanceAmount = $.parseNumber(advanceAmount, {format:"#,##0.00",locale:"us"});
				 actualIncurredAmount = $.parseNumber(actualIncurredAmount, {format:"#,##0.00",locale:"us"});
				 if(accountMode==14061004){//赠送
					 presentAmount+=parseFloat(actualSellPrice);
				 }
                 if(accountMode==14061001){//代办ADVANCE_AMOUNT
                	 agencyReceivable+=parseFloat(advanceAmount);
				 }
				 if(serviceType==14021001){//保险
					 insuranceAmount+=parseFloat(actualIncurredAmount);
					 
				 }
                 if(serviceType==14021002){//购税
                	 taxAmount+=parseFloat(actualIncurredAmount);
				 }
                 if(serviceType==14021003){//上牌
                	 licenceAmount+=parseFloat(actualIncurredAmount);
                 }
                 if(serviceType==14021004){//信贷
                	 creditAmount+=parseFloat(actualIncurredAmount);
                 }
                 if(serviceType==14021007){//其他
                	 otherServiceSum+=parseFloat(actualIncurredAmount);
                 }
				 
			 }
		 }
		 $("#insuranceAmount",container).setDmsValue((parseFloat(insuranceAmount)).toFixed(2));//保险金额
		 $("#licenceAmount",container).setDmsValue((parseFloat(licenceAmount)).toFixed(2));//牌照金额
		 $("#creditAmount",container).setDmsValue((parseFloat(creditAmount)).toFixed(2));//信贷金额
		 $("#taxAmount",container).setDmsValue((parseFloat(taxAmount)).toFixed(2));// 税费金额
		 $("#otherServiceSum",container).setDmsValue((parseFloat(otherServiceSum)).toFixed(2));//其他服务金额
		 $("#agencyReceivable",container).setDmsValue((parseFloat(agencyReceivable)).toFixed(2));//代办应收
		 $("#presentSum",container).setDmsValue((parseFloat(presentAmount)).toFixed(2));//赠送金额
	}
	
	
	var _toFixed = function(val,d) { 
	      var s=val+"";if(!d)d=0;  
	      if(s.indexOf(".")==-1)s+=".";s+=new Array(d+1).join("0");  
	      if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0,"+ (d+1) +"})?)\\d*$").test(s))  
	      {  
	          var s="0"+ RegExp.$2, pm=RegExp.$1, a=RegExp.$3.length, b=true;  
	          if (a==d+2){a=s.match(/\d/g); if (parseInt(a[a.length-1])>4)  
	          {  
	              for(var i=a.length-2; i>=0; i--) {a[i] = parseInt(a[i])+1;  
	              if(a[i]==10){a[i]=0; b=i!=1;} else break;}  
	          }  
	          s=a.join("").replace(new RegExp("(\\d+)(\\d{"+d+"})\\d$"),"$1.$2");  
	      }if(b)s=s.substr(1);return (pm+s).replace(/\.$/, "");} return val+"";  
	}; 
	/**
	 * 结算（结算圆整方式）
	 * param val 值
	 *       settlementType 结算圆整方式 （四舍五入、只入不舍、只舍不如）
	 *       precision  结算精度 （元、角、分）
	 */
	var settlementPre = function(val){
		var settlementType=dmsCommon.getSystemParamInfo("1021","vehicle_settlementMode");
		var precision=dmsCommon.getSystemParamInfo("1021","vehicle_settlementAccuracy");
		var resultVal="";
		val=parseFloat(val);
		resultVal=val;
		if(precision=="10121001"){  //元
			if(settlementType=="10141001"){  //四舍五入 （整数）
				resultVal=parseFloat(val).toFixed(0);
			}else if(settlementType=="10141002"){ //只舍不如 (整数)
				resultVal=parseInt(val);
			}else if(settlementType=="10141003"){ //只入不舍 (保留整数位)
				resultVal=Math.ceil(val);
			}
		}else if(precision=="10121002"){ //角
			if(settlementType=="10141001"){  //四舍五入 
				resultVal=_toFixed(val,1);
			}else if(settlementType=="10141002"){ //只舍不如 
				var tag='.';
				var str=val+"";
				if(str.indexOf(tag)==-1){
					resultVal=val.toFixed(1);
				}else{
					resultVal=parseFloat(str.substring(0,str.lastIndexOf('.')+2));
				}
			}else if(settlementType=="10141003"){ //只入不舍 
				
				 var bb = val+"";  
				 var dian = bb.indexOf('.');  
				 if(dian == -1){
					 resultVal =  val.toFixed(1);  
				 }else{  
				    var cc = bb.substring(dian+1,bb.length);  
				    if(cc.length >=2){  
			/*	    	bb=bb.substring(0,dian+2);
				    	resultVal=parseFloat(bb)+parseFloat(0.1); */ 
				    	resultVal=(bb.toString().match(/^\d+(?:\.\d{0,1})?/));
				    }else{  
				    	resultVal =  val.toFixed(1);  
				    }  
				 }  
			}
		}else if(precision=="10121003"){ //分
			resultVal=val.toFixed(2);
		}
		return resultVal;
	};
	/**
	 *  增值——装潢单汇总相关金额
	 */
	var calcuDecorationRelativeAmount = function(container){
		
		// 合计金额
		var totalAmount = 0;
		// 应收金额
		var receivableAmount = 0;
		// 赠送金额			
		var presentAmount = 0;
		
		$("#decorationProductDataLst tbody tr",getElementContext()).each(function(i){
			var rowNum = $("#decorationProductDataLst",getElementContext()).dmsTable().getTableRows();
			if(rowNum < 1){
				return;
			}
			// 合计金额
			var costAmount = parseFloat($(this).find("td:eq(8) input").val()) * parseFloat($(this).find("td:eq(9) input").val());
			// 销售金额
			var salesAmount = $(this).find("td:eq(15) input").val();
			
			totalAmount = totalAmount + parseFloat(costAmount);
			receivableAmount = receivableAmount + parseFloat(salesAmount);
		});
		
		$("#decorationItemDataLst tbody tr",getElementContext()).each(function(i){
			var rowNum = $("#decorationItemDataLst",getElementContext()).dmsTable().getTableRows();
			if(rowNum < 1){
				return;
			}
			// 工时费
			var dispatchFee = $(this).find("td:eq(9) input").val();
			// 实收工时费
			var actDispatchFee = $(this).find("td:eq(11) input").val();
			totalAmount = totalAmount + parseFloat(dispatchFee);
			receivableAmount = receivableAmount + parseFloat(actDispatchFee);
		});
		
		$("#decorationMaterialDataLst tbody tr",getElementContext()).each(function(i){
			
			var rowNum = $("#decorationMaterialDataLst",getElementContext()).dmsTable().getTableRows();
			if(rowNum < 1){
				return;
			}
			
			// 合计金额
			var costAmount = parseFloat($(this).find("td:eq(8) input").val()) * parseFloat($(this).find("td:eq(9) input").val());
			// 销售金额
			var salesAmount = $(this).find("td:eq(15) input").val();
			
			totalAmount = totalAmount + parseFloat(costAmount);
			receivableAmount = receivableAmount + parseFloat(salesAmount);
		});
		
		$("#totalAmount",getElementContext()).val(totalAmount.toFixed(2));
		$("#receivableAmount",getElementContext()).val(receivableAmount.toFixed(2));
		$("#presentAmount",getElementContext()).val((totalAmount - receivableAmount).toFixed(2));
	};
	return {
		moneyCalculate:function(container){
			moneyCalculate(container);
		},
		moneyVehicle:function(container){
			moneyVehicle(container);
		},
		moneyServiceItems :function(container){
			moneyServiceItems(container);
		},
		saleDatabindChange:function(tableObject,container){
			saleDatabindChange(tableObject,container);
		},
		settlementPre:function(val){
			return settlementPre(val);
		},
		calcuDecorationRelativeAmount:function(container){
			calcuDecorationRelativeAmount(container);
		}
	};
}();


/**
 * 车辆模块相关功能
 */
var dmsVehicle = function() {
	var sample = function(container){
		
	};
	
	return {
		sample:function(){
			sample();
		}
	};
}();

var dmsHomePageReport = function() {
	var homePageReport = function(container,url){
		dmsCommon.ajaxRestRequest({
			url : dmsCommon.getDmsPath()["dms_report"] + "/homePage/statistical",
			type : 'GET',
			sucessCallBack : function(data) {
				if($.trim(data)!="null"){
					$("#complaincounts",container).html('<span data-counter="counterup"  data-value='+data.complainNum+'></span>个');
					$("#repairCar",container).html('<span data-counter="counterup"  data-value='+data.repairNum+'></span>台');
					$("#saleCar",container).html('<span data-counter="counterup"  data-value='+data.saleNum+'></span>台');
					$("#conversionRate",container).html('<span data-counter="counterup"  data-value='+data.conversionRate+'></span>%');		
				}
				var trs = [];
     		    var graphs=[];
			    dataProvider(data.saleStai,trs,graphs);
			    Dashboard.initAmChart5(graphs,trs);
			    Dashboard.handleCounterup();
			}
		});
		
		 dmsCommon.ajaxRestRequest({
				url : dmsCommon.getDmsPath()["dms_report"] + "/homePage/statistical/repairs",
				type : 'GET',
				sucessCallBack : function(data) {
					Dashboard.initCharts(data);
				}
			});
		 
		 dmsCommon.ajaxRestRequest({
				url : dmsCommon.getDmsPath()["dms_report"] + "/homePage/statistical/repairs/summation",
				type : 'GET',
				sucessCallBack : function(data) {
					if($.trim(data)!="null"&&$.trim(data)!=""){
						$("#car_num",container).append(' <h3>'+data[0].car_num+'台</h3>');
						$("#cars_num",container).append(' <h3>'+data[0].cars_num+'台</h3>');
						$("#productionValue",container).append(' <h3>'+data[0].DIS_AMOUNT+'万元</h3>');
					}

				}
			});
	};
 	var dataProvider=function(list,trs,graphs){
	        var brand_code={"index":0};
    	     $.each(list,function(n,ListValue) {  
    	    	 var json={};
    	    	 var brandCode='';
    	    	 $.each(ListValue,function(i,value) {
    	    			brandCode=list[n][i].SERIES_CODE;
    	    		    if(brand_code[list[n][i].SERIES_NAME]!=list[n][i].SERIES_NAME||brand_code[list[n][i].SERIES_NAME]==null){
    	    		    	var jsonG={};
    	    		    	if(!isStringNull(list[n][i].SERIES_NAME)){

    	    		    	brand_code[''+list[n][i].SERIES_NAME+'']=list[n][i].SERIES_NAME
    	    		    	brand_code['index']=brand_code['index']+1;
    	    		    	brand_code[''+list[n][i].SERIES_CODE+'']=brand_code['index'];
    	    		    	
    	    		    	 jsonG['balloonText']='[[title]] of [[category]]:[[value]]';
		    	    		 jsonG['fillAlphas']=brand_code['index'];
		    	    		 jsonG['id']='AmGraph-'+brand_code[''+list[n][i].SERIES_CODE+'']+'';
		    	    		 jsonG['type']='column';
		    	    		 jsonG['title']=list[n][i].SERIES_NAME==null?"未知车系" : list[n][i].SERIES_NAME;
		    	    		 jsonG['valueField']='column-'+brand_code[''+list[n][i].SERIES_CODE+'']+'';
		    	    		 graphs.push(jsonG);
    	    		    	}
    	    		     }
    	    		     json['category']=list[n][i].EMPLOYEE_NAME;
    	    		     json['column-'+brand_code[brandCode]+'']=list[n][i].SAL_NUM
    	    		     //console.log("杜卡迪:"+JSON.stringify(brand_code[brandCode]));
    	    	 }); 
    	    	 trs.push(json);
    	    }); 
    	    
    	};
	return {
		homePageReport:function(container,url){
			homePageReport(container,url);
		}
	};
}();



