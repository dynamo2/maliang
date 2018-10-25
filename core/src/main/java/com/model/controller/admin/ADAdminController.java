package com.model.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.controller.BasicController;
import com.model.data.Admin;

@Controller
@RequestMapping(value = "admin")
public class ADAdminController extends BasicController{
	
	@RequestMapping(value = "list.htm")
	public String list(HttpServletRequest request,Model model) {
		if(!this.checkLogin(request)) {
			return "redirect:/admin/login.htm";
		}
		
		List<Admin> admins = this.adminService.finds();

		Map result = new HashMap();
		result.put("admins",admins);
		
		model.addAttribute("result", this.toJSON(result));
		
		return this.defaultPage(model, "admin/list");
	}
	
	protected String defaultPage(Model model,String page) {
		model.addAttribute("mainInclude", page);
		return "/model/admin/main";
	}
	
	@RequestMapping(value = "edit.htm")
	public String edit(String id,Model model) {
		Admin admin = this.adminService.get(id);
		
		String title = "新增管理员";
		if(admin != null) {
			title = "编辑"+admin.getAccount();
		}
		if(admin == null) {
			admin = new Admin();
		}
		
		Map result = new HashMap();
		result.put("title",title);
		result.put("admin",admin);
		
		model.addAttribute("result", this.toJSON(result));
		
		return this.defaultPage(model, "admin/edit");
	}
	
	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request,Model model) {

		Admin admin = this.readMongodbModel(request, "admin", Admin.class);
		
		this.adminService.save(admin);
		
		return "redirect:/admin/list.htm";
	}
	
	@RequestMapping(value = "login/do.htm")
	public String doLogin(HttpServletRequest request,Model model) {
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		
		if(account == null || account.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			model.addAttribute("errorMessage", "账号或密码错误");
			return "/model/admin/admin/login";
		}
		
		Admin query = new Admin();
		query.setAccount(account);
		
		Admin admin = this.adminService.get(query);
		/**
		 * 登录成功
		 * **/
		if(admin != null && admin.getPassword().equals(password)) {
			HttpSession session = request.getSession();
			session.setAttribute("admin",admin);
			
			return "redirect:/admin/device/list.htm";
		}
		
		
		model.addAttribute("errorMessage", "账号或密码错误");
		return "/model/admin/admin/login";
	}
	
	@RequestMapping(value = "loginOut.htm")
	public String loginOut(HttpServletRequest request,Model model) {
		HttpSession session = request.getSession();
		session.removeAttribute("admin");
		
		return "/model/admin/admin/login";
	}
	
	private boolean checkLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object admin = session.getAttribute("admin");
		
		if(admin == null) {
			return false;
		}
		return admin instanceof Admin;
	}
	
	@RequestMapping(value = "login.htm")
	public String login(HttpServletRequest request,Model model) {

		
		return "/model/admin/admin/login";
	}
}
