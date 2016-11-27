package com.maliang.core.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.model.Block;
import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;

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
		Workflow flow = business.workFlow(fid);
		readBlock(flow,business.getUniqueCode());
		
		ArithmeticExpression.execute(flow.getCode(), params);
		
		String type = (String)MapHelper.readValue(params,"type");
		if("ajax".equalsIgnoreCase(type)){
			return ArithmeticExpression.execute(flow.getAjax(), params);
		}
		return ArithmeticExpression.execute(flow.getResponse(), params);
	}
	
	public void readBlock(Workflow flow,String defaultUniqueCode){
		flow.setJavaScript(this.readBlock(flow.getJavaScript(), defaultUniqueCode));
		flow.setCode(this.readBlock(flow.getCode(), defaultUniqueCode));
		flow.setRequestType(this.readBlock(flow.getRequestType(), defaultUniqueCode));
		flow.setResponse(this.readBlock(flow.getResponse(), defaultUniqueCode));
		flow.setAjax(this.readBlock(flow.getAjax(), defaultUniqueCode));
	}
	
	public String readBlock(String code,String defaultUniqueCode) {
		if(code == null)return null;
		
		String startRgx = "\\$\\{";
		String endRgx = "\\}";

		int startIndex = 0;
		
		Pattern pStart = Pattern.compile(startRgx);
		Pattern pEnd = Pattern.compile(endRgx);
		
		Matcher mStart = pStart.matcher(code);
		Matcher mEnd = pEnd.matcher(code);
		
		StringBuffer sbf = new StringBuffer(code);
		while(mStart.find(0)){
			mEnd.find(mStart.start()+2);
			
			BlockNode node = new BlockNode(sbf.toString(),defaultUniqueCode,mStart.start(),mEnd.start());
			node.replace(sbf);
			
			mStart = pStart.matcher(sbf.toString());
			mEnd = pEnd.matcher(sbf.toString());
		}

		return sbf.toString();
	}
	
	class BlockNode {
		final String source;
		final String defaultUniqueCode;
		final int start;
		final int end;
		String label;
		
		BlockNode(String s,String dc,int st,int e){
			this.source = s;
			this.defaultUniqueCode = dc;
			this.start = st;
			this.end = e;
			
			this.read();
		}
		
		String getLabel(){
			return this.label;
		}
		
		StringBuffer replace(StringBuffer sbf){
			if(sbf == null){
				sbf = new StringBuffer(source);
			}
			
			return sbf.replace(start, end+1,getBlockFromDB());
		}
		
		private String getBlockFromDB(){
			if(!this.label.contains(".")){
				this.label = this.defaultUniqueCode+"."+this.label;
			}
			
			Block block = businessDao.getBlock(this.label);
			if(block != null){
				return readBlock(block.getCode(),defaultUniqueCode);
			}
			return "";
		}
		
		private void read(){
			this.label = this.source.substring(start+2,end);
			if(!label.isEmpty()){
				this.label = this.label.trim();
			}
		}
	}
}


