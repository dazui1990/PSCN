package com.yonyou.dms.manage.service.shop;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.rabbitmq.client.AMQP.Basic.Return;
import com.yonyou.dms.framework.domains.DTO.baseData.DictDto;
import com.yonyou.dms.framework.domains.DTO.baseData.RegionDto;
import com.yonyou.dms.framework.service.cache.impl.DictCacheServiceImpl;
import com.yonyou.dms.framework.service.cache.impl.RegionCacheSerivceImpl;
import com.yonyou.dms.framework.service.excel.ExcelDataType;
import com.yonyou.dms.framework.service.excel.ExcelExportColumn;
import com.yonyou.dms.framework.util.http.FrameHttpUtil;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.exception.UtilException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.function.utils.io.IOUtils;

/**
 * 根据 Excel数据信息，生成Excel文件流 ExcelGenerator 接口的默认实现，可继承此类并并改写相关方法实现 Created by
 * wfl.
 */

@Component
public class ExcelGeneratorServiceImpl implements ExcelGeneratorService {

	@Resource(name = "DictCache")
	DictCacheServiceImpl<List<DictDto>> dictCacheSerivce;

	@Resource(name = "RegionCache")
	RegionCacheSerivceImpl<RegionDto> regionCacheService;

	// 定义日志接口
	private static final Logger logger = LoggerFactory.getLogger(ExcelGeneratorServiceImpl.class);

	/**
	 * DBS导出
	 *
	 */
	@Override
	public void dbsInfoGenerateExcel(List<int[]> untilList, Map<String, List<Map>> excelData,
			List<List<ExcelExportColumn>> columnDefineList, String fileName, HttpServletRequest request,
			HttpServletResponse response) {

		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}
		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();
			Set<String> sheetSet = excelData.keySet();
			List<Map> rowList = excelData.get("巡回计划");
			int freeLine = 0;
			// 创建sheet 页
			HSSFSheet sheet = workbook.createSheet("巡回计划");
			for (int i = 0; i < columnDefineList.size(); i++) {
				generateTitleRow(sheet, columnDefineList.get(i), i);
			}
			for (int i = 0; i < untilList.size(); i++) {
				int[] flag = untilList.get(i);
				if (freeLine < flag[1]) {
					freeLine = flag[1];
				}
			}

			addMergedRegion(sheet, untilList);
			dbsGenerateDataRows(sheet, rowList, columnDefineList.get(3), workbook);
			setSheetFinishStyle(sheet, 0);
			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}
	}

	protected void dbsGenerateDataRows(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList, Workbook workbook) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();

		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + 4));
			for (int j = 0; j < columnDefineList.size(); j++) {
				if (j == 0 || j == 1) {
					sheet.setColumnWidth((short) j, (short) 2000);
				} else {
					sheet.setColumnWidth((short) j, (short) 6000);
				}
				dbsCreateCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, columnCellStyle,
						columnDefineList.get(j), workbook);
			}
		}
	}

	private void dbsCreateCell(Object cellValue, Row row, int cellIndex, Map<Integer, CellStyle> columnCellStyle,
			ExcelExportColumn excelExportColumn, Workbook workbook) {
		Cell cell = row.createCell(cellIndex);
		// 如果是空值
		if (cellValue == null) {
			if (columnCellStyle.get(-1) == null) {
				// 放入字符串样式,使用-1 作为空值的判断依据
				columnCellStyle.put(-1, getSheetStringStyle(row.getSheet().getWorkbook()));
			}
			cell.setCellStyle(columnCellStyle.get(-1));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((String) cellValue);
			return;
		}
		// 设置字符串类型
		if (cellValue instanceof String) {
			if (excelExportColumn.getDataType() != null) {
				columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				// 设置ID 对应的名称
				cell.setCellValue(getNamesByCodes((String) cellValue, excelExportColumn.getDataType()));
			} else {
				columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue((String) cellValue);
			}
			return;
		}
		if (cellValue instanceof Integer || cellValue instanceof Long) {
			if (excelExportColumn.getDataType() != null) {
				columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (cellValue instanceof Integer) {
					cell.setCellValue((double) (int) cellValue);
				}
				// 设置ID 对应的名称
				// cell.setCellValue(getNameByCode((Number)cellValue,excelExportColumn.getDataType()));
			} else {
				columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				System.out.println("asdasd----------------------------------------------");
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (cellValue instanceof Integer) {
					cell.setCellValue((double) (int) cellValue);
				} else {
					cell.setCellValue((double) (long) cellValue);
				}
			}
			return;
		}
	}

	/**
	 * 生成一行
	 */
	protected void generateDataRows(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();

		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + 1));

			for (int j = 0; j < columnDefineList.size(); j++) {
				createCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, columnCellStyle,
						columnDefineList.get(j));
			}
		}
	}

	/**
	 * 生成表格数据
	 */
	protected void generateDataRowsExpend(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();
		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + 1));
			for (int j = 0; j < columnDefineList.size(); j++) {
				// 如果是
				sheet.setColumnWidth((short) j, (short) 3200);
				createCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, columnCellStyle,
						columnDefineList.get(j));
			}
		}
	}
	


	protected void niguriReportDataRows(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();
		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + 1));
			for (int j = 0; j < columnDefineList.size(); j++) {
				// 如果是
			
					sheet.setColumnWidth((short) j, (short) 3200);
				
				createCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, columnCellStyle,
						columnDefineList.get(j));
			}
		}
	}

	/***
	 * 导出exl 扩展方法
	 */
	@Override
	public void generateExcelExpend(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
			String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response) {

		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}
		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();
			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {
				@SuppressWarnings("rawtypes")
				List<Map> rowList = excelData.get(sheetName);
				// 创建sheet 页
				// 创建sheet 页
				HSSFSheet sheet = workbook.createSheet(sheetName);
				// 生成标题
				generateTitleRow(sheet, columnDefineList);
				// 生成数据
				generateDataRowsExpend(sheet, rowList, columnDefineList);
				addMergedRegion(sheet, list);
				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyle(sheet, 0);
			}
			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}
	}

	/***
	 * 导出exl 扩展方法
	 */
	@Override
	public void niguriReportExcel(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
			String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response) {

		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}
		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();
			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {
				@SuppressWarnings("rawtypes")
				List<Map> rowList = excelData.get(sheetName);
				// 创建sheet 页
				// 创建sheet 页
				HSSFSheet sheet = workbook.createSheet(sheetName);
				// 生成标题
				generateTitleRow(sheet, columnDefineList);
				// 生成数据
				niguriReportDataRows(sheet, rowList, columnDefineList);
				addMergedRegion(sheet, list);
				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyle(sheet,0);
			}
			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}
	}

	public void addMergedRegion(HSSFSheet sheet, List<int[]> list) {
		if(null!=list && list.size()>0){
		for (int[] i : list) {
			sheet.addMergedRegion(new CellRangeAddress(i[0], i[1], i[2], i[3]));
		}}
	}

	/**
	 * 创建一个单元格
	 */
	private void createCell(Object cellValue, Row row, int cellIndex, Map<Integer, CellStyle> columnCellStyle,
			ExcelExportColumn excelExportColumn) {
		Cell cell = row.createCell(cellIndex);

		// 如果是空值
		if (cellValue == null) {
			if (columnCellStyle.get(-1) == null) {
				// 放入字符串样式,使用-1 作为空值的判断依据
				columnCellStyle.put(-1, getSheetStringStyle(row.getSheet().getWorkbook()));
			}
			cell.setCellStyle(columnCellStyle.get(-1));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((String) cellValue);
			return;
		}

		try {
			cellValue = Integer.parseInt((String) cellValue);
		} catch (Exception e) {
			// System.out.println(cellValue+"不是数字类型");
		}

		// 设置字符串类型
		if (cellValue instanceof String) {
			if (excelExportColumn.getDataType() != null) {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				// 设置ID 对应的名称
				cell.setCellValue(getNamesByCodes((String) cellValue, excelExportColumn.getDataType()));
			} else {
				if (columnCellStyle.get(cellIndex) == null) {
					// 放入字符串样式
					columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue((String) cellValue);
			}
			return;

		}

		// 设置Double 类型
		if (cellValue instanceof Double || cellValue instanceof BigDecimal) {
			if (columnCellStyle.get(cellIndex) == null) {
				columnCellStyle.put(cellIndex,
						getSheetDoubleStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
			}
			cell.setCellStyle(columnCellStyle.get(cellIndex));
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			if (cellValue instanceof BigDecimal) {
				cell.setCellValue(((BigDecimal) cellValue).doubleValue());
			} else {
				cell.setCellValue((Double) cellValue);
			}
			return;
		}

		// 设置整形格式
		if (cellValue instanceof Integer || cellValue instanceof Long) {
			if (excelExportColumn.getDataType() != null) {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex, getSheetCodeDescStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				// 设置ID 对应的名称
				cell.setCellValue(getNameByCode((Number) cellValue, excelExportColumn.getDataType()));
			} else {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex,
							getSheetIntegerStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (cellValue instanceof Integer) {
					cell.setCellValue((double) (int) cellValue);
				} else {
					cell.setCellValue((double) (long) cellValue);
				}
			}
			return;
		}

		// 设置日期格式
		if (cellValue instanceof Date) {
			if (columnCellStyle.get(cellIndex) == null) {
				// 放入日期格式
				columnCellStyle.put(cellIndex,
						getSheetDateStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
			}
			cell.setCellStyle(columnCellStyle.get(cellIndex));
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Date) cellValue);
			return;
		}
	}

	/**
	 * 
	 * 根据代码获得名称,代码存储为：1001001,10011002 ,用","分隔的代码
	 */
	private String getNamesByCodes(String values, ExcelDataType excelDataType) {
		String[] valuesArray = values.split(",");
		StringBuilder sb = new StringBuilder();
		for (String value : valuesArray) {
			if (!StringUtils.isNullOrEmpty(value)) {
				sb.append(getNameByCode(Long.parseLong(value), excelDataType)).append(",");
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * 
	 * 根据代码获得名称
	 */
	private String getNameByCode(Number value, ExcelDataType excelDataType) {
		// 如果是TC_CODE
		if (excelDataType == ExcelDataType.Dict) {
			return dictCacheSerivce.getDescByCodeId(Integer.parseInt(value.toString()));
		}
		// 如果是省份城市
		if (excelDataType == ExcelDataType.Region_Provice) {
			return regionCacheService.getProvinceNameById((long) value);
		}
		// 如果是城市
		if (excelDataType == ExcelDataType.Region_City) {
			return regionCacheService.getCityNameById((long) value);
		}
		// 如果是区县
		if (excelDataType == ExcelDataType.Region_Country) {
			return regionCacheService.getCountryNameById((long) value);
		}
		return null;
	}

	/**
	 * 生成标题
	 */
	private void generateTitleRow(Sheet sheet, List<ExcelExportColumn> columnDefineList) {
		// 生成标题
		Row row = sheet.createRow(0);
		CellStyle cellStyle = getSheetTitleStyle(sheet.getWorkbook());
		// 生成标题
		for (int i = 0; i < columnDefineList.size(); i++) {

			Cell cell = row.createCell(i);
			// 生成标题的信息
			createStringCell(columnDefineList.get(i).getTitle(), cell, cellStyle);
		}
	}

	private void generateTitleRow(Sheet sheet, List<ExcelExportColumn> columnDefineList, int line) {
		// 生成标题
		Row row = sheet.createRow(line);
		CellStyle cellStyle = getSheetTitleStyle(sheet.getWorkbook());
		// 生成标题
		for (int i = 0; i < columnDefineList.size(); i++) {

			Cell cell = row.createCell(i);
			// 生成标题的信息
			createStringCell(columnDefineList.get(i).getTitle(), cell, cellStyle);
		}
	}

	/**
	 * 
	 * 创建字符串类型
	 */
	private void createStringCell(Object cellValue, Cell cell, CellStyle cellstyle) {
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellStyle(cellstyle);
		cell.setCellValue((String) cellValue);
	}

	/**
	 * 
	 * 当sheet 页加载完成后，设置sheet 面的样式
	 * 
	 * @author zhangxc
	 * @date 2016年9月28日
	 * @param sheet
	 */
	private void setSheetFinishStyle(Sheet sheet, int colSize) {
		// 设置字段宽度
		for (int i = 0; i < colSize; i++) {
			sheet.autoSizeColumn((short) i);
		}

		// 冻结首行
		// sheet.createFreezePane(0, 1, 0, 1);
	}
	
	private void setNiguriSheetStyle(Sheet sheet, int colSize) {
		// 设置字段宽度
		for (int i = 0; i < colSize; i++) {
			sheet.autoSizeColumn((short) i);
		}

		// 冻结首行
		 sheet.createFreezePane(4, 1, 4, 1);
	}
	/**
	 * 
	 * 获得默认的字体
	 */
	private CellStyle getDefaultCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT); // 水平布局：居中
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
		cellStyle.setBorderTop(CellStyle.BORDER_THIN); // 设置边框
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 设置边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN); // 设置边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN); // 设置边框
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	/**
	 * 
	 * 获得默认的字体
	 */
	private Font getDefaultFont(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 10); // 字体高度
		font.setColor(Font.COLOR_NORMAL); // 字体颜色
		font.setFontName("微软雅黑"); // 字体
		// font.setItalic(true); //是否使用斜体
		return font;
	}

	/**
	 * 
	 * 获得默认的字体
	 */
	private Font getDefaultFont1(Workbook workbook) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 10); // 字体高度
		font.setColor(Font.COLOR_RED); // 字体颜色
		font.setFontName("微软雅黑"); // 字体
		// font.setItalic(true); //是否使用斜体
		return font;
	}

	/**
	 * 设置excel 的字段
	 */
	private CellStyle getSheetTitleStyle(Workbook workbook) {
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		font.setFontHeightInPoints((short) 10); // 字体高度
		font.setBoldweight(Font.BOLDWEIGHT_BOLD); // 宽度

		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetDateStyle(Workbook workbook, String format) {
		// 如果format 未指定，则使用yyyy-MM-dd
		format = format == null ? "yyyy-MM-dd" : format;
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		// 格式化样式
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetStringStyle(Workbook workbook) {
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetCodeDescStyle(Workbook workbook) {
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetDoubleStyle(Workbook workbook, String format) {
		// 如果format 未指定，则使用yyyy-MM-dd
		format = format == null ? "#,##0.00##" : format;
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		// 格式化样式
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetIntegerStyle(Workbook workbook, String format) {
		// 如果format 未指定，则使用yyyy-MM-dd
		format = format == null ? "#,##0" : format;
		// 修改字段样式
		Font font = getDefaultFont(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		// 格式化样式
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		return cellStyle;
	}

	/**
	 * 
	 * 获得日期格式的样式
	 */
	private CellStyle getSheetIntegerStyle1(Workbook workbook, String format) {
		// 如果format 未指定，则使用yyyy-MM-dd
		format = format == null ? "#,##0" : format;
		// 修改字段样式
		Font font = getDefaultFont1(workbook);
		// 修改默认单元格样式
		CellStyle cellStyle = getDefaultCellStyle(workbook);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		// 格式化样式
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		return cellStyle;
	}

	/**
	 * 创建workbook
	 * 
	 */
	private Workbook createWorkbook() {
		Workbook workbook = new HSSFWorkbook();
		return workbook;
	}

	/**
	 * 初始化输出流
	 */
	private OutputStream initOutputStream(HttpServletRequest request, HttpServletResponse response, String fileName)
			throws UtilException {
		try {
			FrameHttpUtil.setExportFileName(request, response, fileName);
			return response.getOutputStream();
		} catch (Exception e) {
			throw new UtilException("excel 流初始化失败", e);
		}
	}

	/**
	 * 设置 WorkBook 相关属性,扩展属性
	 *
	 * @param wb
	 *            WorkBook
	 */
	@Deprecated
	protected void setWorkbookAttribute(Workbook wb) {
	}

	/**
	 * 设置 Sheet 相关属性
	 *
	 * @param sheetName
	 *            Sheet名称
	 * @param sheet
	 *            Sheet
	 */
	@Deprecated
	protected void setSheetAttribute(String sheetName, Sheet sheet,
			@SuppressWarnings("rawtypes") final List<Map> rowList, String[] keys, String[] columnNames) {
	}

	/**
	 * 设置 Row 相关属性
	 *
	 * @param row
	 *            Row
	 * @param rowIndex
	 *            行号
	 * @param cellList
	 *            单元格值列表
	 */
	@Deprecated
	protected void setRowAttribute(Row row, Map<String, Object> cellList) {
	}

	/**
	 * 设置 Cell 相关属性，如字体 样式等
	 *
	 * @param row
	 *            row 对象
	 * @param cell
	 *            cell 对象
	 * @param rowIndex
	 *            行号
	 * @param cellIndex
	 *            列号
	 * @param cellVal
	 *            单元格值
	 */
	@Deprecated
	protected void setCellAttribute(Row row, Cell cell, String cellVal) {

	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void generateExcel1(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion,
			String fileName, HttpServletRequest request, HttpServletResponse response) {
		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}

		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();

			// 标题行
			Map<String, List<ExcelExportColumn>> columnDefineListMap0 = new HashMap<String, List<ExcelExportColumn>>();

			Set<String> keysSet = excelData.keySet();
			for (String key : keysSet) {
				
				// 创建sheet 页
				HSSFSheet sheet = workbook.createSheet(key);

				List<Map> rowList = excelData.get(key);
				// int freeLine = 0;

				// 生成标题
				for (int i = 0; i < columnDefineList.size(); i++) {
					columnDefineListMap0 = columnDefineList.get(i);
					generateTitleRows(sheet, columnDefineListMap0.get(key), i);
				}

				// 合并单元格
				List<int[]> list = (List<int[]>) mergedRegion.get(key);
				addMergedRegion(sheet, list);

				// 生成数据
				generateDataRows(sheet, rowList, columnDefineListMap0.get(key), columnDefineList.size());

				// 当数据加载完成后设置sheet格式
				setSheetFinishStyles(sheet, columnDefineListMap0.get(key).size(), 0);
			}

			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}

	}

	private void generateTitleRows(Sheet sheet, List<ExcelExportColumn> columnDefineList, int line) {
		// 生成标题
		Row row = sheet.createRow(line);
		CellStyle cellStyles = getSheetTitleStyle(sheet.getWorkbook());
		// 生成标题
		for (int i = 0; i < columnDefineList.size(); i++) {
			Cell cell = row.createCell(i);
			// 生成标题的信息
			createStringCell(columnDefineList.get(i).getTitle(), cell, cellStyles);
		}
	}

	protected void generateDataRows(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList, int addLine) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();

		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + addLine));
			for (int j = 0; j < columnDefineList.size(); j++) {
				createCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, 
						columnCellStyle, columnDefineList.get(j));
			}
		}
	}

	private void setSheetFinishStyles(Sheet sheet, int colSize, int FreeLine) {
		// 设置字段宽度自适应
		for (int i = 0; i < colSize; i++) {
			sheet.autoSizeColumn((short) i,true);
		}

		// 冻结首行
		// sheet.createFreezePane(0, FreeLine, 0, FreeLine);
	}

	@Override
	public void generateExcel(@SuppressWarnings("rawtypes") Map<String, List<Map>> excelData,
			List<ExcelExportColumn> columnDefineList, String fileName, HttpServletRequest request,
			HttpServletResponse response) {
		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}

		Workbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = createWorkbook();

			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {
				@SuppressWarnings("rawtypes")
				List<Map> rowList = excelData.get(sheetName);
				// 创建sheet 页
				Sheet sheet = workbook.createSheet(sheetName);

				// 生成标题
				generateTitleRow(sheet, columnDefineList);

				// 生成数据
				generateDataRows(sheet, rowList, columnDefineList);

				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyle(sheet, columnDefineList.size());

			}

			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void generateExcelForN2(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion,
			String fileName, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}

		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();

			Map<String, List<ExcelExportColumn>> columnDefineListMap1 = columnDefineList.get(0);
			Map<String, List<ExcelExportColumn>> columnDefineListMap2 = columnDefineList.get(1);

			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {

				List<Map> rowList = excelData.get(sheetName);
				int freeLine = 0;
				// 创建sheet 页
				HSSFSheet sheet = workbook.createSheet(sheetName);
				Set<String> colSet = columnDefineListMap2.keySet();
				for (String col : colSet) {
					if (col.equals(sheetName)) {
						// 生成标题
						int count = 0;
						for (Map<String, List<ExcelExportColumn>> columnDefineListMap : columnDefineList) {
							generateTitleRows(sheet, columnDefineListMap.get(col), count);
							count++;
						}
						// generateTitleRows(sheet,
						// columnDefineListMap2.get(col), 1);

						List<int[]> list = (List<int[]>) mergedRegion.get(col);
						for (int i = 0; i < list.size(); i++) {
							int[] flag = list.get(i);
							if (freeLine < flag[1]) {
								freeLine = flag[1];
							}
						}
						addMergedRegion(sheet, list);

						// 生成数据
						generateDataRows(sheet, rowList, columnDefineListMap1.get(col), 2);
					}
				}

				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyles(sheet, columnDefineListMap1.size(), freeLine);

			}

			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}

	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void generateExcel2(Map<String, List<Map>> excelData, List<Map> columnDefineList, Map mergedRegion,
			String fileName, HttpServletRequest request, HttpServletResponse response) {
		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}

		XSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new XSSFWorkbook();

			Map<String, List<ExcelExportColumn>> columnDefineListMap1 = columnDefineList.get(0);
			Map<String, List<ExcelExportColumn>> columnDefineListMap2 = columnDefineList.get(1);

			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {

				List<Map> rowList = excelData.get(sheetName);
				int freeLine = 0;
				// 创建sheet 页
				XSSFSheet sheet = workbook.createSheet(sheetName);
				Set<String> colSet = columnDefineListMap2.keySet();
				for (String col : colSet) {
					if (col.equals(sheetName)) {
						// 生成标题
						int count = 0;
						for (Map<String, List<ExcelExportColumn>> columnDefineListMap : columnDefineList) {
							generateTitleRows(sheet, columnDefineListMap.get(col), count);
							count++;
						}
						// generateTitleRows(sheet,
						// columnDefineListMap2.get(col), 1);

						List<int[]> list = (List<int[]>) mergedRegion.get(col);
						for (int i = 0; i < list.size(); i++) {
							int[] flag = list.get(i);
							if (freeLine < flag[1]) {
								freeLine = flag[1];
							}
						}
						addMergedRegion1(sheet, list);

						// 生成数据
						generateDataRows(sheet, rowList, columnDefineListMap1.get(col), 3);
					}
				}

				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyles(sheet, columnDefineListMap1.size(), freeLine);

			}

			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}

	}

	public void addMergedRegion1(XSSFSheet sheet, List<int[]> list) {
		for (int[] i : list) {
			sheet.addMergedRegion(new CellRangeAddress(i[0], i[1], i[2], i[3]));
		}
	}

	@Override
	public void calculatedDataExcelExpend(Map<String, List<Map>> excelData, List<ExcelExportColumn> columnDefineList,
			String fileName, List<int[]> list, HttpServletRequest request, HttpServletResponse response) {
		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}
		HSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new HSSFWorkbook();
			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {
				@SuppressWarnings("rawtypes")
				List<Map> rowList = excelData.get(sheetName);
				// 创建sheet 页
				// 创建sheet 页
				HSSFSheet sheet = workbook.createSheet(sheetName);
				// 生成标题
				generateTitleRow(sheet, columnDefineList);
				// 生成数据
				calculatedDataRowsExpend(sheet, rowList, columnDefineList);
				addMergedRegion(sheet, list);
				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyle(sheet, 0);
			}
			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}
		
	}
	
	/**
	 * niguri测算表生成数据的方法  为添入公式 重写
	 * @param sheet
	 * @param rowList
	 * @param columnDefineList
	 */
	protected void calculatedDataRowsExpend(final Sheet sheet, @SuppressWarnings("rawtypes") final List<Map> rowList,
			final List<ExcelExportColumn> columnDefineList) {
		if (CommonUtils.isNullOrEmpty(rowList)) {
			return;
		}
		// 确定excel 每一列的格式化样式
		Map<Integer, CellStyle> columnCellStyle = new HashMap<>();
		// 生成数据
		for (int i = 0; i < rowList.size(); i++) {
			@SuppressWarnings("rawtypes")
			final Map cellList = rowList.get(i);
			// 生成一行
			Row row = sheet.createRow((i + 1));
			for (int j = 0; j < columnDefineList.size(); j++) {
				// 如果是
				sheet.setColumnWidth((short) j, (short) 3200);
				calculatedDatacreateCell(cellList.get(columnDefineList.get(j).getFieldName()), row, j, columnCellStyle,
						columnDefineList.get(j),i+1);
			}
		}
	}
	/**
	 * 重写创建单元格的方法，为写入公式用
	 * @param cellValue
	 * @param row
	 * @param cellIndex
	 * @param columnCellStyle
	 * @param excelExportColumn
	 */
	private void calculatedDatacreateCell(Object cellValue, Row row, int cellIndex, Map<Integer, CellStyle> columnCellStyle,
			ExcelExportColumn excelExportColumn,int rowindex) {
		Cell cell = row.createCell(cellIndex);
		if(rowindex%14==0){
			if((cellIndex>5 &&cellIndex<16)||(cellIndex>20&&cellIndex<31)||(cellIndex==36)){
				//通过列号获取列名
				String col = CellReference.convertNumToColString(cellIndex);//当前月的列名
				String col2 = CellReference.convertNumToColString(cellIndex-1);//前一个月的列名
				String col3 = CellReference.convertNumToColString(cellIndex-2);//前两个月的列名
				cell.setCellFormula("ROUND("+col+rowindex+"/AVERAGE("+col+(rowindex-5)+","+col2+(rowindex-5)+","+col3+(rowindex-5)+"),1)");
				cell.setCellStyle(getSheetDoubleStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				return;
				
			}else if(cellIndex==19||cellIndex==34){
				String col = CellReference.convertNumToColString(cellIndex);//当前月的列名
				String col2 = CellReference.convertNumToColString(cellIndex-4);//前一个月的列名
				String col3 = CellReference.convertNumToColString(cellIndex-5);//前两个月的列名
				cell.setCellFormula(col+rowindex+"/AVERAGE("+col+(rowindex-5)+","+col2+(rowindex-5)+","+col3+(rowindex-5)+")");
				cell.setCellStyle(getSheetDoubleStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				return;
			}else if(cellIndex==20||cellIndex==35){
				String col = CellReference.convertNumToColString(cellIndex);//当前月的列名
				String col2 = CellReference.convertNumToColString(cellIndex-1);//前一个月的列名
				String col3 = CellReference.convertNumToColString(cellIndex-5);//前两个月的列名
				cell.setCellFormula(col+rowindex+"/AVERAGE("+col+(rowindex-5)+","+col2+(rowindex-5)+","+col3+(rowindex-5)+")");
				cell.setCellStyle(getSheetDoubleStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				return;
			}else if(cellIndex==4||cellIndex==5||cellIndex==16||cellIndex==17||cellIndex==18||cellIndex==31||cellIndex==32||cellIndex==33||cellIndex==37){
				cellValue="-";
			}
		}

		// 如果是空值
		if (cellValue == null) {
			if (columnCellStyle.get(-1) == null) {
				// 放入字符串样式,使用-1 作为空值的判断依据
				columnCellStyle.put(-1, getSheetStringStyle(row.getSheet().getWorkbook()));
			}
			cell.setCellStyle(columnCellStyle.get(-1));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((String) cellValue);
			return;
		}

		try {
			cellValue = Integer.parseInt((String) cellValue);
		} catch (Exception e) {
			// System.out.println(cellValue+"不是数字类型");
		}

		// 设置字符串类型
		if (cellValue instanceof String) {
			if (excelExportColumn.getDataType() != null) {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				// 设置ID 对应的名称
				cell.setCellValue(getNamesByCodes((String) cellValue, excelExportColumn.getDataType()));
			} else {
				if (columnCellStyle.get(cellIndex) == null) {
					// 放入字符串样式
					columnCellStyle.put(cellIndex, getSheetStringStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue((String) cellValue);
			}
			return;

		}

		// 设置Double 类型
		if (cellValue instanceof Double || cellValue instanceof BigDecimal) {
			if (columnCellStyle.get(cellIndex) == null) {
				columnCellStyle.put(cellIndex,
						getSheetDoubleStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
			}
			cell.setCellStyle(columnCellStyle.get(cellIndex));
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			if (cellValue instanceof BigDecimal) {
				cell.setCellValue(((BigDecimal) cellValue).doubleValue());
			} else {
				cell.setCellValue((Double) cellValue);
			}
			return;
		}

		// 设置整形格式
		if (cellValue instanceof Integer || cellValue instanceof Long) {
			if (excelExportColumn.getDataType() != null) {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex, getSheetCodeDescStyle(row.getSheet().getWorkbook()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				// 设置ID 对应的名称
				cell.setCellValue(getNameByCode((Number) cellValue, excelExportColumn.getDataType()));
			} else {
				if (columnCellStyle.get(cellIndex) == null) {
					columnCellStyle.put(cellIndex,
							getSheetIntegerStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
				}
				cell.setCellStyle(columnCellStyle.get(cellIndex));
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				if (cellValue instanceof Integer) {
					cell.setCellValue((double) (int) cellValue);
				} else {
					cell.setCellValue((double) (long) cellValue);
				}
			}
			return;
		}

		// 设置日期格式
		if (cellValue instanceof Date) {
			if (columnCellStyle.get(cellIndex) == null) {
				// 放入日期格式
				columnCellStyle.put(cellIndex,
						getSheetDateStyle(row.getSheet().getWorkbook(), excelExportColumn.getFormat()));
			}
			cell.setCellStyle(columnCellStyle.get(cellIndex));
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Date) cellValue);
			return;
		}
	}

	/**
	 * 巡回报告导出
	 * @param excelData 业务数据Map
	 * @param columnListMap 标题数据Map Key值相匹配
	 * @date 2018年6月13日
	 * 
	 * 重写generateExcel方法
	 * 可传入多个标题头 key=sheetName
	 */
	@Override
	public void patrolReporteExcel(Map<String, List<Map>> excelData, Map<String, List<List<ExcelExportColumn>>> columnListMap,
			Map<String,List<int[]>> mergeMap, String fileName, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		// 如果excelData 中没有数据，则返回错误
		if (CommonUtils.isNullOrEmpty(excelData)) {
			throw new ServiceBizException("No excel data !");
		}

		XSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		try {
			// 初始化输出流
			outputStream = initOutputStream(request, response, fileName);
			// 初始化workbook
			workbook = new XSSFWorkbook();

			Set<String> sheetSet = excelData.keySet();
			for (String sheetName : sheetSet) {
				@SuppressWarnings("rawtypes")
				List<Map> rowList = excelData.get(sheetName);
				// 创建sheet 页
				XSSFSheet sheet = workbook.createSheet(sheetName);
				
				//用于生成数据的标题
				List<ExcelExportColumn> lastColumnList = new ArrayList<ExcelExportColumn>();

                List<List<ExcelExportColumn>> columnList = columnListMap.get(sheetName);
				if (!CollectionUtils.isEmpty(columnList)) {
					for (int i = 0; i < columnList.size(); i++) {
						// 生成标题
						generateTitleRow(sheet, columnList.get(i), i);

						// 最后一个标题 生成数据
						if (i == columnList.size()-1) {
							lastColumnList = columnList.get(i);
						}
					}
				}

				// 生成数据
			    generateDataRows(sheet, rowList, lastColumnList, columnList.size());
				
				if(!CollectionUtils.isEmpty(mergeMap.get(sheetName))){
	                //合并标题
	                addMergedRegion1(sheet, mergeMap.get(sheetName));
				}
				
				//设置宽度
				for (int i = 0; i < lastColumnList.size(); i++) {
					sheet.setColumnWidth((short) i, (short) 4000);
				}
			
				// 当数据加载完成后设置sheet 格式
				setSheetFinishStyle(sheet, columnListMap.get(sheetName).size());
			}

			workbook.write(outputStream);
		} catch (Exception exception) {
			logger.warn(exception.getMessage(), exception);
			throw new ServiceBizException(exception.getMessage(), exception);
		} finally {
			IOUtils.closeStream(workbook);
			IOUtils.closeStream(outputStream);
		}
	}
}
