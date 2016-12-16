package com.maliang.core.model;

import java.util.ArrayList;
import java.util.List;

import com.maliang.core.dao.ObjectMetadataDao;

public class Trigger extends MongodbModel{
	private int mode=2;//1=insert,2=update
	
	private String name;
	
	/**
	 * 触发器执行的条件
	 * **/
	private String when;
	
	private String remark;
	
	@Mapped(type=TriggerAction.class)
	private List<TriggerAction> actions;
	

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<TriggerAction> getActions() {
		return actions;
	}

	public void setActions(List<TriggerAction> actions) {
		this.actions = actions;
	}

	public static void main(String[] args) {
		ObjectMetadataDao dao = new ObjectMetadataDao();
		
		ObjectMetadata om = dao.getByName("Order");
		
		String tid = "5850ed2a8f77f51d6d68579b";
		Trigger tt = dao.getTriggerById(tid);
		System.out.println(" ttt : " + tt);
		
		List<TriggerAction> ts = new ArrayList<TriggerAction>();
		tt.setActions(ts);
		
		TriggerAction ta = new TriggerAction();
		ta.setField("items.product.orderStock");
		ta.setCode("items.$.product.orderStock-items.$.num");
		ts.add(ta);
		
		dao.saveTrigger(om.getId().toString(), tt);
		
		tt = dao.getTriggerById(tid);
		System.out.println("after save ttt : " + tt);
		
		
//		ObjectMetadata newOm = new ObjectMetadata();
//		newOm.setId(om.getId());
//		
//		List<Trigger> ts = new ArrayList<Trigger>();
//		newOm.setTriggers(ts);
//		
//		Trigger tri = new Trigger();
//		tri.setMode(2);
//		tri.setName("付款减库存");
//		tri.setWhen("status");
//		ts.add(tri);
//		
//		dao.save(newOm);
		
		//System.out.println("tri : " + om.getTriggers());
	}
}
