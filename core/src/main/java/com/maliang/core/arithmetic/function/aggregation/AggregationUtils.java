package com.maliang.core.arithmetic.function.aggregation;

import java.util.ArrayList;
import java.util.List;

import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

public class AggregationUtils {
	public static Object sum(Object[] datas){
		return Sum.doSum(datas);
	}
	
	public static Object[] expand(Object[] expandList,String[] names){
		if(Utils.isEmpty(expandList))return expandList;
		if(Utils.isEmpty(names))return expandList;
		
		int end = 0;
		List<Object> newDatas = new ArrayList<Object>();
		for(Object obj:expandList){
			boolean isExpand = false;
			List<String> expands = new ArrayList<String>();
			end = 0;
			Object parent = obj;
			for(String n: names){
				expands.add(n);
				Object val = MapHelper.readValue(parent,n);
				if(val instanceof List){
					for(Object listObj : (List)val){
						Object nd = Utils.clone(obj);
						MapHelper.setValue(nd,expands,listObj);
						newDatas.add(nd);
					}

					isExpand = true;
					break;
				}
				
				end++;
				parent = val;
			}
			if(!isExpand){
				newDatas.add(obj);
			}
		}
		
		Object[] results = newDatas.toArray();
		if(end < names.length){
			results = expand(results,names);
		}
		
		return results;
	}
}
