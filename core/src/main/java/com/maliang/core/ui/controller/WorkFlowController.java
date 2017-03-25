package com.maliang.core.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.HtmlTemplateReplacer;
import com.maliang.core.arithmetic.function.SessionFunction;
import com.maliang.core.exception.TianmaException;
import com.maliang.core.exception.TurnToPage;
import com.maliang.core.model.Block;
import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;
import com.maliang.core.service.BusinessService;
import com.maliang.core.util.Utils;

@Controller
@RequestMapping(value = "flows")
public class WorkFlowController extends BasicController {
	BusinessService businessService = new BusinessService();
	static Map<String, Map<String, String>> CLASS_LABELS = new HashMap<String, Map<String, String>>();
	
	@RequestMapping(value = "flow.htm")
	public String business(Model model, HttpServletRequest request) {
		try {
			Workflow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow, request);

			model.addAttribute("resultJson", resultJson);
			return "/business/flow";
		} catch (TurnToPage page) {
			model.addAttribute("resultJson", json(page.getResult()));
			return "/business/flow";
		} catch (TianmaException e) {
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}
	
	@RequestMapping(value = "ajax.htm")
	@ResponseBody
	public String ajaxBusiness(HttpServletRequest request) {
		Workflow workFlow = readWorkFlow(request);

		return this.executeAjaxWorkFlow(workFlow, request);
	}

	@RequestMapping(value = "js.htm")
	@ResponseBody
	public String javaScript(HttpServletRequest request) {
		Workflow workFlow = readWorkFlow(request);
		return workFlow.getJavaScript();
	}
	
	private String executeAjaxWorkFlow(Workflow flow, HttpServletRequest request) {
		Map<String, Object> params = executeCode(flow, request);
		Object ajaxMap = AE.execute(flow.getAjax(), params);
		return this.json(ajaxMap);
	}
	
	private Workflow readWorkFlow(HttpServletRequest request) {
		String businessId = request.getParameter("bid");
		String businessName = request.getParameter("bn");
		int flowStep = this.getInt(request, "fid", -1);

		Business business = businessDao.getByID(businessId);
		if (business == null) {
			business = businessDao.getByName(businessName);
		}

		request.getSession().setAttribute("SYS_BUSINESS",business);
		
		if (business == null) {
			return null;
		}
		
		Workflow flow = business.workFlow(flowStep);
		businessService.readBlock(flow,business.getUniqueCode(),Block.TYPE_CODE);
		
		return flow;
	}
	
	private String executeWorkFlow(Workflow flow, HttpServletRequest request) {
		Map<String, Object> params = executeCode(flow, request);
		Object responseMap = AE.execute(flow.getResponse(),
				params);

		String response = JSONObject.fromObject(responseMap).toString();

		Business business = (Business)Utils.getSessionValue("SYS_BUSINESS");
		response = businessService.readBlock(response,business.getUniqueCode(),Block.TYPE_HTML);
		
		return new HtmlTemplateReplacer(response).replace(null);
	}
	
	private Map<String, Object> executeCode(Workflow flow,
			HttpServletRequest request) {
		Map<String, Object> params = readRequestParameters(request);
		AE.execute(flow.getCode(), params);

		return params;
	}
	
	private Map<String, Object> readRequestParameters(HttpServletRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SessionFunction.HTTP_REQUEST_KEY, request);
		params.put("request", this.readRequestMap(request));

		params.put("bid", request.getParameter("bid"));
		params.put("bn", request.getParameter("bn"));
		params.put("fid", getInt(request, "fid", -1));

		return params;
	}
	
	
	
	
	
	/*****
	 * TEST
	 * 
	 * *****/
	
	@RequestMapping(value = "request.htm")
	@ResponseBody
	public String request(Model model, HttpServletRequest request) {
		Map<String, Object> params = readRequestMap(request);
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
			JSONArray ja = (JSONArray) json.get(reqName);
			
			Object reqValue = null;
			if(ja.size() == 1){
				reqValue = ((JSONArray) json.get(reqName)).get(0);
			}else if(ja.size() > 1){
				reqValue = ja;
			}
			if (reqValue == null)
				continue;

			if(reqName.endsWith("[]")){
				reqName = reqName.substring(0,reqName.length()-2);
			}
			setValue(reqMap, reqName, reqValue);
		}

		return reqMap;
	}
}
