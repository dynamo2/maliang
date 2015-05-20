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
		Parentheses pt = Parentheses.compile(s,42,new char[]{',','}'});
		
		System.out.println(pt);
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
	
	public static Object execute(String expre,Map<String,Object> params){
		Object value = MapHelper.readValue(params, expre);
		if(value == null){
			Parentheses pt = Parentheses.compile(expre, 0);
			value = pt.getValue(params);
		}
		
		return value;
	}
	
	static class Node {
		public Object getValue(Map<String,Object> paramsMap){
			return null;
		}
	}
	
	static class FunctionNode extends Node {
		private Function function;
		
		public FunctionNode(Function fun){
			this.function = fun;
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			return this.function.execute(paramsMap);
		}
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
			return expression.getValue(paramsMap);
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
								addFunction(key);
								continue;
							}
						}
						
						addParentheses();
						continue;
					}
					
					if(ch == '{'){
						addFunction('{','}');
						continue;
					}
					
					if(ch == '\''){
						Substring sbs = new Substring(source,'\'',this.cursor);
						if(sb == null){
							sb = new StringBuffer();
						}
						sb.append(sbs.getCompleteContent());
						
						System.out.println(source);
						System.out.println("content: "+sbs.getCompleteContent());
						
						this.cursor = sbs.getEndIndex();
						continue;
					}
					
					if(ch == '['){
						addFunction('[',']');
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
			
			private void addFunction(char startChar,char endChar){
				Substring sbs = new Substring(source,startChar,endChar,this.cursor);
				Function fun = new Function(sbs.getCompleteContent());
				addFunctionNode(fun);
				
				this.cursor = sbs.getEndIndex();
				sb = null;
			}
			
			private void addFunction(String key){
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
			
			private void addParentheses(){
				Parentheses pt = new Compiler(source,this.cursor,this.endChars).compile();
				this.cursor = pt.endIndex;
				sary[++arrayIndex] = pt.expressionStr();
				Set<Integer> indexSet = new HashSet<Integer>();
				indexSet.add(arrayIndex);
				
				expreMap.put(pt.expression, indexSet);
			}
			
			private void addNode(Node node){
				sary[++arrayIndex] = node.toString();
				Set<Integer> indexSet = new HashSet<Integer>();
				indexSet.add(arrayIndex);
				expreMap.put(node, indexSet);
			}
		}
		
		public static Parentheses compile(String source,int index,char[] endChars) {
			return new Compiler(source,index,endChars).compile();
		}
		
		public static Parentheses compile2(String source,int index,char[] endChars) {
			Parentheses parentheses = new Parentheses(source,index);
			if(source.charAt(index) == '('){
				index++;
			}
			
			String[] sary = new String[source.length()-index];
			StringBuffer sb = null;
			int arrayIndex = -1;
			Map<Node,Set<Integer>> expreMap = new HashMap<Node,Set<Integer>>();
			Set<Integer> indexSet = null;
			List<Operator> priorityOpts = new ArrayList<Operator>();
			for(int i = index; i < source.length(); i++){
				char ch = source.charAt(i);
				
				if(Operator.isOperator(ch)){
					if(sb != null){
						if(!sb.toString().trim().isEmpty()){
							sary[++arrayIndex] = sb.toString().trim();
						}
						sb = null;
					}
					
					Operator opt = new Operator(source,i);
					sary[++arrayIndex] = opt.getOperatorKey();
					opt.setArrayIndex(arrayIndex);
					priorityOpts.add(opt);
					i = opt.getEndIndex();
					
					continue;
				}
				
				if(ch == '('){
					if(sb != null){
						String key = sb.toString().trim();
						if(!key.isEmpty() && !Operator.isOperator(key)){
							Function fun = new Function(key,source,i);
							
							sary[++arrayIndex] = fun.toString();
							indexSet = new HashSet<Integer>();
							indexSet.add(arrayIndex);
							expreMap.put(new FunctionNode(fun), indexSet);
							
							i = fun.getEndIndex();
							sb = null;
							continue;
						}
					}
					
					Parentheses pt = Parentheses.compile(source,i);
					i = pt.endIndex;
					sary[++arrayIndex] = pt.expressionStr();
					indexSet = new HashSet<Integer>();
					indexSet.add(arrayIndex);
					
					expreMap.put(pt.expression, indexSet);
					continue;
				}
				
				if(ch == '{'){
					Substring sbs = new Substring(source,'{','}',i);
					Function fun = new Function(sbs.getCompleteContent());
					
					sary[++arrayIndex] = fun.toString();
					indexSet = new HashSet<Integer>();
					indexSet.add(arrayIndex);
					expreMap.put(new FunctionNode(fun), indexSet);
					
					i = sbs.getEndIndex();
					sb = null;
					continue;
				}
				
				if(ch == '['){
					Substring sbs = new Substring(source,'[',']',i);
					Function fun = new Function(sbs.getCompleteContent());
					
					sary[++arrayIndex] = fun.toString();
					indexSet = new HashSet<Integer>();
					indexSet.add(arrayIndex);
					expreMap.put(new FunctionNode(fun), indexSet);
					
					i = sbs.getEndIndex();
					sb = null;
					continue;
				}
				
				if(ch == ')' ){
					parentheses.endIndex = i;
					break;
				}
				
				if(endChars != null && endChars.length > 0){
					boolean isBreak = false;
					for(char ec : endChars){
						if(ch == ec){
							parentheses.endIndex = i-1;
							isBreak = true;
							break;
						}
					}
					
					if(isBreak){
						break;
					}
				}
				
				if(sb == null){
					sb = new StringBuffer(ch);
				}
				sb.append(ch);
			}
			
			if(sb != null){
				sary[++arrayIndex] = sb.toString();
			}
			
			parentheses.expression = buildExpression(priorityOpts,expreMap,sary);
			return parentheses;
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
				Object leftV = this.left.getValue(paramsMap);
				if(this.operator.isAnd()){
					return false;
				}else if(this.operator.isOr()){
					return LogicalCalculator.getBoolean(leftV);
				}
				
				return leftV;
			}
			
			Object valueLeft = this.left.getValue(paramsMap);
			Object valueRight = this.right.getValue(paramsMap);
			
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
			
			if(valueLeft instanceof String || valueRight instanceof String){
				return calculateString(valueLeft.toString(),valueRight.toString());
			}

			if(valueLeft instanceof Integer && valueRight instanceof Integer){
				return IntegerCalculator.calculate((Integer)valueLeft,(Integer)valueRight,this.operator);
			}

			double dl = ((Number)valueLeft).doubleValue();
			double dr = ((Number)valueRight).doubleValue();
			return DoubleCalculator.calculate(dl,dr,this.operator);
		}
		
		
		private String calculateString(String left,String right){
			if(!this.operator.isPlus()){
				throw new RuntimeException("Error operator '"+this.operator.getOperatorKey()+"' for String");
			}
			
			return left+right;
		}
		
		public String toString(){
			return "("+this.left+this.operator+this.right+")";
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
			
			return MapHelper.readValue(paramsMap,this.operand);
		}
		
		public boolean isString(){
			return this.operand.startsWith("'") && this.operand.endsWith("'");
		}
		
		public String toString(){
			return this.operand;
		}
	}

	/*
	static class Node {
		public final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		
		public Object getValue(Map<String,Object> paramsMap){
			return null;
		}
		
		public static Date readDate(String source){
			if(source == null || source.isEmpty()){
				return null;
			}
			
			source = source.trim();
			if(source.startsWith("D")){
				try {
					String ds = source.substring(1,source.length());
					
					return dateFormat.parse(ds);
				} catch (ParseException e) {}
			}

			return null;
		}
		
		public static boolean isDateIncrement(String source){
			if(source == null || source.isEmpty()){
				return false;
			}
			
			source = source.trim();
			if(source.length() < 2){
				return false;
			}
			
			char lastChar = source.charAt(source.length()-1);
			if(lastChar == 'd' 
				|| lastChar == 'y'
				|| lastChar == 'm'
				|| lastChar == 'h'
				|| lastChar == 'i'
				|| lastChar == 's'){
				
				try {
					String str = source.substring(0, source.length()-1);
					new Integer(str);
					return true;
				}catch(Exception e){}
			}
			return false;
		}
	}
	
	static class FunctionNode extends Node {
		private Function function;
		
		public FunctionNode(Function fun){
			this.function = fun;
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			return this.function.execute(paramsMap);
		}
	}
	
	static class Parentheses extends Node{
		private final String source;
		private final int startIndex;
		private int endIndex;
		private Node expression;
		
		public Parentheses(String source,int index){
			this.source = source;
			this.startIndex = index;
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			return expression.getValue(paramsMap);
		}
		
		public String expressionStr(){
			return source.substring(this.startIndex,this.endIndex+1);
		}
		
		public static Parentheses compile(String source,int index) {
			Parentheses parentheses = new Parentheses(source,index);
			if(source.charAt(index) == '('){
				index++;
			}
			
			String[] sary = new String[source.length()-index];
			StringBuffer sb = null;
			int arrayIndex = -1;
			Map<Node,Set<Integer>> expreMap = new HashMap<Node,Set<Integer>>();
			Set<Integer> indexSet = null;
			List<Operator> priorityOpts = new ArrayList<Operator>();
			for(int i = index; i < source.length(); i++){
				char ch = source.charAt(i);
				
				if(Operator.isOperator(ch)){
					if(sb != null){
						if(!sb.toString().trim().isEmpty()){
							sary[++arrayIndex] = sb.toString().trim();
						}
						sb = null;
					}
					
					sary[++arrayIndex] = ch+"";
					priorityOpts.add(new Operator(ch,arrayIndex));
					
					continue;
				}
				
				if(ch == '('){
					if(sb != null){
						String key = sb.toString().trim();
						if(!key.isEmpty() && !Operator.isOperator(key)){
							Function fun = new Function(key,source,i);
							
							sary[++arrayIndex] = fun.toString();
							indexSet = new HashSet<Integer>();
							indexSet.add(arrayIndex);
							expreMap.put(new FunctionNode(fun), indexSet);
							
							i = fun.getEndIndex();
							sb = null;
							continue;
						}
					}
					
					Parentheses pt = Parentheses.compile(source,i);
					i = pt.endIndex;
					sary[++arrayIndex] = pt.expressionStr();
					indexSet = new HashSet<Integer>();
					indexSet.add(arrayIndex);
					
					expreMap.put(pt.expression, indexSet);
					continue;
				}
				
				if(ch == ')'){
					parentheses.endIndex = i;
					break;
				}
				
				if(sb == null){
					sb = new StringBuffer(ch);
				}
				sb.append(ch);
			}
			
			if(sb != null){
				sary[++arrayIndex] = sb.toString();
			}
			
			if(priorityOpts.isEmpty() && sary[0] != null){
				if(expreMap.size() > 0){
					for(Map.Entry<Node, Set<Integer>> entry:expreMap.entrySet()){
						parentheses.expression = entry.getKey();
						return parentheses;
					}
				}
				
				parentheses.expression = new Operand(sary[0]);
				return parentheses;
			}
			
			Collections.sort(priorityOpts);
			Expression root = null;
			for(Operator op:priorityOpts){
				int opIndex = op.getIndex();
				
				int preIndex = opIndex-1;
				int nextIndex = opIndex+1;
				
				Node preNode = getExpression(expreMap,preIndex);
				Node nextNode = getExpression(expreMap,nextIndex);
				
				if(preNode == null){
					preNode = new Operand(sary[preIndex]);
				}
				
				if(nextNode == null){
					nextNode = new Operand(sary[nextIndex]);
				}
				
				root = new Expression();
				root.setLeft(preNode);
				root.setRight(nextNode);
				root.setOperator(op);
				
				indexSet = new HashSet<Integer>();
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
				indexSet.add(op.getIndex());
				
				expreMap.put(root, indexSet);
			}
			
			parentheses.expression = root;
			return parentheses;
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
				return this.left.getValue(paramsMap);
			}
			
			Object valueLeft = this.left.getValue(paramsMap);
			Object valueRight = this.right.getValue(paramsMap);
			
			if(valueLeft instanceof Date){
				return calculateDate((Date)valueLeft,valueRight.toString());
			}
			
			if(valueRight instanceof Date){
				return calculateDate((Date)valueRight,valueLeft.toString());
			}
			
			if(valueLeft instanceof String || valueRight instanceof String){
				return calculateString(valueLeft.toString(),valueRight.toString());
			}

			if(valueLeft instanceof Integer && valueRight instanceof Integer){
				Integer il = (Integer)valueLeft;
				Integer ir = (Integer)valueRight;
				
				return calculate(il,ir);
			}

			double dl = ((Number)valueLeft).doubleValue();
			double dr = ((Number)valueRight).doubleValue();
			return calculate(dl,dr);
		}
		
		private Date calculateDate(Date date,String increment){
			if(!this.operator.isPlus() && !this.operator.isSubstruction()){
				throw new RuntimeException("Error operator '"+this.operator.optChar+"' for Date");
			}
			
			if(!Node.isDateIncrement(increment)){
				throw new RuntimeException("Error parameter '"+increment+"' for Date");
			}
			
			Calendar cal = Calendar.getInstance();  
			cal.setTime(date);
			char flagChar = increment.charAt(increment.length()-1);
			int incre = new Integer(increment.substring(0,increment.length()-1));
			incre = this.operator.isPlus() ? incre:-incre;
			
			if(flagChar == 'y'){
				cal.add(Calendar.YEAR,incre);
			}else if(flagChar == 'm'){
				cal.add(Calendar.MONTH,incre);
			}else if(flagChar == 'd'){
				cal.add(Calendar.DATE,incre);
			}else if(flagChar == 'h'){
				cal.add(Calendar.HOUR_OF_DAY,incre);
			}else if(flagChar == 'i'){
				cal.add(Calendar.MINUTE,incre);
			}else if(flagChar == 's'){
				cal.add(Calendar.SECOND,incre);
			}

			return cal.getTime();
		}
		
		private String calculateString(String left,String right){
			if(!this.operator.isPlus()){
				throw new RuntimeException("Error operator '"+this.operator.optChar+"' for String");
			}
			
			return left+right;
		}
		
		private Integer calculate(int x,int y){
			if(this.operator.isPlus()){
				return plus(x,y);
			}
			
			if(this.operator.isSubstruction()){
				return this.substruction(x,y);
			}
			
			if(this.operator.isMultiplication()){
				return this.multiplication(x, y);
			}
			
			if(this.operator.isDivision()){
				return this.division(x,y);
			}
			
			return null;
		}
		
		private Double calculate(double x,double y){
			if(this.operator.isPlus()){
				return plus(x,y);
			}
			
			if(this.operator.isSubstruction()){
				return this.substruction(x,y);
			}
			
			if(this.operator.isMultiplication()){
				return this.multiplication(x, y);
			}
			
			if(this.operator.isDivision()){
				return this.division(x,y);
			}
			
			return null;
		}
		
		private int plus(int x,int y){
			return x+y;
		}
		
		private double plus(double x,double y){
			return x+y;
		}
		
		private int substruction(int x,int y){
			return x-y;
		}
		
		private double substruction(double x,double y){
			return x-y;
		}
		
		private int multiplication(int x,int y){
			return x*y;
		}
		
		private double multiplication(double x,double y){
			return x*y;
		}
		
		private int division(int x,int y){
			return x/y;
		}
		
		private double division(double x,double y){
			return x/y;
		}

		public String toString(){
			return "("+this.left+this.operator+this.right+")";
		}
	}
	
	static class Operand extends Node {
		private String operand;
		String numberRegex = "^\\d\\d*\\.{0,1}\\d*\\d$";
		
		public Operand(String op){
			if(op != null){
				this.operand = op.trim();
			}
		}
		
		public Object getValue(Map<String,Object> paramsMap){
			if(this.isString()){
				return this.operand.subSequence(1,this.operand.length()-1);
			}
			
			if(this.operand.startsWith("D")){
				Date date = Node.readDate(this.operand);
				if(date != null){
					return date;
				}
			}
			
			if(Node.isDateIncrement(this.operand)){
				return this.operand;
			}
			
			try {
				return new Integer(this.operand);
			}catch(NumberFormatException e){
				try {
					return new Double(this.operand);
				}catch(NumberFormatException e1){}
			}
			
			return MapHelper.readValue(paramsMap,this.operand);
		}
		
		
		public boolean isString(){
			return this.operand.startsWith("'") && this.operand.endsWith("'");
		}
		
		public boolean isDate(){
			if(this.operand.startsWith("D")){
				try {
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String ds = this.operand.substring(1,this.operand.length());
					
					format.parse(ds);
					return true;
				} catch (ParseException e) {}
			}
			return false;
		}
		
		public boolean isNumber(){
			Pattern pt = Pattern.compile(numberRegex);
			Matcher mt = pt.matcher(this.operand);
			//return mt.find();
			return true;
		}
		
		public String toString(){
			return this.operand;
		}
	}
	
	static class Operator extends Node implements Comparable<Operator> {
		private char optChar;
		private int arrayIndex;
		
		public Operator(char oc,int index){
			this.optChar = oc;
			this.arrayIndex = index;
		}
		
		public int getIndex(){
			return this.arrayIndex;
		}
		
		public boolean isPlus(){
			return this.optChar == '+';
		}
		
		public boolean isSubstruction(){
			return this.optChar == '-';
		}
		
		public boolean isMultiplication(){
			return this.optChar == '*';
		}
		
		public boolean isDivision(){
			return this.optChar == '/';
		}
		
		public int compareTo(Operator that) {
			return this.priority()-that.priority();
		}
		
		public String toString(){
			//return "index = "+this.arrayIndex+", operator = "+this.optChar;
			return this.optChar+"";
		}
		
		private int priority(){
			if(optChar == '*' || optChar == '/'){
				return 1;
			}
			
			if(optChar == '+' || optChar == '-'){
				return 2;
			}
			return 100;
		}
		
		public static boolean isOperator(char c){
			return c == '+' || c == '-' || c == '*' || c == '/';
		}
		
		public static boolean isOperator(String s){
			return "+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s);
		}
	}
	*/
	
	/*
	public static void main2(String[] args) {
		String source = "1+2*3*4*5/6-7+8*9+10-11";
		
		String[] sary = new String[source.length()];
		StringBuffer sb = null;
		int arrayIndex = -1;
		List<Operator> priorityOpts = new ArrayList<Operator>();
		for(int i = 0; i < source.length(); i++){
			char ch = source.charAt(i);
			
			if(isOperator(ch)){
				if(sb != null){
					sary[++arrayIndex] = sb.toString();
					sb = null;
				}
				
				sary[++arrayIndex] = ch+"";
				priorityOpts.add(new Operator(ch,arrayIndex));
				
				continue;
			}
			if(sb == null){
				sb = new StringBuffer(ch);
			}
			
			sb.append(ch);
			
			//The lasted
			if(i == source.length()-1){
				sary[++arrayIndex] = sb.toString();
			}
		}
		
		Collections.sort(priorityOpts);
		
		/*
		for(int ii = 0; ii < sary.length; ii++){
			if(sary[ii] != null){
				//System.out.println(sary[ii]);
			}
		}
		
		System.out.println("");
		System.out.println("********** Operators ***********");
		for(Operator opp : priorityOpts){
			System.out.println(opp);
		}*
		
		Map<Node,Set<Integer>> expreMap = new HashMap<Node,Set<Integer>>();
		Expression root = null;
		Set<Integer> indexSet = null;
		for(Operator op:priorityOpts){
			int opIndex = op.getIndex();
			
			int preIndex = opIndex-1;
			int nextIndex = opIndex+1;
			
			Node preNode = getExpression(expreMap,preIndex);
			Node nextNode = getExpression(expreMap,nextIndex);
			
			if(preNode == null){
				preNode = new Operand(sary[preIndex]);
			}
			
			if(nextNode == null){
				nextNode = new Operand(sary[nextIndex]);
			}
			
			root = new Expression();
			root.setLeft(preNode);
			root.setRight(nextNode);
			root.setOperator(op);
			
			indexSet = new HashSet<Integer>();
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
			indexSet.add(op.getIndex());
			
			expreMap.put(root, indexSet);
		}
		
		System.out.println(root +" ="+root.getValue());
	}*/
}
