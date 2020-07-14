package com.yonyou.dms.manage.controller.basedata;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.dms.framework.domain.LoginInfoDto;
import com.yonyou.dms.framework.util.bean.ApplicationContextHelper;
import com.yonyou.dms.manage.service.basedata.dealer.DealerService;
import com.yonyou.dms.manage.service.basedata.regionOrg.RegionOrgService;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

/**
 * 简要描述: 下拉框初始加载
 *
 * @author Jiangxy
 * @date 2018年5月24日
 */
@Controller
@TxnConn
@RequestMapping("/basedata/select")
public class QuerySelectController extends BaseController{
    
    @Resource
    private DealerService dealerService;
    
    @Resource
    private RegionOrgService regionOrgService;

    /**
     * 获取特约店下拉框(权限控制)
     * @author Jiangxy
     * @date 2018年5月24日
     * @return
     */
    @RequestMapping(value="/dealers",method=RequestMethod.GET)
    @ResponseBody
    public List<Map> findDealers(){
    	
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		Long userId = loginInfo.getUserId();
		String employeeNo = loginInfo.getEmployeeNo();
		
    	return dealerService.queryDealers(userId,employeeNo);
    }

    /**
     * 获取大区下拉框(权限控制)
     * @author Jiangxy
     * @date 2018年7月2日
     * @return
     */
    @RequestMapping(value="/bigOrg",method=RequestMethod.GET)
    @ResponseBody
    public List<Map> findBigOrg(){
    	
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		Long userId = loginInfo.getUserId();
		String employeeNo = loginInfo.getEmployeeNo();
		
    	return regionOrgService.queryBigOrg(userId, employeeNo);
    }
    
    /**
     * 获取督导下拉框(权限控制)
     * @author Jiangxy
     * @date 2018年7月2日
     * @return
     */
    @RequestMapping(value="/dudao",method=RequestMethod.GET)
    @ResponseBody
    public List<Map> findDudaoInfo(){
    	
		LoginInfoDto loginInfo = ApplicationContextHelper.getBeanByType(LoginInfoDto.class);
		Long userId = loginInfo.getUserId();
		String employeeNo = loginInfo.getEmployeeNo();
		
    	return regionOrgService.findDudaoInfo(userId, employeeNo);
    }
}