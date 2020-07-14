/**
 * Load i18n properties
 */
(function($){
    $.dmsI18n = function(){};

    $.extend($.dmsI18n,
        {
            languageMap : {"zh_CN":"zh","en_US":"en"}
        },
        {
            loadProperties : function() {
                jQuery.i18n.properties({
                    language: this.languageMap[$.cookie("language")],
                    name: "i18n",
                    path: "../assets/global/properties/",
                    mode: "map"
                });
            }
        },
        {
            replaceI18n : function (htmlCode) {
                return htmlCode.replace(/#jl\.i18n\.[\w|\d]*#/ig, function (i18n_str) {
                    return jQuery.i18n.prop(i18n_str.substring(1, i18n_str.length - 1));
                });
            }
        }
    );
})(jQuery);

$(function(){
    $.dmsI18n.loadProperties();
});