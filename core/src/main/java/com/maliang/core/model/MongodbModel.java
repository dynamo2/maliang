package com.maliang.core.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.bson.types.ObjectId;

public class MongodbModel {
	protected ObjectId id;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
//	public void setId(String id) {
//		System.out.println("-------- setId(String id) : " + id);
//		try {
//			this.id = new ObjectId(id);
//		}catch(IllegalArgumentException e){
//			this.id = new ObjectId();
//		}
//	}
	
	public String toString(){
		StringBuffer sbf = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds){
				String fieldName = pd.getName();
				if(fieldName.equals("class"))continue;
				
				Object fieldValue = pd.getReadMethod().invoke(this);
				
				if(sbf == null){
					sbf = new StringBuffer("{");
				}else {
					sbf.append(",");
				}
				sbf.append(fieldName).append("=").append(fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(sbf != null){
			sbf.append("}");
			return sbf.toString();
		}
		
		return super.toString();
	}
}
