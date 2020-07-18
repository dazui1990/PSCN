package com.yonyou.dms.manage.controller.budgetManage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.service.excel.ExcelExportColumn;
import com.yonyou.dms.framework.service.excel.ExcelRead;
import com.yonyou.dms.framework.service.excel.ExcelReadCallBack;
import com.yonyou.dms.framework.service.excel.impl.AbstractExcelReadCallBack;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetApplicationImportDto;
import com.yonyou.dms.manage.service.budgetManage.BudgetApplicationImportService;
import com.yonyou.dms.manage.service.shop.ExcelGeneratorService;
import com.yonyou.dms.manage.tool.CommonUtils;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 预算申请操作
 * @author guohao
 *
 */
@Controller
@TxnConn
@RequestMapping("/budgetApplication")
public class BudgetApplicationImportController extends BaseController {

	private static final Logger logger = Logger.getLogger(BudgetApplicationImportController.class);

	@Autowired
	private ExcelRead<BudgetApplicationImportDto> excelRestService;

	@Autowired
	private BudgetApplicationImportService budgetApplicationImportService;

	@Autowired
	private ExcelGeneratorService excelService;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public List<BudgetApplicationImportDto> uploadFiles(@RequestParam(value = "file") MultipartFile importFile,
			UriComponentsBuilder uriCB, HttpServletRequest request) throws Exception {

		final List<BudgetApplicationImportDto> dataList = new ArrayList<BudgetApplicationImportDto>();
		final Map<String, String> keyMap = new HashMap<String, String>();

		ImportResultDto<BudgetApplicationImportDto> importResult = excelRestService.analyzeExcelFirstSheet(importFile,
				new AbstractExcelReadCallBack<BudgetApplicationImportDto>(BudgetApplicationImportDto.class,
						new ExcelReadCallBack<BudgetApplicationImportDto>() {
							@Override
							public void readRowCallBack(BudgetApplicationImportDto dto, boolean isValidateSucess) {
								try {

									// 校验项目,部门,类型不能为空的情况。并校验各月金额不能为空和非数字类型情况
									String week = CommonUtils.getString(dto.getWeek());
									String prNo = CommonUtils.getString(dto.getPr_no());
									String prItem = CommonUtils.getString(dto.getPr_item());
									String prAmount = CommonUtils.getString(dto.getPr_amount());

									String cn0fIo = CommonUtils.getString(dto.getCn0f_io());
									String cn0gIo = CommonUtils.getString(dto.getCn0g_io());
									String remark = CommonUtils.getString(dto.getRemark());

									if (!CommonUtils.isNull(keyMap.get(prNo))) {
										throw new ServiceBizException("PR_NO重复");
									}

									if (CommonUtils.isNull(week)) {
										throw new ServiceBizException("周不能为空");
									} else if (CommonUtils.getByteLength(week) > 10) {
										throw new ServiceBizException("周长度超过10个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (CommonUtils.isNull(prNo)) {
										throw new ServiceBizException("PR_NO不能为空");
									} else if (CommonUtils.getByteLength(prNo) > 15) {
										throw new ServiceBizException("PR_NO长度超过15个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (CommonUtils.isNull(prItem)) {
										throw new ServiceBizException("PR_ITEM不能为空");
									} else if (CommonUtils.getByteLength(prItem) > 255) {
										throw new ServiceBizException("PR_ITEM长度超过255个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (CommonUtils.isNull(prAmount)) {
										throw new ServiceBizException("PR_AMOUNT不能为空");
									} else if (!CommonUtils.isNumber(prAmount)) {
										throw new ServiceBizException("PR_AMOUNT只能是数字类型");
									}

									if (CommonUtils.isNull(cn0fIo) && CommonUtils.isNull(cn0gIo)) {
										throw new ServiceBizException("CN0F_IO和CN0G_IO不能为空");
									}

									if (CommonUtils.getByteLength(cn0fIo) > 25) {
										throw new ServiceBizException("CN0F_IO长度超过25个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (CommonUtils.getByteLength(cn0gIo) > 25) {
										throw new ServiceBizException("CN0G_IO长度超过25个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (!CommonUtils.isNull(remark) && CommonUtils.getByteLength(remark) > 500) {
										throw new ServiceBizException("Comments长度超过500个字节（一个中文三个字节，一个英文一个字节）");
									}

									// 保存用户导入进来的数据,只有以上数据全部合法才执行
									if (isValidateSucess) {
										dataList.add(dto);

										keyMap.put(prNo, prNo);
									}

								} catch (Exception e) {
									throw e;
								}

							}

						}));

		if (importResult.isSucess()) {
			LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);

			List<Map> list = budgetApplicationImportService.queryBudgetInfo();

			List<Map> prList = budgetApplicationImportService.queryPrInfo();

			budgetApplicationImportService.saveOrUpdate(dataList, list, prList, loginInfo);

			return importResult.getDataList();
		} else {
			throw new ServiceBizException("导入出错,请见错误列表", importResult.getErrorList());
		}

	}

	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void resultExcel(@RequestParam Map<String, String> queryParam, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Map> resultList = budgetApplicationImportService.getExcelResult(queryParam);
		Map<String, List<Map>> excelData = new HashMap<>();
		excelData.put("预算申请导出", resultList);

		if (resultList != null && resultList.size() > 0) {
			List<ExcelExportColumn> exportColumnList = new ArrayList<ExcelExportColumn>();
			exportColumnList.add(new ExcelExportColumn("week", "Week"));
			exportColumnList.add(new ExcelExportColumn("pr_no", "PR No."));
			exportColumnList.add(new ExcelExportColumn("pr_item", "PR Item"));
			exportColumnList.add(new ExcelExportColumn("pr_amount", "PR Amount"));
			exportColumnList.add(new ExcelExportColumn("cn0g_io", "CN0G IO"));
			exportColumnList.add(new ExcelExportColumn("cn0f_io", "CN0F IO"));
			exportColumnList.add(new ExcelExportColumn("budget_item", "Budget Item"));
			exportColumnList.add(new ExcelExportColumn("annual_budget", "Annual Budget"));
			exportColumnList.add(new ExcelExportColumn("pr_consumption_amount", "PR Consumption Amount"));
			exportColumnList.add(new ExcelExportColumn("remaining_budget", "Remaining Budget"));
			exportColumnList.add(new ExcelExportColumn("budget_burning_rate", "Budget Burning Rate"));
			exportColumnList.add(new ExcelExportColumn("fucntion", "Function"));
			exportColumnList.add(new ExcelExportColumn("statusDesc", "Status"));
			exportColumnList.add(new ExcelExportColumn("remark", "Comments"));

			excelService.generateExcel(excelData, exportColumnList, "预算申请导出.xls", request, response);
		} else {
			List<ExcelExportColumn> exportColumnList = new ArrayList<>();
			exportColumnList.add(new ExcelExportColumn("kong", "根据查询条件无查询内容!"));
			excelService.generateExcel(excelData, exportColumnList, "预算申请导出.xls", request, response);
		}
	}

	/**
	 * 模板下载
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public void importTempletExcels(HttpServletRequest request, HttpServletResponse response) {

		XSSFWorkbook wb = null;
		OutputStream os = null;
		String fileName = "budgetApplicationImportTemplate.xlsx";

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {

			Resource res = new ClassPathResource("template/budgetApplicationImportTemplate.xlsx");
			InputStream input = res.getInputStream();
			wb = new XSSFWorkbook(input);

			bis = new BufferedInputStream(input);
			os = response.getOutputStream();
			CommonUtils.setResponseHeader(response, fileName);

			logger.info("============导出结束==============：");
			os = response.getOutputStream();
			wb.write(os);
			os.flush();
		} catch (Exception e) {
			logger.error("下载文件出错", e);
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
