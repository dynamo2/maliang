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
import com.maliang.core.model.Business;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.Subproject;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

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
	
	@RequestMapping(value = "subproject.htm")
	@ResponseBody
	public String subproject(String id,String pid) {
		Subproject subp = this.projectDao.getSubprojectById(id);
		Project project = this.projectDao.getByID(pid);
		
		Map<String,Object> params = newMap("subproject",subp);
		params.put("project", project);
		
		String s = "{json:['form','subproject',"
						+ "[['$pid','','hidden',project.id,'[n]'],"
							+ "['id','','hidden',subproject.id,'[n]'],"
							+ "['$pname','项目','label',project.name,'[n]'],"
							+ "['name','子项目名称','text',subproject.name,'[n]'],"
							+ "['key','key','text',subproject.key,'[n]']]]}";
		Object val = AE.execute(s, params);
		
		return this.json(val);
	}
	
	@RequestMapping(value = "saveSubproject.htm", method = RequestMethod.POST)
	@ResponseBody
	public String saveSub(HttpServletRequest request, Model model) {
		Subproject subp = this.readMongodbModel(request, "subproject", Subproject.class);
		String pid = request.getParameter("pid");
		
		this.projectDao.saveSubproject(pid, subp);
		return this.json(subp);
	}
	
	@RequestMapping(value = "deleteSubproject.htm")
	@ResponseBody
	public String deleteSubproject(HttpServletRequest request) {
		String id = request.getParameter("id");
		
		List<Business> bs = this.businessDao.listBySubproject(id);
		if(!Utils.isEmpty(bs)){
			return this.json("{result:0,error:'不能删除，还有业务模块"+bs.size()+"个'}");
		}
		
		this.projectDao.deleteSubproject(id);
		return this.json("{result:1}");
	}
	
	@RequestMapping(value = "save.htm", method = RequestMethod.POST)
	@ResponseBody
	public String save(HttpServletRequest request, Model model) {
		Project project = readFromRequest(request);
		
		this.projectDao.save(project);
		
		return this.json(project);
	}
	
	@RequestMapping(value = "toProject.htm")
	@ResponseBody
	public String toProject(String id) {
		System.out.println("--- to Project : " + id);
		
		List<Project> projects = this.projectDao.list();
		Map<String,Object> params = newMap("id",id);
		params.put("projects", projects);
		
		String s = "{json:['form','',"
				+ "[['$id','','hidden',id,'[n]'],"
					+ "['$pid','所属项目',['select',each(projects){{key:this.id,label:this.name}}],'','[n]']"
					+ "]]}";
		
		Object val = AE.execute(s,params);

		return this.json(val);
	}
	
	@RequestMapping(value = "businessList.htm")
	@ResponseBody
	public String businessList() {
		List<Project> projects = projectDao.list();
		
		List<Object> results = new ArrayList<Object>();
		List<String> pids = new ArrayList<String>();
		for(Project p:projects){
			Map<String,Object> params = newMap("project",p);
			String pid = p.getId().toString();
			String bpid = "Project,"+pid;
			pids.add(bpid);
			
			String s = "{text:project.name+'('+project.key+')',category:'project',key:project.id,parent:'root'}";
			Object val = AE.execute(s,params);
			results.add(val);
			
			//瀛愰」鐩妭鐐�
			if(!Utils.isEmpty(p.getSubprojects())){
				for(Subproject sp:p.getSubprojects()){
					String spid = sp.getId().toString();
					params = newMap("subproject",sp);
					params.put("parent", pid);
					
					s = "{text:subproject.name+'('+subproject.key+')',category:'subproject',key:subproject.id,parent:parent}";
					val = AE.execute(s,params);
					results.add(val);
					
					//瀛愰」鐩殑business鑺傜偣
					String bspid = "Subproject,"+sp.getId().toString();
					pids.add(bspid);
					results.addAll(this.businessNodes(newMap("project",bspid),newMap("parent",spid)));
				}
			}
			
			results.addAll(this.businessNodes(newMap("project",bpid),newMap("parent",pid)));
		}
		
		//鍔犺浇鍓╀綑鐨刡usiness鑺傜偣
		Map query = (Map)AE.execute("{project:{$nin:pids}}",newMap("pids",pids));
		results.addAll(businessNodes(query,newMap("parent","root")));

		return this.json(newMap("result",results));
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
			String pid = p.getId().toString();
			
			//椤圭洰鑺傜偣
			String s = "{text:project.name+'('+project.key+')',category:'project',key:project.id,parent:'root'}";
			Object val = AE.execute(s,params);
			results.add(val);
			
			//瀛愰」鐩妭鐐�
			if(!Utils.isEmpty(p.getSubprojects())){
				for(Subproject sp:p.getSubprojects()){
					params = newMap("subproject",sp);
					params.put("parent", pid);
					
					s = "{text:subproject.name+'('+subproject.key+')',category:'subproject',key:subproject.id,parent:parent}";
					val = AE.execute(s,params);
					results.add(val);
				}
			}

			//瀵硅薄妯″瀷鑺傜偣
			results.addAll(metadataNodes(newMap("project",pid),newMap("parent",pid)));
		}
		
		Map query = (Map)AE.execute("{project:{$nin:pids}}",newMap("pids",pids));
		results.addAll(metadataNodes(query,newMap("parent","root")));

		return this.json(newMap("result",results));
	}
	
	private List<Object> businessNodes(Map query,Map<String,Object> params){
		List<Business> businesses = this.businessDao.list(query);
		
		params.put("businesses", businesses);
		
		String s = "each(businesses){{text:this.name+'('+this.uniqueCode+')',category:'business',key:this.id,parent:parent}}";
		return (List<Object>)AE.execute(s,params);
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
