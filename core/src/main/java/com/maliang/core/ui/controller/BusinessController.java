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
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.function.SessionFunction;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.exception.TianmaException;
import com.maliang.core.exception.TurnToPage;
import com.maliang.core.model.Business;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.Workflow;
import com.maliang.core.service.BusinessService;

@Controller
@RequestMapping(value = "business")
public class BusinessController extends BasicController {
	
	BusinessService businessService = new BusinessService();
	static Map<String, Map<String, String>> CLASS_LABELS = new HashMap<String, Map<String, String>>();
	static final String BUSINESS_LIST;

	static {
		BUSINESS_LIST = "{list:each(list){{" + "name:this.name,"
				+ "id:this.id+''" + "}}}";
	}

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
	
	@RequestMapping(value = "delete.htm")
	@ResponseBody
	public String delete(String id) {
		this.businessDao.remove(id);
		return "删除业务： " + id;
	}

	@RequestMapping(value = "business.htm")
	public String business(Model model, HttpServletRequest request) {
		try {
			Workflow workFlow = readWorkFlow(request);
			String resultJson = executeWorkFlow(workFlow, request);

			model.addAttribute("resultJson", resultJson);
			return "/business/business";
		} catch (TurnToPage page) {
			model.addAttribute("resultJson", json(page.getResult()));
			return "/business/business";
		} catch (TianmaException e) {
			model.addAttribute("errorMsg", e.getMessage());
			return "/business/error";
		}
	}

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

	@RequestMapping(value = "workFlow.htm")
	@ResponseBody
	public String workFlow(String id, HttpServletRequest request) {
		Workflow workFlow = businessDao.getWorkFlowById(id);
		
		String json = this.json(workFlow);

		return json;
	}

	@RequestMapping(value = "save.htm")
	@ResponseBody
	public String saveWorkFlow(HttpServletRequest request) {
		Map<String, Object> reqMap = this.readRequestMapNotJSONFilter(request);
		
		Map<String,Object> business = (Map<String,Object>)reqMap.get("business");
		this.businessDao.updateBySet(business);

		Business nb = businessDao.getByID(business.get("id").toString());
		return this.json(nb);
	}
	
	@RequestMapping(value = "add.htm")
	@ResponseBody
	public String add(HttpServletRequest request) {
		Map<String, Object> reqMap = this.readRequestMapNotJSONFilter(request);
		
		Map<String,Object> business = (Map<String,Object>)reqMap.get("business");
		this.businessDao.save(business);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list", businessDao.list());

		Object editMap = AE.execute(BUSINESS_LIST, params);
		return this.json(editMap);
	}
	
	@RequestMapping(value = "edit2.htm")
	@ResponseBody
	public String edit2(String id) {
		Business business = this.businessDao.getByID(id);
		if(business == null){
			business = new Business();
		}

		List<Project> projects = this.projectDao.list();
		Map<String,Object> params = newMap("business",business);
		params.put("projects", projects);

		String s = "{json:['form','business',"
						+ "[['id','','hidden',business.id,'[n]'],"
							+ "['project','所属项目',['select',each(projects){{key:this.id,label:this.name}}],metadata.project.id,'[n]'],"
							+ "['name','名称','text',business.name,'[n]'],"
							+ "['uniqueCode','唯一代码','text',business.uniqueCode,'[n]']]]}";
		Object val = AE.execute(s, params);
		
		return this.json(val);
	}

	@RequestMapping(value = "saveMove.htm")
	@ResponseBody
	public String saveMove(String id,String pid) {
		Business business = this.businessDao.getByID(id);
		Project project = this.projectDao.getByID(pid);
		
		if(business != null && project != null){
			if(!isSameProject(project,business)){
				Business newBus = new Business();
				
				newBus.setId(business.getId());
				newBus.setProject(project);
				
				this.businessDao.save(newBus);
			}
		}
		
		return "{}";
	}
	
	private boolean isSameProject(Project project,Business business){
		return business != null 
				&& project != null
				&& project.getId() != null
				&& business.getProject() != null 
				&& project.getId().equals(business.getProject().getId());
	}

	@RequestMapping(value = "edit.htm")
	public String edit(String id, Model model, HttpServletRequest request) {
		Business business = businessDao.getByID(id);
		if (business == null) {
			business = new Business();
		}
		
		model.addAttribute("resultJson", json(business));
		return "/business/edit";
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
		
		System.out.println("---------- executeAjaxWorkFlow rquest : " + params);

		Object ajaxMap = ArithmeticExpression.execute(flow.getAjax(), params);

		return JSONObject.fromObject(ajaxMap).toString();
	}

	private Map<String, Object> executeCode(Workflow flow,
			HttpServletRequest request) {
		Map<String, Object> params = readRequestParameters(request);
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
	
	public static void main(String[] args) {
		ObjectMetadataDao omDao = new ObjectMetadataDao();
		ObjectMetadata om = omDao.getByName("Project");
		
		String omName = om.getName().toLowerCase();		
		String form = "['form','"+omName+"',[['$fid','','hidden',2],['$bid','','hidden',bid],";
		for(ObjectField f : om.getFields()){
			String type = "'text'";
			if(FieldType.DATE.is(f.getType())){
				type = "'date'";
			}else if(FieldType.LINK_COLLECTION.is(f.getType())){
				type = "['select',each("+f.getLinkedObject()+"){{key:this.id,label:this.name}}]";
			}
			
			form += "['"+f.getName()+"','"+f.getLabel()+"',"+type+","+omName+"."+f.getName()+",'[n]'],";//f.getName()+","+f.getLabel()+",";
		}
		
		form += "['$submit','','submit','保存','[n]']]]";
		
		String responce = "{title:'编辑"+om.getLabel()+"',json:[${NAV},"+form+"]}";
		System.out.println(responce);
		
		
		String editCode = "{addToParams({"+omName+":db."+om.getName()+".get(request.id)})}";
		System.out.println(editCode);
		
		String saveCode = "addToParams({c:db."+om.getName()+".save(request."+omName+")})";
		String saveResponce = "business({bid:bid,fid:3})";
		System.out.println(saveCode);
		System.out.println(saveResponce);
		
		String head = "[";
		String tbody = "each("+omName+"s){[";
		for(ObjectField f : om.getFields()){
			head += "'"+f.getLabel()+"',";
			tbody += "this."+f.getName()+",";
		}
		head += "'操作']";
		tbody += "[['a','编辑',{id:this.id}],['a','库存',{id:this.id,fid:5}]]]}";
		String tableList = "['tableList',"+head+","+tbody+"]";
		String listResponce = "{title:'"+om.getLabel()+"列表',date:{bid:bid},json:[${SYS.NAV},${NAV},"+tableList+"]}";
		System.out.println("============ List Responce ===============");
		System.out.println(listResponce);
	}
}