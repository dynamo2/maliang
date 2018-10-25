package com.maliang.core.arithmetic.node;

import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

public class Test {
	public static void main(String[] args) {
		String s = "{list:[{name:'美迪若雅CO2碳酸注氧面膜6次/盒',price:438,items:[{spec:[{name:[{n1:'wang',n2:'ziqing'},{n1:'zhang',n2:'hui'}],num:3},{name:'40ML',num:8}],price:100},{spec:'60ML',price:200}]},"
				+ "{name:'美迪若雅维生素EC爽肤水\\精华水150ml',price:369,items:[{spec:'100ML',price:300},{spec:'150ML',price:400}]}]}";
		
		Map<String,Object> params = (Map<String,Object>)AE.execute(s);
		
		s = "list.store.num.name.items.product";
		s = "list.items.spec.name.n1";
		Object v = AE.execute(s,params);
		System.out.println(" v : " + v);
		System.out.println(" params : " + params);
//		
//		Object[] vs = Utils.toArray(v);
//		
		String[] ns = new String[4];
		ns[0] = "items";
		ns[1] = "spec";
		ns[2] = "name";
		ns[3] = "n1";
//		
////		ns = new String[1];
////		ns[0] = "store";
//		
		List<Object> es = MapHelper.expand(Utils.toList(params.get("list")),ns);
//		
//		pln(vs,"vs");
	//	pln(es,"es");
		
		System.out.println("---- es : " + es);
		
	}
	
	public static void pln(Object[] os,String flag) {
		for(Object obj : os) {
			System.out.println("-------- "+flag+" : " + obj);
		}
	}
	
	public static void testAnnotation() {
		String s = "{s:'aaaaa'/***注释**/+'gfsgfdsg',/***注释**/b:'eeeee'}";
		
		//s = "['dddd','fdafads'/***注释**/,'eeeee','tttttt']";
		Object v = AE.execute(s);
		System.out.println(v);
	}
}
