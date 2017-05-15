package com.maliang.core.arithmetic;


/**
 * 
 * 将<ht::XX>的内容替换成：<div id='htmlTemplate-XX' style='display:none' />
 * ***/
public class HtmlTemplateReplacer extends Replacer {
	public HtmlTemplateReplacer(String s){
		super(s,"\\<ht::","\\>");
	}
	
	public String doReplace(Object obj){
		mEnd.find(mStart.start()+5);
		String label = this.source.substring(mStart.start()+5,mEnd.start());
		
		return "<div id='htmlTemplate-"+label+"' style='display:none' />";
	}
}