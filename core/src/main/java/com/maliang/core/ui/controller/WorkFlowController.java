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
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.SessionUtil;
import com.maliang.core.util.Utils;

@Controller
@RequestMapping(value = "flows")
public class WorkFlowController extends BasicController {
	BusinessService businessService = new BusinessService();
	static Map<String, Map<String, String>> CLASS_LABELS = new HashMap<String, Map<String, String>>();
	
	@RequestMapping(value = "flow.htm")
	public String flow(Model model, HttpServletRequest request) {
		Workflow workFlow = null;
		String response = null;
		try {
			 workFlow = readWorkFlow(request);
			 response = this.executeResponse(workFlow, request);
		}catch (TurnToPage page) {
			boolean breakOut = false;
			while(!breakOut){
				try {
					SessionUtil.put(request,SessionUtil.BUSINESS,page.getBusiness());
					
					workFlow = page.getWorkflow();
					response = this.executeResponse(workFlow, page.getParams());
					breakOut = true;
				}catch (TurnToPage p) {
					page = p;
					breakOut = false;
				}
			}
		} catch (TianmaException e) {
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
		
		
		model.addAttribute("response", response);
		
		setStaticData(workFlow,model);
		
		return "/business/flow";
		
	}
	
	/***
	 * 设置静态数据：CSS,JS,files（*.css,*.js）
	 * **/
	private void setStaticData(Workflow flow,Model model){
		String css = flow.getCss();
		if(css == null)css = "";
		
		String js = flow.getJavaScript();
		if(js == null)js = "";
		
		Object files = flow.getFiles();
		if(files == null)files = "null";
		
		model.addAttribute("js", js);
		model.addAttribute("css",css);
		model.addAttribute("files",files);
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
		String rs = this.json(ajaxMap);
		return rs;
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
		
		//Session cache
		SessionUtil.put(request,SessionUtil.BUSINESS,business);
		
		Workflow flow = business.workFlow(flowStep);
		
		//Session cache
		SessionUtil.put(request,SessionUtil.FLOW,flow);
		
		return flow;
	}

	private String executeResponse(Workflow flow, Map<String, Object> params) {
		if(flow == null){
			throw new TianmaException("页面出错！");
		}
		
		Business business = (Business)Utils.getSessionValue(SessionUtil.BUSINESS);
		businessService.readBlock(flow,business.getUniqueCode(),Block.TYPE_CODE);
		
		/***
		 * add files from business
		 * **/
		List<Map> files = business.getFiles();
		if(files == null){
			files = flow.getFiles();
		}else if(!Utils.isEmpty(flow.getFiles())) {
			files.addAll(flow.getFiles());
		}
		flow.setFiles(files);
		
		AE.execute(flow.getCode(), params);
		
		Object responseMap = AE.execute(flow.getResponse(),params);

		String response = JSONObject.fromObject(responseMap).toString();
		
		response = businessService.readBlock(response,business.getUniqueCode(),Block.TYPE_HTML);
		
		return new HtmlTemplateReplacer(response).replace(null);
	}
	
	private String executeResponse(Workflow flow, HttpServletRequest request) {
		Map<String, Object> params = readRequestParameters(request);
		return this.executeResponse(flow, params);
		
		/*
		 * Map<String, Object> params = executeCode(flow, request);
		Object responseMap = AE.execute(flow.getResponse(),
				params);

		String response = JSONObject.fromObject(responseMap).toString();

		Business business = (Business)Utils.getSessionValue("SYS_BUSINESS");
		response = businessService.readBlock(response,business.getUniqueCode(),Block.TYPE_HTML);
		
		return new HtmlTemplateReplacer(response).replace(null);
		*/
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
