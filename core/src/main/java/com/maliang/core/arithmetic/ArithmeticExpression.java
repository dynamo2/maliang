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
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;

public class ArithmeticExpression {
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
	
	public static Object execute(String expre){
		return execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		if(StringUtil.isEmpty(expre))return null;
		
		if(params == null) params = new HashMap<String,Object>();
		Object value = MapHelper.readValue(params, expre);
		if(value == null){
			Parentheses pt = Parentheses.compile(expre, 0);
			value = pt.getValue(params);
		}
		
		return value;
	}
	
	static abstract class Node {
		
		public Object getValue(Map<String,Object> paramsMap){
			return null;
		}
		
		public Object getValue(Object obj){
			return null;
		}
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
	
	static class FunctionNode extends Node {
		private Function function;
		
		public FunctionNode(Function fun){
			this.function = fun;
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			return this.function.execute(paramsMap);
		}
	}
	
	static class DotNode extends Node {
		Node preNode;
		String key;
	}
	
	public static class Parentheses extends Node{
		private final String source;
		private final int startIndex;
		private int endIndex;
		private Node expression;
		
		public Parentheses(String source,int index){
			this.source = source;
			this.startIndex = index;
		}
		
		public String toString(){
			return this.expressionStr();
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			Object value = expression.getValue(paramsMap);
			if(value instanceof MapNode){
				return ((MapNode)value).getValue(paramsMap);
			}
			return value;
		}
		
		public String expressionStr(){
			return source.substring(this.startIndex,this.endIndex+1);
		}
		
		public static Parentheses compile(String source,int index) {
			return compile(source,index,null);
		}
		
		static class Compiler {
			private String source;
			private int startIndex;
			private char[] endChars;
			private String[] sary;
			private int arrayIndex = -1;
			private StringBuffer sb = null;
			Map<Node,Set<Integer>> expreMap = new HashMap<Node,Set<Integer>>();
			List<Operator> priorityOpts = new ArrayList<Operator>();
			private int cursor;
			
			Compiler(String str,int idx,char[] eds){
				this.source = str;
				this.startIndex = idx;
				this.endChars = eds;
				
				sary = new String[source.length()-this.startIndex];
			}
			
			private String readSbf(){
				if(sb == null)return null;
				
				return sb.toString().trim();
			}
			
			public Parentheses compile() {
				Parentheses parentheses = new Parentheses(source,this.startIndex);
				if(source.charAt(this.startIndex) == '('){
					this.startIndex++;
				}

				this.cursor = this.startIndex;
				for(;this.cursor< source.length(); this.cursor++){
					char ch = readChar();
					
					if(Operator.isOperator(ch)){
						addOperator();
						continue;
					}
					
					if(ch == '('){
						if(sb != null){
							String key = readSbf();
							if(isFunctionKey(key)){
								readFunction(key);
								continue;
							}
						}
						
						readParentheses();
						continue;
					}
					
					if(ch == '{'){
						readFunction('{','}');
						continue;
					}
					
					if(ch == '['){
						readFunction('[',']');
						continue;
					}
					
					if(ch == '\''){
						readString();
						continue;
					}
					
					if(ch == ')' ){
						parentheses.endIndex = this.cursor;
						break;
					}
					
					if(isEndChar(ch)){
						parentheses.endIndex = this.cursor-1;
						break;
					}
					
					if(sb == null){
						sb = new StringBuffer(ch);
					}
					sb.append(ch);
				}
				
				if(sb != null){
					sary[++arrayIndex] = readSbf();
				}
				
				parentheses.expression = buildExpression(priorityOpts,expreMap,sary);
				return parentheses;
			}
			
			private boolean isFunctionKey(String key){
				return !key.isEmpty() && !Operator.isOperator(key);
			}
			
			private boolean isEndChar(char ch){
				if(endChars != null && endChars.length > 0){
					for(char ec : endChars){
						if(ch == ec){
							return true;
						}
					}
				}
				
				return false;
			}
			
			private char readChar(){
				return source.charAt(this.cursor);
			}
			
			private void readString(){
				Substring sbs = new Substring(source,'\'',this.cursor);
				if(sb == null){
					sb = new StringBuffer();
				}
				sb.append(sbs.getCompleteContent());
				this.cursor = sbs.getEndIndex();
			}
			
			private void addOperator(){
				if(sb != null){
					String k = readSbf();
					if(!k.isEmpty()){
						sary[++arrayIndex] = k;
					}
					sb = null;
				}
				
				Operator opt = new Operator(source,this.cursor);
				sary[++arrayIndex] = opt.getOperatorKey();
				opt.setArrayIndex(arrayIndex);
				priorityOpts.add(opt);
				this.cursor = opt.getEndIndex();
			}
			
			private void readFunction(char startChar,char endChar){
				Substring sbs = new Substring(source,startChar,endChar,this.cursor);
				Function fun = new Function(sbs.getCompleteContent());
				addFunctionNode(fun);
				
				this.cursor = sbs.getEndIndex();
				sb = null;
			}
			
			private void readFunction(String key){
				Function fun = new Function(key,source,this.cursor);
				addFunctionNode(fun);

				this.cursor = fun.getEndIndex();
				sb = null;
			}

			private void addFunctionNode(Function fun){
				sary[++arrayIndex] = fun.toString();
				Set<Integer> indexSet = new HashSet<Integer>();
				indexSet.add(arrayIndex);
				expreMap.put(new FunctionNode(fun), indexSet);
			}
			
			private void readParentheses(){
				Parentheses pt = new Compiler(source,this.cursor,this.endChars).compile();
				this.cursor = pt.endIndex;
				sary[++arrayIndex] = pt.expressionStr();
				Set<Integer> indexSet = new HashSet<Integer>();
				indexSet.add(arrayIndex);
				
				expreMap.put(pt.expression, indexSet);
			}
		}
		
		public static Parentheses compile(String source,int index,char[] endChars) {
			return new Compiler(source,index,endChars).compile();
		}
		
		private static Node buildExpression(List<Operator> priorityOpts,Map<Node,Set<Integer>> expreMap,String[] expreArray){
			if(priorityOpts.isEmpty() && expreArray[0] != null){
				if(expreMap.size() > 0){
					for(Map.Entry<Node, Set<Integer>> entry:expreMap.entrySet()){
						return entry.getKey();
					}
				}
				
				return new Operand(expreArray[0]);
			}
			
			Collections.sort(priorityOpts);
			Expression root = null;
			for(Operator op:priorityOpts){
				int opIndex = op.getArrayIndex();
				
				int preIndex = opIndex-1;
				int nextIndex = opIndex+1;
				
				Node preNode = getExpression(expreMap,preIndex);
				Node nextNode = getExpression(expreMap,nextIndex);
				
				if(preNode == null){
					preNode = new Operand(expreArray[preIndex]);
				}
				
				if(nextNode == null){
					nextNode = new Operand(expreArray[nextIndex]);
				}
				
				root = new Expression();
				root.setLeft(preNode);
				root.setRight(nextNode);
				root.setOperator(op);
				
				Set<Integer> indexSet = new HashSet<Integer>();
				if(expreMap.containsKey(preNode)){
					indexSet.addAll(expreMap.remove(preNode));
				}else {
					indexSet.add(preIndex);
				}
				
				if(expreMap.containsKey(nextNode)){
					indexSet.addAll(expreMap.remove(nextNode));
				}else {
					indexSet.add(nextIndex);
				}
				indexSet.add(op.getArrayIndex());
				
				expreMap.put(root, indexSet);
			}

			return root;
		}
		
		private static Node getExpression(Map<Node,Set<Integer>> expreMap,int index){
			if(expreMap == null || expreMap.isEmpty()){
				return null;
			}
			
			for(Map.Entry<Node, Set<Integer>> entry : expreMap.entrySet()){
				for(Integer i:entry.getValue()){
					if(i == index){
						return entry.getKey();
					}
				}
			}
			
			return null;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}

		public Node getExpression() {
			return expression;
		}

		public void setExpression(Node expression) {
			this.expression = expression;
		}

		public String getSource() {
			return source;
		}

		public int getStartIndex() {
			return startIndex;
		}
	}
	
	static class Expression extends Node {
		private Node left;
		private Node right;
		private Operator operator;
		
		public Node getLeft() {
			return left;
		}
		public void setLeft(Node left) {
			this.left = left;
		}
		public Node getRight() {
			return right;
		}
		public void setRight(Node right) {
			this.right = right;
		}
		public Operator getOperator() {
			return operator;
		}
		public void setOperator(Operator operator) {
			this.operator = operator;
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			if(this.right == null){
				return singleExpression(paramsMap);
			}

			if(this.operator.isDot()){
				return calculateDot(paramsMap);
			}
			
			Object valueLeft = this.left.getValue(paramsMap);
			Object valueRight = this.right.getValue(paramsMap);
			if(valueLeft instanceof MapNode){
				valueLeft = ((MapNode)valueLeft).getValue(paramsMap);
			}
			if(valueRight instanceof MapNode){
				valueRight = ((MapNode)valueRight).getValue(paramsMap);
			}
			
			if(this.operator.isLogical()){
				return LogicalCalculator.calculate(valueLeft, valueRight, operator);
			}
			
			if(this.operator.isComparison()){
				return CompareCalculator.calculate(valueLeft, valueRight, operator);
			}
			
			if(valueLeft instanceof Date){
				return DateCalculator.calculate((Date)valueLeft,valueRight.toString(),this.operator);
			}
			
			if(valueRight instanceof Date){
				return DateCalculator.calculate((Date)valueRight,valueLeft.toString(),this.operator);
			}
			
			if(valueLeft instanceof List){
				List list = (List)valueLeft;
				if(valueRight == null){
					return list;
				}else if(valueRight instanceof List){
					if(!this.operator.isPlus()){
						throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
					}
					
					list.addAll((List)valueRight);
					return list;
				}else {
					return calculateString(valueLeft.toString(),valueRight.toString());
				}
			}
			
			if(valueLeft instanceof Map){
				Map map = (Map)valueLeft;
				if(valueRight == null){
					return map;
				}else if(valueRight instanceof Map){
					if(!this.operator.isPlus()){
						throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
					}
					
					map.putAll((Map)valueRight);
					return map;
				}else {
					return calculateString(valueLeft.toString(),valueRight.toString());
				}
			}
			
			if(valueRight instanceof List){
				if(valueLeft == null){
					return (List)valueRight;
				}
			}
			
			if(valueRight instanceof Map){
				if(valueLeft == null){
					return (Map)valueRight;
				}
			}

			if(valueLeft instanceof String || valueRight instanceof String){
				return calculateString(valueLeft==null?null:valueLeft.toString(),valueRight==null?null:valueRight.toString());
			}

			if(valueLeft instanceof Integer && valueRight instanceof Integer){
				return IntegerCalculator.calculate((Integer)valueLeft,(Integer)valueRight,this.operator);
			}

			double dl = ((Number)valueLeft).doubleValue();
			double dr = ((Number)valueRight).doubleValue();
			return DoubleCalculator.calculate(dl,dr,this.operator);
		}
		
		/**
		 * 单一操作数的计算
		 * **/
		private Object singleExpression(Map<String,Object> paramsMap){
			Object leftV = this.left.getValue(paramsMap);
			if(this.operator.isAnd()){
				return false;
			}else if(this.operator.isOr()){
				return LogicalCalculator.getBoolean(leftV);
			}
			
			return leftV;
		}
		
		/**
		 * '.'操作符的运算
		 * **/
		private Object calculateDot(Map<String,Object> paramsMap){
			Object valueLeft = this.left.getValue(paramsMap);
			Object valueRight = null;
			
			if(valueLeft instanceof MapNode){
				valueLeft = ((MapNode)valueLeft).getValue(paramsMap);
			}
			
			if(this.right instanceof Operand){
				valueRight = this.right.getValue(paramsMap);
				
				if(valueRight instanceof MapNode){
					return ((MapNode)valueRight).getValue(valueLeft);
				}
				
				return null;
			}
			if(this.right instanceof FunctionNode){
				((FunctionNode)this.right).function.setKeyValue(valueLeft);
				return this.right.getValue(paramsMap);
			}
			
			return null;
		}
		
		
		private String calculateString(String left,String right){
			if(!this.operator.isPlus()){
				throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
			}
			
			return left+right;
		}
		
		public String toString(){
			return "("+this.left+this.operator+this.right+")";
			//return ""+this.left+this.operator+this.right;
		}
	}
	
	static class MapNode extends Node {
		String key;
		
		MapNode(String k){
			this.key = k;
		}
		
		public Object getValue(Object obj){
			return MapHelper.readValue(obj,key);
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			return this.getValue((Object)paramsMap);
		}
	}
	
	static class Operand extends Node {
		private String operand;
		
		public Operand(String op){
			if(op != null){
				this.operand = op.trim();
			}
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			if(this.operand.equalsIgnoreCase("true")){
				return true;
			}
			
			if(this.operand.equalsIgnoreCase("false")){
				return false;
			}
			
			if(this.isString()){
				return this.operand.subSequence(1,this.operand.length()-1);
			}
			
			if(this.operand.startsWith("D")){
				Date date = DateCalculator.readDate(this.operand);
				if(date != null){
					return date;
				}
			}
			
			if(DateCalculator.isDateIncrement(this.operand)){
				return this.operand;
			}
			
			try {
				return new Integer(this.operand);
			}catch(NumberFormatException e){
				try {
					return new Double(this.operand);
				}catch(NumberFormatException e1){}
			}
			
			return new MapNode(this.operand);
			//return MapHelper.readValue(paramsMap,this.operand);
		}
		
		public boolean isString(){
			return this.operand.startsWith("'") && this.operand.endsWith("'");
		}
		
		public String toString(){
			return this.operand;
		}
	}
}
