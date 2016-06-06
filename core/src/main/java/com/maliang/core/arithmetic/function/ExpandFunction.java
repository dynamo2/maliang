package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class ExpandFunction {
	public static void main(String[] args) {
		String s = "{ps:[{product:'111',num:1},{product:'222',num:2}],"
					+ "merge:[{product:'333',num:3},{product:'444',num:4},{product:'111',num:5,name:'feadasf'}]}";
		
		s = "{ps:[{product:'111',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]},"
					+ "{product:'222',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}],"
				+ "merge:[{product:'333',stores:[{warehouse:'蓝天园仓库',num:1000},{warehouse:'六合仓库',num:2000}]}]}";
		
		s = "{ws:db.Warehouse.search()}";
		Object v = AE.execute(s);
		
		s = "{ns:ws.expand('stores')}";
		v = AE.execute(s,(Map)v);
		System.out.println("v : " + v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		Object names = function.executeExpression(params);
		
		val = Utils.toArray(val);
		if(names instanceof String && !StringUtil.isEmpty((String)names)){
			val = MapHelper.expand((Object[])val, ((String)names).split("\\."));
		}
		
		List<Object> results = new ArrayList<Object>();
		for(Object o : (Object[])val){
			results.add(o);
		}

		return results;
	}
	
	static boolean match(String str,Map params){
		Object v = AE.execute(str, params);
		return v != null && (v instanceof Boolean) && (Boolean)v;
	}
}
