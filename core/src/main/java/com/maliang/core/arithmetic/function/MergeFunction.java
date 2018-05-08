package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ComparatorUtils;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class MergeFunction {
	public static void main(String[] args) {
		String s = "{ps:[{product:'111',num:1},{product:'222',num:2}],"
					+ "merge:[{product:'333',num:3},{product:'444',num:4},{product:'111',num:5,name:'feadasf'}]}";
		
		s = "{ps:[{product:'111',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]},"
					+ "{product:'333',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}],"
				+ "merge:[{product:'333',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}]}";
		
//		Object v = AE.execute(s);
//		
//		s = "ps.merge({merge:merge,on:'this.product=merge.product'})";
//		
//		v = AE.execute(s,(Map)v);
//		System.out.println("v : " + v);
//		
//		v = MapHelper.expand(Utils.toArray(v), "stores".split("\\."));
//		for(Object oo : (Object[])v){
//			System.out.println(oo);
//		}
		
		
		s = "{o1:{name:'尺寸', items:'S,M', id:'5ae7f57d9f7b0310c270eb59'},o2:{name:'大小', items:'S,M', id:'FDAFDSAFSDA'}}";
		s = "{ps:[{spec:'S,黑',price:100,weight:300},{spec:'M,黑',price:100},{spec:'S,红',price:100},{spec:'M,红'}],l:[{spec:'S,黑',id:'5ae7f57d9f7b0310c270eb59',price:100,store:20},{spec:'M,黑',price:100,store:20}]}";
		Object v = AE.execute(s,null);
		
		System.out.println(" v : " + v);
		s = "[ps.merge({merge:l,id:['spec','price']})]";
		//s = "[ps.find(spec='S,黑')]";
		Object o2 = AE.execute(s,(Map<String,Object>)v);
		
		System.out.println("ps : " + ((Map<String,Object>)v).get("ps"));
		//System.out.println("o2 : " + o2);
		//System.out.println("o2 : " + o2);
//		s = "{ps:{product:'aaa'},merge:{product:'333',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}}";
//	
//		v = AE.execute(s);{name:'大小', items:['X','L'], id:'FDAFDSAFSDA'}
//		
//		s = "ps.merge({merge:merge})";O
//		
//		v = AE.execute(s,(Map)v);
//		System.out.println("----- v : " + v);
		
		String sss = "S,黑";
		int i = sss.indexOf("S");
		System.out.println(" i : " + i);
		
		List ls = new ArrayList();
		ls.add(0);
		ls.add(8);

		boolean is = Utils.isIntegers("");
		
		System.out.println("is : " + is);
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
	
	public static Object merge(Function function,Map<String,Object> params){
		Object kv = function.getKeyValue();
		Object ev = function.executeExpression(params);

		Object merge = readMergeValue(ev);
		Object id = readIDValue(ev);
		Object append = readValue(ev,"append");
		
		doMerge(merge,kv,id,append);
		
		return kv;
	}
	
	private static void doMerge(Object from,Object to,Object id,Object append) {
		boolean canAppend = canAppend(append);
		if(to instanceof List) {
			if(from instanceof Map) {
				boolean merged = false;
				for(Object o : (List)to) {
					if(o instanceof Map) {
						if(matchID((Map)o,(Map)from,id)) {
							((Map)o).putAll((Map)from);
							merged = true;
						}
					}
				}

				if(!merged && canAppend) {
					((List)to).add(from);
				}
			}else if(from instanceof List) {
				for(Object mo : (List)from) {
					doMerge(mo,to,id,append);
				}
			}else {
				if(canAppend) {
					((List)to).add(from);					
				}
			}
		}else if(to instanceof Map) {
			if(from instanceof Map) {
				if(matchID((Map)to,(Map)from,id)) {
					((Map)to).putAll((Map)from);
				}
			}
		}
	}
	
	private static boolean canAppend(Object append) {
		if(append == null) {
			return false;
		}
		
		if(append instanceof Boolean) {
			return (Boolean)append;
		}
		return false;
	}
	
	private static boolean matchID(Map m1,Map m2,Object id) {
		if(Utils.isEmpty(m1)|| Utils.isEmpty(m2)) {
			return false;
		}
		
		if(Utils.isEmpty(id)) {
			return true;
		}
		
		if(id instanceof List) {
			boolean match = false;
			for(Object oi : (List)id) {
				match = matchID(m1,m2,oi);
				
				if(!match) {
					return false;
				}
			}
			
			return true;
		}else {
			Object v1 = ((Map)m1).get(id);
			Object v2 = ((Map)m2).get(id);
			
			return v1 != null && v1.equals(v2);
		}
	}
	
	public static Object readMergeValue(Object val) {
		Object v = MapHelper.readValue(val,"merge");
		
		if(v == null) {
			return val;
		}
		return v;
	}
	
	public static Object readIDValue(Object val) {
		return readValue(val,"id");
	}
	
	public static Object readValue(Object val,String key) {
		if(val != null && val instanceof Map) {
			if(((Map)val).containsKey("merge")) {
				return ((Map)val).get(key);
			}
		}
		
		return null;
	}
	
	public static Object mapMerge(Map sMap,Map merge){
		if(Utils.isEmpty(sMap) || Utils.isEmpty(merge)) {
			return sMap;
		}
		
		sMap.putAll(merge);
		return sMap;
	}
	
	
	static boolean match(String str,Map params){
		Object v = AE.execute(str, params);
		return v != null && (v instanceof Boolean) && (Boolean)v;
	}
}
