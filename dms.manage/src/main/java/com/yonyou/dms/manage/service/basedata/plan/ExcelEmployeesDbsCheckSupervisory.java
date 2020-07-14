package com.yonyou.dms.manage.service.basedata.plan;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yonyou.dms.framework.service.excel.ExcelExportColumn;

public interface ExcelEmployeesDbsCheckSupervisory
{
    /**
     * 
    * 导出excel 数据，通过ExcelExportColumn 定义导出的样式
    * @author zhangxc
    * @date 2016年9月28日
    * @param excelData
    * @param columnDefine
    * @param fileName
    * @param response
     */
   void generateExcel(@SuppressWarnings("rawtypes") Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,String fileName,HttpServletRequest request, HttpServletResponse response);
  
   void generateExcel1(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion,String fileName, HttpServletRequest request, HttpServletResponse response);
   
   void generateExcelPlan(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion,String fileName, HttpServletRequest request, HttpServletResponse response);
   
   void dbsInfoGenerateExcel(List<int[]> untilList,@SuppressWarnings("rawtypes") Map<String, List<Map>> excelData, List<List<ExcelExportColumn>> titleList,String fileName,HttpServletRequest request, HttpServletResponse response);

  
}