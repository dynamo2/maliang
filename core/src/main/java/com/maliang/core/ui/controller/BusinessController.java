package com.maliang.core.ui.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.function.SessionFunction;
import com.maliang.core.dao.BusinessDao;
import com.maliang.core.exception.TianmaException;
import com.maliang.core.exception.TurnToPage;
import com.maliang.core.model.Business;
import com.maliang.core.model.WorkFlow;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.StringUtil;

@Controller
@RequestMapping(value = "business")
public class BusinessController extends BasicController {
	static BusinessDao businessDao = new BusinessDao();
	static Map<String,Map<String,String>> CLASS_LABELS = new HashMap<String,Map<String,String>>();
	static final String BUSINESS_LIST;
	
	static {
		Map<String,String> blMap = new LinkedHashMap<String,String>();
		blMap.put("name", "名称");
		blMap.put("workFlows", "流程");
		CLASS_LABELS.put(Business.class.getCanonicalName(), blMap);
		
		Map<String,String> wfMap = new LinkedHashMap<String,String>();
		wfMap.put("step", "step");
		wfMap.put("requestType", "requestType");
		wfMap.put("code", "code");
		wfMap.put("response", "response");
		wfMap.put("javaScript", "javaScript");
		wfMap.put("ajax", "ajax");
		CLASS_LABELS.put(WorkFlow.class.getCanonicalName(), wfMap);
		
		
		BUSINESS_LIST = "{list:each(list){{"
				+ "name:this.name,"
				+ "id:this.id+''"
			+ "}}}";
	}
	
	/*************** new code start ***********************/
	
	@RequestMapping(value = "main.htm")
	public String main(Model model) {
		List<Business> blist = businessDao.list();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("list",blist);
		
		Object editMap =  ArithmeticExpression.execute(BUSINESS_LIST, params);
		String resultJson = this.json(editMap);
		
		model.addAttribute("resultJson",resultJson);
		return "/business/main";
	}
	
	@RequestMapping(value = "business2.htm")
	public String business2(Model model,HttpServletRequest request) {
		try {
			WorkFlow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow,request);
			
			model.addAttribute("resultJson", resultJson);
			return "/business/business2";
		}catch(TurnToPage page){
			model.addAttribute("resultJson", json(page.getResult()));
			return "/business/business2";
		}catch(TianmaException e){
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}
	
	@RequestMapping(value = "request.htm")
	@ResponseBody
	public String request(Model model,HttpServletRequest request) {
		Map<String,Object> params = readRequestMap(request);
		System.out.println("================ params : " + params);
		System.out.println("================ province : " + MapHelper.readValue(params, "request.order.address.province"));
		
		return params.toString();
	}
	
	protected Map<String,Object> readRequestMap(HttpServletRequest request){
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		List<String> reqNames = json.names();
		
		Map<String,Object> reqMap = new HashMap<String,Object>();
		if(reqNames == null || reqNames.size() == 0){
			return reqMap;
		}
		
		for(String reqName:reqNames){
			Object reqValue = ((JSONArray)json.get(reqName)).get(0);
			if(reqValue == null)continue;

			setValue(reqMap,reqName,reqValue);
		}
		
		return reqMap;
	}
	
	private static void setValue(Map<String,Object> rootMap,String reqName,Object reqValue){
		if(reqName == null)return;
		
		Object lastKey = null;
		Object lastParent = rootMap;
		Object parent = rootMap;
		String[] parents = reqName.split("\\.");
		for(int i = 0; i < parents.length; i++){
			String name = parents[i];
			boolean last = i == parents.length-1;

			Integer index = null;
			try {
				index = Integer.parseInt(name);
			}catch(Exception e){
				index = null;
			}
			
			if(index != null && index >= 0){
				List list = null;
				if(parent instanceof List){
					list = (List)parent;
				}else {
					list = new ArrayList();
					
					if(lastParent instanceof Map){
						((Map)lastParent).put(lastKey, list);
					}else if(lastParent instanceof List){
						if(lastKey instanceof Integer){
							((List)lastParent).set((Integer)lastKey,list);
						}
					}
				}
				
				if(index >= list.size()){
					int ii = index - list.size();
					while(ii-- >= 0){
						list.add(null);
					}
				}
				
				if(last){
					list.set(index,reqValue);
					break;
				}
				
				Object temp = list.get(index);
				if(temp == null){
					temp = new HashMap();
					list.set(index, temp);
				}
				
				parent = temp;
				lastParent = list;
				lastKey = index;
			}else {
				if(!(parent instanceof Map)){
					parent = new HashMap();
					if(lastParent instanceof Map){
						((Map)lastParent).put(lastKey, parent);
					}else if(lastParent instanceof List && lastKey instanceof Integer){
						((List)lastParent).set((Integer)lastKey, parent);
					}
				}
				
				if(last){
					((Map)parent).put(name,reqValue);
					break;
				}
				
				Object temp = ((Map)parent).get(name);
				if(temp == null){
					temp = new HashMap<String,Object>();
					((Map)parent).put(name,temp);
				}
				
				lastKey = name;
				lastParent = parent;
				parent = temp;
			}
		}
	}
	
	protected Map<String,Object> readRequestMap222(HttpServletRequest request){
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		List<String> reqNames = json.names();
		
		Map<String,Object> reqMap = new HashMap<String,Object>();
		List<String> ignorePrefix = new ArrayList<String>();
		if(reqNames == null || reqNames.size() == 0)return reqMap;
		
		for(String reqName:reqNames){
			if(reqName.endsWith(".maxIndex")){
				String prefix = reqName.substring(0,reqName.length()-".maxIndex".length());
				readList(request,prefix,reqMap);
				ignorePrefix.add(prefix);
			}
		}
		
		for(String reqName:reqNames){
			boolean ingore = false;
			for(String inPre:ignorePrefix){
				if(reqName.startsWith(inPre)){
					ingore = true;
					break;
				}
			}
			if(ingore)continue;
			
			Object reqValue = ((JSONArray)json.get(reqName)).get(0);
			if(reqValue == null)continue;
			
			String[] reqs = reqName.split("\\.");
			Map<String,Object> parentMap = reqMap;
			for(int i = 0; i < reqs.length-1; i++){
				String req = reqs[i];
				Object val = parentMap.get(req);
				Map<String,Object> preMap = null;//(Map<String,Object>)parentMap.get(req);
				if(val == null || !(val instanceof Map)){
					preMap = new HashMap<String,Object>();
					parentMap.put(req, preMap);
				}else {
					preMap = (Map<String,Object>)val;
				}
				parentMap = preMap;
			}
			parentMap.put(reqs[reqs.length-1], reqValue);
		}
		
		return reqMap;
	}
	
	/*************** new code end ***********************/
	
	@RequestMapping(value = "edit.htm")
	public String edit(String id,Model model,HttpServletRequest request) {
		Business business = businessDao.getByID(id);
		if(business == null){
			business = new Business();
		}
		
		Map<String,Object> bMap = buildInputsMap(business,CLASS_LABELS,"business");
		String resultJson = JSONObject.fromObject(bMap).toString();
		model.addAttribute("resultJson", resultJson);
		
		System.out.println(business.getName()+" bid : " + business.getId());
		
		return "/business/edit";
	}
	
	@RequestMapping(value = "list.htm")
	public String list(Model model,HttpServletRequest request) {
		List<Business> blist = businessDao.list();
		Map<String,String> labels = new HashMap<String,String>();
		labels.put("name", "名称");
		
		Map map = buildULListMap(blist,labels);
		String resultJson = JSONObject.fromObject(map).toString();
		
		model.addAttribute("resultJson", resultJson);
		return "/business/list";
	}
	
	@RequestMapping(value = "save.htm")
	public String save(Model model,HttpServletRequest request) {
		Map<String,Object> reqMap = this.readRequestMap(request);
		Map<String,Object> busMap = (Map<String,Object>)reqMap.get("business");
		
		Business busi = buildToObject(busMap,Business.class);

		businessDao.save(busi);
		
		//return this.list(model, request);
		return this.edit(busi.getId().toString(), model, request);
	}
	
	@RequestMapping(value = "business.htm")
	public String business(Model model,HttpServletRequest request) {
		try {
			WorkFlow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow,request);
			
			model.addAttribute("resultJson", resultJson);
			return "/business/business";
		}catch(TianmaException e){
			//e.printStackTrace();
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}
	
	@RequestMapping(value = "ajax.htm")
	@ResponseBody
	public String ajaxBusiness(HttpServletRequest request) {
		WorkFlow workFlow = readWorkFlow(request);
		
		String json = this.executeAjaxWorkFlow(workFlow,request);
		System.out.println("ajax return json : " + json);
		return json;
	}
	
	@RequestMapping(value = "js.htm")
	@ResponseBody
	public String javaScript(HttpServletRequest request) {
		WorkFlow workFlow = readWorkFlow(request);
		return workFlow.getJavaScript();
	}
	
	@SuppressWarnings("rawtypes")
	private Map buildULListMap(List<Business> list,Map<String,String> labels){
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(Business.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			List dataList = new ArrayList();
			for(Business obj : list){
				Map bdm = new HashMap();
				
				for(PropertyDescriptor pd : pds){
					String fname = pd.getName();
					String label = labels.get(fname);
					if(label == null)continue;
					
					Map nm = new HashMap();
					nm.put("type", "label");
					nm.put("text", pd.getReadMethod().invoke(obj));
					
					bdm.put(fname, nm);
				}
				
				List ol = new ArrayList();
				Map om = new HashMap();
				om.put("type", "a");
				om.put("href","/business/edit.htm?id="+obj.getId());
				om.put("text", "编辑");
				ol.add(om);
				
				om = new HashMap();
				om.put("type", "a");
				om.put("href","/business/business.htm?bid="+obj.getId());
				om.put("text", "执行");
				om.put("target", "_blank");
				ol.add(om);
				bdm.put("operator", ol);
				
				dataList.add(bdm);
			}
			
			List headerList = new ArrayList();
			for(Map.Entry<String,String> len : labels.entrySet()){
				Map<String,String> lm = new HashMap<String,String>();
				lm.put("name",len.getKey());
				lm.put("label",len.getValue());
				
				headerList.add(lm);
			}
			Map om = new HashMap();
			om.put("name","operator");
			om.put("label","操作");
			headerList.add(om);
			
			Map ulMap = new HashMap();
			ulMap.put("header", headerList);
			ulMap.put("data", dataList);
			
			Map resultMap = new HashMap();
			resultMap.put("ul-list", ulMap);
			
			return resultMap;
		}catch(Exception e){
			return null;
		}
	}

	private WorkFlow readWorkFlow(HttpServletRequest request){
		String businessId = request.getParameter("bid");
		String businessName = request.getParameter("bn");
		int flowStep = this.getInt(request, "fid",-1);
		
		Business business = businessDao.getByID(businessId);
		if(business == null){
			business = businessDao.getByName(businessName);
		}
		
		if(business == null){
			return null;
		}
		return business.workFlow(flowStep);
	}
	
	private String executeWorkFlow(WorkFlow flow,HttpServletRequest request){
		Map<String,Object> params = executeCode(flow,request);
		Object responseMap = ArithmeticExpression.execute(flow.getResponse(), params);
		
		return JSONObject.fromObject(responseMap).toString();
	}
	
	private String executeAjaxWorkFlow(WorkFlow flow,HttpServletRequest request){
		Map<String,Object> params = executeCode(flow,request);
		
		
		Object ajaxMap = ArithmeticExpression.execute(flow.getAjax(), params);
		
		return JSONObject.fromObject(ajaxMap).toString();
	}
	
	private Map<String,Object> executeCode(WorkFlow flow,HttpServletRequest request){
		//Map<String,Object> params = readRequestParameters(flow.getRequestType(),request);
		Map<String,Object> params = readRequestParameters(request);
		
		System.out.println("=========== params ==============");
		System.out.println("executeCode params : " + params);
		ArithmeticExpression.execute(flow.getCode(), params);

		return params;
	}
	
	private Map<String,Object> readRequestParameters(HttpServletRequest request){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put(SessionFunction.HTTP_REQUEST_KEY, request);
		params.put("request", this.readRequestMap(request));
		
		params.put("bid", request.getParameter("bid"));
		params.put("bn", request.getParameter("bn"));
		params.put("fid", getInt(request, "fid",-1));
		
		//System.out.println("readRequestParameters : " + params);
		return params;
	}
	
	/*
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
			
			//resultMap.put(reqName, reqValue);
			
			writeValue(reqName,reqValue,resultMap);
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("bid", request.getParameter("bid"));
		params.put("request", resultMap);
		
		//System.out.println("readRequestParameters : " + params);
		return params;
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
	
	private static void writeValue(String reqName,Object reqValue,Map<String,Object> resultMap){
		if(reqName == null || !reqName.contains(".")){
			resultMap.put(reqName, reqValue);
			return;
		}
		
		String[] reqs = reqName.split("\\.");
		Map<String,Object> parentMap = resultMap;
		for(int i = 0; i < reqs.length-1; i++){
			String reqKey = reqs[i];
			Object value = parentMap.get(reqKey);
			if(value != null && value instanceof Map){
				parentMap = (Map<String,Object>)value;
				continue;
			}
			
			Map<String,Object> newMap = new HashMap<String,Object>();
			if(value != null){
				newMap.put(reqKey, value);
			}
			parentMap.put(reqKey, newMap);
			parentMap = newMap;
		}
		
		parentMap.put(reqs[reqs.length-1], reqValue);
	}*/
	
	public static void main(String[] args) throws Exception {
		
		/*
		List<Business> bs = businessDao.list();
		
		Business business = bs.get(0);
		business = new Business();
		Map<String,Object> bMap = buildInputsMap(business,CLASS_LABELS,"business");
		String json = JSONObject.fromObject(bMap).toString();
		System.out.println(json);*/
		
		//readForm(null);
		
		String str = "{components:[{htmlProps:{tag:'form',id:'product.search.form'},"
				+ "components:[{type:'formInputs',inputs:["
				+ "{name:'product.name',label:'名称',type:'text'},"
				+ "{name:'product.brand',label:'品牌',type:'select',options:each(brands){{value:this.id,text:this.name}}},"
				+ "{name:'product.price',label:'价格',type:'number'}]}]},"
				+ "{type:'datatables',"
				+ "htmlProps:{id:'productsTable'},"
				+ "ajax:'/business/ajax.htm?bid=2',"
				+ "header:['名称','品牌','价格','图片','操作']}]}";
		
//		str = "{accounts:db.Account.search()}";
//		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(str, null);
//		
//		str = "{html:'<table cellspacing='0' cellpadding='0' class='list'>'+sum(each(accounts){'<tr><td>'+this.account+'</td></td>'+this.password+'</td></tr>'})+'</table>'}";
//		Object ov = ArithmeticExpression.execute(str, params);
		
		//System.out.println(ov);
		
		Map<String,Object> rootMap = new HashMap<String,Object>();
		
		setValue(rootMap,"order.info.number","200103040505");
		setValue(rootMap,"order.info.name","淘宝订单");
		setValue(rootMap,"order.info.date","2015-03-04");
		setValue(rootMap,"order.items.0.0.product","神仙水330ml");
		setValue(rootMap,"order.items.0.0.warehouse","六合仓库");
		setValue(rootMap,"order.items.0.0.num","3");
		setValue(rootMap,"order.items.1.0","A");
		setValue(rootMap,"order.items.1.1","B");
		setValue(rootMap,"order.items.1.2","C");
		setValue(rootMap,"product","神仙水330ml");
		setValue(rootMap,"product.name","神仙水330ml");
		setValue(rootMap,"product.brand","SK2");
		
//		setValue(rootMap,"order.items.1.product","HR/赫莲娜绿宝瓶悦活新生精华30ml");
//		setValue(rootMap,"order.items.0.product","神仙水330ml");
//		setValue(rootMap,"order.items.0.num","3");
//		setValue(rootMap,"order.items.1.num","8");
		
		System.out.println(rootMap);
		
	}
	
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
	 * **
	//@RequestMapping(value = "edit.htm")
	public String edit( Model model,HttpServletRequest request) {

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
		
		String resultJson = null;//product(request);
		
		//System.out.println();
		model.addAttribute("resultJson", resultJson);

		return "/business/edit";
	}
	
	
	
	@RequestMapping(value = "product.htm")
	public String product(Model model,HttpServletRequest request) {
		Map<String,String> businessMap = new HashMap<String,String>();
		
		
		Map<String,String> editProduct = new HashMap<String,String>();
		editProduct.put("step", "1");
		editProduct.put("request_type", "{fid:'int',bid:'int'}");
		editProduct.put("code", "addToParams({p2:db.Product.save(request.product),brands:db.Brand.search(),products:db.Product.search()})");
		editProduct.put("response", "{html_template:'<div id='edit_form'></div>',"
				+ "contents:[{type:'form',html_parent:'edit_form',action:'',name:'product.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'fid',type:'hidden',value:2},"
				+ "{name:'product.id',type:'hidden'},"
				+ "{name:'product.name',label:'名称',type:'text'},"
				+ "{name:'product.brand',label:'品牌',type:'select',options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.price',label:'价格',type:'number'}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'a',href:'/detail.htm?id='+this.brand.id,text:this.brand.name},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}");
		
		String resultJson = executeBusiness(editProduct,request);
		model.addAttribute("resultJson", resultJson);
		
		return "/business/edit";
	}
	
	@RequestMapping(value = "brand.htm")
	public String brand(Model model,HttpServletRequest request) {
		Map<String,String> editBrand = new HashMap<String,String>();
		editBrand.put("step", "1");
		editBrand.put("request_type", "{fid:'int',bid:'int'}");
		editBrand.put("code", "addToParams({p2:db.Brand.save(request.brand),brands:db.Brand.search()})");
		editBrand.put("response", "{html_template:'<div id='edit_form'></div>',"
				+ "contents:[{type:'form',html_parent:'edit_form',action:'',name:'brand.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'fid',type:'hidden',value:2},"
				+ "{name:'brand.id',type:'hidden'},"
				+ "{name:'brand.name',label:'名称',type:'text'}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'operator',label:'操作'}],"
				+ "data:each(brands){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}");

		String resultJson = executeBusiness(editBrand,request);
		model.addAttribute("resultJson", resultJson);
		
		return "/business/edit";
	}
	
	private String executeBusiness(Map<String,String> businessMap,HttpServletRequest request){
		Map<String,Object> params = readRequestParameters(businessMap.get("request_type"),request);
		ArithmeticExpression.execute(businessMap.get("code"), params);
		
		System.out.println(params);
		
		Object responseMap = ArithmeticExpression.execute(businessMap.get("response"), params);
		return JSONObject.fromObject(responseMap).toString();
	}
	
	private String step1(HttpServletRequest request){
		String requestType = "{fid:'int',bid:'int'}";
		Map<String,Object> params = readRequestParameters(requestType,request);
		//System.out.println();
		
		String code = "addToParams({product:db.Product.get(request.id),brands:db.Brand.search(),products:db.Product.search()})";
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
				+ "brand:{type:'a',href:'/detail.htm?id='+this.brand.id,text:this.brand.name},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}";
		
		Object responseMap = ArithmeticExpression.execute(response, params);
		return JSONObject.fromObject(responseMap).toString();
	}
	
	
	
	
	
	
	private Map<String,Object> readRequestParameters2(String requestType,HttpServletRequest request){
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
	 * **
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
		
		form = "{html_template:'<div id='search_form'>\n\n</div>',\n"
				+ "contents:[{type:'form',html_parent:'search_form',action:'edit.html',enctype:'',name:'product.edit.form',\n"
				+ "children:{\n"
				+ "inputs:[\n"
				+ "{name:'product.name',label:'名称',type:'text',value:product.name},\n\n\n"
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
		
		form = "{columnTemplate:['<a href=\"/detail.htm?id=${id}\">${name}</a>',"
				+ "'<a href=\"/detail.htm?id=${brand.id}\">${brand.name}</a>',"
				+ "'${price}',"
				+ "'<img src=\"${picture}\" />',"
				+ "'<a href=\"#\">编辑</a>&nbsp;&nbsp;<a href=\"#\">删除</a>']}";
		
		System.out.println(form);
		
		System.out.println("==============");
		Object formMap = ArithmeticExpression.execute(form, params);
		String json = JSONObject.fromObject(formMap).toString();
		System.out.println(json);
		
		System.out.println("".getClass().getCanonicalName());
		
		return json;
	}*/
}
