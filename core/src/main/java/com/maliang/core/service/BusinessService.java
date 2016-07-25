package com.maliang.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.model.Business;
import com.maliang.core.model.WorkFlow;

public class BusinessService {
	BusinessDao businessDao = new BusinessDao();
	
	public Object business(Map<String,Object> params){
		String key = (String)MapHelper.readValue(params,"bid");
		Business business = null;
		if(key != null && !key.isEmpty()){
			business = this.businessDao.getByID(key);
		}
		
		if(business == null){
			key = (String)MapHelper.readValue(params,"bn");
			business = this.businessDao.getByName(key);
		}
		
		int fid = (Integer)MapHelper.readValue(params,"fid",-1);
		WorkFlow flow = business.workFlow(fid);
		
		ArithmeticExpression.execute(flow.getCode(), params);
		
		return ArithmeticExpression.execute(flow.getResponse(), params);
	}
	
	public String readBlock(String code) {
		String startRgx = "\\$\\{";
		String endRgx = "\\}";

		int startIndex = 0;
		
		Pattern pStart = Pattern.compile(startRgx);
		Pattern pEnd = Pattern.compile(endRgx);
		
		Matcher mStart = pStart.matcher(code);
		Matcher mEnd = pEnd.matcher(code);
		
		List<BlockNode> list = new ArrayList<BlockNode>();
		while(mStart.find(startIndex)){
			mEnd.find(mStart.start()+2);
			
			BlockNode node = new BlockNode(code,mStart.start(),mEnd.start());
			list.add(node);
			
			startIndex = mEnd.end();
		}
		
		StringBuffer sbf = new StringBuffer(code);
		for(BlockNode n : list){
			n.replace(sbf,"<TEST333>");
		}
		return sbf.toString();
	}
}

class BlockNode {
	final String source;
	final int start;
	final int end;
	String label;
	
	BlockNode(String s,int st,int e){
		this.source = s;
		this.start = st;
		this.end = e;
		
		this.read();
	}
	
	String getLabel(){
		return this.label;
	}
	
	StringBuffer replace(StringBuffer sbf,String newStr){
		if(sbf == null){
			sbf = new StringBuffer(source);
		}
		return sbf.replace(start, end,newStr);
	}
	
	private void read(){
		this.label = this.source.substring(start+2,end);
		if(!label.isEmpty()){
			this.label = this.label.trim();
		}
	}
}
