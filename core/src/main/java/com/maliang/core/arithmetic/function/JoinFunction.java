package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.node.FunctionNode;
import com.maliang.core.arithmetic.node.Node;
import com.maliang.core.arithmetic.node.Parentheses;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

public class JoinFunction {
	public static void main(String[] args) {
		String s = "{i1:[{w:1,n:11},{w:1,n:100},{w:1,n:200},{w:2,n:2},{w:3,n:3}],i2:[{w:1,n:4},{w:2,n:5},{w:3,n:6}]}";
		
		Map params = (Map)AE.execute(s);
		
		s = "{list:i1.join({right:i2,on:'left.w=right.w'})}";
		params = (Map)AE.execute(s,params);
		
		System.out.println(params);
		
		//s = "list.query(isNull(right.n)|left.n>right.n)";
		s = "list.group({n:this.left.n-this.right.n,id:this.left.w})";
		Object v = AE.execute(s,params);
		
		System.out.println("v : " + v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object left = null;
		if(function.useKeyValue()){
			left = function.getKeyValue();
		}
		
		Object right = null;
		String on = null;
		Object val = function.executeExpression(params);
		if(val != null && val instanceof Map){
			Map map = (Map)val;
			
			if(left == null){
				left = map.get("left");
			}
			right = map.get("right");
			on = (String)map.get("on");
		}
		
		if(left == null){
			return null;
		}
		
		List result = new ArrayList();
		Object joins = right;
		if(Utils.isArray(left)){
			for(Object obj:Utils.toArray(left)){
				Map join = new HashMap();
				join.put("left",obj);
				
				Map newParams = new HashMap();
				newParams.putAll(params);
				newParams.put("left",obj);
				
				if(Utils.isArray(joins)){
					Object[] temp = Utils.toArray(joins);
					for(int i = 0; i < temp.length; i++){
						Object robj = temp[i];
						newParams.put("right", robj);
						
						Object match = AE.execute(on, newParams);
						if(match != null && match instanceof Boolean && (Boolean)match){
							join.put("right",robj);
							
							joins = new Object[temp.length -1];
							for(int ii = 0; ii < temp.length; ii++){
								if(ii == i)continue;
								
								int idx = ii;
								if(ii > i){
									idx = ii-1;
								}
								((Object[])joins)[idx] = temp[ii];
							}
							break;
						}
					}
				}else {
					newParams.put("right", joins);
					
					Object match = AE.execute(on, newParams);
					if(match != null && match instanceof Boolean && (Boolean)match){
						join.put("right",joins);
						joins = null;
					}
				}
				
				result.add(join);
			}
		}
		
		return result;
	}
}
