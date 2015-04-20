package com.maliang.core.service.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.maliang.core.service.MapHelper;

public class FormCompiler {
	static class Node {
		Map<String,Object> source = null;
		String view;
		String formType;
		String prefix;
		List<InputNode> inputs = null;
		Map<String,Object> datas;
		FormInfoNode formInfo = null;
		
		public Node(Map<String,Object> s,Map<String,Object> ds){
			this.source = s;
			this.datas = ds;
			
			this.view = (String)this.source.get("view");
			this.formType = (String)this.source.get("type");
			this.prefix = (String)this.source.get("prefix");
			prefix = prefix == null?"":prefix;
			
			Map<String,Object> fis = (Map<String,Object>)this.source.get("form");
			formInfo = new FormInfoNode(fis,this);
			
			this.initInputs();
		}
		
		public Map<String,Object> buildJson(){
			Map<String,Object> formJson = new HashMap<String,Object>();
			List<Map<String,Object>> inputsJson = new ArrayList<Map<String,Object>>();
			formJson.put("inputs", inputsJson);
			
			formJson.put("name", formInfo.name);
			formJson.put("action", formInfo.action);
			
			for(InputNode input:inputs){
				inputsJson.add(input.buildJson());
			}
			
			return formJson;
		}
		
		private void initInputs(){
			inputs = new ArrayList<InputNode>();
			List<Map<String,Object>> inputList = (List<Map<String,Object>>)this.source.get("inputs");
			for(Map<String,Object> im:inputList){
				inputs.add(new InputNode(im,this));
			}
		}
	}
	
	static class FormInfoNode {
		Map<String,Object> formInfoSource = null;
		Node parent = null;
		
		String name;
		String action;
		
		public FormInfoNode(Map<String,Object> fs,Node n){
			this.formInfoSource = fs;
			this.parent = n;
			
			this.name = (String)MapHelper.readValue(this.formInfoSource, "name","");
			this.action = (String)MapHelper.readValue(this.formInfoSource, "action","");
		}
	}
	
	static class InputNode {
		
		Map<String,Object> inputJson = null;
		Map<String,Object> sourceInput = null;
		
		Node parent;
		String simpleName;
		String inputType;
		String label;
		String operator;
		Object valueKey;
		Object value;
		Map<String,Object> values;
		String canonicalName;
		
		public InputNode(Map<String,Object> input,Node n){
			this.sourceInput = input;
			this.parent = n;
			
			simpleName = (String)input.get("name");
			label = (String)getValue("label","");
			inputType = (String)input.get("type");
			operator = (String)input.get("operator");
			valueKey = input.get("value");
			values = (Map<String,Object>)this.sourceInput.get("values");
			
			if(valueKey instanceof List){
				value = MapHelper.readValue(parent.datas, (List<String>)valueKey);
			}else {
				value = MapHelper.readValue(parent.datas, (String)valueKey);
			}
			
			canonicalName = this.getPrefix()+simpleName;
			inputType = inputType==null?"text":inputType;
		}
		
		private Object getValue(String key,Object defaultValue){
			Object value = this.sourceInput.get(key);
			if(value == null){
				return defaultValue;
			}
			
			return value;
		}
		
		Map<String,Object> buildJson(){
			inputJson = new HashMap<String,Object>();
			
			inputJson.put("label", this.label);
			
			List<Map<String,Object>> infosJson = new ArrayList<Map<String,Object>>();
			inputJson.put("infos", infosJson);
			
			if("between".equals(operator)){
				compileBetween(infosJson);
			}else {
				compileEq(infosJson);
			}
			
			return inputJson;
		}
		
		private List<Map<String,Object>> optionsJson(){
			String listKey = (String)values.get("list");
			List<Map<String,Object>> listData = (List<Map<String,Object>>)MapHelper.readValue(parent.datas, listKey);
			List<Map<String,Object>> optionsJson = new ArrayList<Map<String,Object>>();
			if(listData == null || listData.size()== 0){
				return optionsJson;
			}
			
			Object key = MapHelper.readValue(values, "key","id");
			Object value = MapHelper.readValue(values, "value","name");
			for(Map<String,Object> obj:listData){
				Map option = new HashMap();
				
				option.put("key", obj.get(key).toString());
				option.put("label", obj.get(value).toString());
				
				optionsJson.add(option);
			}
			return optionsJson;
		}
		
		private void compileEq(List<Map<String,Object>> infosJson){
			Map<String,Object> infoJson = new HashMap<String,Object>();
			infoJson.put("type", inputType);
			infoJson.put("name", this.canonicalName);
			infoJson.put("value", value);
			
			if(hasOptions()){
				infoJson.put("options",optionsJson());
			}
			
			infosJson.add(infoJson);
		}
		
		private void compileBetween(List<Map<String,Object>> infosJson){
			Map<String,Object> minJson = new HashMap<String,Object>();
			Map<String,Object> maxJson = new HashMap<String,Object>();
			infosJson.add(minJson);
			infosJson.add(maxJson);
			
			minJson.put("type", inputType);
			maxJson.put("type", inputType);
			
			String cn = this.getPrefix()+"min_"+simpleName;
			minJson.put("name", cn);
			
			cn = this.getPrefix()+"max_"+simpleName;
			maxJson.put("name", cn);
			
			if(value != null && value instanceof Object[]){
				Object[] vs = (Object[])value;
				
				if(vs.length > 0){
					minJson.put("value", vs[0]);
					if(vs.length > 2){
						maxJson.put("value", vs[1]);
					}
				}
			}
			
			if(hasOptions()){
				List<Map<String,Object>> optionsJson = optionsJson();
				
				minJson.put("options",optionsJson);
				maxJson.put("options",optionsJson);
			}
		}
		
		private boolean hasOptions(){
			return "select".equals(inputType) || "radio".equals(inputType) || "checkbox".equals(inputType);
		}
		
		private String getPrefix(){
			return parent.prefix.isEmpty()?"":parent.prefix+".";
		}
	}
	
	/**
	 * search_form:{view:form,type:search,prefix:product,
	 * 		form:{name:searchForm,action:index.html},
	 *      inputs:[{name:fid,type:hidden,value:request.fid},
	 *            {name:name,type:text,label:名称,value:request.product.name},
	 *            {name:brand,type:select,label:品牌,value:request.product.brand,
	 *               	values:{list:brand_list,key:id,value:name}},
	 *            {name:price,type:text,operator:between,label:价格,
	 *            		value:[request.product.min_price,request.product.max_price]},
	 *            {name:expiry_date,type:date,operator:between,
	 *            		value:[request.product.min_expiry_date,request.product.max_expiry_date]}]}
	 *            
	 *
	 *{"label":"名称","info":[{"name":"Product.name",type:"text","value":"天气丹华泫平衡化妆水150ml"}]}
	 *
	 *{"label":"价格","infos":[{"name":"Product.min_price",type:"text","value":"100.00"},
	 *						  {"name":"Product.max_price",type:"text","value":"1000.00"}]}
	 * **/
	public static Map<String,Object> responseForm(Map<String,Object> form,Map<String,Object> params){
		Node node = new Node(form,params);
		return node.buildJson();
	}
	
	public static void main(String[] args) {
		String str = "{view:form,type:search,prefix:product,form:{name:searchForm,action:index.html},"
				+ "inputs:[{name:fid,type:hidden,value:request.fid},"
					+ "{name:name,type:text,label:名称,value:request.product.name},"
				
					/*
					+ "{name:brand,type:select,label:品牌,value:request.product.brand,"
						+ "values:{list:brand_list,key:id,value:name}},"*/
					+ "{name:price,type:text,operator:between,label:价格,"
						+ "value:[request.product.min_price,request.product.max_price]},"
					+ "{name:expiry_date,type:date,operator:between,"
						+ "value:[request.product.min_expiry_date,request.product.max_expiry_date]}]}";
		
		Map<String,Object> form = MapHelper.curlyToMap(str);
		
		Map json = responseForm(form,null);
		
		System.out.println(json);
	}
	
	/**
	 * search_form:{view:form,type:search,prefix:product,
	 * 		form:{name:searchForm},
	 *      inputs:[{name:fid,type:hidden,value:request.fid},
	 *            {name:name,type:text,label:名称,value:request.product.name},
	 *            {name:brand,type:select,label:品牌,value:request.product.brand,
	 *               	values:{list:brand_list,key:id,value:name}},
	 *            {name:price,type:text,operator:between,label:价格,
	 *            		value:[request.product.min_price,request.product.max_price]},
	 *            {name:expiry_date,type:date,operator:between,
	 *            		value:[request.product.min_expiry_date,request.product.max_expiry_date]}]}
	 *            
	 *
	 *{"label":"名称","info":[{"name":"Product.name",type:"text","value":"天气丹华泫平衡化妆水150ml"}]}
	 *
	 *{"label":"价格","infos":[{"name":"Product.min_price",type:"text","value":"100.00"},
	 *						  {"name":"Product.max_price",type:"text","value":"1000.00"}]}
	 * **
	public static void responseForm(Map<String,Object> params){
		String str = "{view:form,type:search,prefix:product,form:{name:searchForm},"
				+ "inputs:[{name:fid,type:hidden,value:request.fid},"
					+ "{name:name,type:text,label:名称,value:request.product.name},"
					+ "{name:brand,type:select,label:品牌,value:request.product.brand,"
						+ "values:{list:brand_list,key:id,value:name}},"
					+ "{name:price,type:text,operator:between,label:价格,"
						+ "value:[request.product.min_price,request.product.max_price]},"
					+ "{name:expiry_date,type:date,operator:between,"
						+ "value:[request.product.min_expiry_date,request.product.max_expiry_date]}]}";
		
		Map<String,Object> form = MapHelper.curlyToMap(str);
		
		Map<String,Object> formJson = new HashMap<String,Object>();
		List<Map<String,Object>> inputsJson = new ArrayList<Map<String,Object>>();
		
		String prefix = (String)MapHelper.readValue(form,"prefix");
		prefix = prefix == null?"":prefix;
		
		List<Map<String,Object>> inputs = (List<Map<String,Object>>)MapHelper.readValue(form,"prefix");
		
		for(Map<String,Object> input:inputs){
			Map<String,Object> inputJson = new HashMap<String,Object>();
			List<Map<String,Object>> infosJson = new ArrayList<Map<String,Object>>();
			inputJson.put("infos", infosJson);
			
			String simpleName = (String)input.get("name");
			String inputLabel = (String)input.get("label");
			String inputType = (String)input.get("type");
			String operator = (String)input.get("operator");
			Object valueKey = input.get("value");
			
			String canonicalName = (prefix.isEmpty()?"":prefix+".")+simpleName;
			Object value = null;
			if(valueKey instanceof String[]){
				value = MapHelper.readValue(params, (String[])valueKey);
			}else {
				value = MapHelper.readValue(params, (String)valueKey);
			}
			
			inputType = inputType==null?"text":inputType;
			if("between".equals(operator)){
				Map<String,Object> minJson = new HashMap<String,Object>();
				Map<String,Object> maxJson = new HashMap<String,Object>();
				infosJson.add(minJson);
				infosJson.add(maxJson);
				
				canonicalName = (prefix.isEmpty()?"":prefix+".")+"min_"+simpleName;
				minJson.put("name", canonicalName);
				minJson.put("type", inputType);
				
				canonicalName = (prefix.isEmpty()?"":prefix+".")+"max_"+simpleName;
				maxJson.put("name", canonicalName);
				maxJson.put("type", inputType);
				
				if(value != null && value instanceof Object[]){
					Object[] vs = (Object[])value;
					
					if(vs.length > 0){
						minJson.put("value", vs[0]);
						if(vs.length > 2){
							maxJson.put("value", vs[1]);
						}
					}
				}
			}

			
			if("select".equals(inputType)){
				
			}
		}
	}*/
	
}
