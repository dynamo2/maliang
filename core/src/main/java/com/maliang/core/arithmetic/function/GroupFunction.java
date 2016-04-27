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

public class GroupFunction {
	public static void main(String[] args) {
		String s = "{orders:[{id:1,price:1,num:11,date:'20160708',items:[{price:22,num:10,distributeItems:[{ware:1,num:1},{ware:2,num:2},{ware:3,num:3}]},{price:22,num:20},{price:22,num:45}]},"
						+ "{id:2,price:1,num:22,date:'20160808',items:[{price:11,num:10},{price:11,num:20},{price:11,num:45}]},"
						+ "{id:3,price:1,num:33,date:'20160808',items:[{price:77,num:10},{price:11,num:20},{price:11,num:45}]}"
					+ "]}";
		
		System.out.println("s : " + s);
		Map params = (Map)AE.execute(s);
		// s = "orders.group({totalPrice:sum(this.items.price*this.item.num),id:this.items.product})";
		// s = "orders.group({totalPrice:sum(this.items.sum(this.price*this.num))})";
		s = "orders.group({total:sum(this.items.sum(this.price*this.num)),count:count()})";
		//s = "orders.items.distributeItems.sum(this.num*this.ware)";
		
		//System.out.println("g : " + s);
		Object v = AE.execute(s,params);
		System.out.println("v : " + v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object[] datas = rootDatas(function,params);
		
		GroupCompiler compiler = new GroupCompiler(datas,function.getExpression());
		compiler.execute(params);
		
		return compiler.getGroupResult();
	}
	
	private static Object[] rootDatas(Function fun,Map<String,Object> params){
		String[] names = fun.getKeySource().split("\\.");
		String rootName = names[0];
		Object rootObj = MapHelper.readValue(params,rootName);
		if(!Utils.isArray(rootObj)){
			List l = new ArrayList();
			l.add(rootObj);
			rootObj = l;
		}
		if(names.length > 2){
			rootObj = MapHelper.expand((Object[])rootObj,Arrays.copyOfRange(names, 1, names.length-1));
		}
		
		if(rootObj instanceof Collection){
			rootObj = ((Collection)rootObj).toArray();
		}
		
		return (Object[])rootObj;
	}
	
	static class GroupCompiler {
		public static final String DEFAULT_ID = "ALL";

		private final String source;
		private String idExpression;
		private Object[] datas;
		private Map<String,Object> expressionMap;
		private Map<Object,Object> resultMap = new HashMap<Object,Object>();
		
		public GroupCompiler(Object[] datas,String source){
			this.datas = datas;
			this.source = source;
		}
		
		public void execute(Map<String,Object> params){
			init(params);
			
			for(String key : this.expressionMap.keySet()){
				String valExpre = (String)this.expressionMap.get(key);
				Parentheses pt = Parentheses.compile(valExpre,0);
				Node node = pt.getNode();
				if(node instanceof FunctionNode){
					this.getValue(params,((FunctionNode)node).getFunction(),key);
				}
			}
		}
		
		public Collection getGroupResult(){
			return this.resultMap.values();
		}
		
		private void init(Map<String,Object> params){
			MapCompiler compiler = new MapCompiler(this.source,1,params,false,false);
			this.expressionMap = compiler.getMap();
			
			this.idExpression = null;
			if(this.expressionMap.containsKey("id")){
				this.idExpression = (String)this.expressionMap.remove("id");
			}
		}

		public Object getValue(Map<String,Object> params,Function function,String resultKey){
			Object idVal = DEFAULT_ID;
			for(Object obj : datas){
				Map<String,Object> newParams = new HashMap<String,Object>();
				newParams.putAll(params);
				newParams.put("this",obj);
				
				idVal = AE.execute(idExpression,newParams);
				if(idVal == null)idVal = DEFAULT_ID;

				if(this.isSum(function)){
					sum(idVal,function,newParams,resultKey);
				}else if(this.isCount(function)){
					count(idVal,resultKey);
				}
			}

			return null;
		}
		
		private boolean isSum(Function fun){
			return this.isFunction(fun,"sum");
		}
		
		private boolean isCount(Function fun){
			return this.isFunction(fun,"count");
		}
		
		private boolean isFunction(Function fun,String name){
			return fun.getKey().equals(name);
		}		
		public void sum(Object idVal,Function fun,Map<String,Object> params,String resultKey){
			Object newVal = fun.executeExpression(params);
			setResult(idVal, Sum.sum(getResult(idVal,resultKey),newVal),resultKey);
		}
		
		public void count(Object idVal,String resultKey){
			setResult(idVal, Sum.sum(getResult(idVal,resultKey),1),resultKey);
		}
		
		public Object getResult(Object idVal,String resultKey){
			return getMatchedResult(idVal).get(resultKey);		
		}
		
		public void setResult(Object idVal,Object newVal,String resultKey){
			getMatchedResult(idVal).put(resultKey, newVal);
		}
		
		public Map getMatchedResult(Object idVal){
			Object matchedResult = resultMap.get(idVal.toString());
			if(matchedResult == null){
				matchedResult = new HashMap();
				resultMap.put(idVal.toString(), matchedResult);
				
				if(!DEFAULT_ID.equals(idVal)){
					((Map)matchedResult).put("id",idVal);
				}
			}
			return (Map)matchedResult;
		}
	}
}
