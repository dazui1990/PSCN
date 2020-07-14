/*
* Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
*
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : cmol.common.function
*
* @File name : UtilException.java
*
* @Author : zhangxianchao
*
* @Date : 2016年2月26日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年2月26日    zhangxianchao    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.interf.exception;

/*
*
* @author zhangxianchao
* UtilException
* @date 2016年2月26日
*/

public class InterfaceException extends RuntimeException {

    /*
     * @author zhangxianchao UtilException
     * @date 2016年2月26日 tags
     */

    private static final long serialVersionUID = -5620134357529456759L;

    public InterfaceException(Exception e){
        super(e);
    }

    public InterfaceException(String msg){
        super(msg);
    }

    public InterfaceException(String msg, Exception e){
        super(msg, e);
    }

}
