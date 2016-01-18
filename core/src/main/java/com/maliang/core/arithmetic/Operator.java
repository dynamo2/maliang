package com.maliang.core.arithmetic;

public class Operator implements Comparable<Operator> {
	private final static char[] OPERATOR_CHAR = {'+','-','*','/','&','|','>','<','=','.'};
	private final String source;
	private char optChar;
	private int arrayIndex;
	private String operatorKey;
	private int startIndex;
	private int endIndex;

	public Operator(String source, int index) {
		this.startIndex = index;
		this.source = source;

		readOperator();
	}

	public String getOperatorKey() {
		return this.operatorKey;
	}

	private void readOperator() {
		int i = startIndex;
		char ch = source.charAt(i);
		this.operatorKey = ch+"";
		if (ch == '<' || ch == '>') {
			char next = source.charAt(++i);
			if (next == '=') {
				this.operatorKey += ""+next;
			} else {
				this.optChar = ch;
				i--;
			}
		} else {
			this.optChar = ch;
		}

		this.endIndex = i;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getArrayIndex() {
		return this.arrayIndex;
	}

	public void setArrayIndex(int index) {
		this.arrayIndex = index;
	}

	public boolean isPlus() {
		return this.optChar == '+';
	}

	public boolean isSubstruction() {
		return this.optChar == '-';
	}

	public boolean isMultiplication() {
		return this.optChar == '*';
	}

	public boolean isDivision() {
		return this.optChar == '/';
	}

	public boolean isAnd() {
		return this.optChar == '&';
	}

	public boolean isOr() {
		return this.optChar == '|';
	}

	public boolean isEq() {
		return this.optChar == '=';
	}

	public boolean isLt() {
		return this.optChar == '<';
	}
	
	public boolean isDot(){
		return this.optChar == '.';
	}

	public boolean isLte() {
		return "<=".equals(this.operatorKey);
	}

	public boolean isGt() {
		return this.optChar == '>';
	}

	public boolean isGte() {
		return ">=".equals(this.operatorKey);
	}

	public boolean isLogical() {
		return this.isAnd() || this.isOr();
	}

	public boolean isComparison() {
		return this.isLt() || this.isGt() || this.isEq() || this.isLte()
				|| this.isGte();
	}

	public int compareTo(Operator that) {
		return this.priority() - that.priority();
	}

	public String toString() {
		return this.optChar + "";
	}

	private int priority() {
		if (this.isDot()) {
			return 0;
		}
		
		if (optChar == '*' || optChar == '/') {
			return 1;
		}

		if (optChar == '+' || optChar == '-') {
			return 2;
		}

		if (this.isComparison()) {
			return 3;
		}

		if (this.isLogical()) {
			return 4;
		}
		
		if(optChar == '?'){
			return 10;
		}

		return 100;
	}

	public static boolean isOperator(char c) {
		for(char oc : OPERATOR_CHAR){
			if(c == oc){
				return true;
			}
		}
		return false;
		
		/*
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '&'
				|| c == '|' || c == '>' || c == '<' || c == '=';*/
	}

	public static boolean isOperator(String s) {
		return "+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s);
	}
}
