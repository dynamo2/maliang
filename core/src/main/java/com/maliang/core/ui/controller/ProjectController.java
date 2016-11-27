package com.maliang.core.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.util.StringUtil;

@Controller
@RequestMapping(value = "project")
public class ProjectController extends BasicController {
	
	@RequestMapping(value = "get.htm")
	@ResponseBody
	public String get(String id) {
		Project project = this.projectDao.getByID(id);
		return this.json(project);
	}
	
	@RequestMapping(value = "edit.htm")
	@ResponseBody
	public String edit(String id) {
		Project project = this.projectDao.getByID(id);
		if(project == null){
			project = new Project();
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		
		String s = "{json:['form','project',"
						+ "[['$id','','hidden',project.id,'[n]'],"
							+ "['$name','名称','text',project.name,'[n]'],"
							+ "['$key','key','text',project.key,'[n]']]]}";
		Object val = AE.execute(s, newMap("project",project));
		
		return this.json(val);
	}
	
	@RequestMapping(value = "save.htm", method = RequestMethod.POST)
	@ResponseBody
	public String save(HttpServletRequest request, Model model) {
		Project project = readFromRequest(request);
		
		System.out.println("=== project : " + project);
		this.projectDao.save(project);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("project",project);
		
		String s = "{uctype:[uctype.name,uctype.key,''+uctype.units,[['button','编辑','edit(\"'+uctype.id+'\")']]]}";
		s = "{success:1}";
		Object val = AE.execute(s, params);
		
		return this.json(project);
	}
	
	@RequestMapping(value = "ajaxList.htm")
	@ResponseBody
	public String ajaxList() {
		List<Project> projects = projectDao.list();
		
		List<Object> results = new ArrayList<Object>();
		List<String> pids = new ArrayList<String>();
		for(Project p:projects){
			pids.add(p.getId().toString());
			Map<String,Object> params = newMap("project",p);
			
			String s = "{text:project.name+'('+project.key+')',category:'project',key:project.id,parent:'root'}";
			Object val = AE.execute(s,params);
			results.add(val);
			
			String ids = p.getId().toString();
			results.addAll(metadataNodes(newMap("project",ids),newMap("parent",ids)));
		}
		
		Map query = (Map)AE.execute("{project:{$nin:pids}}",newMap("pids",pids));
		results.addAll(metadataNodes(query,newMap("parent","root")));

		return this.json(newMap("result",results));
	}
	
	private List<Object> metadataNodes(Map query,Map<String,Object> params){
		List<ObjectMetadata> metadatas = this.metadataDao.list(query);
		params.put("metadatas", metadatas);
		
		String s = "each(metadatas){{text:this.name+'('+this.label+')',category:'metadata',key:this.id,parent:parent}}";
		return (List<Object>)AE.execute(s,params);
	}
	
	
	private Project readFromRequest(HttpServletRequest request){
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		JSONArray ja = (JSONArray)json.get("project");
		
		Project project = (Project)JSONObject.toBean(ja.getJSONObject(0), Project.class);
		
		String jid = (String)ja.getJSONObject(0).get("id");
		if(!StringUtil.isEmpty(jid)){
			//project.setId(jid);
		}
		
		return project;
	}
}
