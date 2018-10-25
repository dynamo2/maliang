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
		
		List<Object> exp = MapHelper.expand(Utils.toList(params.get("orders")),"items.disributeItems".split("\\."));
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
		
		
		s = "addToParams({list:[['XS','S','M','L'],['红','白','黑','灰'],['小鸟','国字','花草'],['男','女'],['毫米','厘米','分米','米']],list:list.regroup('all')})";
		//s = "addToParams({list:['S',['红','黑'],['刺绣','印花'],'男'],list:list.regroup(1)})";
		
		//s = "[['XS','S','M','L'],['红','白','黑','灰'],['小鸟','国字','花草'],['男','女'],['毫米','厘米','分米','米'],['毫克','克','千克']]";
		//s = "[['XS','S','M','L'],['红','白','黑','灰'],['小鸟','国字','花草']]";
		
		s = "addToParams({list:['S','M','红','黑'],has:list.has('黑')})";
		
		s = "addToParams({list:[['A','B'],['C','D'],['E','F'],['G','H'],['I','J']],list:list.regroup(3)})";
		
		Object val = AE.execute(s);
		System.out.println("---- val : " + val);
		
//		List list = Utils.toList(val);
//		int num = 2;
//		
//		System.out.println("---- list : " + list);
//		
//		List result = new ArrayList();
//		for(int in = 1; in <= list.size();in++) {
//			List rd = regroupList(list,in);
//
//			if(in == 1) {
//				for(Object ro : rd) {
//					result.addAll(Utils.toList(ro));
//				}
//			}else {
//				for(Object ro : rd) {
//					List dro = acrossRegroup(Utils.toList(ro));
//					result.addAll(dro);
//				}
//			}
//		}
//		
		for(Object oo : (List)((Map)val).get("list")) {
			//System.out.println("----- oo : " + oo);
		}
		
		System.out.println("ok");
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
		Object value = function.executeExpression(params);
		
		List<Object> list = Utils.toList(val);
		if(value != null) {
			if(value instanceof Integer) {
				int num = (Integer)value;
				List<Object> result = new ArrayList<Object>();
				List<Object> rd = regroupList(list,num);
				for(Object ro : rd) {
					if(num == 1) {
						result.addAll(Utils.toList(ro));
					}else {
						result.addAll(acrossRegroup(Utils.toList(ro)));
					}
				}
				return result;
			}else if("all".equalsIgnoreCase(value.toString())){
				List<Object> result = new ArrayList<Object>();
				for(int in = 1; in <= list.size();in++) {
					List<Object> rd = regroupList(list,in);

					if(in == 1) {
						for(Object ro : rd) {
							result.addAll(Utils.toList(ro));
						}
					}else {
						for(Object ro : rd) {
							List<Object> dro = acrossRegroup(Utils.toList(ro));
							result.addAll(dro);
						}
					}
				}
				return result;
			}
		}
		
		return acrossRegroup(list);
	}
	
	/**
	 * 将list里的数据以num个数为一组，不重复的自由分配组合
	 * 如：list : [['S','M'],['黑','红'],['大','小']]
	 *    num：2
	 *    return：[
	 *    			[['S','M'],['黑','红']],
	 *    			[['S','M'],['大','小']],
	 *    			[['黑','红'],['大','小']]
	 *    		  ]
	 * ***/
	private static List<Object> regroupList(List<Object> list , int num) {
		if(Utils.isEmpty(list)) {
			return list;
		}
		
		if(num == 1) {
			return list;
		}
		
		List<Object> result = new ArrayList<Object>();
		if(num >= list.size()) {
			result.add(list);
			return result;
		}

		for(int i = 0; i < list.size(); i++) {
			
			if(i+1 > list.size()-1) {
				break;
			}
			
			List<Object> nl = regroupList(list.subList(i+1,list.size()),num-1);
			for(Object o : nl) {
				List<Object> temp = new ArrayList<Object>();
				temp.add(list.get(i));
				
				if(num-1 <= 1) {
					temp.add(o);
				}else {
					temp.addAll(Utils.toList(o));
				}
				
				result.add(temp);
			}
		}
		return result;
	}
	
	
	/***
	 * 将List里的数据交叉重组
	 * List：[['S','M'],['黑','白'],['男','女']]
	 * return：[
	 * 			['S','黑','男'],
	 * 			['S','黑','女'],
	 * 			['S','白','男'],
	 * 			['S','白','女'],
	 * 			['M','黑','男'],
	 * 			['M','黑','女'],
	 * 			['M','白','男'],
	 * 			['M','白','女']
	 * 		]
	 * **/
	public static List<Object> acrossRegroup(List<Object> list) {
		if(Utils.isEmpty(list)) {
			return list;
		}
		
		List<Object> results = Utils.toList(list.get(0));
		
		for(int i = 1; i < list.size(); i++) {
			List<Object> ls = Utils.toList(list.get(i));
			
			List<Object> temp = new ArrayList<Object>();
			for(Object o : results) {
				if(Utils.isEmpty(o)) {
					continue;
				}
				
				for(Object oo : ls) {
					if(Utils.isEmpty(oo)) {
						continue;
					}
					List<Object> lo = new ArrayList<Object>();
					if(!(o instanceof List)) {
						lo.add(o);
					}else {
						lo.addAll((List<Object>)o);
					}
					lo.add(oo);
					temp.add(lo);
				}
			}
			results = temp;
		}
		
		return results;
	}
	
	private static List<Object> rootDatas(Function fun,Map<String,Object> params){
		String[] names = fun.getKeySource().split("\\.");
		String rootName = names[0];
		Object rootObj = MapHelper.readValue(params,rootName);
		
		rootObj = Utils.toList(rootObj);

		if(names.length > 2){
			rootObj = MapHelper.expand((List<Object>)rootObj,Arrays.copyOfRange(names, 1, names.length-1));
		}
		
		return (List<Object>)rootObj;
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
