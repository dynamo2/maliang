package com.maliang.core.arithmetic;


/**
 * 
 * 执行<java::>内的内容，并用执行结果替换表达式
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