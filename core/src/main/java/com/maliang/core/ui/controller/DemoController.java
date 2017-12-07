package com.maliang.core.ui.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "demo")
public class DemoController {
	static Map<String,Map<String,String>> CLASS_LABELS = new HashMap<String,Map<String,String>>();
	
	@RequestMapping(value = "html.htm")
	public String bootstrap(Model model) {
		return "/demo/bootstrap/html";
	}
	
	@RequestMapping(value = "d3.htm")
	public String d3(Model model) {
		return "/demo/d3/d3";
	}
	
	@RequestMapping(value = "webvowl.htm")
	public String webvowl(Model model) {
		return "/demo/d3/webvowl";
	}
	
	@RequestMapping(value = "force.htm")
	public String force(Model model) {
		return "/demo/d3/force";
	}
	
	@RequestMapping(value = "f4.htm")
	public String f4(Model model) {
		return "/demo/d3/f4";
	}
	
	@RequestMapping(value = "f5.htm")
	public String f5(Model model) {
		return "/demo/d3/f5";
	}
}
