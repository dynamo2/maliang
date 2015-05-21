package com.maliang.core.ui.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	public String edit(String id, Model model) {

		String str = "EditProduct {_id:objectId,name:'发布商品',"
				+ "flows:[{step:1,"
				+ "datas:[{product:db.Product.get(request.id?)}],"
				+ "forms:["
				+ "{name:'product.id',type:'hidden',value:product.id},"
				+ "{name:'product.name',type:'text',value:product.name},"
				+ "{name:'product.brand',type:'select',value:product.brand,"
						+ "values:{list:db.Brand.query({name:''}),key:object.id,value:object.name}},"
				+ "{name:'product.price',type:'text',value:product.price},"
				+ "{name:'product.expiry_date',type:'text',value:product.expiry_date},"
				+ "{name:'product.picture',type:'text',value:product.picture}],"
				+ "response:'forms'},"
				+ "{step:2,function:db.Product.save(request.product)}]}";
		
		String resultJson = "";
		

		model.addAttribute("resultJson", resultJson);

		return "/metadata/edit";
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
	private static void readForm(){
		String paramStr = "{product:{id:'111111',name:'Product',brand:'aaaa',price:335.8,expiry_date:'2015-03-26'},"
				+ "brands:[{id:'aaaa',name:'雪花秀'},{id:'bbbb',name:'希思黎'},{id:'cccc',name:'Pola'}]}";
		Map<String,Object> params = MapHelper.buildAndExecuteMap(paramStr, null);
		System.out.println(params);
		
		String form = "{type:'form',action:'',name:'product.edit.form',"
				+ "inputs:[{name:'product.id',type:'hidden',value:product.id},"
					+ "{name:'product.name',type:'text',value:product.name},"
					+ "{name:'product.brand',type:'select',value:product.brand,"
						+ "options:each(brands){key:this.id,value:this.name}},"
					+ "{name:'product.price',type:'double',value:product.price},"
					+ "{name:'product.expiry_date',type:'date',value:product.expiry_date},"
					+ "{name:'product.picture',type:'file',value:product.picture}]}";
		
		//form = "{type:'form'}";

		Map<String,Object> formMap = MapHelper.buildAndExecuteMap(form, params);
		System.out.println(formMap);
		
		//System.out.println(JSONObject.fromObject(formMap).toString());
	}
	
	public static void main(String[] args) {
		readForm();
	}
}
