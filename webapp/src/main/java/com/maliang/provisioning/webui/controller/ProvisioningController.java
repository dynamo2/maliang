package com.maliang.provisioning.webui.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/provisioning")
@Controller
public class ProvisioningController {
	private final Log logger = LogFactory.getLog(ProvisioningController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "provisioning/home";
	}

}
