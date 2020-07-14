package com.yonyou.dms.manage.service.shop;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;

import com.yonyou.dms.framework.service.excel.ExcelExportColumn;

/*
 * 根据  Repair、parts、Manager 等功能包产生的 Excel数据，生成一个 Excel文件流，供前台页面下载
 * Created by wfl
 */
 public interface ExcelGeneratorService {

	 /**
	     * 
	    * 导出excel 数据，通过ExcelExportColumn 定义导出的样式
	     */

		void generateExcel1(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion, String fileName,
				HttpServletRequest request, HttpServletResponse response);
		
		void generateExcel2(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion, String fileName,
				HttpServletRequest request, HttpServletResponse response);

	    void generateExcel(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList, String fileName,
			HttpServletRequest request, HttpServletResponse response);

		void dbsInfoGenerateExcel(List<int[]> untilList,@SuppressWarnings("rawtypes") Map<String, List<Map>> excelData,List<List<ExcelExportColumn>> columnDefineList,String fileName,HttpServletRequest request, HttpServletResponse response);

		void generateExcelForN2(Map<String, List<Map>> excelData, List<Map> heads, Map merge, String string,
				HttpServletRequest request, HttpServletResponse response);
      
		/**
		 * 
		 * @param excelData
		 * @param columnDefineList
		 * @param fileName
		 * @param request
		 * @param response
		 * @date 2018年4月3日15:59:19
		 * 导出exl
		 */

		void generateExcelExpend(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
				String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response);

		void niguriReportExcel(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
				String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response);
		
		void calculatedDataExcelExpend(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
				String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response);
		
		/**
		 * 巡回报告导出
		 * @param excelData 业务数据Map
		 * @param columnList 标题数据Map Key值相匹配 储存多个List<ExcelExportColumn>对应多个标题头 (用于合并)
		 * @param mergeList 合并样式
		 * @param fileName
		 * @param request
		 * @param response
		 * @date 2018年6月13日
		 */
	    void patrolReporteExcel(Map<String, List<Map>> excelData,Map<String, List<List<ExcelExportColumn>>> columnListMap, 
	    	Map<String,List<int[]>> mergeMap,String fileName,HttpServletRequest request, HttpServletResponse response);
}