package com.yonyou.dms.manage.controller.basedata;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.manage.domains.DTO.basedata.role.DealerRoleDto;
import com.yonyou.dms.manage.domains.DTO.basedata.user.EmployeeRoleDto;
import com.yonyou.dms.manage.service.basedata.region.DealerRegionService;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 特约店督导关联查询
 * @author ljc
 * date 2018/5/24
 */
@Controller
@TxnConn
@RequestMapping("/basedata/region")
public class DealerRegionController extends BaseController{
	private static final Logger logger=LoggerFactory.getLogger(DealerRegionController.class);
	
	@Autowired
    private DealerRegionService dealerRegionService;
	
	/**
	 * 区域下拉框
	 * @author ljc
	 * date 2018/5/24
	 */
	 @RequestMapping(value="/org",method = RequestMethod.GET)
	 @ResponseBody
	 public List<Map> orgList(){
		 
		 return dealerRegionService.orgList();
	 }
	 
	 /**
	  * 省份下拉框
	  * @author ljc
	  * date 2018/5/25
	  */
	 @RequestMapping(value="/regionId/{orgId}",method = RequestMethod.GET)
	 @ResponseBody
	 public List<Map> regionList(@PathVariable(value = "orgId") Long orgId){
		 
		 return dealerRegionService.regionList(orgId);
	 }
	 
	 /**
	  * 城市下拉框
	  * @author ljc
	  * date 2018/5/25
	  */
	 @RequestMapping(value="/city/{regionId}",method = RequestMethod.GET)
	 @ResponseBody
	 public List<Map> cityList(@PathVariable(value = "regionId") Long regionId){
		 
		 return dealerRegionService.cityList(regionId);
	 }
	 
	 /**
	  * 特约店代码拉框
	  * @author ljc
	  * date 2018/5/25
	  */
	 @RequestMapping(value="/dealerCode",method = RequestMethod.GET)
	 @ResponseBody
	 public List<Map> dealerCodeList(){
		 
		 return dealerRegionService.dealerCodeList();
	 }
	 
	 /**
	  * 查询
	  * @author ljc
	  * date 2018/5/25
	  */
	 @RequestMapping(value="/dealerOrDudao",method = RequestMethod.GET)
	 @ResponseBody
	 public PageInfoDto dealerOrDudao(@RequestParam Map<String, String> queryParam){
		 
		 return dealerRegionService.dealerOrDudaoList(queryParam);
	 }
	 
	 /**
	  * 督导下拉选
	  * @author ljc
	  * @return
	  */
	 @RequestMapping(value="/duDao/{str}",method = RequestMethod.GET)
	 @ResponseBody
	 public List<Map> duDaoList(@PathVariable(value = "str") String str){
		 
		 return dealerRegionService.duDaoList(str);
	 }
	 
	 /**
	  * 分配
	  * @author ljc
	  */
	 @RequestMapping(value = "/distribution/{selectRow}", method = RequestMethod.POST)
	 @ResponseStatus(HttpStatus.CREATED)
	 public void distribution(@RequestBody @Valid DealerRoleDto dealerDto,@PathVariable(value = "selectRow") String selectRow) {
		 
		 dealerRegionService.distribution(dealerDto,selectRow);
	 }
}
