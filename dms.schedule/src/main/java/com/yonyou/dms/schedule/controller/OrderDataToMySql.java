package com.yonyou.dms.schedule.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.domains.PO.TtCarTypeTakePO;
import com.yonyou.dms.framework.manager.TransactionTenantManager;
import com.yonyou.dms.framework.manager.interf.AutoTransactionDataAction;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.jsonSerializer.JSONUtil;
import com.yonyou.f4.schedule.task.TenantSingletonTask;

public class OrderDataToMySql extends TenantSingletonTask{

    // 定义日志接口
    private static final Logger logger = LoggerFactory.getLogger(OrderDataToMySql.class);
    
    @Autowired
    TransactionTenantManager<List<TtCarTypeTakePO>,TtCarTypeTakePO> autoTransManager;
    
    public void execute(){
    	String tenantId = super.getTriggerInfo().getTenants();
    	System.out.println("租户ID:"+tenantId);
//        
//        //执行自动查询逻辑
//        autoTransManager.autoTransListExcute(tenantId, new AutoTransactionListAction<List<TtCarTypeTakePO>>() {
//            @SuppressWarnings({ "unchecked", "rawtypes", "null", "unused" })
//			@Override
//            public List<TtCarTypeTakePO> autoTransAction() {
//            	
//                return null;
//            }
//        });
    	try {
    		TtCarTypeTakePO typeTakePO = null;
        	String ADD_URL = "http://localhost:8080/WDHAC_SALES/DelphiXmlChannel/OrderDataToMySql";
        	URL url = new URL(ADD_URL);
    	    
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            connection.setInstanceFollowRedirects(true);
            
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Language", "zh-cn");
            connection.setRequestProperty("content-type","application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8"); 
            connection.setRequestProperty("contentType", "UTF-8");  
            connection.setRequestProperty("Content-type", "text/html");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Accept-Encoded", "gzip");
            connection.setRequestProperty("User-Agent", "Mozilla/4.0");  
            
            
            connection.connect();//连接

            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            
            Map<String,Object> map=new HashMap<String,Object>();
//            map.put("DEALER_ID", inspectionListDto.getDealerId());
//            map.put("QUERY_DATE", inspectionListDto.getAgendaDate());
            
            Map checkItemMap = new HashMap();
//            checkItemMap.put("checkItem", dataChecks);
//            checkItemMap.put("carCheckItem", carChecks);
//            checkItemMap.put("queryItem", map);
            
			System.out.println("--json数据----"+checkItemMap);
			
			//发送请求参数
            out.write(checkItemMap.toString().getBytes("UTF-8"));//可以避免中文乱码的问题
            out.flush();
            out.close();

            System.out.println("----------");
            //读取响应返回数据
            int resultCode=connection.getResponseCode();
            System.out.println("-------"+resultCode);
            if(resultCode!=200){
            	throw new ServiceBizException("同步数据失败");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "UTF-8");
                sb.append(lines);
            }
            System.out.println(new Date()+"返回的结果----"+sb);
			//将传过来的值转为MAP
			JSONUtil jsonUtil =new JSONUtil();
			Map<String,Object> mapV = jsonUtil.jsonToMap(sb.toString());
			System.out.println("----Map的值1----"+mapV);
			//数据检核信息
			ArrayList<HashMap<String,Object>> checkValue=(ArrayList<HashMap<String,Object>> ) mapV.get("InfoResult");
			System.out.println("----checkValue----"+checkValue);
			//遍历list里的数据
			for(int i=0;i<checkValue.size();i++){
				Map<String,Object> mapValue = checkValue.get(i);
				//删除原有的数据
				typeTakePO = new TtCarTypeTakePO();
				typeTakePO.setInteger("YEAR", mapValue.get("YEAR"));
				typeTakePO.setInteger("MONTH", mapValue.get("MONTH"));
				typeTakePO.setLong("NIGURI_GROUP_ID", mapValue.get("MATERIAL_ID"));
				autoTransManager.autoTransExcute(tenantId,typeTakePO,new AutoTransactionDataAction<TtCarTypeTakePO>() {
					@Override
					public void autoTransAction(TtCarTypeTakePO typeTakePO) {
						List<Map> list = searchList(typeTakePO.get("NIGURI_GROUP_ID").toString());
						TtCarTypeTakePO.delete("YEAR=? AND MONTH=? AND NIGURI_GROUP_ID=? AND MODEL_YEAR=? AND MODEL_ID=? AND PRODUCE_ID=? AND PACK_ID=?", 
								typeTakePO.get("YEAR"),typeTakePO.get("MONTH"),list.get(0).get("SERIES_ID"),list.get(0).get("YEAR_MODEL_ID"),list.get(0).get("MODEL_ID"),list.get(0).get("OBD_ID"),list.get(0).get("PACK_ID"));
								//插入新数据
								typeTakePO.setLong("NIGURI_GROUP_ID", list.get(0).get("SERIES_ID"));
								typeTakePO.setLong("MODEL_YEAR", list.get(0).get("YEAR_MODEL_ID"));
								typeTakePO.setLong("MODEL_ID", list.get(0).get("MODEL_ID"));
								typeTakePO.setLong("PRODUCE_ID", list.get(0).get("OBD_ID"));
								typeTakePO.setLong("PACK_ID", list.get(0).get("PACK_ID"));
								typeTakePO.setInteger("TAKE_VALUE", list.get(0).get("ORDER_NUM"));
								typeTakePO.saveIt();
					}
                });
			}
            reader.close();
            // 断开连接
            connection.disconnect();
    	} catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.debug(e.getMessage().toString());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.debug(e.getMessage().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.debug(e.getMessage().toString());
        }
    }

    
	public List<Map> searchList(String materialId){
		StringBuilder sb = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sb.append("SELECT\n");
		sb.append("C.SERIES_ID,\n");
		sb.append("C.YEAR_MODEL_ID,\n");
		sb.append("C.MODEL_ID,\n");
		sb.append("C.OBD_ID,\n");
		sb.append("A.PACK_ID\n");
		sb.append("FROM\n");
		sb.append("VW_VHCL_MATERIAL A,\n");
		sb.append("TM_DMS_MATERIAL_RELATION B,\n");
		sb.append("VW_NIRURI_MATERIAL C\n");
		sb.append("WHERE\n");
		sb.append("A.PACK_ID = B.SALE_GROUP_ID\n");
		sb.append("AND B.NIGURI_GROUP_ID = C.MODEL_ID\n");
		sb.append("AND A.MATERIAL_ID = ?;\n");
		params.add(materialId);
		return DAOUtil.findAll(sb.toString(), params);
	}
    
}
