package com.maliang.core.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "angularjs")
public class AngularJSLearningController {

	//@RequestMapping(value = "/core/test.json", headers = "Accept=application/json")
	@RequestMapping(value = "test.htm", method = RequestMethod.GET)
	public String test() {
		return "learning/index.html";
	}
}
