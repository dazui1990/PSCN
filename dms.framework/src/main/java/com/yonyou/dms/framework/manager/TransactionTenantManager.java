
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.schedule
*
* @File name : TransactionAutoManager.java
*
* @Author : zhangxc
*
* @Date : 2016年11月9日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年11月9日    zhangxc    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/
	
package com.yonyou.dms.framework.manager;

import java.util.List;

import com.yonyou.dms.framework.manager.interf.AutoTransactionAction;
import com.yonyou.dms.framework.manager.interf.AutoTransactionDataAction;
import com.yonyou.dms.framework.manager.interf.AutoTransactionListAction;

/**
* 事务自动管理器
* @author zhangxc
 * @param <E>
* @date 2016年11月9日
*/

public interface TransactionTenantManager<T extends List<E>, E> {

    T autoTransListExcute(String tenantId,AutoTransactionListAction<T> autoTransaction);
    
    void autoTransExcute(String tenantId,E dataValue,AutoTransactionDataAction<E> autoTransaction);
    
    void autoTransExcute(String tenantId,AutoTransactionAction autoTransaction);
}
