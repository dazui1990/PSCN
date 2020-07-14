"use strict";
/**
 * 定义DMS 主页的相关信息
 */
var dmsIndex = function() {
	
	var menus;
	var handles;
	var systemParams;
	
	var funcTabTemplate = '<li> <a href="#{[menuId]}" data-toggle="tab">{[menuName]} <i class="glyphicon glyphicon-remove"></i> </a> </li>';
	var funcTabContentTemplate = '<div class="tab-pane dmsFuncTab" id="{[menuId]}"></div>';
	/*
	 * 获取操作url
	 * 
	 */
	 
	function handelButtonAcl(container){	
		$("a[data-validateUrl]",container).each(function(i){
		   var url =$(this).attr("data-validateUrl");
		   if(url){
		    	var flag = true;
				$.each(handles,function(h,handle){			
					var reg = new RegExp();
					reg.compile(handle.code);	
					if(url.match(reg)){
					   flag = false;
					   return false;
					}
				});
				if(flag){
					$(this).remove();
				}
		  };
		  var system_type=$(this).attr("data-validateSystemType");
		  var system_code=$(this).attr("data-validateSystemCode");
		  if(system_type&&system_code){
				var settlementType=dmsCommon.getSystemParamInfo(system_type,system_code);
				if(settlementType!='10041001'){
					$(this).remove();
				}
		  }
	   });
	};
							
						
	/**
	 * 显示菜单
	 */
	
	
	var showMenus  = function () {
		
		         
		$("#menus").children(".nav-item").remove();
		dmsCommon.ajaxRestRequest({
			url : dmsCommon.getDmsPath()["web"] + "/common/login/menus",
			type : 'GET',
			sucessCallBack : function(data) {
				menus = data;
				$.each(data, function(i, menuOneObj) {
					appendHtml(menuOneObj);
					if (menuOneObj.children) {
						var twoMenu = menuOneObj.children;
						$.each(twoMenu, function(i, menuTwoObj) {
							appendHtml(menuTwoObj);
							if (menuTwoObj.children) {
								var threeMenu = menuTwoObj.children;
								$.each(threeMenu, function(i, menuThreeObj) {
									appendHtml(menuThreeObj);
								});
							}
						});
					}
				});
				// 触发click 操作
				$("li.start a").click();
			}
		});
		
		//获取操作href
		dmsCommon.ajaxRestRequest({
			url : dmsCommon.getDmsPath()["web"] + "/common/login/handles",
			type : 'GET',
			sucessCallBack : function(data) {
				handles = data;	
			}
		});
		
	};
	
	/**
	 * 根据menuId 获得菜单信息
	 */
	function getMenuInfoById(menuId){
		var findResult = {};
		//进行递归查找
		getMenuObjById(menus,menuId,-1,findResult);
		var menuNameArray = new Array();
		$.each(findResult,function(index,menu){
			menuNameArray.push(menu.menuName);
		});
		return menuNameArray;
	};
	
	/**
	 * 改变菜单
	 */
	function changeActiveFunc(menuId,menuName,url){
		//如果菜单名称没有指定
		if(menuName==undefined){
			menuName = $('.page-sidebar ul li[id='+menuId+'] >a.ajaxify').text();
		}
		//如果URL 没有指定
		if(url==undefined){
			url = $('.page-sidebar ul li[id='+menuId+'] >a.ajaxify').attr("href");
		}
		//改变活动菜单的样式
		changeActiveMenuClass(menuId);
		//加载菜单对应的Tab 页DIV
		var loadFuncDiv = loadFuncTab(menuId,menuName,url);
		
		//点击当前菜单对应的tab 页内容
		$("> ul.nav-tabs a[href='#funcId_"+menuId+"']",$("#dmsTabDiv")).click();
		return loadFuncDiv;

	}
	
	/**
	 * 改变菜单对应的缓存信息
	 */
	function changeFuncCacheData(menuId,url){
		var funcTabContentDiv = $("#dmsTabDiv >div.tab-content > div[id=funcId_"+menuId+"]");
		//还原查询条件
		$(funcTabContentDiv).removeData("memorySearchData");
		$(funcTabContentDiv).removeData("memoryDefaultSearchData");
		//TODO after remove
		$(funcTabContentDiv).data("dmsFuncId",menuId);
		
		//设置URL 信息
		$(funcTabContentDiv).data("pageUrl",url);
	}
	/**
	 * 改变活动菜单
	 */
	function changeActiveMenuClass(menuId){
		var menuContainer = $('.page-sidebar ul');
		menuContainer.children('li.active').removeClass('active');
        menuContainer.children('arrow.open').removeClass('open');
        
        $("li[id='"+menuId+"'] > a.ajaxify",menuContainer).parents('li').each(function () {
            $(this).addClass('active');
            $(this).children('a > span.arrow').addClass('open');
        });
        $("li[id='"+menuId+"'] > a.ajaxify",menuContainer).parents('li').addClass('active');
	}
	
	/**
	 * 加载FuncTab 的内容
	 */
	function loadFuncTab(menuIdValue,menuNameValue,url){
		var menuObj = {menuId:"funcId_"+menuIdValue,menuName:menuNameValue};
		
		//如果这个菜单还没有加载，则加载这个菜单
		if($("> div.tab-content div[id="+menuObj.menuId+"]",$("#dmsTabDiv")).size()==0){
			$("> ul.nav-tabs",$("#dmsTabDiv")).append(funcTabTemplate.format(menuObj));
			$("> div.tab-content",$("#dmsTabDiv")).append(funcTabContentTemplate.format(menuObj));
			
			//重新布局
			$("#dmsTabDiv .nav-tabs").tabdrop("layout");
			//绑定关闭按钮事件
			bindCloseFuncTab(menuIdValue);
			
			//改变当前菜单的ID
			changeFuncCacheData(menuIdValue,url);
			
			//执行页面请求
	        dmsCommon.ajaxPageRequest({
	        	url:url,
	        	container:$("> div.tab-content div[id="+menuObj.menuId+"]",$("#dmsTabDiv"))//定义容器
	        });
			
		}
		return $("> div.tab-content div[id="+menuObj.menuId+"]",$("#dmsTabDiv"));
		
	}
	
	/**
	 * 绑定关闭menuId 功能
	 */
	function bindCloseFuncTab(menuId){
		$("#dmsTabDiv >ul.nav-tabs a[href='#funcId_"+menuId+"'] > i.glyphicon-remove").on("click",function(event){
			event.preventDefault();
			var removeIcon = $(this);
			var tabContentId = $(removeIcon).closest("a").attr("href");
			var dmsFuncTab = $(removeIcon).closest("li");
			var dmsFuncContent = $("#dmsTabDiv >div.tab-content >"+tabContentId);
			
			//如果要删除的tab 页为当前页面，则将当前页面的前一个页面更改为活动
			if($(dmsFuncTab).hasClass("active")){
				if($(dmsFuncTab).prev().hasClass("dropdown")){
					$(dmsFuncTab).next().addClass('active');
					$(dmsFuncContent).next().addClass('active');
				}else{
					$(dmsFuncTab).prev().addClass('active');
					$(dmsFuncContent).prev().addClass('active');
				}
			}
			
			$("#dmsTabDiv >div.tab-content >"+tabContentId).remove();
			$(removeIcon).closest("li").remove();
			
			//重新布局
			$("#dmsTabDiv .nav-tabs").tabdrop("layout");
		});
	}
	
	/**
	 * 查找对应的菜单信息
	 */
	function getMenuObjById(menuArray,menuId,loopTime,findResult){
		loopTime++;
		$.each(menuArray,function(k,searchMenu){
			if(findResult.result!=undefined){
				return false;
			}
			//进行循环
			if(searchMenu.children){
				//标识结果
				findResult[loopTime] = searchMenu;
				//进行循环
				getMenuObjById(searchMenu.children,menuId,loopTime,findResult);
			}else{
				if(searchMenu.menuId==(menuId+"")){
					findResult.result = searchMenu;
					return false;
				}
			}
		});
	}
	

	function appendHtml(Obj){
		if(Obj.menuType==1000 || Obj.menuType==1001){
			if(Obj.menuUrl){
				$("#menus").append("<li id='"+Obj.menuId+"' class='nav-item start'><a href="+Obj.menuUrl
						+" class=\"nav-link ajaxify\"> <i class='"+Obj.menuIcon+"'>" +
						"</i><span class=\"title\">"+Obj.menuName+"</span><span class=\"arrow\"></span></a>" +
								"<ul id="+Obj.menuId+" class=\"sub-menu\"></ul></li>");
			}else{
				$("#menus").append("<li class=\"nav-item\">" +
						"<a href=javascript:; class=\"nav-link nav-toggle\"> <i class='"+Obj.menuIcon+"'>" +
						"</i><span class=\"title\">"+Obj.menuName+"</span><span class=\"arrow\"></span></a>" +
								"<ul id="+Obj.menuId+" class=\"sub-menu\"></ul></li>");
			}
		}
		if(Obj.menuType==1002){
			if(Obj.menuUrl){
				$("ul[id='"+Obj.parentId+"']").append(
						"<li id="+Obj.menuId+" class=\"nav-item\"><a href="+Obj.menuUrl+" class=\"nav-link ajaxify\">" 
						+"<i class='"+Obj.menuIcon+"'></i>"+Obj.menuName+"</span><span class=\"arrow\"></span></a>" +
								"<ul id="+Obj.menuId+" class=\"sub-menu\"></ul></li>"); 
			}else{
				$("ul[id='"+Obj.parentId+"']").append(
						"<li class=\"nav-item\"><a href=javascript:; class=\"nav-link nav-toggle\">" 
						+"<i class='"+Obj.menuIcon+"'></i>"+Obj.menuName+"</span><span class=\"arrow\"></span></a>" +
								"<ul id="+Obj.menuId+" class=\"sub-menu\"></ul></li>");
			}
		}
		if(Obj.menuType==1003){
			if(Obj.menuUrl){
				$("ul[id='"+Obj.parentId+"']").append("<li id="+Obj.menuId+" class=\"nav-item\"><a href="+Obj.menuUrl+" class=\"nav-link ajaxify\">" 
						+"<i class='"+Obj.menuIcon+"'></i>"+Obj.menuName+"</a></li>"); 
			}else{
				$("ul[id='"+Obj.parentId+"']").append("<li id="+Obj.menuId+" class=\"nav-item\"><a href=javascript:; class=\"nav-link nav-toggle\">" 
						+"<i class='"+Obj.menuIcon+"'></i>"+Obj.menuName+"</a></li>");
			}
		}
	};
	
	
			
	
	/**
	 * 加载主页的html 页面
	 */
	var loadIndexHtml = function(){
		var needsLoadDev = $("div[data-yonyou-url]");
	 	if (needsLoadDev.size() > 0) {
	 		needsLoadDev.each(function(index,item) {
	 			var isDetailFlag = $(item).attr("data-isDetailFlag");
	 			var url = $(item).attr("data-yonyou-url");
	 			var container = $(item);
	 			dmsCommon.ajaxPageRequest({
		        	url:url,
		        	container:container,//定义容器
		        	pageData:$(item).data("pageData"),
		        	success:function(html){
		        		//如果是明细界面，则将界面元素标记为只读
		        		if(isDetailFlag&&isDetailFlag=="true"){
		        			$("div.dms-edit",container).attr("data-isDetailFlag",true);
		        		}
		        	},
		        	complete:function(xmlRequest, statusCode){
		        		//初始化操作
		                dmsValidate.init(container);
		        	}
		        });
	         });
	     }
	};
	
	/**
	 * 展示多语言选项
	 */
	var showI18nChoice = function(){
		var cookieLanguage = $.cookie('language');
		var userBrowerInfo = App.getUserBrowerInfo();
		var useLanguage = cookieLanguage==undefined?userBrowerInfo.browserLanguage:cookieLanguage;
		if("zh_CN" == useLanguage){
		 	$("#pageLanguage").text("English");
		 	$("#pageLanguage").attr("language",useLanguage);
		 	$("#pageLanguage").attr("nextLanguage","en_US");
		 	
		 	//加载中文的js 文件
		 	loadjscssfile("../assets/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js","js");
		 	loadjscssfile("../assets/global/plugins/boostrap-table/locale/bootstrap-table-zh-CN.min.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-fileinput/locales/zh.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-select/js/i18n/defaults-zh_CN.min.js","js");
//		 	loadjscssfile("../assets/global/plugins/jquery-validation/js/localization/messages_zh.js","js");
		 	
		 }else{
		 	$("#pageLanguage").text("中文");
		 	$("#pageLanguage").attr("language",useLanguage);
		 	$("#pageLanguage").attr("nextLanguage","zh_CN");
			loadjscssfile("../assets/global/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js","js");
		 	loadjscssfile("../assets/global/plugins/boostrap-table/locale/bootstrap-table-zh-CN.min.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-fileinput/locales/zh.js","js");
		 	loadjscssfile("../assets/global/plugins/bootstrap-select/js/i18n/defaults-zh_CN.min.js","js");
		 	//加载英文的css 文件
		 	loadjscssfile("../assets/layouts/layout/css/custom_en.css","css");
	 	 }
		$.cookie('language', useLanguage, { expires: 365, path: dmsCommon.getDmsPath()["root"] });
	}
	
	/**
	 * 加载css文件或是js 文件
	 */
	var loadjscssfile = function (filename,filetype){
		var fileref;
	    if(filetype == "js"){
	        fileref = document.createElement('script');
	        fileref.setAttribute("type","text/javascript");
	        fileref.setAttribute("src",filename);
	    }else if(filetype == "css"){
	    
	        fileref = document.createElement('link');
	        fileref.setAttribute("rel","stylesheet");
	        fileref.setAttribute("type","text/css");
	        fileref.setAttribute("href",filename);
	    }
	    
	   if(typeof fileref != undefined){
	        document.getElementsByTagName("head")[0].appendChild(fileref);
	    }
	}
	
	/**
	 * 页面头初始化
	 */
	var pageHeaderInit = function(){
		showI18nChoice();
		//初始化界面的信息
    	initIndexPageHtml();
	}
	
	/**
	 * 页面头初始化
	 */
	var pageHeaderInitUI = function(){
		showI18nChoice();
		//初始化界面的信息
    	//initIndexPageHtml();
	}
	/**
	 * 绑定window 窗口事件
	 */
	var bindWindowEvent = function(){
		$(window).on("resize",function () {
			Layout.fixContentHeight();
		});
	}
	
	/**
	 *根据初始化数据加载首页的信息
	 */
	var initIndexPageHtml = function(){
		var commonDataMap = dmsCommon.getCommonData();
		//初始化数据
		$("#dmsHeaderUserName",$("#dmsHeaderContent")).html($("#dmsHeaderUserName").html().format(commonDataMap));
	}
	
	/**
	 * 加载DMS FuncTab
	 */
	function initDmsFuncTab(){
		if ($().tabdrop) {
	        $('.tabbable-tabdrop .nav-pills, .tabbable-tabdrop .nav-tabs').tabdrop({
	            text: '<i class="fa fa-ellipsis-v"></i>&nbsp;<i class="fa fa-angle-down"></i>'
	        });
	    }
	}
	
	/**
	 * 主页初始化
	 */
	var init = function(){
		initDmsFuncTab(); //加载DMS 菜单Tab 页
		dmsCommon.initSystemData();
		loadIndexHtml();
	}
	
	/**
	 * 主页初始化
	 */
	var initUI = function(){
		initDmsFuncTab(); //加载DMS 菜单Tab 页
		//dmsCommon.initSystemData();
		loadIndexHtml();
	}
	
	return {
		init:function(){
			init();
		},
		initUI:function(){
			initUI();
		},
		pageHeaderInit:function(){
			pageHeaderInit();
		},	
		pageHeaderInitUI:function(){
			pageHeaderInitUI();
		},	
		showMenus:function(){
			showMenus();
		},
		showI18nChoice:function(){
			showI18nChoice();
		},
		loadjscssfile:function(filename,filetype){
			loadjscssfile(filename,filetype);
		},
		initIndexPageHtml:function(commonDataMap){
			initIndexPageHtml(commonDataMap);
		},
		getMenuInfoById:function(menuId){
			return getMenuInfoById(menuId);
		},
		changeActiveFunc:function(menuId,menuName){
			return changeActiveFunc(menuId,menuName);
		},
		changeActiveMenuClass:function(menuId){
			changeActiveMenuClass(menuId);
		},
		changeFuncCacheData:function(menuId){
			changeFuncCacheData(menuId);
		},
		handelButtonAcl:function(container){
			handelButtonAcl(container);
		}
		
	};
}();
