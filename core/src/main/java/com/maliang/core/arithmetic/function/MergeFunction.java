package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class MergeFunction {
	public static void main(String[] args) {
		String s = "{ps:[{product:'111',num:1},{product:'222',num:2}],"
					+ "merge:[{product:'333',num:3},{product:'444',num:4},{product:'111',num:5,name:'feadasf'}]}";
		
		s = "{ps:[{product:'111',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]},"
					+ "{product:'222',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}],"
				+ "merge:[{product:'333',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}]}";
		
		Object v = AE.execute(s);
		
		s = "ps.merge({merge:merge})";
		v = AE.execute(s,(Map)v);
		System.out.println("v : " + v);
		
		v = MapHelper.expand(Utils.toArray(v), "stores".split("\\."));
		for(Object oo : (Object[])v){
			System.out.println(oo);
		}
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		
		Collection results = null;
		if(val instanceof Collection){
			results = (Collection)val;
		}else if(val instanceof Object[]){
			results = new ArrayList();
			for(Object obj : (Object[])val){
				results.add(obj);
			}
		}
		
		val = function.executeExpression(params);
		if(Utils.isArray(val)){
			for(Object obj : Utils.toArray(val)){
				results.add(obj);
			}
		}else if(val instanceof Map){
			Object merge = MapHelper.readValue(val,"merge");
			Object on = MapHelper.readValue(val,"on");
			Object filter = ((Map)val).get("filter");
			
			boolean isOn = false;
			boolean isFilter = false;
			if(on instanceof String && !StringUtil.isEmpty((String)on)){
				isOn = true;
			}else if(filter instanceof String && !StringUtil.isEmpty((String)filter)){
				isFilter = true;
			}
			
			for(Object obj : Utils.toArray(merge)){
				
				Map newParams = new HashMap();
				newParams.put("merge",obj);
				
				boolean insert = true;
				for(Object oo:results){
					newParams.put("this",oo);
					if(isOn){
						insert = false;
						
						if(match((String)on,newParams)){
							insert = true;
							break;
						}
					}else if(isFilter){
						if(match((String)filter,newParams)){
							insert = false;
							break;
						}
					}
				}
				
				if(insert){
					results.add(obj);
				}
			}
		}
		
		return results;
	}
	
	static boolean match(String str,Map params){
		Object v = AE.execute(str, params);
		return v != null && (v instanceof Boolean) && (Boolean)v;
	}
}
