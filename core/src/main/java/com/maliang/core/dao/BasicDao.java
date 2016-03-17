package com.maliang.core.dao;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.util.StringUtil;
import com.mongodb.BasicDBObject;

public class BasicDao extends AbstractDao{
	private static String OBJECT_METADATA_KEY = "_object_metadata_";
	protected ObjectMetadataDao metaDao = new ObjectMetadataDao();
	
	public void save(BasicDBObject doc,String collName) {
		this.getDBCollection(collName).save(doc);
	}
	
	public BasicDBObject buildBasicDBObject(Map<String,Object> datas,String objectUniqueMark){
		//String collName = (String)datas.get(OBJECT_METADATA_KEY);
		
		ObjectMetadata metadata = this.metaDao.getByUniqueMark(objectUniqueMark);
		
		return null;
	}
	
	protected BasicDBObject build(Map<String,Object> query){
		if(query == null || query.isEmpty()){
			return null;
		}
		
		return new BasicDBObject(query);
	}
	
	protected static Map<String,Object> buildDBQueryMap(Map<String,Object> queryMap,String prefix){
		Map<String,Object> daoMap = new HashMap<String,Object>();
		String preName = "";
		if(!StringUtil.isEmpty(prefix)){
			preName = prefix+".";
		}
		
		if(queryMap.get("id") != null){
			try {
				daoMap.put(preName+"_id", new ObjectId(queryMap.remove("id").toString()));
			}catch(IllegalArgumentException ae){}
		}
		
		for(String fname : queryMap.keySet()){
			String key = preName+fname;
			Object value = queryMap.get(fname);
			
			if(value != null && value instanceof Map && ((Map)value).size() > 0){
				Map<String,Object> valMap = (Map<String,Object>)value;

				Map<String,Object> m = buildDBQueryMap((Map<String,Object>)value,key);
				daoMap.putAll(m);
			}else {
				daoMap.put(key, value);
			}
		}
		
		return daoMap;
	}
}
