package com.maliang.core.arithmetic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Replacer {
	protected final StringBuffer source;
	protected final String startRgx;
	protected final String endRgx;
	protected Pattern pStart;
	protected Pattern pEnd;
	protected Matcher mStart;
	protected Matcher mEnd;
	protected int cursor = 0;
	
	public Replacer(String s,String sr,String er){
		this.source = new StringBuffer(s);
		this.startRgx = sr;
		this.endRgx = er;
		
		
		this.pStart = Pattern.compile(startRgx);
		this.pEnd = Pattern.compile(endRgx);
	}
	
	/**
	 * 璁＄畻鏍囩鍐呯殑鍐呭锛屽苟杩斿洖
	 * ***/
	public abstract String doReplace(Object obj);
	
	/***
	 * 1. 瑙ｆ瀽鏍囩琛ㄨ揪寮�
	 * 2. 璁＄畻琛ㄨ揪寮忥紝骞剁敤璁＄畻缁撴灉鏇挎崲鏍囩琛ㄨ揪寮�
	 * **/
	public String replace(Object obj){
		if(this.source == null)return null;
		
		cursor = 0;
		mStart = pStart.matcher(this.source);
		mEnd = pEnd.matcher(this.source);
		while(mStart.find(cursor)){
			String rp = doReplace(obj);
			
			this.source.replace(mStart.start(), mEnd.start()+1,rp);
			cursor = mStart.start()+rp.length()+1;
		}
		
		return this.source.toString();
	}
}
