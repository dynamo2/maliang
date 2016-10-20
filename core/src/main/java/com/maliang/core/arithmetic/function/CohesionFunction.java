package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

public class CohesionFunction {
	public static void main(String[] args) {
		String s = "{orders:[{product:'神仙水',warehouse:'仓库1',brand:'sk2',num:100},"
					+ "{product:'POLA极光焕采精华50克',warehouse:'仓库1',brand:'pola',num:100},"
					+ "{product:'POLA新版黑BA面霜30克',warehouse:'仓库1',brand:'pola',num:100},"
					+ "{product:'SK2肌因光蕴环采钻白精华露',warehouse:'仓库1',brand:'sk2',num:100},"
					+ "{product:'神仙水',warehouse:'仓库2',brand:'sk2',num:200},"
					+ "{product:'全能乳液',warehouse:'仓库2',brand:'sk2',num:200},"
					+ "{product:'彩虹水',warehouse:'仓库2',brand:'ba',num:200}]}";
		
		Map params = (Map)AE.execute(s);
		s = "orders.cohesion({id:['warehouse','brand']})";
		
		Object v = AE.execute(s,params);
		System.out.println("v : " + v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object[] datas = Utils.toArray(function.getKeyValue());
		
		CohesionCompiler compiler = new CohesionCompiler(datas,function.getExpression());
		compiler.execute(params);
		
		return compiler.getCohesionResult();
	}
	
	static class CohesionCompiler {
		public static final String ID_KEY = "id";
		public static final String ITEMS_FLAG_KEY = "itemsFlag";

		private final String source;
		private String idExpression;
		private Object[] datas;
		private Map<String,Object> expressionMap;
		private Map<Object,Object> resultMap = new LinkedHashMap<Object,Object>();
		private String itemsFlag = "items";
		
		public CohesionCompiler(Object[] datas,String source){
			this.datas = datas;
			this.source = source;
		}

		public Collection getCohesionResult(){
			return this.resultMap.values();
		}
		
		private void init(Map<String,Object> params){
			MapCompiler compiler = new MapCompiler(this.source,1,params,false,false);
			this.expressionMap = compiler.getMap();
			
			this.idExpression = (String)this.expressionMap.get(ID_KEY);
			
			readItemsFlag(params);
		}

		private void readItemsFlag(Map<String, Object> params) {
			Object flag = this.expressionMap.get(ITEMS_FLAG_KEY);
			if(flag != null){
				flag = AE.execute((String)flag, params);
				
				if(flag != null && !StringUtil.isEmpty(flag.toString())){
					this.itemsFlag = flag.toString().trim();
				}
			}
		}

		public void execute(Map<String,Object> params){
			init(params);
			
			Object idVal = null;
			for(Object obj : datas){
				if(obj instanceof Map){
					Map<String,Object> newParams = new HashMap<String,Object>();
					newParams.putAll((Map)obj);
					
					idVal = AE.execute(idExpression,newParams);
					
					Map idMap = new HashMap();
					if(idVal instanceof List){
						for(Object id:(List)idVal){
							idMap.put(id,((Map)obj).remove(id));
						}
					}

					Map result = getMatchedResult(idMap);
					this.getItems(result).add(obj);
				}
			}
		}
		
		private List getItems(Map result){
			Object items = result.get(itemsFlag);
			if(items == null){
				items = new ArrayList();
				result.put(itemsFlag, items);
				return (List)items;
			}
			
			if(items instanceof List){
				return (List)items;
			}

			return Arrays.asList(Utils.toArray(items));
		}
		
		private Map getMatchedResult(Map idMap){
			Object matchedResult = resultMap.get(idMap.toString());
			if(matchedResult == null){
				matchedResult = new HashMap();
				((Map)matchedResult).putAll(idMap);
				resultMap.put(idMap.toString(), matchedResult);
			}
			return (Map)matchedResult;
		}
	}
}
