package com.maliang.core.arithmetic;

import java.util.ArrayList;
import java.util.List;

public class Substring {
	private String source;
	private int cursorIndex;
	
	private char startChar;
	private char endChar;
	private int startIndex = -1;
	private int endIndex = -1;
	private String innerContent;
	private String completeContent;
	private boolean isMatched = false;
	
	public Substring(String source,char startChar,char endChar,int cursorIndex){
		this.source = source;
		this.startChar = startChar;
		this.endChar = endChar;
		this.cursorIndex = cursorIndex;
		
		matchSubstring();
	}
	
	public Substring(String source,char ch,int cursorIndex){
		this(source,ch,ch,cursorIndex);
	}
	
	public String getSource() {
		return source;
	}
	public int getCursorIndex() {
		return cursorIndex;
	}
	public char getStartChar() {
		return startChar;
	}
	public char getEndChar() {
		return endChar;
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
		
		int[] coors = readCoors();
		
		this.startIndex = coors[0];
		this.endIndex = coors[1];
		
		//System.out.println("s: " + this.startIndex+", e : " + this.endIndex);
		
		if(this.checkIndex()){
//			this.completeContent = filteEsc(source.substring(this.startIndex,this.endIndex+1));
//			this.innerContent = filteEsc(source.substring(this.startIndex+1,this.endIndex));
			
			this.completeContent = source.substring(this.startIndex,this.endIndex+1);
			this.innerContent = source.substring(this.startIndex+1,this.endIndex);
			
			isMatched = true;
		}
	}
	
	public static void main(String[] args) {
		String s = "'aaa|'ddd|'ccc'";
		
		Substring sbs = new Substring(s,'\'',0);
		System.out.println(sbs.innerContent);
		
		s = "'aaa'.wrap(|')";
		//s = "'|''";
		Object v = ArithmeticExpression.execute(s, null);
		System.out.println("v : "+v);
	}
	
	private String filteEsc(String str){
		StringBuffer ns = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
			if(isEsc(str,i))continue;
			
			ns.append(str.charAt(i));
		}
		
		return ns.toString();
	}
	
	private boolean isEsc(int i){
		return this.isEsc(this.source, i);
	}
	
	/***
	 * 鏄惁鏄浆涔夊瓧绗�
	 * **/
	private boolean isEsc(String s,int i){
		if(i >= 0){
			if(s.charAt(i) == '|'){
				return true;
			}
		}
		return false;
	}
	
	private int[] readCoors(){
		int[] coors = new int[]{-1,-1};
		List<Integer> lefts = new ArrayList<Integer>();
		for(int i = this.cursorIndex; i< source.length(); i++){
			char c = source.charAt(i);
			
			if(this.startChar == this.endChar){
				if(c == startChar){
					//if(isEsc(i-1))continue;
					
					if(coors[0] == -1){
						coors[0] = i;
						continue;
					}else {
						coors[1] = i;
						break;
					}
				}
			}else {
				if(c == startChar){
					if(coors[0] == -1){
						coors[0] = i;
					}
					lefts.add(i);
					continue;
				}
				
				if(c == endChar){
					if(lefts.size() > 0){
						lefts.remove(lefts.size()-1);
						if(lefts.size() == 0){
							coors[1] = i;
							break;
						}
					}
					continue;
				}
			}
		}
		
		return coors;
	}
	
	private boolean checkIndex(){
		return this.startIndex >= 0 && this.startIndex < this.source.length() 
				&& this.endIndex >= 0 && this.endIndex < this.source.length()
				&& this.startIndex < this.endIndex;
	}

	/*
	private void matchSubstring(){
		if(source == null || source.trim().isEmpty()){
			return;
		}
		
		int[] coors = new int[]{-1,-1};
		List<Integer> lefts = new ArrayList<Integer>();
		for(int i = this.cursorIndex; i< source.length(); i++){
			char c = source.charAt(i);
			
			if(c == startChar){
				if(coors[0] == -1){
					coors[0] = i;
				}
				lefts.add(i);
				continue;
			}
			
			if(c == endChar){
				if(lefts.size() > 0){
					lefts.remove(lefts.size()-1);
					if(lefts.size() == 0){
						coors[1] = i;
						break;
					}
				}
				continue;
			}
		}
		
		this.startIndex = coors[0];
		this.endIndex = coors[1];
		
		if(this.checkIndex()){
			this.completeContent = source.substring(this.startIndex,this.endIndex+1);
			this.innerContent = source.substring(this.startIndex+1,this.endIndex);
			
			isMatched = true;
		}
	}*/
}
