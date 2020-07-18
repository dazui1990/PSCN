package com.yonyou.dms.manage.controller.budgetManage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.yonyou.dms.framework.service.excel.ExcelRead;
import com.yonyou.dms.framework.service.excel.ExcelReadCallBack;
import com.yonyou.dms.framework.service.excel.impl.AbstractExcelReadCallBack;
import com.yonyou.dms.function.domains.DTO.ImportResultDto;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.manage.domains.DTO.dudget.BudgetExtendImportDto;
import com.yonyou.dms.manage.service.budgetManage.BudgetApplicationImportService;
import com.yonyou.dms.manage.service.budgetManage.BudgetExtendImportService;
import com.yonyou.dms.manage.tool.CommonUtils;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 采购开票数据
 * @author guohao
 *
 */
@Controller
@TxnConn
@RequestMapping("/budgetExtend")
public class BudgetExtendImportController extends BaseController{
	
	private static final Logger logger = Logger.getLogger(BudgetApplicationImportController.class);

	@Autowired
	private ExcelRead<BudgetExtendImportDto> excelRestService;

	@Autowired
	private BudgetExtendImportService budgetExtendImportService;
	
	@Autowired
	private BudgetApplicationImportService budgetApplicationImportService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public List<BudgetExtendImportDto> uploadFiles(@RequestParam(value = "file") MultipartFile importFile,
			UriComponentsBuilder uriCB, HttpServletRequest request) throws Exception {

		final List<BudgetExtendImportDto> dataList = new ArrayList<BudgetExtendImportDto>();
		final Map<String, String> keyMap = new HashMap<String, String>();
		
		ImportResultDto<BudgetExtendImportDto> importResult = excelRestService.analyzeExcelFirstSheet(importFile,
				new AbstractExcelReadCallBack<BudgetExtendImportDto>(BudgetExtendImportDto.class,
						new ExcelReadCallBack<BudgetExtendImportDto>() {
							@Override
							public void readRowCallBack(BudgetExtendImportDto dto, boolean isValidateSucess) {
								try {

									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									
									String purchaseOrder = CommonUtils.getString(dto.getPurchaseOrder());
									String shoppingCart = CommonUtils.getString(dto.getShoppingCart());
									String shoppingCartName = CommonUtils.getString(dto.getShoppingCartName());
									String internalOrder = CommonUtils.getString(dto.getInternalOrder());

									Date poPostingDate = dto.getPoPostingDate();
									String approvedShoppingCartValue = CommonUtils.getString(dto.getApprovedShoppingCartValue());
									String netOrderValueInOrderCurrency = CommonUtils.getString(dto.getNetOrderValueInOrderCurrency());
									String netConfirmedValueInOrderCurrency = CommonUtils.getString(dto.getNetConfirmedValueInOrderCurrency());

									if (!CommonUtils.isNull(keyMap.get(purchaseOrder))) {
										throw new ServiceBizException("Purchase Order重复");
									}

									if (CommonUtils.isNull(purchaseOrder)) {
										throw new ServiceBizException("Purchase Order不能为空");
									} else if (CommonUtils.getByteLength(purchaseOrder) > 25) {
										throw new ServiceBizException("Purchase Order长度超过25个字节（一个中文三个字节，一个英文一个字节）");
									}
									
									if (CommonUtils.isNull(shoppingCart)) {
										throw new ServiceBizException("Shopping Cart不能为空");
									} else if (CommonUtils.getByteLength(shoppingCart) > 15) {
										throw new ServiceBizException("Shopping Cart长度超过15个字节（一个中文三个字节，一个英文一个字节）");
									}

									if (!CommonUtils.isNull(shoppingCartName) && CommonUtils.getByteLength(shoppingCartName) > 255) {
										throw new ServiceBizException("Shopping Cart Name长度超过255个字节（一个中文三个字节，一个英文一个字节）");
									}
									
									if (!CommonUtils.isNull(internalOrder) && CommonUtils.getByteLength(internalOrder) > 25) {
										throw new ServiceBizException("Internal Order长度超过25个字节（一个中文三个字节，一个英文一个字节）");
									}
									
									if (!CommonUtils.isNull(approvedShoppingCartValue) && !CommonUtils.isNumber(approvedShoppingCartValue)) {
										throw new ServiceBizException("Approved Shopping Cart Value只能是数字类型");
									}
									
									if (!CommonUtils.isNull(netOrderValueInOrderCurrency) && !CommonUtils.isNumber(netOrderValueInOrderCurrency)) {
										throw new ServiceBizException("Net Order Value in Order Currency只能是数字类型");
									}
									
									if (!CommonUtils.isNull(netConfirmedValueInOrderCurrency) && !CommonUtils.isNumber(netConfirmedValueInOrderCurrency)) {
										throw new ServiceBizException("Net Confirmed Value in Order Currency只能是数字类型");
									}

									// 保存用户导入进来的数据,只有以上数据全部合法才执行
									if (isValidateSucess) {
										dataList.add(dto);

										keyMap.put(purchaseOrder, purchaseOrder);
									}

								} catch (Exception e) {
									throw e;
								}

							}

						}));
		
		// 数据校验
		budgetExtendImportService.checkData(importResult.getDataList(), importResult);

		if (importResult.isSucess()) {
			
			List<Map> budgetApplyList = budgetApplicationImportService.queryBudgetApply();

			budgetExtendImportService.saveOrUpdate(dataList, budgetApplyList);

			return importResult.getDataList();
		} else {
			throw new ServiceBizException("导入出错,请见错误列表", importResult.getErrorList());
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
		String fileName = "budgetExtendImportTemplate.xlsx";

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {

			Resource res = new ClassPathResource("template/budgetExtendImportTemplate.xlsx");
			InputStream input = res.getInputStream();
			wb = new XSSFWorkbook(input);

			bis = new BufferedInputStream(input);
			os = response.getOutputStream();
			CommonUtils.setResponseHeader(response, fileName);

			logger.info("============采购开票模板导出结束==============：");
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
