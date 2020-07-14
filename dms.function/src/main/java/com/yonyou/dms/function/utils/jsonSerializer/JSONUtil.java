
/** 
*Copyright 2017 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.function
*
* @File name : JSONUtil.java
*
* @Author : zhangxc
*
* @Date : 2017年1月13日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2017年1月13日    zhangxc    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.function.utils.jsonSerializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyou.dms.function.exception.UtilException;

/**
* JSON 转换公共方法
* @author zhangxc
* @date 2017年1月13日
*/

public class JSONUtil {

    /**
     * 将json转化为实体POJO
     * @param jsonStr
     * @param obj
     * @return
     * @throws Exception 
     */
    public static<T> Object jsonToObj(String jsonStr,Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(jsonStr,
                    obj);
        } catch (Exception e) {
            throw new UtilException("json 转换失败",e);
        }
        return t;
    }
     
    /**
     * 将实体POJO转化为JSON
     * @param <T>
     * @param obj
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static<T> String objectToJson(T obj) {
        String jsonStr = null;
        
        if(obj!=null){
            ObjectMapper mapper = new ObjectMapper(); 
            try {
                 jsonStr =  mapper.writeValueAsString(obj);
            } catch (IOException e) {
                throw new UtilException("json 转换失败",e);
            }
        }
        return jsonStr;
    }
    
    /**
     * 
    *
    * @author zhangxc
    * @ description JSON转换为MAP
    * @date 2016年12月7日
    * @param json
    * @return
    * @throws Exception
     */
    public Map<String , Object> jsonToMap(String json) {
        ObjectMapper mapper = new ObjectMapper();  
        try{  
            @SuppressWarnings("unchecked")
            Map<String , Object> map = (Map<String , Object>)mapper.readValue(json, Map.class);
            return map;
        }catch(Exception e){  
            throw new UtilException("json 转换失败",e);
        }
    }
}
