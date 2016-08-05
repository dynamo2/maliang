package com.maliang.core.model;

public class Workflow extends MongodbModel {
	private Integer step;
	private String name;
	private String requestType;
	private String code;
	private String response;
	private String ajax;
	private String javaScript;
	
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getJavaScript() {
		return javaScript;
	}
	public void setJavaScript(String javaScript) {
		this.javaScript = javaScript;
	}
	public String getAjax() {
		return ajax;
	}
	public void setAjax(String ajax) {
		this.ajax = ajax;
	}
}
