package com.maliang.core.arithmetic;

import java.util.Map;

import com.maliang.core.model.Block;
import com.maliang.core.service.BusinessService;

public class AE {
	public static Object execute(String expre){
		return ArithmeticExpression.execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		return ArithmeticExpression.execute(expre,params);
	}
	
	@Deprecated
	public static void main(String[] args) {
		String s = "{gfdsgfdgds${AAA.BBB} rtrewt } fdsafdsa ${ CCCC.DDDD } }";
		
		System.out.println((new BusinessService()).readBlock(s,"SYS",Block.TYPE_CODE));
	}
}


