package com.model.controller.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.controller.BasicController;
import com.model.dao.DictDao;
import com.model.dao.impl.DictDaoImpl;
import com.model.data.Dict;
import com.model.data.DictType;
import com.model.db.BsonUtil;

@Controller
@RequestMapping(value = "admin/dict")
public class ADDictController extends BasicController{
	
	@RequestMapping(value = "list.htm")
	public String list(HttpServletRequest request,Model model) {
		List<Dict> dicts = this.dictService.finds();

		Map<String,Object> result = this.resultMap("dicts",dicts);
		model.addAttribute("result", this.toJSON(result));
		
		return this.defaultPage(model, "dict/list");
	}
	
	@RequestMapping(value = "edit.htm")
	public String edit(String id,Model model) {
		Dict dict = this.dictService.get(id);
		
		String title = "新增数据";
		if(dict != null && dict.getName() != null) {
			title = "编辑数据“"+dict.getName()+"”";
		}
		
		if(dict == null) {
			dict = new Dict();
		}
		
		List<DictType> types = this.dictTypeService.finds();
		Map<String,Object> result = this.resultMap("title",title);
		result.put("dict",dict);
		result.put("types",this.toJSONArray(types));
		
		model.addAttribute("result", this.toJSON(result));
		model.addAttribute("adminName", "wangziqing");
		
		return this.defaultPage(model, "dict/edit");
	}
	
	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request,Model model) {
		Dict dict = this.readMongodbModel(request, "dict", Dict.class);
		
		this.dictService.save(dict);
		
		return this.list(request, model);
	}
	
	public static void main(String[] args) {
		DictDao dao = new DictDaoImpl();
		List<Dict> ds = dao.finds();
		for(Dict d : ds) {
			System.out.println(" d : " + d.getDictType());
		}
		
//		Dict dict = new Dict();
//		dict.setId(new ObjectId());
//		dict.setName("测试");
//		
//		DictType dt = new DictType();
//		dt.setId(new ObjectId());
//		
//		dict.setDictType(dt);
//		
//		try {
//			Document doc = BsonUtil.toBson(dict);
//			
//			System.out.println("----- doc : " + doc);
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	protected Map<String,Object> resultMap(){
		return new HashMap<String,Object>();
	}
	
	protected Map<String,Object> resultMap(String key,Object val){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(key,val);
		
		return result;
	}
	
	@RequestMapping(value = "type/list.htm")
	public String typeList(HttpServletRequest request,Model model) {
		List<DictType> dictTypes = this.dictTypeService.finds();

		Map result = new HashMap();
		result.put("dictTypes",dictTypes);
		
		model.addAttribute("result", this.toJSON(result));
		
		return this.defaultPage(model, "dict/type/list");
	}

	@RequestMapping(value = "type/edit.htm")
	public String typeEdit(String id,Model model) {
		DictType dictType = this.dictTypeService.get(id);
		
		String title = "新增数据类型";
		if(dictType != null && dictType.getName() != null) {
			title = "编辑类型“"+dictType.getName()+"”";
		}
		
		if(dictType == null) {
			dictType = new DictType();
		}
		
		Map result = new HashMap();
		result.put("title",title);
		result.put("dictType",this.toJSON(dictType));
		
		model.addAttribute("result", result);
		model.addAttribute("adminName", "wangziqing");
		
		return this.defaultPage(model, "dict/type/edit");
	}
	
	@RequestMapping(value = "type/save.htm")
	public String typeSave(HttpServletRequest request,Model model) {
		DictType type = this.readMongodbModel(request, "dictType", DictType.class);
		
		this.dictTypeService.save(type);
		
		return this.typeList(request, model);
	}
}
