package com.model.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.data.ModelType;
import com.model.service.ModelTypeService;
import com.model.service.impl.ModelTypeServiceImpl;

@Controller
@RequestMapping(value = "model/device")
public class DeviceModelController {
private ModelTypeService service = new ModelTypeServiceImpl();
	
	@RequestMapping(value = "list.htm")
	public String list(Model model) {
		List<ModelType> types = service.finds();
		
		System.out.println("-------------- types : " + types);
		
		model.addAttribute("types", types);
		return "/model/web/type/list";
	}
}
