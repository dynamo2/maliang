package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.node.FunctionNode;
import com.maliang.core.arithmetic.node.MapNode;
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
		
		Object[] exp = MapHelper.expand(Utils.toArray(params.get("orders")),"items.disributeItems".split("\\."));
		for(Object obj:exp){
			System.out.println("== " + obj);
		}
		
		// s = "orders.group({totalPrice:sum(this.items.price*this.item.num),id:this.items.product})";
		// s = "orders.group({totalPrice:sum(this.items.sum(this.price*this.num))})";
		s = "orders.group({total:sum(this.items.sum(this.price*this.num)),count:count(),id:{date:this.date,price:this.price}})";
		//s = "orders.items.distributeItems.sum(this.num*this.ware)";
		
		//System.out.println("g : " + s);
		Object v = AE.execute(s,params);
		System.out.println("v : " + v);
		
		
		s = "addToParams({list:[['XS','S','M','L'],['红','白','黑','灰'],['小鸟','国字','花草'],['男','女']],list:list.regroup()})";
		s = "addToParams({list:['S',['红','黑'],['刺绣','印花'],'男'],list:list.regroup()})";
		
		Object val = AE.execute(s);
		System.out.println("---- val : " + val);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object val = function.getKeyValue();
		Object[] datas = Utils.toArray(val);
		//Object[] datas = rootDatas(function,params);
		
		if(datas == null)datas = new Object[0];
		
		GroupCompiler compiler = new GroupCompiler(datas,function.getExpression());
		compiler.execute(params);
		
		return compiler.getGroupResult();
	}
	
	public static Object regroup(Function function,Map<String,Object> params) {
		Object val = function.getKeyValue();
		List list = Utils.toList(val);
		List results = Utils.toList(list.get(0));
		
		for(int i = 1; i < list.size(); i++) {
			List ls = Utils.toList(list.get(i));
			
			List temp = new ArrayList();
			for(Object o : results) {
				if(Utils.isEmpty(o)) {
					continue;
				}
				
				for(Object oo : ls) {
					if(Utils.isEmpty(oo)) {
						continue;
					}
					List lo = new ArrayList();
					if(!(o instanceof List)) {
						lo.add(o);
					}else {
						lo.addAll((List)o);
					}
					lo.add(oo);
					temp.add(lo);
				}
			}
			results = temp;
		}
		
		return results;
	}
	
	private static Object[] rootDatas(Function fun,Map<String,Object> params){
		String[] names = fun.getKeySource().split("\\.");
		String rootName = names[0];
		Object rootObj = MapHelper.readValue(params,rootName);
		
		rootObj = Utils.toArray(rootObj);

		if(names.length > 2){
			rootObj = MapHelper.expand((Object[])rootObj,Arrays.copyOfRange(names, 1, names.length-1));
		}
		
		return Utils.toArray(rootObj);
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
				this.getValue(params,pt.getNode(),key);
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

		public Object getValue(Map<String,Object> params,Node node ,String resultKey){
			Object idVal = DEFAULT_ID;
			for(Object obj : datas){
				Map<String,Object> newParams = new HashMap<String,Object>();
				newParams.putAll(params);
				newParams.put("this",obj);
				
				idVal = AE.execute(idExpression,newParams);
				if(idVal == null)idVal = DEFAULT_ID;
				
				if(node instanceof FunctionNode){
					Function function = ((FunctionNode)node).getFunction();
					
					if(this.isSum(function)){
						sum(idVal,function,newParams,resultKey);
					}else if(this.isCount(function)){
						count(idVal,resultKey);
					}
				}else {
					Object newVal = node.getValue(newParams);
					if(newVal instanceof MapNode){
						newVal = ((MapNode)newVal).getValue(newParams);
					}
					setResult(idVal,newVal,resultKey);
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
