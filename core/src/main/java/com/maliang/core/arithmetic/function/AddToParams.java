package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.MapHelper;

public class AddToParams {
	public static void main(String[] args) {
		String paramStr = "{product:{id:'111111',name:'Product',brand:'aaaa',price:335.8,expiry_date:'2015-03-26'},"
				+ "brands:[{id:'aaaa',name:'雪花秀'},{id:'bbbb',name:'希思黎'},{id:'cccc',name:'Pola'}],"
				+ "products:[{id:'1111',name:'Bioeffect EGF生长因子精华',brand:'Bioeffect',price:850.00,picture:'0-item_pic.jpg'},"
				+ "{id:'2222',name:'Aminogenesis活力再生胶囊 ',brand:'Aminogenesis',price:275.00,picture:'0-item_pic.jpg'}]}";
		
		Map<String,Object> params = MapHelper.buildAndExecuteMap(paramStr, null);
		
		params = new HashMap();
		String str = "{code:addToParams({product:{id:'111111',name:'Product',brand:'aaaa',price:335.8,expiry_date:'2015-03-26'},edit_form:{id:'edit_'+product.id}})}";
		
		Object formMap = ArithmeticExpression.execute(str, params);

		System.out.println(params);
		
		String s = "{name:'product.brand',label:'品牌',type:'select',value:product.brand.id,options:each(brands){{value:this.id,text:this.name}}}";
		
	}
	public static Map<String,Object> execute(Function function,Map<String,Object> params){
		System.out.println("addToParams expression : " + function.getExpression());
		new MapCompiler(function.getExpression(),1,params,true);
		return params;
	}
}
