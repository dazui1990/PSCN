package com.yonyou.dms.web.controller.basedata;


import java.io.File;

import it.sauronsoftware.AudioAttributes;
import it.sauronsoftware.Encoder;
import it.sauronsoftware.EncoderException;
import it.sauronsoftware.EncodingAttributes;
import it.sauronsoftware.VideoAttributes;
 
public class Audio {
	public static void main(String[] args) {
		File source = new File("E:\\视频文件.mp4");
		File target = new File("E:\\ee.mp4");
		try {
			AudioAttributes audio = new AudioAttributes();// 音频属性
			audio.setCodec("libmp3lame");// libfaac PGM编码
			audio.setBitRate(new Integer(128000));// 音频比特率
			audio.setChannels(new Integer(2));// 声道
			audio.setSamplingRate(new Integer(44100));// 采样率
			VideoAttributes video = new VideoAttributes();// 视频属性
			video.setCodec("mpeg4");// 视频编码
			video.setBitRate(new Integer(15));// 视频比特率
			video.setFrameRate(new Integer(30));// 帧率
			EncodingAttributes attrs = new EncodingAttributes();// 转码属性
			attrs.setFormat("mp4");// 视频格式
			attrs.setAudioAttributes(audio);// 音频属性
			attrs.setVideoAttributes(video);// 视频属性
			Encoder encoder = new Encoder();// 创建解码器
			encoder.encode(source, target, attrs);
		}
		catch (EncoderException e) {
			e.printStackTrace();
		}
	}
	
	public static void Video(String sourcename,String targetname){
		File source = new File(sourcename);
		File target = new File(targetname);
		try {
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(56000));
		audio.setChannels(new Integer(1));
		audio.setSamplingRate(new Integer(22050));
		VideoAttributes video = new VideoAttributes();
		video.setCodec("mpeg4");
		video.setBitRate(new Integer(800000));
		video.setFrameRate(new Integer(15));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp4");
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(source, target, attrs);
		/*//压缩成功后删除原有的上传的文件
		AudioAttributes audio = new AudioAttributes();// 音频属性
		audio.setCodec("libmp3lame");// libfaac PGM编码
		audio.setBitRate(new Integer(128000));// 音频比特率
		audio.setChannels(new Integer(2));// 声道
		audio.setSamplingRate(new Integer(44100));// 采样率
		VideoAttributes video = new VideoAttributes();// 视频属性
		video.setCodec("mpeg4");// 视频编码
		video.setBitRate(new Integer(15));// 视频比特率
		video.setFrameRate(new Integer(30));// 帧率
		EncodingAttributes attrs = new EncodingAttributes();// 转码属性
		attrs.setFormat("mp4");// 视频格式
		attrs.setAudioAttributes(audio);// 音频属性
		attrs.setVideoAttributes(video);// 视频属性
		Encoder encoder = new Encoder();// 创建解码器
		encoder.encode(source, target, attrs);*/

		}
		catch (EncoderException e) {
			e.printStackTrace();
		}
	}
	
	
}
 
