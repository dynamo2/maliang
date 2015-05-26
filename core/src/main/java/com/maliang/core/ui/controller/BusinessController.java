package com.maliang.core.ui.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.service.MapHelper;

@Controller
@RequestMapping(value = "business")
public class BusinessController {

	
	/**
	 *  EditProduct {
	 *   _id:objectId,
	 *   name:'edit product',
	 *   flows:[{step:1,
	 *   		code:{product:db.Product.get(request.id),brands:db.Brand.search()},
	 *   		response:[edit_from],
	 *   		edit_from:{type:form,action:'',name:'product.edit.form',inputs:[
	 *            {name:'product.id',type:'hidden',value:product.id},
	 *            {name:'product.name',type:'text',value:product.name},
	 *            {name:'product.brand',type:'select',value:product.brand,
	 *               values:{
	 *              	list:db.Brand.query({name:''}),
	 *              	key:object.id,
	 *                  value:object.name}
	 *            },
	 *            {name:'product.price',type:'text',value:product.price},
	 *            {name:'product.expiry_date',type:'text',value:product.expiry_date},
	 *            {name:'product.picture',type:'text',value:product.picture}]}
	 *           },
	 *       {step:2,code{c1:db.Product.save(request.product)}}
	 *   ]
	 * }
	 * **/
	@RequestMapping(value = "edit.htm", method = RequestMethod.GET)
	public String edit(String id, Model model,HttpServletRequest request) {

		String str = "EditProduct {_id:objectId,name:'发布商品',"
				+ "flows:[{step:1,"
				+ "code:addToParams({product:db.Product.get(request.id),brands:db.Brand.search()}}),"
				+ "response:{html_template:'<div id='search_form'></div>',"
				+ "contents:[{type:'form',html_parent:'search_form',action:'edit.html',enctype:'',name:'product.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'product.name',label:'名称',type:'text',value:product.name},"
				+ "{name:'product.brand',label:'品牌',type:'select',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.category',label:'分类',type:'radio',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.label',label:'标签',type:'checkbox',value:product.brand,options:each(brands)},"
				+ "{label:'价格',type:'between',min:{name:'product.min_price',type:'number',value:product.min_price},max:{name:'product.max_price',type:'number',value:product.max_price}},"
				+ "{name:'product.description',label:'描述',type:'textarea',value:'product.description'},"
				+ "{name:'product.expiry_date',label:'有效期',type:'date',value:product.expiry_date},"
				+ "{name:'product.picture',label:'图片',type:'picture',value:product.picture}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'label',text:this.brand},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "},"
				+ "{step:2,code:addToParams({product:db.Product.save(request.product)}),"
				+ "response:{}"
				+ "}]}";
		
		String requestType = "{id:'string'}";
		Map<String,Object> params = readRequestParameters(requestType,request);
		
		String code = "addToParams({product:db.Product.get(request.id),brands:db.Brand.search()}})";
		ArithmeticExpression.execute(code, params);
		
		String response = "{html_template:'<div id='edit_form'></div>',"
				+ "contents:[{type:'form',html_parent:'edit_form',action:'/business/edit.htm',name:'product.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'fid',type:'hidden',value:2},"
				+ "{name:'product.id',type:'hidden',value:product.id},"
				+ "{name:'product.name',label:'名称',type:'text',value:product.name},"
				+ "{name:'product.brand',label:'品牌',type:'select',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.price',label:'价格',type:'number',value:product.price}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'label',text:this.brand},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}";
		
		Object responseMap = ArithmeticExpression.execute(response, null);
		String resultJson = JSONObject.fromObject(responseMap).toString();
		
		//System.out.println();
		model.addAttribute("resultJson", resultJson);

		return "/business/edit";
	}
	
	private Map<String,Object> readRequestParameters(String requestType,HttpServletRequest request){
		Object value = ArithmeticExpression.execute(requestType,null);
		Map<String,Object> typeMap = null;
		if(value != null && value instanceof Map){
			typeMap = (Map<String,Object>)value;
		}else {
			typeMap = new HashMap<String,Object>();
		}
		
		Enumeration<String> requestParamNames = request.getParameterNames();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Object reqValue = null;
		while(requestParamNames.hasMoreElements()){
			String reqName = requestParamNames.nextElement();
			
			String type = typeMap.containsKey(reqName)?typeMap.get(reqName).toString():"string";
			if(type.startsWith("array.")){
				reqValue = this.correctParamType(type, request.getParameterValues(reqName));
			}else {
				reqValue = this.correctParamType(type, request.getParameter(reqName));
			}
			
			resultMap.put(reqName, reqValue);
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("request", resultMap);
		
		return params;
		
//		for(Map.Entry<String, String[]> entry : requestParamMap.entrySet()){
//			String key = entry.getKey();
//			String[] vs = entry.getValue();
//			
//			Object type = typeMap.containsKey(key)?typeMap.get(key):"string";
//			if("string".equals(type)){
//				String v = null;
//				if(vs != null && vs.length > 0){
//					
//				}
//			}
//		}
	}
	
	private Object correctParamType(String type,String value){
		if("string".equals(type)){
			return value;
		}else if("int".equals(type)){
			try {
				return Integer.valueOf(value);
			}catch(Exception e){
				return null;
			}
		}else if("double".equals(type)){
			try {
				return Double.valueOf(value);
			}catch(Exception e){
				return null;
			}
		}
		
		return value;
	}
	
	private Object[] correctParamType(String type,String[] values){
		if(type == null || !type.startsWith("array.")){
			return values;
		}
		
		type = type.substring(6);
		Object[] vary = new Object[values.length];
		int idx = 0;
		for(String v : values){
			vary[idx++] = correctParamType(type,v);
		}
		
		return vary;
	}
	
	/***
	 * edit_from:{type:form,action:'',name:'product.edit.form',inputs:[
	 *            {name:'product.id',type:'hidden',value:product.id},
	 *            {name:'product.name',type:'text',value:product.name},
	 *            {name:'product.brand',type:'select',value:product.brand,
	 *               values:{
	 *              	list:db.Brand.query({name:''}),
	 *              	key:object.id,
	 *                  value:object.name}
	 *            },
	 *            {name:'product.price',type:'text',value:product.price},
	 *            {name:'product.expiryh_date',type:'text',value:product.expiry_date},
	 *            {name:'product.picture',type:'text',value:product.picture}]}
	 *           }
	 * **/
	private static String readForm(String form){
		String paramStr = "{product:{id:'111111',name:'Product',brand:'aaaa',price:335.8,expiry_date:'2015-03-26'},"
				+ "brands:[{id:'aaaa',name:'雪花秀'},{id:'bbbb',name:'希思黎'},{id:'cccc',name:'Pola'}],"
				+ "products:[{id:'1111',name:'Bioeffect EGF生长因子精华',brand:'Bioeffect',price:850.00,picture:'0-item_pic.jpg'},"
				+ "{id:'2222',name:'Aminogenesis活力再生胶囊 ',brand:'Aminogenesis',price:275.00,picture:'0-item_pic.jpg'}]}";

//				+ "products:[{id:'1111',name:'Bioeffect EGF生长因子精华',brand:'Bioeffect',price:850.00,picture:'http://gd3.alicdn.com/bao/uploaded/i3/TB1tnleHpXXXXX3XXXXXXXXXXXX_!!0-item_pic.jpg'},"
//				+ "{id:'2222',name:'Aminogenesis活力再生胶囊 ',brand:'Aminogenesis',price:275.00,picture:'http://gd4.alicdn.com/bao/uploaded/i4/12625030946839737/T1hQ_oFm0fXXXXXXXX_!!0-item_pic.jpg'}]}";
		Map<String,Object> params = MapHelper.buildAndExecuteMap(paramStr, null);
		//System.out.println(params);

		form = "{type:'form',action:'',name:'product.edit.form',"
				+ "inputs:[{name:'product.id',type:'hidden',value:product.id},"
					+ "{name:'product.name',type:'text',value:product.name},"
					+ "{name:'product.brand',type:'select',value:product.brand,"
						+ "options:each(brands){key:this.id,value:this.name}},"
					+ "{name:'product.price',type:'double',value:product.price},"
					+ "{name:'product.expiry_date',type:'date',value:product.expiry_date},"
					+ "{name:'product.picture',type:'file',value:product.picture}]}";


		form = "{html_template:'<div id='edit_form'></div>',"
				+ "contents:[{type:'form',html_parent:'edit_form',enctype:'',name:'product.edit.form',"
				+ "children{"
				+ "inputs:[{name:'product.id',type:'hidden',value:product.id},"
				+ "{name:'product.name',label:'名称',type:'text',value:product.name},"
				+ "{name:'product.brand',label:'品牌',type:'select',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.category',label:'分类',type:'radio',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.label',label:'标签',type:'checkbox',value:product.brand,options:each(brands)},"
				+ "{name:'product.price',label:'价格',type:'number',value:product.price},"
				+ "{name:'product.description',label:'描述',type:'textarea',value:'product.description'},"
				+ "{name:'product.expiry_date',label:'有效期',type:'date',value:product.expiry_date},"
				+ "{name:'product.picture',label:'图片',type:'picture',value:product.picture}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'label',text:this.brand},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}";
		
		//list:{label,data,page,title}
		
		form = "{html_template:'<div id='search_form'></div>',"
				+ "contents:[{type:'form',html_parent:'search_form',action:'edit.html',enctype:'',name:'product.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'product.name',label:'名称',type:'text',value:product.name},"
				+ "{name:'product.brand',label:'品牌',type:'select',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.category',label:'分类',type:'radio',value:product.brand,options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.label',label:'标签',type:'checkbox',value:product.brand,options:each(brands)},"
				+ "{label:'价格',type:'between',min:{name:'product.min_price',type:'number',value:product.min_price},max:{name:'product.max_price',type:'number',value:product.max_price}},"
				+ "{name:'product.description',label:'描述',type:'textarea',value:'product.description'},"
				+ "{name:'product.expiry_date',label:'有效期',type:'date',value:product.expiry_date},"
				+ "{name:'product.picture',label:'图片',type:'picture',value:product.picture}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'label',text:this.brand},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}";
		
		Object formMap = ArithmeticExpression.execute(form, params);
		String json = JSONObject.fromObject(formMap).toString();
		System.out.println(json);
		
		return json;
	}
	
	public static void main(String[] args) {
		readForm("");
	}
}
