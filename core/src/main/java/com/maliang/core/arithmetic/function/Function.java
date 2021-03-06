package com.maliang.core.arithmetic.function;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.Substring;
import com.maliang.core.exception.Break;
import com.maliang.core.exception.ReturnException;
import com.maliang.core.exception.ThrowException;
import com.maliang.core.service.BusinessService;
import com.maliang.core.service.MapHelper;

public class Function {
	BusinessService businessService = new BusinessService();
	
	//key(expression){body}
	private String key;
	public String expression;
	private String body;
	private Object keyValue;
	private String keySource;
	private boolean useKeyValue = false;
	
	private final String source;
	private final int startIndex;
	private int endIndex;
	private IfFunction ifFunction;
	private boolean isUserFunction;
	private UserFunction userFunction;
	
	public Function(String k,String s,int startIndex){
		this.key = k;
		this.source = s;
		this.startIndex = startIndex;
		this.keySource = k;

		if(key != null)key = key.trim();
		
		if("if".equals(key)){
			ifFunction = new IfFunction(this.source,this.startIndex);
			this.endIndex = ifFunction.getEndIndex();
		}else {
			readOthers();
		}
	}
	
	public void setKeyValue(Object kv){
		this.keyValue = kv;
		this.useKeyValue = true;
	}
	
	public boolean useKeyValue(){
		return this.useKeyValue;
	}
	
	public Object getKeyValue(){
		return this.keyValue;
	}
	
	public String getKeySource(){
		return this.keySource;
	}
	
	public Function(String body){
		this.source = null;
		this.startIndex = -1;
		
		if(body != null){
			this.body = body.trim();
		}
	}
	
	public Object executeExpression(Map<String,Object> params){
		if(this.expression == null || this.expression.trim().isEmpty()){
			return null;
		}
		
		return AE.execute(this.expression, params);
	}
	
	public static void pushThis(Map<String,Object> params){
		if(params == null || !params.containsKey("this"))return;
		
		if(!params.containsKey("these")){
			params.put("these", new Stack());
		}
		((Stack)params.get("these")).push(params.get("this"));
	}
	
	public static void popThis(Map<String,Object> params){
		if(params == null || !params.containsKey("these"))return;
		
		Stack these = (Stack)params.get("these");
		params.put("this", these.pop());
		if(these.size() == 0){
			params.remove("these");
		}
	}
	
	boolean isMap(){
		return key == null && this.body.startsWith("{") && this.body.endsWith("}");
	}
	
	boolean isList(){
		return key == null && this.body.startsWith("[") && this.body.endsWith("]");
	}
	
	public Object execute(Map<String,Object> params){
		try {
			return this.doExecute(params);
		}catch(ReturnException re) {
			return re.excute(params);
		}catch(ThrowException te) {
			te.doThrow();
			return null;
		}
	}

	private Object doExecute(Map<String,Object> params){
		this.executeKey(params);
		
		if(this.isMap()){
			return MapFunction.execute(this, params);
		}
		
		if(this.isList()){
			return ListFunction.execute(this, params);
		}
		
		if("return".equalsIgnoreCase(key)){
			throw new ThrowException(new ReturnException(this));
		}
		
		if("isList".equalsIgnoreCase(key)){
			return ListFunction.isList(this, params);
		}
		
		if("toList".equalsIgnoreCase(key)){
			return ListFunction.toList(this, params);
		}
		
		if("add".equalsIgnoreCase(key)){
			return ListFunction.add(this, params);
		}
		
		if("prepend".equalsIgnoreCase(key)){
			return ListFunction.prepend(this, params);
		}
		
		if("loop".equalsIgnoreCase(key)){
			return LoopFunction.loop(this, params);
		}
		
		if("random".equalsIgnoreCase(key)){
			return MathFunction.random(this, params);
		}
		
		if("print".equalsIgnoreCase(key)){
			System.out.println(this.executeExpression(params));
			return null;
		}
		
		if("break".equalsIgnoreCase(key)){
			throw new Break();
		}
		
		if("to".equalsIgnoreCase(key)){
			return BusinessFunction.toPage(this, params);
		}
		
		if("blist".equalsIgnoreCase(key)){
			return BusinessFunction.businesses(this, params);
		}
		
		if("flow".equalsIgnoreCase(key)){
			return BusinessFunction.flow(this, params);
		}
		
		if("set".equalsIgnoreCase(key)){
			return AssignFunction.set(this, params);
		}
		
		if("setIfNull".equalsIgnoreCase(key)){
			return AssignFunction.setIfNull(this, params);
		}
		
		if("setIfEmpty".equalsIgnoreCase(key)){
			return AssignFunction.setIfEmpty(this, params);
		}
		
		if("update".equalsIgnoreCase(key)){
			return AssignFunction.update(this, params);
		}
		
//		if("wrap".equalsIgnoreCase(key)){
//			return WrapFunction.execute(this, params);
//		}
		
		if("exe".equalsIgnoreCase(key)){
			return ExecuteFunction.execute(this, params);
		}
		
		if("date".equalsIgnoreCase(key)){
			return DateFunction.date(this, params);
		}
		
		if("hourFormat".equalsIgnoreCase(key) || "hf".equalsIgnoreCase(key)){
			return DateFunction.hourFormat(this, params);
		}
		
		if("dateFormat".equalsIgnoreCase(key) || "df".equalsIgnoreCase(key)){
			return DateFunction.dateFormat(this, params);
		}

		if("timestampFormat".equalsIgnoreCase(key) || "tsf".equalsIgnoreCase(key)){
			return DateFunction.timestampFormat(this, params);
		}
		
		if("between".equalsIgnoreCase(key)){
			return Between.execute(this, params);
			//return null;
		}
		
		if("has".equalsIgnoreCase(key)){
			return ListFunction.contains(this, params);
		}
		
		if("join".equalsIgnoreCase(key)){
			return ListFunction.join(this, params);
		}
		
		if("sum".equalsIgnoreCase(key)){
			return Sum.execute(this, params);
		}
		
		if("int".equalsIgnoreCase(key) || "Integer".equalsIgnoreCase(key)){
			return TypeFunction.intExecute(this, params);
		}
		
		if("long".equalsIgnoreCase(key) || "Long".equalsIgnoreCase(key)){
			return TypeFunction.longExecute(this, params);
		}
		
		if("short".equalsIgnoreCase(key) || "Short".equalsIgnoreCase(key)){
			return TypeFunction.shortExecute(this, params);
		}
		
		if("double".equalsIgnoreCase(key)){
			return TypeFunction.doubleExecute(this, params);
		}
		
		if("string".equalsIgnoreCase(key)){
			return TypeFunction.stringExecute(this, params);
		}
		
		if("toCode".equals(key)){
			return TypeFunction.codeExecute(this, params);
		}
		
		if("each".equals(key)){
			return Each.execute(this, params);
		}
		
		if("eachUpdate".equals(key)){
			return Each.eachUpdate(this, params);
		}
		
		if("size".equals(key)){
			return SizeFunction.execute(this, params);
		}
		
		if("if".equals(key)){
			//return If.execute(this, params);
			if(this.ifFunction != null){
				return this.ifFunction.execute(params);
			}
			return null;
		}
		
		if("addToParams".equals(key)){
			return AddToParams.execute(this, params);
		}
		
		if("session".equals(key)){
			return SessionFunction.execute(this, params);
		}
		
		if("business".equals(key)){
			return business(params);
		}
		
		if("notNull".equals(key)){
			return Check.notNull(this, params);
		}
		
		if("isNull".equals(key)){
			return Check.isNull(this, params);
		}
		
		if("isEmpty".equals(key)){
			return Check.isEmpty(this, params);
		}
		
		if("not".equals(key)){
			return Check.not(this, params);
		}
		
		if("check".equals(key)){
			return Check.execute(this, params);
		}
		
		if("query".equals(key)){
			return QueryFunction.find(this, params);
		}
		
		if("find".equals(key)){
			return QueryFunction.find(this, params);
		}
		
		if("findOne".equals(key)){
			return QueryFunction.findOne(this, params);
		}
		
		if("remove".equals(key)){
			return QueryFunction.remove(this, params);
		}
		
		if("removeKey".equals(key)){
			return QueryFunction.removeKey(this, params);
		}
		
		if("max".equals(key)){
			//return MaxFunction.execute(this, params);
			return AggregationFunction.max(this, params);
		}
		
		if("min".equals(key)){
			//return MaxFunction.execute(this, params);
			return AggregationFunction.min(this, params);
		}
		
		if("tree".equals(key)){
			return TreeFunction.execute(this, params);
		}

		if("group".equals(key)){
			return GroupFunction.execute(this, params);
		}
		
		if("regroup".equals(key)){
			return GroupFunction.regroup(this, params);
		}
		
		if("join".equals(key)){
			return JoinFunction.execute(this, params);
		}
		
		if("cohesion".equals(key)){
			return CohesionFunction.execute(this, params);
		}
		
//		if("merge".equals(key)){
//			return MergeFunction.execute(this, params);
//		}
		
		if("merge".equals(key)){
			return MergeFunction.merge(this, params);
		}
		
		if("expand".equals(key)){
			return ExpandFunction.execute(this, params);
		}
		
		if("oid".equals(key)){
			return DBFunction.oid(this, params);
		}
		
		if("parent".equals(key)){
			return TreeModelFunction.parent(this, params);
		}
		
		if("parents".equals(key)){
			return TreeModelFunction.parents(this, params);
		}
		
		if("children".equals(key)){
			return TreeModelFunction.children(this, params);
		}
		
		if("allChildren".equals(key)){
			return TreeModelFunction.allChildren(this, params);
		}
		
//		if("filter".equals(key)){
//			return FilterFunction.execute(this, params);
//		}
		
		if(this.isDBFun()){
			return DBFunction.execute(this, params);
		}
		
		if(this.isUserFunction) {
			UserFunction.put(params, this.userFunction);
			return null;
		}
		
		try {
			return doUserFunction(params);
		}catch(Break be) {}
		

//		if(this.isHtmlFun()){
//			return HtmlFunction.execute(this, params);
//		}
		
		/*
		if(key != null && key.contains(".")){
			return this.execute2(params);
		}*/
		
		return defaultValue(params);
		//return ListFunction.execute(this, params);
	}
	
	private Object doUserFunction(Map<String,Object> params) {
		Object val = UserFunction.readFunction(params, key);
		if(val == null) {
			throw new Break();
		}

		UserFunction userFun = (UserFunction)val;
		return userFun.execute(this, params);
	}
	
	private Object defaultValue(Map<String,Object> params){
		Object val = null;
		if(this.useKeyValue){
			val = MapHelper.readValue(this.keyValue,this.key);
		}else {
			val = ArithmeticExpression.execute(this.key, params);
		}

		if(val != null){
			if(val instanceof List){
				List<Object> list = (List<Object>)val;
				Object idx = this.executeExpression(params);
				
				if(idx != null && idx instanceof Integer && list.size() > (Integer)idx){
					return list.get((Integer)idx);
				}
			}else if(val instanceof Map){
				Map<Object,Object> map = (Map<Object,Object>)val;
				Object key = this.executeExpression(params);
				
				if(key != null && key instanceof String){
					return MapHelper.readValue(map,(String)key);
				}
				
				return map.get(key);
			}
		}
		
		return null;
	}
	
	private boolean isDBFun(){
		return key != null && (key.startsWith("db.") || key.startsWith("pdb."));
	}
	
	private boolean isHtmlFun(){
		return key != null && (key.startsWith("h.") || key.startsWith("html."));
	}
	
	/**
	 * execute key value
	 * example:a.b.c.e()
	 *     a.b.c is e function's parameter
	 * **/
	private void executeKey(Map<String,Object> params){
		if(this.isDBFun() || this.isHtmlFun()){
			return;
		}

		if(key != null && key.contains(".")){
			String kv = key.substring(0,key.lastIndexOf("."));
			this.keyValue = ArithmeticExpression.execute(kv, params);
			
			key = key.substring(key.lastIndexOf(".")+1);
			this.useKeyValue = true;
		}
	}
	
	/*
	public Object execute2(Map<String,Object> params){
		//System.out.println("key : " + this.key);
		
		String operatedKey = key.substring(0,key.lastIndexOf('.'));
		String operator = key.substring(key.lastIndexOf('.')+1,key.length());
		
		if("int".equalsIgnoreCase(operator) || "Integer".equalsIgnoreCase(operator)){
			return TypeFunction.intExecute(operatedKey, params);
		}
		
		if("double".equalsIgnoreCase(operator)){
			return TypeFunction.doubleExecute(operatedKey, params);
		}
		
		if("float".equalsIgnoreCase(operator)){
			return TypeFunction.floatExecute(operatedKey, params);
		}
		
		if("string".equalsIgnoreCase(operator)){
			return TypeFunction.stringExecute(operatedKey, params);
		}
		
//		if("between".equalsIgnoreCase(operator)){
//			return Between.execute(operatedKey, this, params);
//		}
		
		return null;
	}*/
	
	public static void main(String[] args) {
		String ps = "{i1:{i11:{i111:{i1111:33333}}}}";
		Map pars = (Map)ArithmeticExpression.execute(ps,null);
		
		String s = "i1.i11.i111.i1111.int()+999";
		Object ii = ArithmeticExpression.execute(s,pars);
		
		System.out.println(ii.getClass());
		System.out.println(ii);
		
		System.out.println(ArithmeticExpression.execute("i1.i11.i111.i1111.int().between([1,10000000.99])",pars));
	}
	
	
	
	private Object business(Map<String,Object> params){
		Object value = this.executeExpression(params);
		if(value != null && value instanceof Map){
			Map<String,Object> map = (Map<String,Object>)value;

			return this.businessService.business(map);
		}
		return null;
	}
	
	public void setBody(String bd){
		this.body = bd;
	}
	
	public int getEndIndex(){
		return this.endIndex;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getExpression(){
		return this.expression;
	}
	
	public String getBody(){
		return this.body;
	}
	
	public boolean isEmptyBody(){
		if(body == null || this.body.trim().isEmpty()){
			return true;
		}
		
		return false;
	}
	
	
	
	private void readOthers(){
		int i = this.startIndex;
		
		Substring subs = new Substring(this.source,'(',')',i);
		if(subs.isMatched()){
			this.expression = subs.getInnerContent();
			
			i = subs.getEndIndex();
			subs = new Substring(this.source,')','{',i);
			if(subs.isMatched()){
				String space = subs.getInnerContent().trim();
				
				if(space.isEmpty()){
					i = this.readBody(subs.getEndIndex());
				}else if("::".equals(space)) {
					i = this.readBody(subs.getEndIndex());
					
					this.isUserFunction = true;
					this.userFunction = new UserFunction(this.key,this.expression,this.body);
				}
			}
		}else {
			throw new RuntimeException("Error function");
		}
		
		this.endIndex = i;
	}
	
	private int readBody(int startIndex) {
		Substring subs = new Substring(this.source,'{','}',startIndex);
		if(subs.isMatched()){
			this.body = subs.getInnerContent();
		}
		
		return subs.getEndIndex();
	}
	
	public String toString(){
		return key + "("+this.expression+")" +( body == null?"":"{"+body+"}");
	}
}
