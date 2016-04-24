package com.maliang.core.arithmetic.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maliang.core.arithmetic.Operator;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.arithmetic.function.Function;

public class Parentheses extends Node{
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
	
	public Node getExpression(){
		return this.expression;
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
		ExpressionNode root = null;
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
			
			root = new ExpressionNode();
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

	public Node getNode() {
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

