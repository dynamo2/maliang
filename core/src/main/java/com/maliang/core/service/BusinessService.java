package com.maliang.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.model.Block;
import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;
import com.maliang.core.util.SessionUtil;
import com.maliang.core.util.Utils;

public class BusinessService {
	BusinessDao businessDao = new BusinessDao();
	
	public List<Map<String,Object>> businesses(){
		List<Business> bes =  this.businessDao.listByProject();
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		
		if(!Utils.isEmpty(bes)){
			for(Business b : bes){
				Map<String,Object> map = new LinkedHashMap<String,Object>();
				
				map.put("id",b.getId());
				map.put("name", b.getName());
				map.put("flows", new ArrayList<Map<String,Object>>());
				
				if(!Utils.isEmpty(b.getWorkflows())){
					List<Map<String,Object>> flows = (List<Map<String,Object>>)map.get("flows");
					for(Workflow wf : b.getWorkflows()){
						Map<String,Object> wm = new LinkedHashMap<String,Object>();
						wm.put("id",wf.getId());
						wm.put("name",wf.getName());
						wm.put("step",wf.getStep());
						
						flows.add(wm);
					}
				}
				
				results.add(map);
			}
		}
		
		return results;
	}
	
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
		
		System.out.println("---------- fid : " + fid);
		
		Workflow flow = business.workFlow(fid);
		
		System.out.println("---------- flow id : " + flow.getId());
		readBlock(flow,business.getUniqueCode(),Block.TYPE_CODE);
		
		AE.execute(flow.getCode(), params);
		
		String type = (String)MapHelper.readValue(params,"type");
		if("ajax".equalsIgnoreCase(type)){
			return AE.execute(flow.getAjax(), params);
		}
		return AE.execute(flow.getResponse(), params);
	}
	
	public Business readBusiness(Map<String,Object> params){
		String key = (String)MapHelper.readValue(params,"bid");
		Business business = null;
		if(key != null && !key.isEmpty()){
			business = this.businessDao.getByID(key);
		}
		
		if(business == null){
			key = (String)MapHelper.readValue(params,"bn");
			business = this.businessDao.getByName(key);
		}
		
		return business;
	}
	
	public Workflow readWorkflow(Business business,Map<String,Object> params){
		if(business == null){
			return null;
		}
		
		int fid = (Integer)MapHelper.readValue(params,"fid",-1);
		
		Workflow flow = business.workFlow(fid);
		
		return flow;
	}
	
	public void readBlock(Workflow flow,String defaultUniqueCode,int blockType){
		flow.setJavaScript(this.readBlock(flow.getJavaScript(), defaultUniqueCode,blockType));
		flow.setCode(this.readBlock(flow.getCode(), defaultUniqueCode,blockType));
		flow.setRequestType(this.readBlock(flow.getRequestType(), defaultUniqueCode,blockType));
		flow.setResponse(this.readBlock(flow.getResponse(), defaultUniqueCode,blockType));
		flow.setAjax(this.readBlock(flow.getAjax(), defaultUniqueCode,blockType));
	}
	
	

	public String readBlock(String code,String defaultUniqueCode,int blockType) {
		if(code == null)return null;
		
		String startRgx = "\\$\\{";
		String endRgx = "\\}";

		Pattern pStart = Pattern.compile(startRgx);
		Pattern pEnd = Pattern.compile(endRgx);
		
		StringBuffer sbf = new StringBuffer(code);
		int cursor = 0;

		Matcher mStart = pStart.matcher(sbf);
		Matcher mEnd = pEnd.matcher(sbf);
		BlockReplacer replacer = new BlockReplacer(sbf,defaultUniqueCode,blockType);
		while(mStart.find(cursor)){
			mEnd.find(mStart.start()+2);
			
			replacer.replace(mStart.start(), mEnd.start());
			cursor = replacer.cursor;
		}

		return sbf.toString();
	}
	
	class BlockReplacer {
		final StringBuffer source;
		final String defaultUniqueCode;
		final int replaceType;
		int cursor = 0;
		
		BlockReplacer(StringBuffer s,String dc,int t){
			this.source = s;
			this.defaultUniqueCode = dc;
			this.replaceType = t;
		}
		
		void replace(int start,int end){
			String label = this.readLabel(start+2,end);
			
			Block block = businessDao.getBlock(label);
			if(block == null){
				this.source.replace(start, end+1,"");
				return;
			}
			
			this.cursor = end+1;
			if(matchType(block)){
				String repCnt = readBlock(block.getCode(),defaultUniqueCode,this.replaceType);
				if(repCnt == null)repCnt = "";
				
				this.source.replace(start, end+1,repCnt);
				
				this.cursor = start+repCnt.length();
			}
		}
		
		private String readLabel(int start,int end){
			String label = this.source.substring(start,end);
			if(!label.isEmpty()){
				label = label.trim();
			}
			
			if(!label.contains(".")){
				label = this.defaultUniqueCode+"."+label;
			}
			return label;
		}
		
		private boolean matchType(Block block){
			if(block == null)return false;
			if(this.replaceType == 0)return false;
			
			if(this.replaceType == 1){
				return block.getType() == null || block.getType() == 1;
			}
			
			return block.getType() == this.replaceType;
		}
	}
	
	
	
	public String readBlock2222222222(String code,String defaultUniqueCode,int blockType) {
		if(code == null)return null;
		
		String startRgx = "\\$\\{";
		String endRgx = "\\}";

		Pattern pStart = Pattern.compile(startRgx);
		Pattern pEnd = Pattern.compile(endRgx);
		
		Matcher mStart = pStart.matcher(code);
		Matcher mEnd = pEnd.matcher(code);
		
		StringBuffer sbf = new StringBuffer(code);
		while(mStart.find(0)){
			mEnd.find(mStart.start()+2);
			
			BlockNode node = new BlockNode(sbf.toString(),
					defaultUniqueCode,mStart.start(),mEnd.start(),blockType);
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
		final int replaceType;
		Block block;
		String label;
		
		BlockNode(String s,String dc,int st,int e,int t){
			this.source = s;
			this.defaultUniqueCode = dc;
			this.start = st;
			this.end = e;
			this.replaceType = t;
			
			this.read();
		}
		
		String getLabel(){
			return this.label;
		}

		StringBuffer replace(StringBuffer sbf){
			if(sbf == null){
				sbf = new StringBuffer(source);
			}
			
			if(matchType()){
				sbf.replace(start, end+1,getBlockFromDB());
			}
			return sbf;
		}
		
		private boolean matchType(){
			if(this.block == null)return false;
			if(this.replaceType == 0)return false;
			
			if(this.replaceType == 1){
				return this.block.getType() == null || this.block.getType() == 1;
			}
			
			return this.block.getType() == this.replaceType;
		}
		
		private String getBlockFromDB(){
			if(this.block != null){
				return readBlock(this.block.getCode(),defaultUniqueCode,this.replaceType);
			}
			return "";
		}
		
		private void read(){
			this.label = this.source.substring(start+2,end);
			if(!label.isEmpty()){
				this.label = this.label.trim();
			}
			
			if(!this.label.contains(".")){
				this.label = this.defaultUniqueCode+"."+this.label;
			}
			
			this.block = businessDao.getBlock(this.label);
		}
	}
}


