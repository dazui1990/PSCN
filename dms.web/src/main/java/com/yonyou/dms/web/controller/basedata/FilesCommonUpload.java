
/** 
*Copyright 2016 Yonyou Corporation Ltd. All Rights Reserved.
* This software is published under the terms of the Yonyou Software
* License version 1.0, a copy of which has been included with this
* distribution in the LICENSE.txt file.
*
* @Project Name : dms.web
*
* @File name : FilesCommonUpload.java
*
* @Author : zhangxc
*
* @Date : 2016年10月13日
*
----------------------------------------------------------------------------------
*     Date       Who       Version     Comments
* 1. 2016年10月13日    zhangxc    1.0
*
*
*
*
----------------------------------------------------------------------------------
*/

package com.yonyou.dms.web.controller.basedata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.yonyou.dms.framework.domains.DTO.file.FileUploadInfoDto;
import com.yonyou.dms.framework.domains.PO.file.FileUploadInfoPO;
import com.yonyou.dms.framework.service.FileStoreService;
import com.yonyou.dms.function.common.DictCodeConstants;
import com.yonyou.dms.function.exception.ServiceBizException;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.controller.BaseController;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 文件常规上传方法
 * 
 * @author zhangxc
 * @date 2016年10月13日
 */
@Controller
@TxnConn
@RequestMapping("/basedata/upload")
public class FilesCommonUpload extends BaseController {

	@Autowired
	FileStoreService fileStoreService;

	/**
	 * 
	 * 上传文件，并返回ID
	 * 
	 * @author zhangxc
	 * @date 2016年9月18日
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Map<String, String> uploadFiles(@RequestParam(value = "dmsFile") MultipartFile importFile) {
		String fileId = fileStoreService.writeFile(importFile);
		String a = importFile.getOriginalFilename();
		// 创建文件DTO 对象
		FileUploadInfoDto fileUploadDto = new FileUploadInfoDto();
		fileUploadDto.setFileId(fileId);
		fileUploadDto.setFileName(importFile.getOriginalFilename());
		fileUploadDto.setFileSize((int) importFile.getSize());
		fileUploadDto.setIsValid(DictCodeConstants.STATUS_NOT_VALID);

		Long fileUploadInfoId = fileStoreService.insertFileUploadInfo(fileUploadDto);
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("fileUploadId", fileUploadInfoId.toString());
		return returnMap;
	}

	@RequestMapping(value = "/dbs", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Map<String, String> uploadFile(@RequestParam(value = "dmsFile") MultipartFile importFile) {
		// String fileId = fileStoreService.writeFile(importFile);

		Map<String, String> returnMap = new HashMap<>();
		// 创建文件DTO 对象
		FileUploadInfoDto fileUploadDto = new FileUploadInfoDto();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		StringBuilder sb = new StringBuilder();
		sb.append("/");
		sb.append("BucketNode1");
		sb.append("/");
		sb.append("localeDataNode1");
		sb.append("/");
		sb.append(sdf.format(new Date()));
		Long sizelengt=importFile.getSize();
		//如果大于20M则直接异常处理
		if(sizelengt>20971520){
			  throw new ServiceBizException("文件大小超过20M,无法上传!");
		}else{
		File dest1 = new File("d://dmsFileStore/files/fs1" + sb.toString());
		if (dest1.exists()) {
			System.out.println("文件夹已经创建");
		} else {
			System.out.println("创建文件夹");
			dest1.mkdir();
		}
		StringBuilder sbd = new StringBuilder();
		sbd.append("/");
		sbd.append("BucketNode1");
		sbd.append("/");
		sbd.append("localeDataNode1");
		sbd.append("/");
		sbd.append(sdf.format(new Date()));
		sbd.append("/");
		String filename = importFile.getOriginalFilename();
		String suffix = filename.substring(filename.lastIndexOf("."));
		String fileId = "";
		File dest = null;
		try {
			if (suffix.contains("jpg") || suffix.contains("png") || suffix.contains("bmp") || suffix.contains("jpeg")
					|| suffix.contains("gif")||suffix.contains("JPG") || suffix.contains("PNG") || suffix.contains("BMP")
					|| suffix.contains("JPEG")
					|| suffix.contains("GIF")) {
				sbd.append(UUID.randomUUID().toString());
				//不用原来的后缀，其他格式压缩时图片大小会变大变小，统一格式后压缩
				sbd.append(".jpg");
				fileId = sbd.toString();
				dest = new File("d:/dmsFileStore/files/fs1" + fileId);
//				Thumbnails.of(importFile.getInputStream()).scale(1f).outputQuality(0.25f).toFile(dest);
			     Thumbnails.of(importFile.getInputStream())
			              .scale(0.8f)//放大还是缩小
			              .outputQuality(0.25f)//图片质量
			              .toFile(dest);
				fileUploadDto.setFileSize((int)dest.length());
				fileUploadDto.setFileName(importFile.getOriginalFilename());
			} else if (suffix.contains("mov") || suffix.contains("MOV")
					|| suffix.contains("mp4") || suffix.contains("MP4") 
					|| suffix.contains("wma")|| suffix.contains("AVI")|| suffix.contains("avi")) {
//				 'AVI','wma','rmvb','rm','flash','mp4','mid','3GP','wmv','mov','MOV'
				 String fileId1 =   fileStoreService.writeFile(importFile);
				String sourcename = "d:/dmsFileStore/files/fs1" + fileId1;
				StringBuilder sbd1 = new StringBuilder();
				sbd1.append("/");
				sbd1.append("BucketNode1");
				sbd1.append("/");
				sbd1.append("localeDataNode1");
				sbd1.append("/");
				sbd1.append(sdf.format(new Date()));
				sbd1.append("/");
				sbd1.append(UUID.randomUUID().toString());
				sbd1.append(suffix);
				fileId = sbd1.toString();
				// 指定的目录
				String targetname = "d:/dmsFileStore/files/fs1" + fileId;
				// 压缩视频
				Audio.Video(sourcename, targetname);
				//删除之前的文件
				File deletefile=new File(sourcename);
				deletefile.delete();
				dest = new File("d:/dmsFileStore/files/fs1" + fileId);
				fileUploadDto.setFileSize((int) dest.length());
				fileUploadDto.setFileName(filename);
			} else {
				fileId = fileStoreService.writeFile(importFile);
				fileUploadDto.setFileSize((int) importFile.getSize());
				fileUploadDto.setFileName(importFile.getOriginalFilename());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileUploadDto.setFileId(fileId);


		fileUploadDto.setIsValid(DictCodeConstants.STATUS_NOT_VALID);
		Long fileUploadInfoId = fileStoreService.insertFileUploadInfo(fileUploadDto);
		returnMap.put("fileUploadId", fileUploadInfoId.toString());}
		return returnMap;
	}

	/**
	 * 
	 * 删除上传的附件，目前为空实现，什么都不做
	 * 
	 * @author zhangxc
	 * @date 2016年9月18日
	 */
	@RequestMapping(value = "/delete/{billType}/{fileId}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUploadFiles(@PathVariable(value = "billType") Integer billType,
			@PathVariable(value = "fileId") Long fileId) {
		FileUploadInfoPO.delete("FILE_UPLOAD_INFO_ID=?", fileId);
	}
	
	/**
	 * 删除上传附件信息记录(逻辑删除 不删除实际文件)
	 * @param billType
	 * @param fileId
	 */
	@RequestMapping(value = "/deleteInfo/{billId}/{billType}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFileInfo(@PathVariable(value = "billType") Integer billType,@PathVariable(value = "billId") Long billId) {
		FileUploadInfoPO.delete("BILL_ID=? AND BILL_TYPE=?", billId, billType);
	}
}
