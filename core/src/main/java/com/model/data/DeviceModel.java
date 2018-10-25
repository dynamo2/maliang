package com.model.data;

import java.util.List;

import org.bson.types.ObjectId;

import com.model.db.BsonUtil;
import com.model.db.Column;
import com.model.db.Table;

@Table(name="EZ_DeviceModel")
public class DeviceModel {
	private ObjectId id;
	private String name;
	private Integer status;
	private String cover;
	
	
	//private boolean isPublish;
	
//	@Column(name="type",linked=ModelType.class)
	private ModelType type;
	
	private List<String> photos;
	
//	@Column(name="deviceDicts",linked=DeviceDict.class)
	private List<DeviceDict> deviceDicts;
	
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public ModelType getType() {
		return type;
	}
	public void setType(ModelType type) {
		this.type = type;
	}
	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}
	
	public List<DeviceDict> getDeviceDicts() {
		return deviceDicts;
	}

	public void setDeviceDicts(List<DeviceDict> deviceDicts) {
		this.deviceDicts = deviceDicts;
	}

	public boolean isPublished() {
		return new Integer(2).equals(this.status);
	}
	
	public boolean isNewModel() {
		return new Integer(1).equals(this.status);
	}
	
	public boolean isCanceled() {
		return new Integer(3).equals(this.status);
	}
	
	public static void main(String[] args) {
		
		BsonUtil bu;
	}
}
