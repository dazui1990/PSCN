
/** 
 *Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
 * This software is published under the terms of the Yonyou Software
 * License version 1.0, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 *
 * @Project Name : dms.manage
 *
 * @File name : PositionServiceImpl.java
 *
 * @Author : ZhengHe
 *
 * @Date : 2016年7月15日
 *
----------------------------------------------------------------------------------
 *     Date       Who       Version     Comments
 * 1. 2016年7月15日    ZhengHe    1.0
 *
 *
 *
 *
----------------------------------------------------------------------------------
 */

package com.yonyou.dms.manage.service.basedata.position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yonyou.dms.framework.DAO.DAOUtil;
import com.yonyou.dms.framework.DAO.PageInfoDto;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.dms.function.utils.common.CommonUtils;
import com.yonyou.dms.function.utils.common.StringUtils;
import com.yonyou.dms.manage.domains.DTO.basedata.PositionDTO;
import com.yonyou.dms.manage.domains.PO.basedata.PositionPo;

/**
 * PositionService实现类
 * @author ZhengHe
 * @date 2016年7月15日
 */
@Service
public class PositionServiceImpl implements PositionService{

    /**
     * 根据条件查询职务信息
     * @author ZhengHe
     * @date 2016年7月15日
     * @param queryParams
     * @return
     * @throws ServiceBizException
     * (non-Javadoc)
     * @see com.yonyou.dms.manage.service.basedata.position.PositionService#queryPosition(java.util.Map)
     */
    @Override
    public PageInfoDto queryPosition(Map<String, String> queryParams) throws ServiceBizException {
        StringBuilder sqlSb = new StringBuilder("SELECT POSITION_ID,DEALER_CODE,POSITION_CODE,POSITION_NAME,POSITION_DESC,IS_VALID,RECORD_VERSION FROM tm_position where 1=1");
        List<Object> params = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(queryParams.get("positionName"))){
            sqlSb.append(" and POSITION_NAME like ? ");
            params.add("%"+queryParams.get("positionName")+"%");
        }
        if(!StringUtils.isNullOrEmpty(queryParams.get("isValid"))){
            sqlSb.append(" and IS_VALID=?");
            params.add(Integer.parseInt(queryParams.get("isValid")));
        }
        PageInfoDto pageInfoDto = DAOUtil.pageQuery(sqlSb.toString(),params);
        return pageInfoDto;
    }

    /**
     * 
     * @author ZhengHe
     * @date 2016年7月18日
     * @param ptdto
     * @return
     * @throws ServiceBizException
     * (non-Javadoc)
     * @see com.yonyou.dms.manage.service.basedata.position.PositionService#addPosition(com.yonyou.dms.manage.domains.DTO.basedata.PositionDTO)
     */
    @Override
    public Long addPosition(PositionDTO ptdto) throws ServiceBizException {
        if(!CommonUtils.isNullOrEmpty(getPositionByCode(ptdto.getPositionCode()))){
            throw new ServiceBizException("已存在此职务代码");
        }
        if(!CommonUtils.isNullOrEmpty(getPositionByName(ptdto.getPositionName().trim()))){
            throw new ServiceBizException("已存在此职务名称");
        }
        PositionPo ptPo=new PositionPo();
        setPositionPo(ptPo,ptdto);
        ptPo.saveIt();
        return ptPo.getLongId();
    }

    /**
     * 
     * @author ZhengHe
     * @date 2016年7月18日
     * @param id
     * @return
     * @throws ServiceBizException
     * (non-Javadoc)
     * @see com.yonyou.dms.manage.service.basedata.position.PositionService#getPositionById(java.lang.Long)
     */
    @Override
    public PositionPo getPositionById(Long id) throws ServiceBizException {
        return PositionPo.findById(id);
    }

    /**
     * 
     * @author ZhengHe
     * @date 2016年7月18日
     * @param ptdto
     * @throws ServiceBizException
     * (non-Javadoc)
     * @see com.yonyou.dms.manage.service.basedata.position.PositionService#modifyPosition(com.yonyou.dms.manage.domains.DTO.basedata.PositionDTO)
     */
    @Override
    public void modifyPosition(Long id,PositionDTO ptdto) throws ServiceBizException {
        PositionPo ptPo=PositionPo.findById(id);
        if(!StringUtils.isEquals(ptPo.get("POSITION_NAME"), ptdto.getPositionName())&&!CommonUtils.isNullOrEmpty(getPositionByName(ptdto.getPositionName().trim()))){
            throw new ServiceBizException("已存在此职务名称");
        }
        setPositionPo(ptPo,ptdto);
        ptPo.saveIt();
    }

    /**
     * 设置职务
     * @author ZhengHe
     * @date 2016年7月18日
     * @param ptPo
     * @param ptdto
     */
    public void setPositionPo(PositionPo ptPo,PositionDTO ptdto){
        ptPo.set("POSITION_CODE",ptdto.getPositionCode());
        ptPo.set("POSITION_NAME",ptdto.getPositionName());
        ptPo.set("IS_VALID",ptdto.getIsValid());
        ptPo.set("POSITION_DESC",ptdto.getPositionDesc());
    }

    /**
     * 查询员工信息（下拉框）
     * @author jcsi
     * @date 2016年8月1日
     * @return
     * (non-Javadoc)
     * @see com.yonyou.dms.manage.service.basedata.position.PositionService#findAllPosition()
     */
    @Override
    public List<Map> findAllPosition() {
        StringBuilder sb=new StringBuilder("select DEALER_CODE,POSITION_CODE,POSITION_NAME from tm_position where IS_VALID=? ");
        List<Object> param=new ArrayList<>();
        param.add(DictCodeConstants.STATUS_IS_YES);
        return DAOUtil.findAll(sb.toString(), param);
    }

    

    
    /**
    * 根据Code查询信息
    * @author ZhengHe
    * @date 2016年10月18日
    * @param positionCode
    * @return
    * @throws ServiceBizException
    */
    	
    public List<Map> getPositionByCode(String positionCode) throws ServiceBizException {
        StringBuilder sqlSb = new StringBuilder("SELECT POSITION_ID,DEALER_CODE,POSITION_CODE,POSITION_NAME,POSITION_DESC,IS_VALID,RECORD_VERSION FROM tm_position where 1=1");
        List<Object> params = new ArrayList<>();
        sqlSb.append(" and POSITION_CODE=?");
        params.add(positionCode);
        List<Map> positionList=DAOUtil.findAll(sqlSb.toString(), params);
        return positionList;
    }
    
    
    /**
    * 根据name查询信息
    * @author ZhengHe
    * @date 2016年10月19日
    * @param positionName
    * @return
    * @throws ServiceBizException
    */
    	
    public List<Map> getPositionByName(String positionName) throws ServiceBizException {
        StringBuilder sqlSb = new StringBuilder("SELECT POSITION_ID,DEALER_CODE,POSITION_CODE,POSITION_NAME,POSITION_DESC,IS_VALID,RECORD_VERSION FROM tm_position where 1=1");
        List<Object> params = new ArrayList<>();
        sqlSb.append(" and POSITION_NAME=?");
        params.add(positionName);
        List<Map> positionList=DAOUtil.findAll(sqlSb.toString(), params);
        return positionList;
    }

}
