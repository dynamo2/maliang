package com.maliang.core.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 读取器
 * 
 * 读取“开始符”和“结束符”之间的内容
 * 支持嵌套
 * **/
public class Reader {
	private String source;
	private int cursorIndex;
	
	private String startSign;
	private String endSign;
	private int startIndex = -1;
	private int endIndex = -1;
	private String innerContent;
	private String completeContent;
	private boolean isMatched = false;
	private boolean nest = true;
	
	
	public Reader(String source,String startSign,String endSign,int cursorIndex,boolean nest){
		this.source = source;
		this.startSign = startSign;
		this.endSign = endSign;
		this.cursorIndex = cursorIndex;
		this.nest = nest;
		
		matchSubstring();
	}
	
	public Reader(String source,String sign,int cursorIndex,boolean nest){
		this(source,sign,sign,cursorIndex,nest);
	}
	
	public Reader(String source,String sign,int cursorIndex){
		this(source,sign,sign,cursorIndex,false);
	}
	
	public String getSource() {
		return source;
	}
	public int getCursorIndex() {
		return cursorIndex;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public String getInnerContent() {
		return innerContent;
	}
	public String getCompleteContent() {
		return completeContent;
	}
	public boolean isMatched(){
		return this.isMatched;
	}
	
	private void matchSubstring(){
		if(source == null || source.trim().isEmpty()){
			return;
		}
		
		readCoors();
		if(this.endIndex > this.source.length()){
			this.endIndex = this.source.length();
		}

		if(this.checkIndex()){
			int cEnd = this.endIndex+this.endSign.length();
			if(cEnd > this.source.length()){
				cEnd = this.source.length();
			}
			
			this.completeContent = source.substring(this.startIndex,cEnd);
			this.innerContent = source.substring(this.startIndex+this.startSign.length(),this.endIndex);
			
			isMatched = true;
		}
	}
	
	public static void main(String[] args) {
		String s = "'''<div class='row mt-4 mb-4 pb-2 border-bottom'>'''<div class=\"col-6 mt-3\"><h4><span><ht::title></span><ht::editLink></h4></div><div class=\"mt-3 col-6 text-right\"><ht::newLink></div></div>'''";
		Pattern p = Pattern.compile("\'\'\'");
		Matcher m = p.matcher(s);
		boolean b = m.matches();
		
		if(m.matches()){
			System.out.println("m.start() : " + m.start());
		}
		
		long bt = System.currentTimeMillis();
		
		s = "{x : '''<div class=\"row mt-4 mb-4 pb-2 border-bottom\">''',b:6}";
		//s = "'<div class=\"row mt-4 mb-4 pb-2 border-bottom\">'"; 
		s = "'''";
		Object val = AE.execute(s);
		System.out.println("val : '" + val +"'");
		
		Reader rs = new Reader(s,"'''",0);
		System.out.println("innerContent : '" + rs.innerContent+"'");
		
		System.out.println(" time : " + (System.currentTimeMillis()-bt));
		
		System.out.println("char : "+(char)-1);
	}
	
	private void readCoors(){
		List<Integer> lefts = new ArrayList<Integer>();
		
		int i = this.cursorIndex;
		int idx = this.source.indexOf(this.startSign,i);
		if(idx == -1){
			return;
		}
		this.startIndex = idx;
		i = idx + this.startSign.length();
		
		if(i >= source.length()){
			this.endIndex = this.source.length();
			return;
		}
		
		while(i < source.length()){
			/**
			 * 读取“结束符”位置
			 * **/
			idx = this.source.indexOf(this.endSign,i);
			
			/***
			 * 没有检索到结束符，直接跳到文档末尾
			 * **/ 
			if(idx == -1){
				this.endIndex = this.source.length();
				break;
			}
			
			/***
			 * 如果：“开始符” == “结束符”，则不进入嵌套检查
			 * **/
			if(this.startSign.equals(this.endSign) || !this.nest){
				this.endIndex = idx;
				break;
			}
			
			/**
			 * 读取“开始符”位置，用于比较
			 * **/
			int sidx = this.source.indexOf(this.startSign,i);
			/**
			 * “开始符”在“结束符”的前面，有嵌套，进入嵌套循环
			 * **/
			if(sidx > 0 && sidx < idx){
				lefts.add(sidx);
				i = sidx + this.startSign.length();
				continue;
			}
			
			/**
			 * 嵌套未结束，本轮“结束符”优先匹配被嵌套的“开始符”，读取下一轮的“结束符”
			 * **/
			if(lefts.size() > 0){
				lefts.remove(lefts.size()-1);
				i = idx + this.endSign.length();
				continue;
			}
			
			/***
			 * 没有嵌套，跳出检索
			 * **/
			this.endIndex = idx;
			break;
		}
	}
	
	private boolean checkIndex(){
		return this.startIndex >= 0 && this.startIndex < this.source.length() 
				&& this.endIndex >= 0 && this.endIndex <= this.source.length()
				&& this.startIndex < this.endIndex;
	}

}
