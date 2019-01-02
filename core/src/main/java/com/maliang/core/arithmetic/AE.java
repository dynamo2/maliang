package com.maliang.core.arithmetic;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AE {
	public static Object execute(String expre){
		return ArithmeticExpression.execute(expre,null);
	}
	
	public static Object execute(String expre,Map<String,Object> params){
		return ArithmeticExpression.execute(expre,params);
	}
	
	public static void main(String[] args) {
		String s = "{gfdsgfdgds${AAA.BBB} rtrewt } fdsafdsa ${ CCCC.DDDD } }";
		
		//System.out.println((new BusinessService()).readBlock(s,"SYS",Block.TYPE_CODE));
		
		String sr = " ccccc \"aaa\\\"a\\\"a\\\"a\\\"qqqa\\\'a\" bbbbbbb \"dddddddddddddd\"";
		Matcher regex = Pattern.compile("\"((\\\\.|[^\"\\\\])*)\"").matcher(sr);
//		while(regex.find()) {
//			System.out.println("regex.group() : " + regex.group());
//			String g = regex.group(1);
//			System.out.println("---- g : " + g);
//		}
		
		/**
addToParams({
    setMeal:db.SetMeal.get(request.id),
    c:setMeal.items.setIfEmpty([{name:''}]),
    c:print('-------------- setMeal : ' + setMeal.items.menus.menu.name),
    reqData:'{id:null,spId:"'+setMeal.shop.id+'",sid:"'+setMeal.id+'"}',
    items:[{name:'烤肉类',id:'a',menus:[{}]},{name:'烤蔬菜',id:'b'}],
    
    menusTable:{
            type:'row',
            column-css:['col-4 p-2 border-bottom','col-1 p-2 border-bottom','col-1 p-2 border-bottom'],
            body:[
                ['雪花肥牛','1份','88元'],
                ['祥云刺身','1份','42元'],
                ['蔬菜拼盘','1份','22元']
            ]
    },
    c:setMeal.items.each(){[
        this.menusTable.set({
            type:'row',
            column-css:['col-4 p-2 border-bottom','col-1 p-2 border-bottom','col-1 p-2 border-bottom'],
            body:this.menus.each(){[
                this.menu.name,this.num+'份',this.price+'元'
            ]}
        })
    ]}
})
		
		
		**/
		sr = "{aaa:11,ccc:{ddd:'aaa',price:22.34},bbb:'22'}";
		regex = Pattern.compile("\\{([^\\}\\{]*)\\}").matcher(sr);
		while(regex.find()) {
			//System.out.println("regex.group() : " + regex.group());
			String g = regex.group(1);
			System.out.println("---- g : " + g);
		}
	}
}


