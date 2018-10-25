package com.model.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.model.dao.DeviceModelDao;
import com.model.dao.impl.DeviceModelDaoImpl;
import com.model.data.DeviceModel;
import com.model.data.Dict;
import com.model.data.DictType;
import com.model.data.ModelType;
import com.model.service.DeviceModelService;
import com.model.service.DictService;
import com.model.service.ModelTypeService;
import com.model.service.Pager;
import com.mongodb.client.model.Filters;

public class DeviceModelServiceImpl extends ServiceImpl<DeviceModel> implements DeviceModelService {
	public DeviceModelDao deviceDao;
	public ModelTypeService typeService = new ModelTypeServiceImpl();
	public DictService dictService = new DictServiceImpl();
	
	public DeviceModelServiceImpl() {
		this.deviceDao = new DeviceModelDaoImpl();
		
		this.setDao(this.deviceDao);
	}
	
	public List<DeviceModel> finds(Pager page){
		return this.deviceDao.finds(page);
	}
	
	public List<DeviceModel> finds(Pager page,Map search){
		
		Bson bq = new Document();
		Bson sort = new Document();
		
		Bson keyQuery = this.buildKeyQuery(search);
		String searchKey = (String)search.get("key");
		if(searchKey != null && searchKey.length() > 0) {
			
			List<ModelType> types = typeService.finds(keyQuery, sort);
			List<ObjectId> tids = new ArrayList<ObjectId>();
			for(ModelType mt : types) {
				tids.add(mt.getId());
			}
			search.put("types",types);
			Bson tq = Filters.in("type",tids);
			
			
			List<Dict> dicts = this.dictService.finds(keyQuery, sort);
			List<ObjectId> dids = new ArrayList<ObjectId>();
			Map<ObjectId,DictType> dtypeMap = new HashMap<ObjectId,DictType>();
			for(Dict dt : dicts) {
				dids.add(dt.getId());
				
				DictType dtype = dt.getDictType();
				DictType temp = dtypeMap.get(dtype.getId());
				if(temp == null) {
					dtypeMap.put(dtype.getId(),dtype);
				}else {
					dtype = temp;
				}
				 
				
				if(dtype.getDicts() == null) {
					dtype.setDicts(new ArrayList<Dict>());
				}
				dtype.getDicts().add(dt);
				dt.setDictType(null);
			}
			
			search.put("dictTypes",dtypeMap.values());
			
			Bson dq = Filters.in("deviceDicts.dict",dids);
			bq = Filters.or(keyQuery,tq,dq);
		}

		return this.deviceDao.finds(bq,sort, page);
	}
	
	public List<DeviceModel> finds(Map<String,Object> search,Map<String,Object> not,Pager page){
		
		Bson keyQuery = this.buildKeyQuery(search);
		
		Bson typeQuery = this.buildObjectIdsQuery(search,"types","type",false);
		Bson dictQuery = this.buildObjectIdsQuery(search,"dicts","deviceDicts.dict",false);
		
		Bson notType = this.buildObjectIdsQuery(not,"types","type",true);
		Bson notDict = this.buildObjectIdsQuery(not,"dicts","deviceDicts.dict",true);
		
		Bson query = Filters.and(Filters.or(keyQuery,typeQuery,dictQuery),Filters.or(notType,notDict));
		
		System.out.println("-------- query s: " + query);
		
		return this.deviceDao.finds(query,new Document(), page);
	}
	
	private Bson buildKeyQuery(Map<String,Object> search) {
		Bson query = new Document();
		if(search == null || search.isEmpty()) {
			return query;
		}
		
		String searchKey = (String)search.get("key");
		if(searchKey == null || searchKey.isEmpty()) {
			return query;
		}
		if(searchKey != null && searchKey.length() > 0) {
			String[] keys = searchKey.split(" ");
			List<Bson> nqs = new ArrayList<Bson>();
			for(String k : keys) {
				if(k.trim().isEmpty()) {
					continue;
				}
				
				Pattern pattern = Pattern.compile("^.*" + k+ ".*$", Pattern.CASE_INSENSITIVE);
				nqs.add(Filters.regex("name", pattern));
			}
			query = Filters.or(nqs);
		}
		return query;
	}
	
	private ObjectId toObjectId(Object obj) {
		if(obj == null) {
			return null;
		}
		
		if(obj instanceof ObjectId) {
			return (ObjectId)obj;
		}
		
		return new ObjectId(obj.toString());
	}
	
	private Bson buildObjectIdsQuery(Map<String,Object> map,String key,String field,boolean not) {
		if(map == null) {
			return new Document();
		}
		
		Object val = map.get(key);
		if(val instanceof List) {
			List<ObjectId> tids = new ArrayList<ObjectId>();
			for(Object id:(List)val) {
				if(id == null) {
					continue;
				}
				
				try {
					tids.add(toObjectId(id));
				}catch(Exception e) {}
			}
			
			Bson query = Filters.in(field,tids);
			if(not) {
				query = Filters.not(query);
			}
			return query;
		}
		
		if(not) {
			return Filters.ne(field,toObjectId(val));
		}
		
		return Filters.eq(field,toObjectId(val));
	}

	private void readSameDictType(List<DictType> dictTypes,DictType dtype) {
		
	}
	
	public List<DeviceModel> finds(Bson bson, Bson sort, int limit, int skip){
		return null;
	}
	
}
