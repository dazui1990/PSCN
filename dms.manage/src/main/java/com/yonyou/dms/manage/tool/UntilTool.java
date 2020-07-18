package com.yonyou.dms.manage.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.function.exception.ServiceBizException;

public class UntilTool {
	
	
	/*
	 * 判断是否为数字格式不限制位数
	 * 
	 * @param o 待校验参数
	 * 
	 * @return 如果为全数字,返回true;否则,返回false
	 */
	public static boolean isNumber(String o) {
		return (Pattern.compile("[0-9]*").matcher(o).matches());
	}
	
	/*
	 * 生成版本号
	 * 
	 * @param dateFormat 待校验参数
	 * 
	 * @return Sting
	 */
	public static String version(){
		Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmssSSS");  		
		return sdf.format(date)  ;
	}
	
	//生成预算单号
	/*
	 * 生成版本号
	 * 
	 * @param type 需要生成预算单号的类型
	 * po 需要生成预算单的表 例如:tm_budget_maste_data
	 * 
	 * @return Sting 生成的预算单号
	 */
	public static String orderNo(String type,String po){
		Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");  
        String dateTime=sdf.format(date);
        //带上日期和po去相关数据库查询当天是否生成预算单号,如果没有返回00000,
        //如果有则在基础上面+1,超出长度抛出异常
    	String serial=findNo(po,dateTime);
        
        
		
		return  type.concat(dateTime).concat(serial); 
	}
	
	
      public static String findNo(String po,String dateTime){

          StringBuilder sql = new StringBuilder("select sn_no,substring(max(sn_no),9) orNo  from "+po+" where 1=1");
          sql.append(" and sn_no like ? ");
          List<Object> orgParams = new ArrayList<>();
//          orgParams.add(po);
          orgParams.add("%"+dateTime+"%");
        List<Map>noResult = DAOUtil.findAll(sql.toString(), orgParams);
        
          if(noResult!=null && noResult.size()>0 &&noResult.get(0).get("orNo")!=null &&!"".equals(noResult.get(0).get("orNo"))){
        	Integer num= Integer.valueOf(noResult.get(0).get("orNo").toString())+1 ;
        	  if(num>99999){
        		  throw new ServiceBizException("已超过数值99999最大上限");
        	  }
              return addZeroInFor(num,5);
          }else{
              return "00001";
          }
      }
      
      /**
  	 * 该方法使用for循环进行补零，譬如将数字9变成09,009，就需要对9补上2个3个零
  	 * @param int num:需要补零的数字
  	 * @param int len:补零之后数字的总长度
  	 * **/
  	public static String addZeroInFor(int num,int len){
  		StringBuffer sf = new StringBuffer();
  		sf.append(num);
  		int diff = len-sf.length();
  		if(diff>0){//说明位数不足
  			for(int i=0;i<diff;i++){
  				sf.insert(0,"0");
  			}
  		}
  		return sf.toString();
  	}


}
