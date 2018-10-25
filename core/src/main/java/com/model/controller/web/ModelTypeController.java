package com.model.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.controller.BasicController;
import com.model.data.ModelType;
import com.model.service.ModelTypeService;
import com.model.service.impl.ModelTypeServiceImpl;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "model/type")
public class ModelTypeController extends BasicController {
	
	
	@RequestMapping(value = "list.htm")
	public String list(Model model) {
		List<ModelType> types = this.typeService.finds();
		
		System.out.println("-------------- types : " + types);
		
		model.addAttribute("types", this.toJSONArray(types));
		return "/model/web/type/list";
	}
}
