package com.maliang.core.ui.controller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.bson.types.ObjectId;
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
import com.maliang.core.model.Workflow;
import com.maliang.core.service.BusinessService;
import com.maliang.core.service.MapHelper;

@Controller
@RequestMapping(value = "business")
public class BusinessController extends BasicController {
	static BusinessDao businessDao = new BusinessDao();
	static BusinessService businessService = new BusinessService();
	static Map<String, Map<String, String>> CLASS_LABELS = new HashMap<String, Map<String, String>>();
	static final String BUSINESS_LIST;

	static {
		Map<String, String> blMap = new LinkedHashMap<String, String>();
		blMap.put("name", "名称");
		blMap.put("workflows", "流程");
		blMap.put("blocks", "代码块");
		CLASS_LABELS.put(Business.class.getCanonicalName(), blMap);

		Map<String, String> wfMap = new LinkedHashMap<String, String>();
		wfMap.put("step", "step");
		wfMap.put("requestType", "requestType");
		wfMap.put("code", "code");
		wfMap.put("response", "response");
		wfMap.put("javaScript", "javaScript");
		wfMap.put("ajax", "ajax");
		CLASS_LABELS.put(Workflow.class.getCanonicalName(), wfMap);

		BUSINESS_LIST = "{list:each(list){{" + "name:this.name,"
				+ "id:this.id+''" + "}}}";
	}

	/*************** new code start ***********************/

	@RequestMapping(value = "main.htm")
	public String main(Model model) {
		List<Business> blist = businessDao.list();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list", blist);

		Object editMap = ArithmeticExpression.execute(BUSINESS_LIST, params);
		String resultJson = this.json(editMap);

		model.addAttribute("resultJson", resultJson);
		return "/business/main";
	}

	@RequestMapping(value = "business2.htm")
	public String business2(Model model, HttpServletRequest request) {
		try {
			Workflow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow, request);

			model.addAttribute("resultJson", resultJson);
			return "/business/business2";
		} catch (TurnToPage page) {
			model.addAttribute("resultJson", json(page.getResult()));
			return "/business/business2";
		} catch (TianmaException e) {
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}

	@RequestMapping(value = "request.htm")
	@ResponseBody
	public String request(Model model, HttpServletRequest request) {
		Map<String, Object> params = readRequestMap(request);
		System.out.println("================ params : " + params);
		System.out
				.println("================ province : "
						+ MapHelper.readValue(params,
								"request.order.address.province"));

		return params.toString();
	}

	protected Map<String, Object> readRequestMap(HttpServletRequest request) {
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		List<String> reqNames = json.names();

		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (reqNames == null || reqNames.size() == 0) {
			return reqMap;
		}

		for (String reqName : reqNames) {
			Object reqValue = ((JSONArray) json.get(reqName)).get(0);
			if (reqValue == null)
				continue;

			setValue(reqMap, reqName, reqValue);
		}

		return reqMap;
	}

	@RequestMapping(value = "workFlow.htm")
	@ResponseBody
	public String workFlow(String id, HttpServletRequest request) {
		Workflow workFlow = businessDao.getWorkFlowById(id);
		
		String json = this.json(workFlow);
		System.out.println("work flow json : " + json);

		return json;
	}

	@RequestMapping(value = "saveWorkFlow.htm")
	@ResponseBody
	public String saveWorkFlow(HttpServletRequest request) {
		Map<String, Object> reqMap = this.readRequestMapNotJSONFilter(request);
		
		Map<String,Object> business = (Map<String,Object>)reqMap.get("business");
		this.businessDao.updateBySet(business);

		Business nb = businessDao.getByID(business.get("id").toString());
		return JSONObject.fromObject(nb, defaultJsonConfig).toString();
	}

	/*************** new code end ***********************/

	@RequestMapping(value = "edit.htm")
	public String edit(String id, Model model, HttpServletRequest request) {
		Business business = businessDao.getByID(id);
		if (business == null) {
			business = new Business();
		}
		
		String json = JSONObject.fromObject(business, defaultJsonConfig).toString();
		//System.out.println("================ business json : " + json);

		Map<String, Object> bMap = buildInputsMap(business, CLASS_LABELS,"business");
		String resultJson = JSONObject.fromObject(bMap).toString();
		
		model.addAttribute("resultJson", json);

		return "/business/edit2";
	}

	@RequestMapping(value = "list.htm")
	public String list(Model model, HttpServletRequest request) {
		List<Business> blist = businessDao.list();
		Map<String, String> labels = new HashMap<String, String>();
		labels.put("name", "名称");

		Map map = buildULListMap(blist, labels);
		String resultJson = JSONObject.fromObject(map).toString();

		model.addAttribute("resultJson", resultJson);
		return "/business/list";
	}

	@RequestMapping(value = "save.htm")
	public String save(Model model, HttpServletRequest request) {
		Map<String, Object> reqMap = this.readRequestMapNotJSONFilter(request);
		Map<String, Object> busMap = (Map<String, Object>) reqMap
				.get("business");

		Business busi = buildToObject(busMap, Business.class);

		businessDao.save(busi);

		// return this.list(model, request);
		return this.edit(busi.getId().toString(), model, request);
	}

	@RequestMapping(value = "business.htm")
	public String business(Model model, HttpServletRequest request) {
		try {
			Workflow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow, request);

			model.addAttribute("resultJson", resultJson);
			return "/business/business";
		} catch (TianmaException e) {
			// e.printStackTrace();
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}

	@RequestMapping(value = "ajax.htm")
	@ResponseBody
	public String ajaxBusiness(HttpServletRequest request) {
		Workflow workFlow = readWorkFlow(request);

		String json = this.executeAjaxWorkFlow(workFlow, request);
		System.out.println("ajax return json : " + json);
		return json;
	}

	@RequestMapping(value = "js.htm")
	@ResponseBody
	public String javaScript(HttpServletRequest request) {
		Workflow workFlow = readWorkFlow(request);
		return workFlow.getJavaScript();
	}

	@SuppressWarnings("rawtypes")
	private Map buildULListMap(List<Business> list, Map<String, String> labels) {

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(Business.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			List dataList = new ArrayList();
			for (Business obj : list) {
				Map bdm = new HashMap();

				for (PropertyDescriptor pd : pds) {
					String fname = pd.getName();
					String label = labels.get(fname);
					if (label == null)
						continue;

					Map nm = new HashMap();
					nm.put("type", "label");
					nm.put("text", pd.getReadMethod().invoke(obj));

					bdm.put(fname, nm);
				}

				List ol = new ArrayList();
				Map om = new HashMap();
				om.put("type", "a");
				om.put("href", "/business/edit.htm?id=" + obj.getId());
				om.put("text", "编辑");
				ol.add(om);

				om = new HashMap();
				om.put("type", "a");
				om.put("href", "/business/business.htm?bid=" + obj.getId());
				om.put("text", "执行");
				om.put("target", "_blank");
				ol.add(om);
				bdm.put("operator", ol);

				dataList.add(bdm);
			}

			List headerList = new ArrayList();
			for (Map.Entry<String, String> len : labels.entrySet()) {
				Map<String, String> lm = new HashMap<String, String>();
				lm.put("name", len.getKey());
				lm.put("label", len.getValue());

				headerList.add(lm);
			}
			Map om = new HashMap();
			om.put("name", "operator");
			om.put("label", "操作");
			headerList.add(om);

			Map ulMap = new HashMap();
			ulMap.put("header", headerList);
			ulMap.put("data", dataList);

			Map resultMap = new HashMap();
			resultMap.put("ul-list", ulMap);

			return resultMap;
		} catch (Exception e) {
			return null;
		}
	}

	private Workflow readWorkFlow(HttpServletRequest request) {
		String businessId = request.getParameter("bid");
		String businessName = request.getParameter("bn");
		int flowStep = this.getInt(request, "fid", -1);

		Business business = businessDao.getByID(businessId);
		if (business == null) {
			business = businessDao.getByName(businessName);
		}

		if (business == null) {
			return null;
		}
		
		Workflow flow = business.workFlow(flowStep);
		businessService.readBlock(flow,business.getUniqueCode());
		
		return flow;
	}

	private String executeWorkFlow(Workflow flow, HttpServletRequest request) {
		Map<String, Object> params = executeCode(flow, request);
		Object responseMap = ArithmeticExpression.execute(flow.getResponse(),
				params);

		return JSONObject.fromObject(responseMap).toString();
	}

	private String executeAjaxWorkFlow(Workflow flow, HttpServletRequest request) {
		Map<String, Object> params = executeCode(flow, request);

		Object ajaxMap = ArithmeticExpression.execute(flow.getAjax(), params);

		return JSONObject.fromObject(ajaxMap).toString();
	}

	private Map<String, Object> executeCode(Workflow flow,
			HttpServletRequest request) {
		// Map<String,Object> params =
		// readRequestParameters(flow.getRequestType(),request);
		Map<String, Object> params = readRequestParameters(request);

		System.out.println("=========== params ==============");
		System.out.println("executeCode params : " + params);
		ArithmeticExpression.execute(flow.getCode(), params);

		return params;
	}

	private Map<String, Object> readRequestParameters(HttpServletRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SessionFunction.HTTP_REQUEST_KEY, request);
		params.put("request", this.readRequestMap(request));

		params.put("bid", request.getParameter("bid"));
		params.put("bn", request.getParameter("bn"));
		params.put("fid", getInt(request, "fid", -1));

		// System.out.println("readRequestParameters : " + params);
		return params;
	}
	
	public static void main(String[] args) throws Exception {

		/*
		 * List<Business> bs = businessDao.list();
		 * 
		 * Business business = bs.get(0); business = new Business();
		 * Map<String,Object> bMap =
		 * buildInputsMap(business,CLASS_LABELS,"business"); String json =
		 * JSONObject.fromObject(bMap).toString(); System.out.println(json);
		 */

		// readForm(null);

		String str = "{components:[{htmlProps:{tag:'form',id:'product.search.form'},"
				+ "components:[{type:'formInputs',inputs:["
				+ "{name:'product.name',label:'名称',type:'text'},"
				+ "{name:'product.brand',label:'品牌',type:'select',options:each(brands){{value:this.id,text:this.name}}},"
				+ "{name:'product.price',label:'价格',type:'number'}]}]},"
				+ "{type:'datatables',"
				+ "htmlProps:{id:'productsTable'},"
				+ "ajax:'/business/ajax.htm?bid=2',"
				+ "header:['名称','品牌','价格','图片','操作']}]}";

		// str = "{accounts:db.Account.search()}";
		// Map<String,Object> params =
		// (Map<String,Object>)ArithmeticExpression.execute(str, null);
		//
		// str =
		// "{html:'<table cellspacing='0' cellpadding='0' class='list'>'+sum(each(accounts){'<tr><td>'+this.account+'</td></td>'+this.password+'</td></tr>'})+'</table>'}";
		// Object ov = ArithmeticExpression.execute(str, params);

		// System.out.println(ov);

		Map<String, Object> rootMap = new HashMap<String, Object>();

		setValue(rootMap, "order.info.number", "200103040505");
		setValue(rootMap, "order.info.name", "淘宝订单");
		setValue(rootMap, "order.info.date", "2015-03-04");
		setValue(rootMap, "order.items.0.0.product", "神仙水330ml");
		setValue(rootMap, "order.items.0.0.warehouse", "六合仓库");
		setValue(rootMap, "order.items.0.0.num", "3");
		setValue(rootMap, "order.items.1.0", "A");
		setValue(rootMap, "order.items.1.1", "B");
		setValue(rootMap, "order.items.1.2", "C");
		setValue(rootMap, "product", "神仙水330ml");
		setValue(rootMap, "product.name", "神仙水330ml");
		setValue(rootMap, "product.brand", "SK2");

		// setValue(rootMap,"order.items.1.product","HR/赫莲娜绿宝瓶悦活新生精华30ml");
		// setValue(rootMap,"order.items.0.product","神仙水330ml");
		// setValue(rootMap,"order.items.0.num","3");
		// setValue(rootMap,"order.items.1.num","8");

		System.out.println(rootMap);

	}
}

class TOStringProcessor implements JsonValueProcessor{

	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		if(arg0 == null)return null;
		
		return arg0.toString();
	}

	public Object processObjectValue(String arg0, Object arg1,
			JsonConfig arg2) {
		if(arg1 == null)return null;
		
		return arg1.toString();
	}
}
