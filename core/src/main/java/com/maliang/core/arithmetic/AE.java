package com.maliang.core.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maliang.core.service.BusinessService;

public class AE {
	public static Object execute(String expre){
		return ArithmeticExpression.execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		return ArithmeticExpression.execute(expre,params);
	}
	
	public static void main(String[] args) {
		String s = "{gfdsgfdgds${AAA.BBB} rtrewt } fdsafdsa ${ CCCC.DDDD } }";
		
		System.out.println((new BusinessService()).readBlock(s));
	}
}

