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

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.MongodbModel;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.Trigger;
import com.maliang.core.model.TriggerAction;
import com.maliang.core.model.UCType;
import com.maliang.core.util.StringUtil;
import com.maliang.core.util.Utils;

@Controller
@RequestMapping(value = "metadata")
public class ObjectMetadataController extends BasicController {
	static Map<String,Map<String,String>> CLASS_LABELS = new HashMap<String,Map<String,String>>();
	static final String EDIT_CODE;
	static final String EDIT_CODE2;
	static final String METADATA_LIST;
	
	static {
		Map<String,String> blMap = new LinkedHashMap<String,String>();
		blMap.put("name", "名称");
		blMap.put("label", "标签");
		blMap.put("fields", "字段");
		blMap.put("modelType", "模型结构");
		CLASS_LABELS.put(ObjectMetadata.class.getCanonicalName(), blMap);
		
		Map<String,String> wfMap = new LinkedHashMap<String,String>();
		wfMap.put("name", "名称");
		wfMap.put("label", "标签");
		wfMap.put("modelType", "模型结构");
		
		/*
		wfMap.put("type", "瀛楁绫诲瀷");
		wfMap.put("linkedObject", "鍏宠仈瀵硅薄");
		wfMap.put("relationship", "鍏宠仈鏂瑰紡");
		wfMap.put("elementType", "鍏冪礌绫诲瀷");*/
		CLASS_LABELS.put(ObjectField.class.getCanonicalName(), wfMap);
		
		
		EDIT_CODE = "{name:{prefix:'objectMetadata',name:'name',label:'名称',type:'text',value:metadata.name},"
				+ "id:{prefix:'objectMetadata',name:'id',label:null,type:'hidden',value:metadata.id+''},"
				+ "label:{prefix:'objectMetadata',name:'label',label:'标签',type:'text',value:metadata.label},"
				+ "fields:{item-labels:{name:'名称',label:'标签',type:'字段类型'},prefix:'objectMetadata',"
					+ "name:'fields',label:'字段',type:'list',item-prefix:'objectMetadata.fields',"
					+ "value:each(metadata.fields){{"
						+ "name:{prefix:'objectMetadata.fields',name:'name',type:'text',value:this.name},"
						+ "label:{prefix:'objectMetadata.fields',name:'label',type:'text',value:this.label},"
						+ "type:[{prefix:'objectMetadata.fields',name:'type',type:'select',value:this.type,"
							+ "options:each(fieldTypes){{key:this.code,label:this.name}}},"
						+ "if(this.type=8 | this.type = 7){{prefix:'objectMetadata.fields',name:'linkedObject',type:'select',"
							+ "value:this.linkedObject,options:each(allMetadatas){{key:this.id+'',label:this.name}}}},"
						+ "if(this.type = 9){{prefix:'objectMetadata.fields',name:'elementType',type:'select',value:this.elementType,"
							+ "options:each(fieldTypes){{key:this.code,label:this.name}}}},"
						+ "if(this.type = 9 & (this.elementType=8 | this.elementType = 7)){{prefix:'objectMetadata.fields',name:'linkedObject',type:'select',"
							+ "value:this.linkedObject,options:each(allMetadatas){{key:this.id+'',label:this.name}}}}"
						+ "]"
				+ "}}}}";
	}
	
	/********************** Test start ******************************/
	/**
	 * md: ObjectMetadata
	 * types: FieldType.values()
	 * **/
	static {
		EDIT_CODE2 = "{"
				+ "global:{"
					+ "fieldTypes:each(types){{key:this.code,label:this.name}},"
					+ "modelTypes:each(modelTypes){{key:this.code,label:this.name}},"
					+ "fieldTemplate:{"
						+ "name:{},label:{},type:{type:'select',options:'fieldTypes'}"
					+ "}"
				+ "},"
				+ "metadata:{"
					+ "name:{value:md.name},"
					+ "id:{value:md.id+''},"
					+ "label:{value:md.label},"
					+ "modelType:{type:'select',value:md.modelType,options:'modelTypes'},"
					+ "fields:each(md.fields){tree([this,'fields']){{"
						+ "name:{value:this.name},"
						+ "label:{value:this.label},"
						+ "type:{type:'select',value:this.type,linkedObject:this.linkedObject,elementType:this.elementType,options:'fieldTypes'}"
					+ "}}}"
				+ "}}";
		
		METADATA_LIST = "metadataList:each(list){{"
					+ "name:this.name,"
					+ "label:this.label,"
					+ "modelType:this.modelType,"
					+ "id:this.id+''"
				+ "}}";
	}

	@RequestMapping(value = "main.htm")
	public String list2(Model model) {
		List<ObjectMetadata> metadataList = metadataDao.list();

		List<Map> datas = new ArrayList<Map>();
		for (ObjectMetadata data : metadataList) {
			Map result = new HashMap();
			System.out.println(data.getId().toString());
			result.put("id", data.getId().toString());
			result.put("name", data.getName());
			result.put("label", data.getLabel());
			result.put("modelType", data.getModelType());
			
			datas.add(result);
		}

		Map resultMap = new HashMap();
		resultMap.put("metadataList", datas);

		model.addAttribute("resultJson", json(resultMap));
		return "/metadata/gojs/main";
	}
	
	@RequestMapping(value = "code.htm")
	public String code(String code,Model model) {
		String result = "";
		if(!StringUtil.isEmpty(code)){
			Object val = AE.execute(code);
			if(val != null){
				result = val.toString();
			}
		}
		
//		System.out.println("----------- code result -----------------");
//		System.out.println(result);
//		System.out.println("----------- code result end -----------------");
		
		model.addAttribute("result", result);
		return "/metadata/code";
	}
	
	@RequestMapping(value = "dbDatas.htm")
	@ResponseBody
	public String dbDatas(HttpServletRequest request) {
		String oid = request.getParameter("oid");
		
		System.out.println("----- dbDatas oid : " + oid);
		ObjectMetadata omd = this.metadataDao.getByID(oid);
		Project pjo = omd.getProject();
		
		String collName = omd.getName();
		if(pjo != null) {
			collName = pjo.getKey()+"_"+collName;
		}
		
		System.out.println("----- dbDatas collName : " + collName);
		
		String code = "pdb."+collName+".find()";
		Object result = null;
		if(!StringUtil.isEmpty(code)){
			result = AE.execute(code);
		}
		if(result == null){
			result = "";
		}
		
//		System.out.println("----------- code2 result -----------------");
//		System.out.println(result);
//		System.out.println("----------- code2 result end -----------------");
		
		return this.json(this.newMap("result",result.toString()));
	}
	
	@RequestMapping(value = "code2.htm")
	@ResponseBody
	public String runCode(HttpServletRequest request) {
		String code = request.getParameter("code");
		Object result = null;
		if(!StringUtil.isEmpty(code)){
			result = AE.execute(code);
		}
		if(result == null){
			result = "";
		}
		
		System.out.println("----------- code2 result -----------------");
		System.out.println(result);
		System.out.println("----------- code2 result end -----------------");
		
		return this.json(this.newMap("result",result.toString()));
	}
	
	@RequestMapping(value = "edit3.htm")
	@ResponseBody
	public String edit3(String id,String pid) {
		ObjectMetadata metadata = metadataDao.getEditMeta(id);
		if(metadata == null){
			metadata = new ObjectMetadata();
			metadata.setProject(this.projectDao.getByID(pid));
		}
		List<Project> projects = this.projectDao.list();
		Map<String,Object> params = newMap("metadata",metadata);
		params.put("projects", projects);
		
		String s = "{json:['form','metadata',"
				+ "[['$id','','hidden',metadata.id,'[n]'],"
					+ "['$project.id','项目',['select',[{key:'',label:'无'}]+each(projects){{key:this.id,label:this.name}}],metadata.project.id,'[n]'],"
					+ "['$name','名称','text',metadata.name,'[n]'],"
					+ "['$label','标签','text',metadata.label,'[n]'],"
					+ "['$modelType','模型结构',['select',[{key:'',label:'默认'},{key:'2',label:'Tree'}]],metadata.modelType,'[n]']]]}";
		
		Object val = AE.execute(s,params);

		return this.json(val);
	}
	
	@RequestMapping(value = "triggers.htm")
	@ResponseBody
	public String triggerList(String id) {
		ObjectMetadata metadata = metadataDao.getByID(id);
		Map<String,Object> params = newMap("metadata",metadata);
		
		String s = "{json:['dialog',[['button','鏂板','editTrigger(\"\",\"'+metadata.id+'\")'],"
				+ "['tableList',['鍚嶇О','鏉′欢','琛屼负','鎿嶄綔'],"
					+ "each(metadata.triggers){[this.name,this.when,"
						+ "['tableBlock',each(this.actions){[this.field,this.code]}],"
						+ "[['button','缂栬緫','editTrigger(\"'+this.id+'\",\"'+metadata.id+'\")'],['button','鍒犻櫎','']]]}]],"
			+ "{title:'瑙﹀彂鍣ㄥ垪琛�',width:1000,height:700}]"
		+ "}";
		
		Object val = AE.execute(s,params);

		System.out.println("triggerList json : " + val);
		return this.json(val);
	}
	
	@RequestMapping(value = "trigger.htm")
	@ResponseBody
	public String trigger(String id,String omId) {
		Trigger trigger = this.metadataDao.getTriggerById(id);
		if(trigger == null){
			trigger = new Trigger();
			trigger.setMode(2);
		}
		
		if(Utils.isEmpty(trigger.getActions())){
			TriggerAction ta = new TriggerAction();
			List<TriggerAction> as = new ArrayList<TriggerAction>();
			as.add(ta);
			trigger.setActions(as);
		}
		
		Map<String,Object> params = newMap("trigger",trigger);
		params.put("omId", omId);
		
		String s = "{json:['form','',["
					+ "['id','','hidden',omId,'[n]'],"
					+ "['trigger.id','','hidden',trigger.id,'[n]'],"
					+ "['trigger.mode','绫诲瀷',['radio',{1:'insert',2:'update'}],trigger.mode,'[n]'],"
					+ "['trigger.name','鍚嶇О','text',trigger.name,'[n]'],"
					+ "['trigger.when','鏉′欢','text',trigger.when,'[n]'],"
					+ "['trigger.actions','琛屼负',"
						+ "['list',['瀛楁','鎵ц浠ｇ爜'],"
							+ "each(trigger.actions){["
								+ "['field','','text',this.field],"
								+ "['code','','text',this.code]"
						+ "]}],"
					+ "'','[n]']"
			+ "]]}";
		
		Object val = AE.execute(s,params);

		System.out.println("trigger form : " + val);
		return this.json(val);
	}
	
	@RequestMapping(value = "saveTrigger.htm", method = RequestMethod.POST)
	@ResponseBody
	public String saveTrigger(HttpServletRequest request) {
		Trigger trigger = readMongodbModel(request,"trigger",Trigger.class);
		String omid = request.getParameter("metaId");
		
		this.metadataDao.saveTrigger(omid, trigger);
		
		return "";
	}

	@RequestMapping(value = "saveMove.htm")
	@ResponseBody
	public String saveMove(String id,String pid) {
		ObjectMetadata metadata = this.metadataDao.getByID(id);
		Project project = this.projectDao.getByID(pid);
		
		if(!isSameProject(project,metadata)){
			String oldCollName = metadata.getName();
			if(metadata.getProject() != null){
				oldCollName = metadata.getProject().getKey()+"_"+oldCollName;
			}
			
			String newCollName = project.getKey()+"_"+metadata.getName();
			this.metadataDao.renameCollection(oldCollName, newCollName);
			
			metadata.setProject(project);
			this.metadataDao.save(metadata);
		}

		return "{}";
	}
	
	private boolean isSameProject(Project project,ObjectMetadata metadata){
		return metadata != null 
				&& project != null
				&& project.getId() != null
				&& metadata.getProject() != null 
				&& project.getId().equals(metadata.getProject().getId());
	}
	
	@RequestMapping(value = "saveCopy.htm")
	@ResponseBody
	public String saveCopy(String id,String pid) {
		ObjectMetadata metadata = this.metadataDao.getByID(id);
		Project project = this.projectDao.getByID(pid);
		if(!isSameProject(project,metadata)){
			metadata.setId(null);
			metadata.setProject(project);
			this.metadataDao.save(metadata);
		}

		return "{}";
	}
	
	@RequestMapping(value = "edit2.htm", method = RequestMethod.POST)
	@ResponseBody
	public String edit2(String id) {
		ObjectMetadata metadata = metadataDao.getEditMeta(id);

		return jsonEditCode2(metadata);
	}
	
	@RequestMapping(value = "save2.htm", method = RequestMethod.POST)
	@ResponseBody
	public String save2(HttpServletRequest request, Model model) {
		ObjectMetadata reqMetadata = readMongodbModel(request,"metadata",ObjectMetadata.class);
		
		System.out.println("********* save reqMetadata  : ");
		System.out.println(reqMetadata);
		System.out.println("***************************");
		
		metadataDao.save(reqMetadata);
		
		return jsonEditCode2(reqMetadata);
	}
	
	@RequestMapping(value = "delete2.htm", method = RequestMethod.POST)
	@ResponseBody
	public String delete2(String id) {
		metadataDao.remove(id);
		return "{}";
	}
	
	@RequestMapping(value = "linkedObject2.htm")
	@ResponseBody
	public String linkedObject2() {
		String desc = "{name:'linkedObject',type:'select',options:each(metadatas){{key:this.name,label:this.name}}}";
		
		List<ObjectMetadata> metadataList = metadataDao.list();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("metadatas",metadataList);
		
		Map<String,Object> linkedMap = (Map<String,Object>)ArithmeticExpression.execute(desc, params);
		return json(linkedMap);
	}
	
	@RequestMapping(value = "elementType2.htm")
	@ResponseBody
	public String elementType2() {
		String desc = "{name:'elementType',type:'select',options:each(fieldTypes){{key:this.code,label:this.name}}}";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fieldTypes",fieldTypes());
		
		Map<String,Object> map = (Map<String,Object>)ArithmeticExpression.execute(desc, params);
		return json(map);
	}
	
	private void printFields(List<ObjectField> ofs){
		if(ofs == null || ofs.size() == 0)return;
		
		for(ObjectField of : ofs){
			System.out.println(of);
			printFields(of.getFields());
		}
	}

	private String jsonEditCode2(ObjectMetadata metadata){
		this.metadataDao.removeModelTypeFields(metadata);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("md",metadata);
		params.put("types",fieldTypes());
		params.put("modelTypes",this.modelTypes());
		
		Object editMap =  AE.execute(EDIT_CODE2, params);
		return this.json(editMap);
	}
	
	private List<Object> fieldTypes(){
		List<Object> fts = new ArrayList<Object>();
		for(FieldType ft : FieldType.values()){
			fts.add(ft);
		}
		
		List<UCType> ucts = this.uctypeDao.list();
		Map<String,Object> types = null;
		for(UCType t : ucts){
			types = new HashMap<String,Object>();
			types.put("code",t.getKey());
			types.put("name",t.getName());
			fts.add(types);
		}
		
		return fts;
	}
	
	private List<ModelType> modelTypes(){
		List<ModelType> fts = new ArrayList<ModelType>();
		for(ModelType ft : ModelType.values()){
			fts.add(ft);
		}
		
		return fts;
	}
	
//	private ObjectMetadata readMetadataFromRequest(HttpServletRequest request){
//		JSONObject json = JSONObject.fromObject(request.getParameterMap());
//		JSONArray ja = (JSONArray)json.get("metadata");
//
//		ObjectMetadata metadata = (ObjectMetadata)JSONObject.toBean(ja.getJSONObject(0), 
//				ObjectMetadata.class,newMap("fields",ObjectField.class));
//		
//		return metadata;
//	}
	
	private Object order(String id){
		String str = "{"
				+ "name:{value:'Order'},"
				+ "id:{value:'"+id+"'},"
				+ "label:{value:'璁㈠崟'},"
				+ "fields:[{"
					+ "name:{value:'info'},"
					+ "label:{value:'鍩烘湰淇℃伅'},"
					+ "type:{type:'select',value:7,options:'fieldTypes'},"
					+ "fields:[{"
						+ "name:{value:'number'},"
						+ "label:{value:'璁㈠崟鍙�'},"
						+ "type:{type:'select',value:3,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'owner'},"
						+ "label:{value:'璁㈠崟鎵�灞炰汉'},"
						+ "type:{type:'select',value:6,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'status'},"
						+ "label:{value:'鐘舵��'},"
						+ "type:{type:'select',value:1,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'totalPrice'},"
						+ "label:{value:'鎬婚'},"
						+ "type:{type:'select',value:2,options:'fieldTypes'}"
					+ "}]"
				+ "},{"//// 鏀惰揣鍦板潃
					+ "name:{value:'address'},"
					+ "label:{value:'鏀惰揣鍦板潃'},"
					+ "type:{type:'select',value:7,options:'fieldTypes'},"
					+ "fields:[{"
						+ "name:{value:'province'},"
						+ "label:{value:'鐪�'},"
						+ "type:{type:'select',value:3,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'city'},"
						+ "label:{value:'甯�'},"
						+ "type:{type:'select',value:3,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'zoom'},"
						+ "label:{value:'閭紪'},"
						+ "type:{type:'select',value:3,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'address'},"
						+ "label:{value:'璇︾粏鍦板潃'},"
						+ "type:{type:'select',value:3,options:'fieldTypes'}"
					+ "},{"
						+ "name:{value:'accepter'},"
						+ "label:{value:'鏀朵欢浜�'},"
						+ "type:{type:'select',value:8,linkedObject:'User',options:'fieldTypes'}"
					+ "}]"
				+ "}]"
			+ "}";
		return ArithmeticExpression.execute(str, null);
	}
	/********************** Test end ******************************/
	
	@RequestMapping(value = "edit.htm", method = RequestMethod.GET)
	public String edit(String id, Model model) {
		String resultJson = "";
		ObjectMetadata metadata = null;
		if (id != null && !id.trim().isEmpty()) {
			metadata = metadataDao.getEditMeta(id);
		}else {
			metadata = new ObjectMetadata();
			
			ObjectField ofield = new ObjectField();
			metadata.setFields(new ArrayList<ObjectField>());
			metadata.getFields().add(ofield);
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("metadata",metadata);
		params.put("fieldTypes",FieldType.values());
		
		List<ObjectMetadata> metadataList = metadataDao.list();
		params.put("allMetadatas",metadataList);
		
		Object editMap = ArithmeticExpression.execute(EDIT_CODE, params);
		resultJson = JSONObject.fromObject(editMap).toString();
		
		model.addAttribute("resultJson", resultJson);

		return "/metadata/edit";
	}

	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request, Model model) {
		Map<String,Object> reqMap = this.readRequestMap(request);
		Map<String,Object> busMap = (Map<String,Object>)reqMap.get("objectMetadata");
		ObjectMetadata mt = buildToObject(busMap,ObjectMetadata.class);
		
		metadataDao.save(mt);
		return list(model);
	}

	@RequestMapping(value = "list.htm")
	public String list(Model model) {
		List<ObjectMetadata> metadataList = metadataDao.list();

		List<Map> datas = new ArrayList<Map>();
		for (ObjectMetadata data : metadataList) {
			Map result = new HashMap();
			result.put("id", data.getId().toString());
			result.put("name", data.getName());
			result.put("label", data.getLabel());

			datas.add(result);
		}

		Map resultMap = new HashMap();
		resultMap.put("metadataList", datas);

		model.addAttribute("resultJson", JSONObject.fromObject(resultMap)
				.toString());
		return "/metadata/list";
	}

	@RequestMapping(value = "detail.htm")
	public String detail(String id, Model model) {
		ObjectMetadata metadata = metadataDao.getByID(id);
		String resultJson = toJson(metadata);

		model.addAttribute("resultJson", resultJson);

		return "/metadata/detail";
	}

	@RequestMapping(value = "delete.htm")
	public String delete(String id, Model model) {
		metadataDao.remove(id);

		return list(model);
	}
	
	@RequestMapping(value = "linkedObject.htm")
	@ResponseBody
	public String linkedObject() {
		String desc = "{prefix:'objectMetadata.fields',name:'linkedObject',label:'鍏宠仈瀵硅薄',value:null,"
				+ "type:'select',options:each(metadatas){{key:this.name,label:this.name}}}";
		
		List<ObjectMetadata> metadataList = metadataDao.list();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("metadatas",metadataList);
		
		Map<String,Object> linkedMap = (Map<String,Object>)ArithmeticExpression.execute(desc, params);
		return JSONObject.fromObject(linkedMap).toString();
	}
	
	@RequestMapping(value = "elementType.htm")
	@ResponseBody
	public String elementType() {
		String desc = "{prefix:'objectMetadata.fields',name:'elementType',"
				+ "type:'select',options:each(fieldTypes){{key:this.code,label:this.name}}}";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fieldTypes",FieldType.values());
		
		Map<String,Object> map = (Map<String,Object>)ArithmeticExpression.execute(desc, params);
		return JSONObject.fromObject(map).toString();
	}
	
	public static void main(String[] args) {
		String typeDes = "{name:{prefix:'objectMetadata',name:'name',label:'鍚嶇О',type:'text',value:metadata.name},"
				+ "id:{prefix:'objectMetadata',name:'id',label:null,type:'hidden',value:metadata.id},"
				+ "label:{prefix:'objectMetadata',name:'label',label:'鏍囩',type:'text',value:metadata.label},"
				+ "fields:{item-labels:{name:'鍚嶇О',label:'鏍囩',type:'瀛楁绫诲瀷'},prefix:'objectMetadata',"
					+ "name:'fields',label:'瀛楁',type:'list',item-prefix:'objectMetadata.fields',"
					+ "value:[each(metadata.fields){{name:{prefix:'objectMetadata.fields',name:'name',label:'鍚嶇О',type:'text',value:this.name},"
						+ "id:{prefix:'objectMetadata.fields',name:'id',label:null,type:'hidden',value:this.id},"
						+ "label:{prefix:objectMetadata.fields,name:'label',label:'鏍囩',type:'text',value:this.label},"
						+ "type:{prefix:'objectMetadata.fields',name:'type',label:'瀛楁绫诲瀷',type:'select',value:this.type,"
							+ "options:each(fieldTypes){{key:this.code,label:this.name}}},"
						+ "linkedObjected:if(metadata.type==8){{prefix:'objectMetadata.fields',name:'linkedObject',"
							+ "type:'select',value:this.linkedObject,"
							+ "options:each(allMetadatas){{key:this.id,label:this.name}}}}}}]}}";
		
		
		//Account.id : 56d69195fe559fe3d66284db
		String account = "{account:{account:'wmx',password:'123456',"
						+ "personal_profile:{real_name:'鐜嬬編闇�',email:'wmx@tm.com',age:100,"
						+ "address:[{province:'姹熻嫃鐪�',city:'鍗椾含甯�',zone:'榧撴ゼ鍖�'},{province:'娴欐睙鐪�',city:'婀栧窞甯�',zone:'瀹夊悏鍘�'}]}}}";
		
		account = "{list:db.Account.get('56dd3903e45701ce0113bdda')}";
		account = "{list:db.Account.search()}";
		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(account, null);
		
		account = "{ads:list(list.size-1)}";
		params = (Map<String,Object>)ArithmeticExpression.execute(account, params);
		
		account = "ads";
		//System.out.println(ArithmeticExpression.execute(account, params));
		
		Class pc = Project.class;
		System.out.println(MongodbModel.class.isAssignableFrom(Project.class));
		
		
		/*
		String str = "db.Account.";
		
		
		ObjectMetadata metadata = new ObjectMetadata();
		ObjectField ofield = new ObjectField();
		metadata.setFields(new ArrayList<ObjectField>());
		metadata.getFields().add(ofield);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("metadata",metadata);
		params.put("fieldTypes",FieldType.values());
		
		Object v = ArithmeticExpression.execute(typeDes, params);
		System.out.println(v);
		//System.out.println(MapHelper.readValue(params,"field.type"));
		 * */
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fieldTypeMap(ObjectField ofield) {
		
		//{"uniqueMark":{"prefix":"objectMetadata","name":"uniqueMark","label":null,"type":"textarea","value":null},"name":{"prefix":"objectMetadata","name":"name","label":"鍚嶇О","type":"textarea","value":null},"id":{"prefix":"objectMetadata","name":"id","label":null,"type":"hidden","value":null},"label":{"prefix":"objectMetadata","name":"label","label":"鏍囩","type":"textarea","value":null},"fields":{"item-labels":{"name":"鍚嶇О","label":"鏍囩"},"prefix":"objectMetadata","name":"fields","label":"瀛楁","type":"list","item-prefix":"objectMetadata.fields","value":[{"uniqueMark":{"prefix":"objectMetadata.fields","name":"uniqueMark","label":null,"type":"textarea","value":null},"name":{"prefix":"objectMetadata.fields","name":"name","label":"鍚嶇О","type":"textarea","value":null},"id":{"prefix":"objectMetadata.fields","name":"id","label":null,"type":"hidden","value":null},"label":{"prefix":"objectMetadata.fields","name":"label","label":"鏍囩","type":"textarea","value":null},"linkedObject":{"prefix":"objectMetadata.fields","name":"linkedObject","label":null,"type":"textarea","value":null},"relationship":{"prefix":"objectMetadata.fields","name":"relationship","label":null,"type":"textarea","value":null},"type":{"prefix":"objectMetadata.fields","name":"type","label":null,"type":"textarea","value":0},"elementType":{"prefix":"objectMetadata.fields","name":"elementType","label":null,"type":"textarea","value":0}}]}};
		//"name":{"prefix":"objectMetadata.fields","name":"name","label":"鍚嶇О","type":"textarea","value":null}
		//{type:{prefix:'objectMetadata.fields',name:'type',label:'瀛楁绫诲瀷',type:'select'}}
		String typeDes = "{type:{prefix:'objectMetadata.fields',name:'type',label:'瀛楁绫诲瀷',value:field.type,"
					+ "type:'select',options:each(fieldTypes){key:this.code,label:this.name}}}";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("field",ofield);
		params.put("fieldTypes",FieldType.values());
		
		String str = "name:{prefix:'objectMetadata',name:'name',label:'鍚嶇О',type:'text',value:metadata.name},"
				+ "id:{prefix:'objectMetadata',name:'id',label:null,type:'hidden',value:metadata.id},"
				+ "label:{prefix:'objectMetadata',name:'label',label:'鏍囩',type:'text',value:metadata.label},"
				+ "fields:{item-labels:{name:'鍚嶇О',label:'鏍囩',type:'瀛楁绫诲瀷'},prefix:'objectMetadata',"
					+ "name:'fields',label:'瀛楁',type:'list',item-prefix:'objectMetadata.fields',"
					+ "value:[each(metadata.fields){{name:{prefix:'objectMetadata.fields',name:'name',label:'鍚嶇О',type:'text',value:this.name},"
						+ "id:{prefix:'objectMetadata.fields',name:'id',label:null,type:'hidden',value:this.id},"
						+ "label:{prefix:objectMetadata.fields,name:'label',label:'鏍囩',type:'text',value:this.label},"
						+ "type:{prefix:'objectMetadata.fields',name:'type',label:'瀛楁绫诲瀷',type:'select',value:this.type}}}}]}}";
		
		 return (Map<String,Object>)ArithmeticExpression.execute(typeDes, params);
	}

	public static Map<String, Object> linkedObjectMap(
			List<ObjectMetadata> metadataList, ObjectField ofield) {
		Object value = ofield != null ? ofield.getLinkedObject() : "";

		Map<String, Object> objMap = new HashMap<String, Object>();
		objMap.put("label", "");

		Map<String, Object> objInfoMap = new HashMap<String, Object>();
		objMap.put("info", objInfoMap);

		objInfoMap.put("name", "field.linkedObject");
		objInfoMap.put("type", "select");
		objInfoMap.put("value", value);

		List<Map> options = new ArrayList<Map>();
		for (ObjectMetadata data : metadataList) {
			Map result = new HashMap();
			result.put("label", data.getName());
			result.put("key", data.getId().toString());

			options.add(result);
		}
		objInfoMap.put("options", options);

		return objMap;
	}

	public static Map<String, Object> relationshipMap(ObjectField ofield) {
		Object value = ofield != null ? ofield.getRelationship() : "";

		Map<String, Object> objInfoMap = new HashMap<String, Object>();
		objInfoMap.put("name", "field.relationship");
		objInfoMap.put("type", "select");
		objInfoMap.put("value", value);

		List<Map> options = new ArrayList<Map>();
		Map result = new HashMap();
		result.put("label", "1瀵瑰");
		result.put("key", "1");
		options.add(result);

		result = new HashMap();
		result.put("label", "1瀵�1");
		result.put("key", "2");
		options.add(result);

		objInfoMap.put("options", options);

		Map<String, Object> objMap = new HashMap<String, Object>();
		objMap.put("label", "");
		objMap.put("info", objInfoMap);

		return objMap;
	}

	private static String toEditJson() {
		Map result = new HashMap();

		result.put("id", "");
		result.put("name", "");
		result.put("label", "");

		List<ObjectMetadata> metadataList = new ObjectMetadataDao().list();

		List fields = new ArrayList();
		result.put("fields", fields);
		
		
		Map<String, Object> fieldsMap = new HashMap<String, Object>();

		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldsMap.put("name", fieldMap);
		fieldMap.put("label", "鍚嶇О");

		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.name");
		infoMap.put("value", "");
		infoMap.put("type", "text");
		fieldMap.put("info", infoMap);

		fieldMap = new HashMap<String, Object>();
		fieldsMap.put("label", fieldMap);
		fieldMap.put("label", "鏍囩");

		infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.label");
		infoMap.put("value", "");
		infoMap.put("type", "text");
		fieldMap.put("info", infoMap);

		fieldMap = new HashMap<String, Object>();
		fieldsMap.put("type", fieldMap);
		fieldMap.put("label", "绫诲瀷");

		infoMap = new HashMap<String, Object>();
		infoMap.put("name", "field.type");
		infoMap.put("value", "");
		infoMap.put("type", "select");

		List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
		for (FieldType type : FieldType.values()) {
			Map<String, Object> tmap = new HashMap<String, Object>();
			tmap.put("label", type.getName());
			tmap.put("key", type.getCode());

			types.add(tmap);
		}
		infoMap.put("options", types);
		fieldMap.put("info", infoMap);

		fieldsMap.put("linkedObject", linkedObjectMap(metadataList, null));
		fieldsMap.put("relationship", relationshipMap(null));

		fields.add(fieldsMap);

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

	private static String toEditJson(ObjectMetadata metadata) {
		Map result = new HashMap();

		// String json =
		// "{"label":"鍚嶇О","type":"text","info":{"name":"Product.name","value":"澶╂皵涓瑰崕娉钩琛″寲濡嗘按150ml"}}";
		// {"label":"鍒嗙被","type":"select","info":{"name":"Product.product_type","options":[
		// {"label":"娲侀潰","key":"552e3362d27776926941e009"},{"label":"鍗稿","key":"552e3362d27776926941e00a"},{"label":"鐖借偆姘�","key":"552e3362d27776926941e00b"},{"label":"淇濇箍姘�","key":"552e3362d27776926941e00c"},{"label":"绮惧崕","key":"552e3362d27776926941e00d"},{"label":"闈㈤湝/涔虫恫","key":"552e3362d27776926941e00e"},{"label":"鐪奸湝","key":"552e3362d27776926941e00f"},{"label":"闈㈣啘","key":"552e3362d27776926941e010"},{"label":"鍘昏璐ㄤ骇鍝�","key":"552e3362d27776926941e011"}]

		result.put("id", metadata.getId().toString());
		result.put("name", metadata.getName());
		result.put("label", metadata.getLabel());

		List<ObjectMetadata> metadataList = new ObjectMetadataDao().list();

		List fields = new ArrayList();
		result.put("fields", fields);
		for (ObjectField of : metadata.getFields()) {
			Map<String, Object> fieldsMap = new HashMap<String, Object>();

			Map<String, Object> fieldMap = new HashMap<String, Object>();
			fieldsMap.put("name", fieldMap);
			fieldMap.put("label", "鍚嶇О");

			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.name");
			infoMap.put("value", of.getName());
			infoMap.put("type", "text");
			fieldMap.put("info", infoMap);

			fieldMap = new HashMap<String, Object>();
			fieldsMap.put("label", fieldMap);
			fieldMap.put("label", "鏍囩");

			infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.label");
			infoMap.put("value", of.getLabel());
			infoMap.put("type", "text");
			fieldMap.put("info", infoMap);

			fieldMap = new HashMap<String, Object>();
			fieldsMap.put("type", fieldMap);
			fieldMap.put("label", "绫诲瀷");

			infoMap = new HashMap<String, Object>();
			infoMap.put("name", "field.type");
			infoMap.put("value", of.getType());
			infoMap.put("type", "select");

			List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
			for (FieldType type : FieldType.values()) {
				Map<String, Object> tmap = new HashMap<String, Object>();
				tmap.put("label", type.getName());
				tmap.put("key", type.getCode());

				types.add(tmap);
			}
			infoMap.put("options", types);
			fieldMap.put("info", infoMap);

			fieldsMap.put("linkedObject", linkedObjectMap(metadataList, of));
			fieldsMap.put("relationship", relationshipMap(of));

			fields.add(fieldsMap);
		}

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

	private static String toJson(ObjectMetadata metadata) {
		Map result = new HashMap();
		result.put("id", metadata.getId().toString());
		result.put("name", metadata.getName());
		result.put("label", metadata.getLabel());

		List fields = new ArrayList();
		result.put("fields", fields);
		for (ObjectField of : metadata.getFields()) {
			Map map = new HashMap();
			map.put("name", of.getName());
			map.put("label", of.getLabel());
			map.put("type", of.getType());
			map.put("linkedObject", of.getLinkedObject());
			map.put("relationship", of.getRelationship());

			fields.add(map);
		}

		Map model = new HashMap();
		model.put("metadata", result);

		return JSONObject.fromObject(model).toString();
	}

////	public static void main(String[] args) {
//		ObjectMetadata metadata = new ObjectMetadataDao()
//				.getByID("55504baad277e12f364ffb40");
//
//		System.out.println(toEditJson(metadata));
//	}
	
	private <T> List<T> modelList(HttpServletRequest request,
			String objName, Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();
		
		Integer maxIndex = getInt(request,"maxIndex",-1);
		try {
			List<T> resultList = new ArrayList<T>();

			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			if(maxIndex > 0){
				for(int i = 0; i <= maxIndex; i++){
					T result = null;
					for(PropertyDescriptor pd : pds){
						if(pd.getName().equals("class"))continue;
						
						String reqName = i+"."+objName+"."+pd.getName();
						String reqValue = request.getParameter(reqName);
						if(reqValue != null){
							if(result == null){
								result = cls.newInstance();
								resultList.add(result);
							}
							
							setFieldValue(pd,reqValue,result);
						}
					}
				}
			}
			
			
			/*
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;

				String fieldName = pname.substring(objName.length() + 1,
						pname.length());
				String[] fieldValue = request.getParameterValues(pname);

				int i = 0;
				for (String fvalue : fieldValue) {
					if (fvalue == null || fvalue.trim().isEmpty()) {
						i++;
						continue;
					}

					T result = null;
					if (resultList.size() > i) {
						result = resultList.get(i);
					} else {
						result = cls.newInstance();
						resultList.add(result);
					}

					fvalue = fvalue.trim();
					setFieldValue(pds, fieldName, fvalue, result);
					i++;
				}
			}*/

			return resultList;
		} catch (Exception e) {
		}

		return null;
	}

	private static void setFieldValue(PropertyDescriptor[] pds,
			String fieldName, String fieldValue, Object obj) {
		for (PropertyDescriptor pd : pds) {
			if (pd.getName().equals(fieldName)) {
				Object value = fieldValue;
				String propertyTypeName = pd.getPropertyType().getSimpleName();

				if (pd.getName().equals("id")) {
					try {
						value = new ObjectId(fieldValue);
					}catch(Exception e){
						value = null;
					}
					
				} else if (propertyTypeName.equals("Integer")
						|| propertyTypeName.equals("int")) {
					try {
						value = new Integer(fieldValue);
					} catch (Exception e) {
						value = null;
					}
				} else if (propertyTypeName.equals("Double")
						|| propertyTypeName.equals("double")) {
					try {
						value = new Double(fieldValue);
					} catch (Exception e) {
						value = null;
					}
				}

				try {
					pd.getWriteMethod().invoke(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return;
			}
		}
	}
	
	private static void setFieldValue(PropertyDescriptor pd,String fieldValue, Object obj) {
		Object value = fieldValue;
		String propertyTypeName = pd.getPropertyType().getSimpleName();

		if (pd.getName().equals("id")) {
			try {
				value = new ObjectId(fieldValue);
			}catch(Exception e){
				value = null;
			}
			
		} else if (propertyTypeName.equals("Integer")
				|| propertyTypeName.equals("int")) {
			try {
				value = new Integer(fieldValue);
			} catch (Exception e) {
				value = null;
			}
		} else if (propertyTypeName.equals("Double")
				|| propertyTypeName.equals("double")) {
			try {
				value = new Double(fieldValue);
			} catch (Exception e) {
				value = null;
			}
		}

		try {
			pd.getWriteMethod().invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	private static <T> T modelObject(HttpServletRequest request,
			String objName, Class<T> cls) {
		Enumeration<String> paramNames = request.getParameterNames();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			T result = cls.newInstance();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			while (paramNames.hasMoreElements()) {
				String pname = paramNames.nextElement().trim();
				if (!pname.startsWith(objName + "."))
					continue;

				String fieldName = pname.substring(objName.length() + 1,
						pname.length());
				String fieldValue = request.getParameter(pname);

				setFieldValue(pds, fieldName, fieldValue, result);
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
