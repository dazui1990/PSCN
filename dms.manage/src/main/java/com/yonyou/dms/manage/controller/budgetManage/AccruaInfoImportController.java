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
import com.yonyou.dms.manage.domains.DTO.dudget.AccruaInfoImportDto;
import com.yonyou.dms.manage.service.budgetManage.AccruaInfoImportService;
import com.yonyou.dms.manage.service.shop.ExcelGeneratorService;
import com.yonyou.dms.manage.tool.CommonUtils;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 计提信息操作
 * @author guohao
 *
 */
@Controller
@TxnConn
@RequestMapping("/accrua")
public class AccruaInfoImportController extends BaseController {

	private static final Logger logger = Logger.getLogger(AccruaInfoImportController.class);

	@Autowired
	private ExcelRead<AccruaInfoImportDto> excelRestService;

	@Autowired
	private AccruaInfoImportService accruaInfoImportService;

	@Autowired
	private ExcelGeneratorService excelService;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public List<AccruaInfoImportDto> uploadFiles(@RequestParam(value = "file") MultipartFile importFile,
			UriComponentsBuilder uriCB, HttpServletRequest request) throws Exception {

		final List<AccruaInfoImportDto> dataList = new ArrayList<AccruaInfoImportDto>();
		final Map<String, String> keyMap = new HashMap<String, String>();

		ImportResultDto<AccruaInfoImportDto> importResult = excelRestService.analyzeExcelFirstSheet(importFile,
				new AbstractExcelReadCallBack<AccruaInfoImportDto>(AccruaInfoImportDto.class,
						new ExcelReadCallBack<AccruaInfoImportDto>() {
							@Override
							public void readRowCallBack(AccruaInfoImportDto dto, boolean isValidateSucess) {
								try {

									// 校验项目,部门,类型不能为空的情况。并校验各月金额不能为空和非数字类型情况
									String accrualMonth = CommonUtils.getString(dto.getAccrualMonth());
									String accrualReason = CommonUtils.getString(dto.getAccrualReason());
									String shoppingCart = CommonUtils.getString(dto.getShoppingCart());
									String amount = CommonUtils.getString(dto.getAmount());

									if (!CommonUtils.isNull(keyMap.get(shoppingCart))) {
										throw new ServiceBizException("Shopping Cart Number重复");
									}

									if (CommonUtils.isNull(accrualMonth)) {
										throw new ServiceBizException("Accrual Month不能为空");
									} else if (CommonUtils.getByteLength(accrualMonth) != 6) {
										throw new ServiceBizException("Accrual Month格式有误（例202001）");
									}

									if (CommonUtils.isNull(shoppingCart)) {
										throw new ServiceBizException("Shopping Cart Number不能为空");
									} else if (CommonUtils.getByteLength(shoppingCart) > 15) {
										throw new ServiceBizException("Shopping Cart Number长度超过15个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (!CommonUtils.isNull(accrualReason) && CommonUtils.getByteLength(accrualReason) > 255) {
										throw new ServiceBizException("Accrual Reason长度超过255个字节（一个中文三个字节，一个英文一个字节）");
									}
									
									if (!CommonUtils.isNull(amount) && !CommonUtils.isNumber(amount)) {
										throw new ServiceBizException("Amount只能是数字类型");
									}

									// 保存用户导入进来的数据,只有以上数据全部合法才执行
									if (isValidateSucess) {
										dataList.add(dto);

										keyMap.put(shoppingCart, shoppingCart);
									}

								} catch (Exception e) {
									throw e;
								}

							}

						}));

		if (importResult.isSucess()) {
			
			
			List<Map> list = accruaInfoImportService.queryAccruaInfoIsExists(importResult.getDataList());
			List<AccruaInfoImportDto> updateList = new ArrayList<AccruaInfoImportDto>();
			List<AccruaInfoImportDto> insertList = new ArrayList<AccruaInfoImportDto>();
			if (CommonUtils.isNull(list)) {
				insertList = importResult.getDataList();
			} else {
				for (AccruaInfoImportDto dto : importResult.getDataList()) {
					int count = 0;
					for (Map map : list) {
						if ((CommonUtils.getString(map.get("pr_no")).equals(dto.getShoppingCart()) 
								&& CommonUtils.getString(map.get("accrual_month")).equals(dto.getAccrualMonth()))) {
							count ++;
						}
					}
					if (count == 0) {
						insertList.add(dto);
					} else {
						updateList.add(dto);
					}
				}
			}

			if (!CommonUtils.isNull(insertList)) {
				accruaInfoImportService.saveAccruaInfo(insertList);
			}
			
			if (!CommonUtils.isNull(updateList)) {
				accruaInfoImportService.updateAccruaInfo(updateList);
			}

			return importResult.getDataList();
		} else {
			throw new ServiceBizException("导入出错,请见错误列表", importResult.getErrorList());
		}
	}

	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void resultExcel(@RequestParam Map<String, String> queryParam, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<Map> exportList = null;
		exportList = accruaInfoImportService.getAccruaInfo1(queryParam);
		if (CommonUtils.isNull(exportList)) {
			exportList = accruaInfoImportService.getAccruaInfo(queryParam);
		}
		Map<String, List<Map>> excelData = new HashMap<>();
		excelData.put("计提信息导出", exportList);
		if (exportList != null && exportList.size() > 0) {
			List<ExcelExportColumn> exportColumnList = new ArrayList<ExcelExportColumn>();
			
			exportColumnList.add(new ExcelExportColumn("accrualMonth", "Accrual Month"));
			exportColumnList.add(new ExcelExportColumn("fucntion", "Function Name"));
			exportColumnList.add(new ExcelExportColumn("shopping_cart", "Shopping Cart Number"));
			exportColumnList.add(new ExcelExportColumn("cost_center", "Cost center"));
			exportColumnList.add(new ExcelExportColumn("", "GL"));
			exportColumnList.add(new ExcelExportColumn("Shopping cart name", "Shopping cart name"));
			exportColumnList.add(new ExcelExportColumn("io", "IO"));
			exportColumnList.add(new ExcelExportColumn("pr_amount", "PR Amont"));
			exportColumnList.add(new ExcelExportColumn("grAmont", "GR Amont"));
			exportColumnList.add(new ExcelExportColumn("poGr", "PO-GR"));
			
			if (!CommonUtils.isNull(exportList)) {
				exportColumnList.add(new ExcelExportColumn("accrual_reason", "Accrual Reason"));
				exportColumnList.add(new ExcelExportColumn("amount", "Amount"));
			}

			excelService.generateExcel(excelData, exportColumnList, "计提信息导出.xls", request, response);
		} else {
			List<ExcelExportColumn> exportColumnList = new ArrayList<>();
			exportColumnList.add(new ExcelExportColumn("kong", "根据查询条件无查询内容!"));
			excelService.generateExcel(excelData, exportColumnList, "计提信息导出.xls", request, response);
		}
	}

}
