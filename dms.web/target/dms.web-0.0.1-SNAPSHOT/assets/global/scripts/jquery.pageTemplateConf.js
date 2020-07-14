"use strict";
/**
 * 根据类型来动态生成页面
 *  依赖jquery; pageCommon.js
 */
;(function ($) {

    //默认参数
    var defaluts = {};

    function formatString(str, args) {
        var reg = new RegExp("\\[\\[[^(\\s|\\})]+\\]\\]", "g");
        var reg2 = new RegExp("[^\\[\\[\\]\\]]+", "g");
        var matchResult = str.match(reg);
        var matchKeys;
        $.each(matchResult, function (index, item) {
            var matchResult2 = item.match(reg2);
            //拿到对应的值
            var val = getDataByKey(matchResult2, args);
            if (val) {
                matchKeys = matchKeys == undefined ? {} : matchKeys;
                matchKeys[matchResult2] = val;
            } else {
                matchKeys = matchKeys == undefined ? {} : matchKeys;
                matchKeys[matchResult2] = "";
            }
        });
        for (var key in matchKeys) {
            var reg3 = new RegExp("(\\[\\[" + key + "\\]\\])", "g");
            str = str.replace(reg3, matchKeys[key]);
        }
        return str;
    }

    //模板
    var templates = {
        //日期范围
        "10681001": '<div class="col-xs-12 col-sm-12 col-md-8 col-lg-6"> <div class="form-group"> <label class="control-label col-xs-4 col-sm-2">[[CONF_NAME]]</label> <div class="col-xs-8 col-sm-10"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <div [[TAG_ATTRIBUTE]] > <input class="form-control" readonly="readonly" name="paramValue1" value="[[VALUE1]]" type="text"> <span class="input-group-addon">至</span> <input class="form-control" readonly="readonly" name="paramValue2" value="[[VALUE2]]" type="text"> <span class="input-group-btn"> <button class="btn default input-clear" type="button"> <i class="fa fa-close"></i> </button> </span> </div> </div> </div> </div>',
        //文本框范围
        "10681002": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <div class="input-group"> <input type="hidden" name="paramId" value="{[CONF_ID]}" /> <input type="text" class="form-control decimal" name="paramValue1" value="[[VALUE1]]"> <span class="input-group-addon">至</span> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input type="text" class="form-control decimal" name="paramValue2" value="[[VALUE2]]"> </div> </div> </div> </div>',
        //下拉框
        "10681003": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <select name="paramValue1" class="bs-select form-control" data-value="[[VALUE1]]" [[TAG_ATTRIBUTE]]> </select> </div> </div> </div>',
        //品牌
        "10681004": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <select id="brand" class="bs-select form-control" name="paramValue1" data-url="/vehicleProduct/brandsdict" data-model="dms_basedata" data-labelValue="BRAND_ID" data-lableDesc="BRAND_NAME" data-value="[[VALUE1]]"> </select> </div> </div> </div>',
        //车系
        "10681005": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <select id="series" parent="brand" class="bs-select form-control" name="paramValue1" data-url="/vehicleProduct/brandsdict/{[brand]}/seriessdict" data-model="dms_basedata" data-labelValue="SERIES_ID" data-lableDesc="SERIES_NAME" data-value="[[VALUE1]]"> </select> </div> </div> </div>',
        //车型
        "10681006": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <select id="model" parent="series" class="bs-select form-control" name="paramValue1" data-url="/vehicleProduct/brandsdict/{[brand]}/seriessdict/{[series]}/modelsdict" data-model="dms_basedata" data-labelValue="MODEL_ID" data-lableDesc="MODEL_NAME" data-value="[[VALUE1]]"> </select> </div> </div> </div>',
        //文本框
        "10681007": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input name="paramValue1" type="text" class="form-control" data-value="[[VALUE1]]" /> </div> </div> </div>',
        //生日开始
        "10681008": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <div class="input-group"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input type="text" class="form-control digits" maxlength="2" min="1" max="12" name="paramValue1" value="[[VALUE1]]" /> <span class="input-group-addon">月</span> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input type="text" class="form-control digits" maxlength="2" min="1" max="31" name="paramValue2" value="[[VALUE2]]" /> <span class="input-group-addon">日</span> </div> </div> </div> </div>',
        //生日结束
        "10681009": '<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3"> <div class="form-group"> <label class="control-label col-xs-4">[[CONF_NAME]]</label> <div class="col-xs-8"> <div class="input-group"> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input type="text" class="form-control digits" maxlength="2" min="1" max="12" name="paramValue1" value="[[VALUE1]]" /> <span class="input-group-addon">月</span> <input type="hidden" name="paramId" value="[[CONF_ID]]" /> <input type="text" class="form-control digits" maxlength="2" min="1" max="31" name="paramValue2" value="[[VALUE2]]" /> <span class="input-group-addon">日</span> </div> </div> </div> </div>'
    };

    var methods = {
        "init": function (options) {
            return this;
        },
        "appendByConfCode": function (options) {
            var $that = this;
            $that.empty();
            if (!options.hasOwnProperty("confCode") || !options["confCode"]) {
                return;
            }
            var url = dmsCommon.getDmsPath()["dms_basedata"] + "/pageTemplateConfs/" + options["confCode"];
            if (options.hasOwnProperty("billType") && options["billType"] && options.hasOwnProperty("billId") && options["billId"]) {
                url += "/" + options["billType"] + "/" + options["billId"];
            }
            //进行ajax 请求
            dmsCommon.ajaxRestRequest({
                url: url,
                type: 'GET',
                sucessCallBack: function (data) {
                    if (data) {
                        $.each(data, function (index, item) {
                            var temp = templates[item.DATA_TYPE];
                            var resultStr = formatString(temp, item);
                            resultStr = resultStr.replace(/&quot;/g, "\"");
                            if (item.DATA_TYPE == "10681001") {
                                if ($("div.required", resultStr).length) {
                                    $("div.required input[type=text]", resultStr).each(function (i, inputObj) {
                                        var inputHtml = inputObj.outerHTML;
                                        var newInputHtml = $(inputObj).addClass("required")[0].outerHTML;
                                        var reg = new RegExp(inputHtml, "g");
                                        resultStr = resultStr.replace(reg, newInputHtml);
                                    });
                                }
                            }
                            $that.append(resultStr);
                        });
                        //执行初始化
                        dmsCommon.init($that);
                        dmsCommon.afterInit($that);
                    }
                }
            });
        }
    };

    $.fn.pageTemplateConf = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.pageTemplateConf');
        }
    };
})(jQuery);
