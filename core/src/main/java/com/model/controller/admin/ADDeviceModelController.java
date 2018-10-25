package com.model.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.model.controller.BasicController;
import com.model.data.DeviceModel;
import com.model.data.Dict;
import com.model.data.DictType;
import com.model.data.ModelType;
import com.model.service.Pager;

@Controller
@RequestMapping(value = "admin/device")
public class ADDeviceModelController extends BasicController{
	
	private List<String> uploadFile(MultipartHttpServletRequest request) {
		System.out.println("-------------- uploadFile ");
		
		List<MultipartFile> fileList=request.getFiles("files");
		List<String> fnames = new ArrayList<String>();
		for(MultipartFile multipartFile : fileList) {
			
			//获得原文件名
			String originalFilename = multipartFile.getOriginalFilename();
			System.out.println("originalFilename: "+originalFilename);

			//设置保存路径. 
			String path ="/home/wmx/桌面/图片/upload/";

			//检查该路径对应的目录是否存在. 如果不存在则创建目录
			File dir=new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String filePath = path + originalFilename;
			System.out.println("filePath: "+filePath);
			
			

			//保存文件
			File dest = new File(filePath);
			if (!(dest.exists())) {
				try {
					multipartFile.transferTo(dest);
					fnames.add(filePath);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return fnames;
	}
	
	@RequestMapping(value = "list.htm")
	public String list(HttpServletRequest request,Model model) {
		
		Pager page = readPager(request);
		
		String searchKey = request.getParameter("search.key");
		Map search = new HashMap();
		search.put("key",searchKey);
		
		List<DeviceModel> devices = this.deviceService.finds(page,search);
		
		Map result = new HashMap();
		result.put("devices",devices);
		result.put("page",page);
		result.put("search",search);
		
		model.addAttribute("result", this.toJSONString(result));
		model.addAttribute("adminName", "wangziqing");
		model.addAttribute("mainInclude", "device/main_list");
		
		return "/model/admin/main";
	}

	@RequestMapping(value = "ajax/list.htm")
	@ResponseBody
	public String ajaxList(HttpServletRequest request,Model model) {
		Pager page = readPager(request);
		
		Map<String, Object> reqMap = this.readRequestMap(request);
		Map<String, Object> reqSearch = (Map<String, Object>)reqMap.get("search");
		Map<String, Object> reqNot = (Map<String, Object>)reqMap.get("not");
		
		List<DeviceModel> devices = this.deviceService.finds(reqSearch, reqNot, page);

		Map result = new HashMap();
		result.put("devices",devices);
		result.put("page",page);
		
		System.out.println("------- ajax/list.htm devices : " + this.toJSONArray(devices));
		
		//return "{'page':{'curPage':1,'end':5}}";
		return this.toJSONString(result);
		//return this.toJSON(result);
	}
	
	@RequestMapping(value = "edit.htm")
	public String edit(String id,Model model) {
		List<ModelType> types = this.typeService.finds();
		DeviceModel device = this.deviceService.get(id);
		
		String title = "新增族模型";
		if(device != null && device.getName() != null) {
			title = "编辑"+device.getName();
		}
		if(device == null) {
			device = new DeviceModel();
		}
		
		List<DictType> dictTypes = this.dictTypeService.finds();
		Map<String,List<Dict>> dictMap = new HashMap<String,List<Dict>>();
		for(DictType dt : dictTypes) {
			Dict dq = new Dict();
			dq.setDictType(dt);
			
			System.out.println("---- dt : " + this.toJSONString(dt));
			
			List<Dict> dicts = this.dictService.finds(dq);
			dictMap.put(dt.getId().toString(),dicts);
		}
		
		Map result = new HashMap();
		result.put("title",title);
		result.put("types",types);
		result.put("device",device);
		result.put("dictTypes",dictTypes);
		result.put("dictMap",dictMap);
		
		model.addAttribute("result", this.toJSON(result));
		
		model.addAttribute("adminName", "wangziqing");
		model.addAttribute("mainInclude", "device/main_edit");
		
		return "/model/admin/main";
	}
	
	@RequestMapping(value = "save.htm")
	public String save(HttpServletRequest request,Model model) {
		
		System.out.println("------------------- request : " + request.getClass().getCanonicalName());
		
		if(request instanceof MultipartHttpServletRequest) {
			this.uploadFile((MultipartHttpServletRequest)request);
		}

		DeviceModel device = this.readMongodbModel(request, "deviceModel", DeviceModel.class);
		device.setStatus(1);
		
//		try {
//			System.out.println("--------- save device bson : " + BsonUtil.toBson(device));
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
		
		//this.deviceService.save(device);
		
		Map result = new HashMap();
		result.put("device",device);
		
		System.out.println("---- save device : " + this.toJSONString(result));
		
		return "redirect:/admin/device/list.htm";
		//return this.list(request, model);
	}
}
