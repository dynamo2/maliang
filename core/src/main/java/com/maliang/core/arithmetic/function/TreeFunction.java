package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.service.MapHelper;

public class TreeFunction {
	public static void main(String[] args) {
		String str = "{metadata:{"
		+ "name:{val:md.name},"
		+ "label:{val:md.label},"
		+ "fields:each(md.fields){tree([this,'fields']){{"
		+ "name:{val:this.name},"
		+ "label:{val:this.label},"
		+ "type:{type:'select',val:this.type,options:'fieldTypes'}"
		+ "}}}}}";
		
		
		ObjectMetadata md = new ObjectMetadata();
		md.setName("Product");
		md.setLabel("产品");
		
		List<ObjectField> fields = new ArrayList<ObjectField>();
		md.setFields(fields);
		
		/**** 基本信息  开始 ****/
		ObjectField of = new ObjectField();
		of.setName("info");
		of.setLabel("基本信息");
		of.setType(7);
		fields.add(of);
		
		List<ObjectField> ffs = new ArrayList<ObjectField>();
		of.setFields(ffs);
		
		ObjectField oof = new ObjectField();
		oof.setName("name");
		oof.setLabel("产品名称");
		oof.setType(1);
		ffs.add(oof);
		
		oof = new ObjectField();
		oof.setName("price");
		oof.setLabel("产品价格");
		oof.setType(1);
		ffs.add(oof);
		
		oof = new ObjectField();
		oof.setName("description");
		oof.setLabel("产品描述");
		oof.setType(1);
		ffs.add(oof);
		/**** 基本信息  结束 ****/
		
		of = new ObjectField();
		of.setName("groupBuying");
		of.setLabel("团购");
		of.setType(7);
		fields.add(of);
		
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("md",md);
		Object val = ArithmeticExpression.execute(str, params);
		
		System.out.println(val);
	}
	public static Object execute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		if(value == null){
			return null;
		}
		
		if(function.isEmptyBody()){
			return null;
		}
		
		if(value instanceof Collection){
			value = ((Collection)value).toArray();
		}
		
		Object data = value;
		String fieldName = "children";
		String flag = "children";
		if(value instanceof Object[]){
			Object[] dataList = (Object[])value;
			data = dataList[0];
			
			if(dataList.length >= 2){
				fieldName = (String)dataList[1];
				flag = fieldName;
			}
			
			if(dataList.length >= 3){
				flag = (String)dataList[2];
			}
		}
		
		// Test 
		//data = params.get("this");
		//System.out.println("========= data : " + params.get("this"));
		Object val = treeNode(data,fieldName,flag,function.getBody(),params);
		return val;
	}
	
	private static Object treeNode(Object data,String fieldName,String flag,String body,Map<String,Object> params){
		Function.pushThis(params);
		
		params.put("this", data);
		Object val = ArithmeticExpression.execute(body, params);
		if(val instanceof Map){
			Map map = (Map)val;
			Object fields = MapHelper.readValue(data, fieldName);
			if(fields != null){
				List<Object> fnodes = new ArrayList<Object>();
				Object[] farray = new Object[0];
				if(fields instanceof Collection){
					farray = ((Collection)fields).toArray();
					
				}else if(fields instanceof Object[]){
					farray = (Object[])fields;
				}
				
				for(Object field:farray){
					fnodes.add(treeNode(field,fieldName,flag,body,params));
				}
				map.put(flag,fnodes);
			}
		}
		params.remove("this");
		
		Function.popThis(params);
		return val;
	}
}
