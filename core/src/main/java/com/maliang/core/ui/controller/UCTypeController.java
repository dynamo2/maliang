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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.UCType;
import com.maliang.core.util.StringUtil;

@Controller
@RequestMapping(value = "uctype")
public class UCTypeController extends BasicController {
	@RequestMapping(value = "main.htm")
	public String list(Model model) {
		List<UCType> types = this.uctypeDao.list();

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("types",types);
		
		String json = "['tableList',['名称','key','units','操作'],each(types){[this.name,this.key,this.units+'',[['button','编辑','edit(\"'+this.id+'\")']]]}]";
		List list = (List)AE.execute(json, params);
		
		Map resultMap = new HashMap();
		resultMap.put("json", list);

		model.addAttribute("resultJson", json(resultMap));
		return "/metadata/uctype";
	}
	
	public static void main(String[] args) {
		UCType t = new UCType();
	}
	
	@RequestMapping(value = "get.htm", method = RequestMethod.POST)
	@ResponseBody
	public String get(String id) {
		UCType uctype = uctypeDao.getByID(id);
		return this.json(uctype);
	}
	
	@RequestMapping(value = "save.htm", method = RequestMethod.POST)
	@ResponseBody
	public String save(HttpServletRequest request, Model model) {
		UCType uctype = readFromRequest(request);
		
		System.out.println("save uctype : " + uctype);
		
		this.uctypeDao.save(uctype);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uctype",uctype);
		
		String s = "{uctype:[uctype.name,uctype.key,''+uctype.units,[['button','编辑','edit(\"'+uctype.id+'\")']]]}";
		Object val = AE.execute(s, params);
		
		return this.json(val);
	}
	
	private UCType readFromRequest(HttpServletRequest request){
		JSONObject json = JSONObject.fromObject(request.getParameterMap());
		JSONArray ja = (JSONArray)json.get("uctype");

		Map cm = new HashMap();
		cm.put("units",String.class);
		cm.put("factors",Integer.class);

		String units = (String)ja.getJSONObject(0).get("units");
		ja.getJSONObject(0).put("units", units.split(","));
		
		UCType metadata = (UCType)JSONObject.toBean(ja.getJSONObject(0), UCType.class,cm);
		
		String jid = (String)ja.getJSONObject(0).get("id");
		if(!StringUtil.isEmpty(jid)){
			//metadata.setId(jid);
		}
		
		return metadata;
	}
}
