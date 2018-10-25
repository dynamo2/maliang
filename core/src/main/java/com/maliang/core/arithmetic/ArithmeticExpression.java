package com.maliang.core.arithmetic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maliang.core.arithmetic.function.Function;
import com.maliang.core.arithmetic.node.Node;
import com.maliang.core.arithmetic.node.Parentheses;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;

public class ArithmeticExpression {
	public static Object execute(String expre){
		return execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		if(StringUtil.isEmpty(expre))return null;
		
		if(params == null) params = new HashMap<String,Object>();
		expre = expre.trim();
		Object value = null;
//		if(!expre.endsWith(")") && !expre.endsWith("]") && !expre.endsWith("}")) {
//			value = MapHelper.readValue(params, expre);
//		}

		if(value == null){
			Parentheses pt = Parentheses.compile(expre, 0);
			value = pt.getValue(params);
		}
		
		return value;
	}
	
	public static void main(String[] args) {
		//testComparion();
		//testMath();
		
		String form = "{type:'form',action:'',name:'product.edit.form',"
				+ "inputs:[{name:'product.id',type:'hidden',value:product.id},"
					+ "{name:'product.name',type:'text',value:product.name},"
					+ "{name:'product.brand',type:'select',value:product.brand,"
						+ "options:each(brands){key:this.id,value:this.name}},"
					+ "{name:'product.price',type:'double',value:product.price},"
					+ "{name:'product.expiry_date',type:'date',value:product.expiry_date},"
					+ "{name:'product.picture',type:'file',value:product.picture}]}";
		
		String s = "{name:'product.price',type:'date',options:each(brands){key:this.id,value:this.name}+2,action:''}";
		Parentheses pt = null;//Parentheses.compile(s,42,new char[]{',','}'});
		
		String ps = "{i1:{i11:{i111:{i1111:33333}}}}";
		Map pars = (Map)ArithmeticExpression.execute(ps,null);
		s = "i1.i11.i111.i1111.string().int().double().string().int().between([1,2222222])";
		s = "{i1:{i11:{i111:{i1111:33333}}}}.i1.i11";
		s = "[1,2,3,4].size";
		s = "'dfadfadsf'.length+88";
		pt = Parentheses.compile(s,0,new char[]{',','}'});
		Object v = null;//ArithmeticExpression.execute(s,pars);
		
		System.out.println(pt.getValue(pars));
		System.out.println(v);
	}
	public static void testComparion(){
		String str = "9+8 > 13 & 9 > 12";
		
		Object v = execute(str,null);
		System.out.println("v = " + v);
	}
	
	public static void testMath() {
		String source = "   D20031130 13:34:23 ";
		Parentheses pt = Parentheses.compile(source,0);
		
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		System.out.println(df.format(pt.getValue(null)));
		
		pt = Parentheses.compile("'dafdsaf'+'fdasfad'", 0);
		System.out.println(pt.getValue(null));
		
		pt = Parentheses.compile("  2 + 3*(  3  +  2  *  (  8  -  6  )  )	  * 	 (  8  -  4  )  +  		8-98+72/(3*3)", 0);
		System.out.println(pt.getValue(null));
		
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Object> goods = new HashMap<String,Object>();
		Map<String,Object> user = new HashMap<String,Object>();
		Map<String,Object> address = new HashMap<String,Object>();
		goods.put("num",2);
		goods.put("price", 3);
		
		user.put("age",8);
		user.put("address", address);
		
		address.put("no",6);
		
		params.put("goods",goods);
		params.put("user", user);
		
		pt = Parentheses.compile("goods.num+goods.price*(3+2*(user.age-user.address.no))*(8-4)+8-98+72/(3*3)", 0);
		System.out.println("params:"+pt.getValue(params));
		
		//{name $eq 'wmx'} $and {age $eq 10}
		String s = "{name $eq (goods.create_date+1m)} $and {age $eq (goods.num+goods.price*(3+2*(user.age-user.address.no))*(8-4)+8-98+72/(3*3))}";
		pt = Parentheses.compile(s,10);
		
		System.out.println(pt);
		System.out.println("age eq:"+Parentheses.compile(s, 49).getValue(params));
		//System.out.println("age eq:"+Parentheses.compile(s, 49).expressionStr());
		System.out.println("pt : " + pt.expressionStr());
		
		params = new HashMap<String,Object>();
		goods = new HashMap<String,Object>();
		try {
			goods.put("create_date", df.parseObject("2015年03月31日 14时04分32秒"));
			params.put("goods",goods);
			pt = Parentheses.compile("goods.create_date+1m", 0);
			//System.out.println("goods.create_date+1y:"+df.format(pt.getValue(params)));
			
			System.out.println("name eq:" + df.format(Parentheses.compile(s, 10).getValue(params)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void testFunction() {
		String str = "sum(each(items){this.price*this.num})";
		
		List<Object> ds = new ArrayList<Object>();
		ds.add(9d);
		ds.add(78d);
		ds.add(9);
		//ds.add("fdaf");
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("datas", ds);
		
		List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
		Map<String,Object> item = new HashMap<String,Object>();
		items.add(item);
		item.put("price", 2d);
		item.put("num", 2);
		
		item = new HashMap<String,Object>();
		items.add(item);
		item.put("price", 3d);
		item.put("num", 3);
		
		params.put("items", items);
		
		Object[] datas = new Object[]{1,2};
		params.put("datas", datas);
		
		str = "items(0)";
		Object v = ArithmeticExpression.execute(str, params);
		System.out.println(v);
		
		v = ArithmeticExpression.execute("datas(1)", params);
		System.out.println(v);
	}
	
	
	
	/*
	 * 
	 * 三元表达式：?:
	 * example: i>2?3:4
	static class TernaryExpression extends Node {
		Node condition;
		Node first;
		Node second;
		int endIndex;
		
		TernaryExpression(Node cond,String source,int idx){
			this.condition = cond;
			read(source,idx);
		}
		
		int getEndIndex(){
			return this.endIndex;
		}
		
		private void read(String source,int sidx){
			Parentheses pt = Parentheses.compile(source, sidx, new char[':']);
			this.first = pt.getExpression();
			
			pt = Parentheses.compile(source, pt.getEndIndex(), null);
			this.second = pt.getExpression();
			
			this.endIndex = pt.getEndIndex();
		}
		
		public String toString(){
			return new StringBuffer().append(this.condition).append('?')
						.append(this.first).append(':')
						.append(this.second).toString();
		}
	}*/

	static class DotNode extends Node {
		Node preNode;
		String key;
	}
	
	
}
