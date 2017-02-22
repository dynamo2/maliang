package com.maliang.core.arithmetic;

import java.util.Map;
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
	 * 计算标签内的内容，并返回
	 * ***/
	public abstract String doReplace(Object obj);
	
	/***
	 * 1. 解析标签表达式
	 * 2. 计算表达式，并用计算结果替换标签表达式
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
