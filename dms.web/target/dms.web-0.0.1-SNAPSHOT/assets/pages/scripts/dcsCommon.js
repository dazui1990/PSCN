/*
 * 品牌与车系联动，给品牌加上change事件
 */ 
var brandToSeries = function(container, brandSelId, seriesSelId, seriesInfo) {
	$("#"+brandSelId,container).bindChange(function(obj){
		var brandIds = new Array();
		var brandId_ = $("#"+brandSelId,container).val();
		if(typeof(brandId_)=='string'){
			brandIds.push(brandId_);
		}else{
			brandIds = brandId_;
		}

		$("#"+seriesSelId, container).html('');
		//循环添加  
        for(var i = 0; i < seriesInfo.length; i++) {
        	if(brandIds != '' && brandIds.length > 0) {
        		var existsBrand = false;
        		for(var j = 0; j < brandIds.length; j++) {
        			if(brandIds[j] == seriesInfo[i].BRAND_ID) {
        				existsBrand = true;
        				break;
        			}
        		}
        		if(!existsBrand) {
        			continue;
        		}
        	}
            var option = $("<option>").val(seriesInfo[i].SERIES_ID).text(seriesInfo[i].SERIES_NAME);  
            $("#"+seriesSelId, container).append(option);
        }
		$("#"+seriesSelId, container).selectpicker('refresh');
		$("#"+seriesSelId, container).selectpicker('show');
	});
}

var initSeriesSel = function(container, seriesSelId, valid) {
	var seriesInfo = new Array();
	dmsCommon.ajaxRestRequest({
		url : dmsCommon.getDmsPath()["factoryBase"] + '/productData/'+valid+'/seriesSel',
		type : 'GET',
		//data : 'brandId='+$("#brandId"+brandSelId,container).val(),
		async : false,
		sucessCallBack : function(data) {
			if(data){
				//$("#"+seriesSelId).empty();
				//$("#"+seriesSelId).multiselect('rebuild');
				$("#"+seriesSelId, container).html('');
				//循环添加  
                for(var i = 0; i < data.length; i++) {  
                    var option = $("<option>").val(data[i].SERIES_ID).text(data[i].SERIES_NAME);  
                    $("#"+seriesSelId, container).append(option);
                    
    				seriesInfo.push({'BRAND_ID':data[i].BRAND_ID, 'SERIES_ID':data[i].SERIES_ID, 'SERIES_NAME':data[i].SERIES_NAME});
                }
				$("#"+seriesSelId, container).selectpicker('refresh');
				$("#"+seriesSelId, container).selectpicker('show');
			}
		}
	});
	return seriesInfo;
}

var pay_payType;
var pay_orderNumId;
var showPayData = function(container, payType, billUnit, brandId, totalMoneyId, orderNumId) {
	pay_payType = payType;
	pay_orderNumId = orderNumId;
	if(!billUnit) {
		dmsCommon.tip({status:"error",msg:"请选择开票单位!"});
		return false;
	}
	if(!payType) {
		$('#payDiv', container).html('');
		return false;
	}
	// 重卡：所有类型都可以使用现金支付
	
	// 首付款：使用额度后，需要付百分比的现金首付款
	$('#payDiv', container).html('');
	showAccount(container, payType, billUnit, brandId, totalMoneyId);
	if(payType != '55211001' && payType == '55211009') {
		// 承兑汇票
		showAccept(container, payType, billUnit, brandId, totalMoneyId);
	} else if(payType != '55211001' && payType != '55211009') {
		// 额度
		showCredence(container, payType, billUnit, brandId, totalMoneyId);
	}
}
var showAccount = function(container, payType, billUnit, brandId, totalMoneyId) {
	$('#payDiv', container).append('<div class="panel panel-default"><div class="panel-heading"><div class="pannel-name">选择现金</div></div>'+
			'<div class="panel-body"><table class="table table-striped table-bordered table-hover table-responsive" id="accountPay"></table></div></div>');
	new Datatable().initLocale({
		src : 'accountPay',
		container:container,
		rowID : "ACCOUNT_ID",
		sortName : "ACCOUNT_ID", //当需要默认排序时写，否则不写，默认情况下不写，sortOrder同样
		sortOrder : "asc",
		checkboxHeader : false, //全选框
		selectItemName : "accountId", //定义checkbox或radio的名字
		columns : [ 
		    {checkbox:true,sortable : false, formatter:function(){
		    	if(pay_payType != '55211009') {
		    		return true;
		    	}
		    	return false;
		    }},
		    {field : "ACCOUNT_CODE",title : "账户代码"},
		    {field : "ACCOUNT_MONEY",title : "账户余额"},
			{field : "FROZEN_MONEY",title : "冻结金额"}, 
			{field : "CAN_USE_MONEY",title : "可使用金额"},
			{field : "USE_MONEY",inputField:"useAccount",title : "本次使用金额",inputNumberFormat : {validate:{validateClass:"moneyMinus"}}}
		]
	});
	// 添加隐藏
	$(function(){
		 $('#accountPay').bootstrapTable('hideColumn','ACCOUNT_ID');
	});
	// 手动加载数据
	$("table[id=accountPay]",container).bootstrapTable('refresh', {url: dmsCommon.getDmsPath()["jmcFinance"]+'/payment/getpaydata?pay_payType=55211001&pay_billUnit='+billUnit+'&pay_brandId='+brandId});
	// 添加鼠标单击事件
	$("#accountPay",container).on("mousedown.useAccount", "input[data-inputname='useAccount']", function(event) {
		if($("#"+event.target.id,container).val() == '') {
			// 如果是额度，现金不可修改，直接使用首付款默认
			$("#"+event.target.id,container).val($("#"+totalMoneyId, container).val());
		}
		return true;
	});
	$("#accountPay",container).on("blur.useAccount", "input[data-inputname='useAccount']", function(event) {
		if(payType != '55211001' && payType !='55211009') {//额度是提示选择首付款
			var selText = $("#firstPayCode", container).find("option:selected").text();
			if(selText =='请选择') {
				$("#"+event.target.id,container).val('');
				dmsCommon.tip({status:"warning",msg:"请先选择首付款！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
		
		if($("#"+event.target.id,container).val() != '' && pay_orderNumId) {
			var useAccount = $("#"+event.target.id,container).val();
			var orderNum = $("#"+pay_orderNumId,container).val();
			if(parseFloat(useAccount) % parseFloat(orderNum) != 0) {
				$("#"+event.target.id,container).val('');
				dmsCommon.tip({status:"warning",msg:"填写的金额不能与数量整除，请重新输入！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
		return true;
	});
}
var showAccept = function(container, payType, billUnit, brandId, totalMoneyId) {
	$('#payDiv', container).append('<div class="panel panel-default"><div class="panel-heading"><div class="pannel-name">选择承兑汇票</div></div>'+
		'<div class="panel-body"><table class="table table-striped table-bordered table-hover table-responsive" id="acceptPay"></table></div></div>');
	new Datatable().initLocale({
	src : 'acceptPay',
	container:container,
	rowID : "REMITTANCE_ID",
	sortName : "REMITTANCE_ID", //当需要默认排序时写，否则不写，默认情况下不写，sortOrder同样
	sortOrder : "asc",
	checkboxHeader : false, //全选框
	selectItemName : "remittanceId", //定义checkbox或radio的名字
	columns : [    
	      {checkbox:true,sortable : false},
	      {field : "REMIT_MARK_NO",title : "汇款记录号"},
	      {field : "REMIT_DATE",title : "汇款日期",dateFormat : {format:"YYYY-MM-DD"}},
	      {field : "AMOUNT",title : "汇款总额"},
	      {field : "CAN_USE_MONEY",title : "可用余额"},
	      {field : "USE_MONEY",inputField:"useAccept",title : "本次使用金额",inputNumberFormat : {validate:{validateClass:"moneyMinus"}}}
		]
	});
	$(function(){
	 $('#acceptPay').bootstrapTable('hideColumn','REMITTANCE_ID');
	});
	$("table[id=acceptPay]",container).bootstrapTable('refresh', {url: dmsCommon.getDmsPath()["jmcFinance"]+'/payment/getpaydata?pay_payType=55211009&pay_billUnit='+billUnit+'&pay_brandId='+brandId});
	$("#acceptPay",container).on("blur.useAccept", "input[data-inputname='useAccept']", function(event) {
		if($("#"+event.target.id,container).val() != '' && pay_orderNumId) {
			var useAccept = $("#"+event.target.id,container).val();
			var orderNum = $("#"+pay_orderNumId,container).val();
			if(parseFloat(useAccept) % parseFloat(orderNum) != 0) {
				$("#"+event.target.id,container).val('');
				dmsCommon.tip({status:"warning",msg:"填写的金额不能与数量整除，请重新输入！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
		return true;
	});
	
	// 添加鼠标单击事件 自动计算金额
	$("#acceptPay",container).on("mousedown.useAccept", "input[data-inputname='useAccept']", function(event) {
		var accountRow = $("#accountPay",container).length>0?$("#accountPay",container).dmsTable().getFirstSelection():null;
		var acceptRow = $("#acceptPay",container).dmsTable().getTableRows();
		var totalUseMoney = 0;//已使用合计
		var totalMoney = parseFloat($("#"+totalMoneyId, container).val());
		if(accountRow){
			var accountUseMoney = $("#useAccount"+accountRow.index,container).val();
			if(accountUseMoney && (parseFloat(accountUseMoney) > 0 )) {
				totalUseMoney = parseFloat(accountUseMoney);
			}
		}
		
		if(acceptRow > 0){
			for(var i=0; i<acceptRow; i++) {
				var oneUseMoney = $("#useAccept"+i,container).val();//选择的承兑汇票
				if(oneUseMoney!=null && parseFloat(oneUseMoney) > 0) {
					totalUseMoney += parseFloat(oneUseMoney);
				}	
			}
			if(totalUseMoney==0){
				$("#"+event.target.id,container).val(totalMoney);
			}
			if((totalUseMoney > 0 ) && (totalUseMoney < totalMoney)){
				$("#"+event.target.id,container).val(totalMoney-totalUseMoney);
			}
		}
		return true;
	});
}
var showCredence = function (container, payType, billUnit, brandId, totalMoneyId) {
	$('#payDiv', container).append('<div class="panel panel-default"><div class="panel-heading"><div class="pannel-name">选择额度</div></div>'+
			'<div class="panel-body"><table class="table table-striped table-bordered table-hover table-responsive" id="credencePay"></table></div></div>');
	new Datatable().initLocale({
		src : 'credencePay',
		container:container,
		rowID : "CREDENCE_ID",
		sortName : "CREDENCE_ID", //当需要默认排序时写，否则不写，默认情况下不写，sortOrder同样
		sortOrder : "asc",
		selectItemName : "credenceId", //定义checkbox或radio的名字
		columns : [    
              {radio:true,sortable : false},
              {field : "EDID",title : "额度模式", codeFormat : {type:"dict",codeType:"5803"}},
              {field : "CREDENCE_CODE",title : "批次号"},
              {field : "XMNAME",title : "额度说明"},
              {field : "FLAG",title : "额度类型", codeFormat : {type:"dict",codeType:"5804"}},
              {field : "TOTAL_AMOUNT",title : "总额度"},
              {field : "LAST_MONEY",inputField:"canUseCred",title : "剩余可用额度",inputHiddenFormat:{hiddenField:"LAST_MONEY"}},
              {field : "USE_MONEY",inputField:"useCred",title : "本次使用金额",inputNumberFormat : {validate:{validateClass:"moneyMinus"}}}
			]
	});
	$(function(){
		 $('#credencePay').bootstrapTable('hideColumn','CREDENCE_ID');
	});
	$("table[id=credencePay]",container).bootstrapTable('refresh', {url: dmsCommon.getDmsPath()["jmcFinance"]+'/payment/getpaydata?pay_payType='+payType+'&pay_billUnit='+billUnit+'&pay_brandId='+brandId});
	// 添加鼠标单击事件	
	$("#credencePay",container).on("mousedown.useCred", "input[data-inputname='useCred']", function(event) {
		var accountRow = $("#accountPay",container).length>0?$("#accountPay",container).dmsTable().getFirstSelection():null;
		var accountUseMoney = $("#useAccount"+accountRow.index,container).val()?parseFloat($("#useAccount"+accountRow.index,container).val()):0;//已使用现金
		var total = $("#"+totalMoneyId, container).val();//价款合计
		if($("#"+event.target.id,container).val() == '' && parseFloat(total)>0) {		
			var remaining = total - accountUseMoney;
			$("#"+event.target.id,container).val(remaining);//应该支付的剩余额度
		}
		return true;
	});
	$("#credencePay",container).on("blur.useCred", "input[data-inputname='useCred']", function(event) {
		if($("#"+event.target.id,container).val() != '' && pay_orderNumId) {
			var useCred = $("#"+event.target.id,container).val();
			var orderNum = $("#"+pay_orderNumId,container).val();
			if(parseFloat(useCred) % parseFloat(orderNum) != 0) {
				$("#"+event.target.id,container).val('');
				dmsCommon.tip({status:"warning",msg:"填写的金额不能与数量整除，请重新输入！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
		return true;
	});
}

var checkShipPayValid = function(container, payType, totalMoneyId) {
	// 1、使用正票，只能使用现金
	// 2、使用承兑汇票，承兑汇票必选，现金可选
	// 3、使用额度，额度必选，现金可选
	var accountRow = $("#accountPay",container).length>0?$("#accountPay",container).dmsTable().getFirstSelection():null;
	var acceptRow = $("#acceptPay",container).length>0?$("#acceptPay",container).dmsTable().getSelections():null;
	var credenceRow = $("#credencePay",container).length>0?$("#credencePay",container).dmsTable().getFirstSelection():null;
	if(payType == '55211001') {
		if(!accountRow) {
			dmsCommon.tip({status:"error",msg:"请选择现金！"});//总共的状态类型：info、success、error、warning
			return false;
		} else if(acceptRow || credenceRow) {
			dmsCommon.tip({status:"error",msg:"使用正票付款时，不能选择承兑汇票或额度支付！"});//总共的状态类型：info、success、error、warning
			return false;
		} else {
			var accountUseMoney = $("#useAccount"+accountRow.index,container).val();
//			var accountCanUseMoney = $("#canUseAccount"+selectRow.index,container).val();
//			var accountUseMoney = accountRow.USE_MONEY;
			var accountCanUseMoney = accountRow.CAN_USE_MONEY;
			if(!accountUseMoney || parseFloat(accountUseMoney) <= 0) {
				dmsCommon.tip({status:"error",msg:"请填写正确的付款金额！"});//总共的状态类型：info、success、error、warning
				return false;
			} else if(parseFloat($("#"+totalMoneyId).val()) != parseFloat(accountUseMoney)) {
				dmsCommon.tip({status:"error",msg:"付款金额与扣款金额不符！"});//总共的状态类型：info、success、error、warning
				return false;
			} else if(parseFloat(accountCanUseMoney) < parseFloat($("#"+totalMoneyId).val())) {
				dmsCommon.tip({status:"error",msg:"现金账户余额不足，请打款或使用其他支付方式！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
	} else if(payType == '55211009') {
		if(!acceptRow) {
			dmsCommon.tip({status:"error",msg:"请选择承兑汇票！"});//总共的状态类型：info、success、error、warning
			return false;
		} else if(credenceRow) {
			dmsCommon.tip({status:"error",msg:"使用承兑汇票付款时，不能选择额度支付！"});//总共的状态类型：info、success、error、warning
			return false;
		} else {
			var acceptUseMoney = 0;
			var acceptCanUseMoney = 0;
			// 判断选择的承兑汇票的数据有效性
			for(var i=0; i<acceptRow.length; i++) {
				var oneUseMoney = $("#useAccept"+acceptRow[i].index,container).val()?parseFloat($("#useAccept"+acceptRow[i].index,container).val()):0;
				var oneCanUseMoney = acceptRow[i].CAN_USE_MONEY?parseFloat(acceptRow[i].CAN_USE_MONEY):0;
				if(oneUseMoney <= 0) {
					dmsCommon.tip({status:"error",msg:"第"+(acceptRow[i].index+1)+"张承兑汇票中，请填写正确的付款金额！"});
					return false;
				} else if(oneUseMoney > oneCanUseMoney) {
					dmsCommon.tip({status:"error",msg:"第"+(acceptRow[i].index+1)+"张承兑汇票中账户余额不足，请减少使用金额或使用其他支付方式！"});
					return false;
				}
				
				acceptUseMoney += oneUseMoney;
				acceptCanUseMoney += oneCanUseMoney
			}
//			if(acceptUseMoney == 0) {
//				dmsCommon.tip({status:"error",msg:"使用承兑汇票支付时，使用承兑汇票金额必须大于0！"});//总共的状态类型：info、success、error、warning
//				return false;
//			}
			// 如果使用了现金，判断现金是否足够
			if(accountRow) {
				var accountUseMoney = $("#useAccount"+accountRow.index,container).val()?parseFloat($("#useAccount"+accountRow.index,container).val()):0;//使用的现金
				if(accountUseMoney == 0) {
					dmsCommon.tip({status:"error",msg:"如果选择了现金，请填写现金金额！"});
					return false;
				}
				var accountCanUseMoney = accountRow.CAN_USE_MONEY;
				if(accountUseMoney > accountCanUseMoney) {
					dmsCommon.tip({status:"error",msg:"现金余额不足，请使用其他支付方式！"});
					return false;
				}
				acceptUseMoney += accountUseMoney;
			}
			
			if(acceptUseMoney != parseFloat($("#"+totalMoneyId).val())) {
				dmsCommon.tip({status:"error",msg:"付款金额与扣款金额不符！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
	} else {
		if(!credenceRow) {
			dmsCommon.tip({status:"error",msg:"请选择额度！"});//总共的状态类型：info、success、error、warning
			return false;
		} else if(acceptRow) {
			dmsCommon.tip({status:"error",msg:"使用额度付款时，不能选择承兑汇票支付！"});//总共的状态类型：info、success、error、warning
			return false;
		} else if(!accountRow) {
			dmsCommon.tip({status:"error",msg:"使用额度付款时，必须选择现金！"});//总共的状态类型：info、success、error、warning
			return false;
		} else {
			var credenceUseMoney = $("#useCred"+credenceRow.index,container).val()?parseFloat($("#useCred"+credenceRow.index,container).val()):0;//使用的额度
			if(credenceUseMoney == 0) {
				dmsCommon.tip({status:"error",msg:"使用额度支付时，使用额度的金额必须大于0！"});//总共的状态类型：info、success、error、warning
				return false;
			}
			var credenceCanUseMoney = credenceRow.LAST_MONEY;
			// 判断额度可使用金额是否足够
			if(credenceUseMoney > credenceCanUseMoney) {
				dmsCommon.tip({status:"error",msg:"额度余额不足，请使用其他支付方式！"});
				return false;
			}
			
			// 如果使用了现金，判断现金是否足够
			if(accountRow) {
				var accountUseMoney = $("#useAccount"+accountRow.index,container).val()?parseFloat($("#useAccount"+accountRow.index,container).val()):0;//使用的现金
				if(accountUseMoney == 0) {
					dmsCommon.tip({status:"error",msg:"如果选择了现金，请填写现金金额！"});
					return false;
				}
				var accountCanUseMoney = accountRow.CAN_USE_MONEY;
				if(accountUseMoney > accountCanUseMoney) {
					dmsCommon.tip({status:"error",msg:"现金余额不足，请使用其他支付方式！"});
					return false;
				}
				credenceUseMoney += accountUseMoney;
			}
			// 付款金额是否一致
			if(credenceUseMoney != parseFloat($("#"+totalMoneyId).val())) {
				dmsCommon.tip({status:"error",msg:"付款金额与扣款金额不符！"});//总共的状态类型：info、success、error、warning
				return false;
			}
		}
	}
	
	return true;
}