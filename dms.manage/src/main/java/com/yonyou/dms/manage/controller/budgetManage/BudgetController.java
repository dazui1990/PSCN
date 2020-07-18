package com.yonyou.dms.manage.controller.budgetManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.service.excel.ExcelExportColumn;
import com.yonyou.dms.framework.service.excel.ExcelRead;
import com.yonyou.dms.framework.service.excel.ExcelReadCallBack;
import com.yonyou.dms.framework.service.excel.impl.AbstractExcelReadCallBack;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.constant.Constants;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetDto;
import com.yonyou.dms.manage.domains.PO.budget.TmBudgetDetailPO;
import com.yonyou.dms.manage.domains.PO.budget.TmBudgetMasteDataPO;
import com.yonyou.dms.manage.service.budgetManage.BudgetManageService;
import com.yonyou.dms.manage.service.shop.ExcelGeneratorService;
import com.yonyou.dms.manage.tool.UntilTool;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

@Controller
@TxnConn
@RequestMapping("/budget")
public class BudgetController extends BaseController {

	@Autowired
	private ExcelGeneratorService excelService;

	@Autowired
	private ExcelRead<BudgetDto> excelRestService;
	
	@Autowired
	private BudgetManageService budgetManageService;

	/**
	 * 预算模板导出
	 */
	@RequestMapping(value = "/import/template", method = RequestMethod.GET)
	public void importTempletExcels(@RequestParam Map<String, String> queryParam, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, List<Map>> excelData = new HashMap<>();
		List resultList = new ArrayList();
		List<ExcelExportColumn> exportColumnList = new ArrayList<ExcelExportColumn>();
		exportColumnList.add(new ExcelExportColumn("v1", "Sub function"));
		exportColumnList.add(new ExcelExportColumn("v2", "Fucntion"));
		exportColumnList.add(new ExcelExportColumn("v3", "BUDGET ITEM"));
		exportColumnList.add(new ExcelExportColumn("v4", "OP-OEPX/CA-CAPEX"));
		exportColumnList.add(new ExcelExportColumn("v5", "JAN"));
		exportColumnList.add(new ExcelExportColumn("v6", "FEB"));
		exportColumnList.add(new ExcelExportColumn("v7", "MAR"));
		exportColumnList.add(new ExcelExportColumn("v8", "APR"));
		exportColumnList.add(new ExcelExportColumn("v9", "MAY"));
		exportColumnList.add(new ExcelExportColumn("v10", "JUN"));
		exportColumnList.add(new ExcelExportColumn("v11", "JUL"));
		exportColumnList.add(new ExcelExportColumn("v12", "AUG"));
		exportColumnList.add(new ExcelExportColumn("v13", "SEP"));
		exportColumnList.add(new ExcelExportColumn("v14", "OCT"));
		exportColumnList.add(new ExcelExportColumn("v15", "NOV"));
		exportColumnList.add(new ExcelExportColumn("v16", "DEC"));

		excelData.put("预算数据导入模板", resultList);
		excelService.generateExcel(excelData, exportColumnList, "预算数据导入模板.xls", request, response);
	}

	/**
	 * 预算数据=导入
	 */
	@RequestMapping(value = "/import/data/budget", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public List<BudgetDto> importerierDealer(@RequestParam(value = "file") MultipartFile importFile,
			UriComponentsBuilder uriCB, HttpServletRequest request) throws Exception {

		final String year = request.getParameter("year");
		final String month = request.getParameter("month");
		System.out.println("year-month:" + year + "-" + month);
		final List<BudgetDto> dataList = new ArrayList<BudgetDto>();
		// 解析Excel 表格(如果需要进行回调)
		ImportResultDto<BudgetDto> importResult = excelRestService.analyzeExcelFirstSheet(importFile,
				new AbstractExcelReadCallBack<BudgetDto>(BudgetDto.class, new ExcelReadCallBack<BudgetDto>() {
					@Override
					public void readRowCallBack(BudgetDto dto, boolean isValidateSucess) {
						try {

							// 校验项目,部门,类型不能为空的情况。并校验各月金额不能为空和非数字类型情况
							String budget_item = dto.getBudget_item();// 预算项目
							String sub_function = dto.getSUB_FUNCTION();// 预算子部门
							String fuction = dto.getFucntion();// 预算部门
							String jan = dto.getJan();// 一月预算值
							String feb = dto.getFeb();// 二月预算值
							String mar = dto.getMar();// 三月预算值
							String apr = dto.getApr();// 四月预算值
							String may = dto.getMay();// 五月预算值
							String jun = dto.getJun();// 六月预算值
							String jul = dto.getJul();// 七月预算值
							String aug = dto.getAug();// 八月预算值
							String sep = dto.getSep();// 九月预算值
							String oct = dto.getOct();// 十月预算值
							String nov = dto.getNov();// 十一预算值
							String dec = dto.getDec();// 十二预算值

							if (budget_item == null || "".equals(budget_item)) {
								throw new ServiceBizException("预算项目不能为空");
							}

							if (sub_function == null || "".equals(sub_function)) {
								throw new ServiceBizException("预算子部门不能为空");
							}

							if (fuction == null || "".equals(fuction)) {
								throw new ServiceBizException("预算部门不能为空");
							}

							if (jan == null || "".equals(jan)) {
								throw new ServiceBizException("一月预算金额不能为空");
							} else if (!UntilTool.isNumber(jan)) {
								throw new ServiceBizException("一月预算金额不能为非数字类型");
							}

							if (feb == null || "".equals(feb)) {
								throw new ServiceBizException("二月预算金额不能为空");
							} else if (!UntilTool.isNumber(feb)) {
								throw new ServiceBizException("二月预算金额不能为非数字类型");
							}

							if (mar == null || "".equals(mar)) {
								throw new ServiceBizException("三月预算金额不能为空");
							} else if (!UntilTool.isNumber(mar)) {
								throw new ServiceBizException("三月预算金额不能为非数字类型");
							}

							if (apr == null || "".equals(apr)) {
								throw new ServiceBizException("四月预算金额不能为空");
							} else if (!UntilTool.isNumber(apr)) {
								throw new ServiceBizException("四月预算金额不能为非数字类型");
							}

							if (may == null || "".equals(may)) {
								throw new ServiceBizException("五月预算金额不能为空");
							} else if (!UntilTool.isNumber(may)) {
								throw new ServiceBizException("五月预算金额不能为非数字类型");
							}

							if (jan == null || "".equals(jan)) {
								throw new ServiceBizException("六月预算金额不能为空");
							} else if (!UntilTool.isNumber(jan)) {
								throw new ServiceBizException("六月预算金额不能为非数字类型");
							}

							if (jul == null || "".equals(fuction)) {
								throw new ServiceBizException("七月预算金额不能为空");
							} else if (!UntilTool.isNumber(jul)) {
								throw new ServiceBizException("七月预算金额不能为非数字类型");
							}

							if (aug == null || "".equals(aug)) {
								throw new ServiceBizException("八月预算金额不能为空");
							} else if (!UntilTool.isNumber(aug)) {
								throw new ServiceBizException("八月预算金额不能为非数字类型");
							}

							if (sep == null || "".equals(sep)) {
								throw new ServiceBizException("九月预算金额不能为空");
							} else if (!UntilTool.isNumber(sep)) {
								throw new ServiceBizException("九月预算金额不能为非数字类型");
							}

							if (oct == null || "".equals(oct)) {
								throw new ServiceBizException("十月预算金额不能为空");
							} else if (!UntilTool.isNumber(oct)) {
								throw new ServiceBizException("十月预算金额不能为非数字类型");
							}

							if (nov == null || "".equals(nov)) {
								throw new ServiceBizException("十一月预算金额不能为空");
							} else if (!UntilTool.isNumber(nov)) {
								throw new ServiceBizException("十一月预算金额不能为非数字类型");
							}

							if (dec == null || "".equals(dec)) {
								throw new ServiceBizException("十二月预算金额不能为空");
							} else if (!UntilTool.isNumber(dec)) {
								throw new ServiceBizException("十二月预算金额不能为非数字类型");
							}

							// 保存用户导入进来的数据,只有以上数据全部合法才执行
							if (isValidateSucess) {
								dataList.add(dto);
							}

						} catch (Exception e) {
							throw e;
						}

					}

				}));
		if (importResult.isSucess()) {
			String version = UntilTool.version();// 版本号
			String budgetNo = UntilTool.orderNo(Constants.BUDGET, "tm_budget_maste_data");
			// 先把导入当前年份,月份的主表和明细表的有效的数据都至为无效
			StringBuilder sql = new StringBuilder("SELECT 	master_id from  tm_budget_maste_data  where year='" + year
					+ "' and  import_month='" + month + "' and status=10011001\n");
			List<Map> list = DAOUtil.findAll(sql.toString(), null);
			if (list != null && list.size() > 0) {
				for (Map map : list) {
					Long mastId = Long.valueOf(map.get("master_id").toString());
					// 更新明细表状态
					TmBudgetDetailPO.update("status=10011002", "master_id=?", mastId);
					// 更新主表状态
					TmBudgetMasteDataPO.update("status=10011002", "master_id=?", mastId);
				}
			}
			System.out.println("version:" + version);
			// 然后循环插入到主表和明细表中
			for (BudgetDto dto : dataList) {
				// 导入生成新的预算数据
				TmBudgetMasteDataPO po = new TmBudgetMasteDataPO();
				po.setString("year", year);
				po.setString("import_month", month);
				po.setString("fucntion", dto.getFucntion());
				po.setString("sub_function", dto.getSUB_FUNCTION());
				po.setString("budget_item", dto.getBudget_item());
				po.setString("item_type", dto.getItem_type());
				po.setInteger("status", DictCodeConstants.STATUS_IS_VALID);
				po.setString("version", version);
				po.setString("sn_no", budgetNo);
				// po.setString("created_by", "111");
				po.saveIt();
				// 对应主表的信息插入到明细表中
				Long mastetId = po.getLongId();
				System.out.println("mastetId:" + mastetId);
				// 明细表数据
				// 第一季度
				Double firstQuarter = Double.valueOf(dto.getJan()) + Double.valueOf(dto.getFeb())
						+ Double.valueOf(dto.getMar());
				// 第二季度
				Double secondQuarter = Double.valueOf(dto.getApr()) + Double.valueOf(dto.getMay())
						+ Double.valueOf(dto.getJun());
				// 第三季度
				Double thirdQuarter = Double.valueOf(dto.getJul()) + Double.valueOf(dto.getAug())
						+ Double.valueOf(dto.getSep());
				// 第四季度
				Double fourQuarter = Double.valueOf(dto.getOct()) + Double.valueOf(dto.getNov())
						+ Double.valueOf(dto.getDec());
				// 全年
				Double allYear = firstQuarter + secondQuarter + thirdQuarter + fourQuarter;

				TmBudgetDetailPO detailPo = new TmBudgetDetailPO();
				detailPo.setLong("master_id", mastetId);
				detailPo.setString("data_type", "预算");
				detailPo.setString("version", version);
				detailPo.setInteger("status", DictCodeConstants.STATUS_IS_VALID);
				detailPo.setDouble("january_value", dto.getJan());
				detailPo.setDouble("february_value", dto.getFeb());
				detailPo.setDouble("march_value", dto.getMar());
				detailPo.setDouble("first_quarter_value", firstQuarter);
				detailPo.setDouble("april_value", dto.getApr());
				detailPo.setDouble("may_value", dto.getMay());
				detailPo.setDouble("june_value", dto.getJun());
				detailPo.setDouble("second_quarter_value", secondQuarter);
				detailPo.setDouble("july_value", dto.getJul());
				detailPo.setDouble("august_value", dto.getAug());
				detailPo.setDouble("september_value", dto.getSep());
				detailPo.setDouble("third_quarter_value", thirdQuarter);
				detailPo.setDouble("october_value", dto.getOct());
				detailPo.setDouble("november_value", dto.getNov());
				detailPo.setDouble("december_value", dto.getDec());
				detailPo.setDouble("four_quarter_value", fourQuarter);
				detailPo.setDouble("all_year", allYear);
				detailPo.saveIt();
			}
			return importResult.getDataList();
		} else {
			throw new ServiceBizException("导入出错,请见错误列表", importResult.getErrorList());
		}
	}

	/**
	 * 预算导出
	 * 
	 */

	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void resultExcel(@RequestParam Map<String, String> queryParam, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		 List<Map> resultList = budgetManageService.getListResult(queryParam);
		  Map<String, List<Map>> excelData = new HashMap<>();
		  excelData.put("预算数据导出", resultList);	
	 
		  if( resultList != null && resultList.size()>0 ){
			  List<ExcelExportColumn> exportColumnList = new ArrayList<ExcelExportColumn>();
			  exportColumnList.add(new ExcelExportColumn("SN_NO","Sn No"));
			  exportColumnList.add(new ExcelExportColumn("SUB_FUNCTION","Sub function"));
			  exportColumnList.add(new ExcelExportColumn("FUCNTION","Fucntion"));
			  exportColumnList.add(new ExcelExportColumn("BUDGET_ITEM","BUDGET ITEM"));
			  exportColumnList.add(new ExcelExportColumn("ITEM_TYPE","OP-OEPX/CA-CAPEX"));
			  exportColumnList.add(new ExcelExportColumn("","GC/NGC"));
			  exportColumnList.add(new ExcelExportColumn("JANUARY_VALUE","JAN"));
			  exportColumnList.add(new ExcelExportColumn("FEBRUARY_VALUE","FEB"));
			  exportColumnList.add(new ExcelExportColumn("MARCH_VALUE","MAR"));
			  exportColumnList.add(new ExcelExportColumn("FIRST_QUARTER_VALUE","Q1"));
			  exportColumnList.add(new ExcelExportColumn("APRIL_VALUE","APR"));
			  exportColumnList.add(new ExcelExportColumn("MAY_VALUE","MAY"));
			  exportColumnList.add(new ExcelExportColumn("JUNE_VALUE","JUN"));
			  exportColumnList.add(new ExcelExportColumn("SECOND_QUARTER_VALUE","Q2"));
			  exportColumnList.add(new ExcelExportColumn("JULY_VALUE","JUL"));
			  exportColumnList.add(new ExcelExportColumn("AUGUST_VALUE","AUG"));
			  exportColumnList.add(new ExcelExportColumn("SEPTEMBER_VALUE","SEP"));
			  exportColumnList.add(new ExcelExportColumn("THIRD_QUARTER_VALUE","Q3"));
			  exportColumnList.add(new ExcelExportColumn("OCTOBER_VALUE","OCT"));
			  exportColumnList.add(new ExcelExportColumn("NOVEMBER_VALUE","NOV"));
			  exportColumnList.add(new ExcelExportColumn("DECEMBER_VALUE","DEC"));
			  exportColumnList.add(new ExcelExportColumn("ALL_YEAR","TOTAL"));
			  exportColumnList.add(new ExcelExportColumn("","TAG1"));
			  exportColumnList.add(new ExcelExportColumn("","TAG2"));
			  exportColumnList.add(new ExcelExportColumn("","TAG3"));
			  
			  excelService.generateExcel(excelData, exportColumnList, "预算数据导出.xls", request,response);
		  }else{
			  List<ExcelExportColumn> exportColumnList = new ArrayList<>();
			  exportColumnList.add(new ExcelExportColumn("kong","根据查询条件无查询内容!"));
			  excelService.generateExcel(excelData, exportColumnList, "特约店车系导出.xls", request,response);
		  }
	}

	/**
	 * 年份下拉框
	 * 
	 * @return
	 */
	@RequestMapping(value = "/findVerison/{year}/{month}", method = RequestMethod.GET)
	@ResponseBody
	public List<Map> findVerison(@PathVariable(value = "year") String year,
			@PathVariable(value = "month") String month) {

		System.out.println("1111111:" + 11111);

		return null;
	}

}