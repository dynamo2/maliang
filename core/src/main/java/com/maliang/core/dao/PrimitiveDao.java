package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.function.MathFunction;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 閹绘劒绶甸崢鐔奉潗閻ㄥ嚍B閹垮秳缍旈幒銉ュ經閸滃本鏆熼幑锟�
 * **/
public class PrimitiveDao {
	protected static MongoClient mongoClient;
	protected static DB db;
	protected static String DB_FILE = "tianma";
	//protected static String DB_FILE = "jira";
	//protected static String DB_FILE = "tianma";
	//private DBCollection collection = null;

	@SuppressWarnings("rawtypes")
	protected static Map<String,Class> INNER_TYPE = new HashMap<String,Class>();
	
	static{
		use();
	}
	
	
	
	public static String defaultDB(){
		return DB_FILE;
	}
	
	public static void main(String[] args) {
		PrimitiveDao dao = new PrimitiveDao();
		dao.use();
		
		//CommandResult cr = db.command("Business.find()");
		//System.out.println("cr : " + cr);
		
//		Map data = new HashMap();
//		Map query = new HashMap();
//		Map unset = new HashMap();
//		Map notnull = new HashMap();
//		
//		notnull.put("$gt",2);
//		query.put("_id",new ObjectId("58e85b37f2a3bc01a3757402"));
//		query.put("workflows.step", notnull);
//		
//		data.put("$set", unset);
//		unset.put("workflows.$.css", "css33");
//		
//		dao.update(data, query, "Business", false, false);
		
		//dao.rename(,"css","Business");
		
		dao.renameDocument("58e85b37f2a3bc01a3757402", "workflows.ddd", null, "Business");
		
		
		/**
		 * pdb.Business.update({
    query:{
        _id:oid'58e85b37f2a3bc01a3757402'),
        workflows._id:'58e85b37f2a3bc01a3757403'
    },
    update:{
        $unset:{
           workflows.$.css:''
        }
    }
})
		 * 
		 * ***/
		
//		dao.connect("File");
//		
//		String s = "{name:'dao',code:'dao'}";
//		Object val = AE.execute(s);
//		
//		dao.save((Map)val);
//		//dao.removeAll();
//		List<Map> list = dao.find();
//		
//		System.out.println("val : " + val);
//		System.out.println("list : " + list);
	}
	
	public void rename(String oldField,String newField,String coll){
		List<Object> list = this.find(null, coll);

		String[] fns = oldField.split("\\.");
		for(Object obj : list){
			if(!(obj instanceof Map)){
				continue;
			}
			
			renameDocument((Map)obj,fns,newField,coll);
		}
	}
	
	public void renameDocument(String id,String oldField,String newField,String coll){
		Map data = this.getByID(id,coll);
		String[] fns = oldField.split("\\.");
		
		renameDocument(data,fns,newField,coll);
	}
	
	public void renameDocument(Map doc,String[] oldFields,String newField,String coll){
		Map query = Utils.newMap("_id",doc.get("_id"));
		Map set = new HashMap();
		Map update = Utils.newMap("$set",set);

		String parent = null;
		for(int i = 0; i < oldFields.length; i++){
			String fn = oldFields[i];
			
			Object fv = doc.get(fn);
			if(Utils.isEmpty(fv)){
				break;
			}
			
			if(parent == null){
				parent = fn;
			}else {
				parent += "."+fn;
			}
			
			if(Utils.isArray(fv)){	
				String[] subs = (String[])ArrayUtils.subarray(oldFields, i+1, oldFields.length);
				renameItems(Utils.toArray(fv),subs,newField);
				
				set.put(parent,fv);
				
				for(Object fo : Utils.toArray(fv)){
					System.out.println("fo : " + ((Map)fo).keySet());
				}
				break;
			}
			
			if(fv instanceof Map) {
				doc = (Map)fv;
			}
			
			if(i == oldFields.length-1){
				set.put(parent,fv);
			}
		}
		
		System.out.println("rename query : " + query);
		System.out.println("rename update : " + update);
		System.out.println("set key : " + set.keySet());
		
		this.update(update, query, coll,true,true);
	}
	
	
	
	/*
	 * 我是
	 * **/
	public void renameItems(Object[] items,String[] oldFields,String newField){
		for(Object io:items){
			if(io instanceof Map){
				Map data = (Map)io;
				
				for(int i = 0; i < oldFields.length; i++){
					String fn = oldFields[i];
					
					if(!data.containsKey(fn)){
						break;
					}
					
					Object fv = data.get(fn);
					
					if(!Utils.isEmpty(fv)){
						if(Utils.isArray(fv)){
							String[] subs = (String[])ArrayUtils.subarray(oldFields, i+1, oldFields.length);
							renameItems(Utils.toArray(fv),subs,newField);
							
							break;
						}
						
						if(fv instanceof Map){
							data = (Map)fv;
						}
					}
					
					if(i == oldFields.length-1){
						if(!Utils.isEmpty(newField)){
							data.put(newField,fv);
						}
						data.remove(fn);
					}
					
					if(fv == null){
						break;
					}
				}
			}
		}
	}
	
	public void rename(Map data,String oldField,String newField){
		if(!data.containsKey(oldField)){
			return;
		}
		
		Object val = data.get(oldField);
		data.put(newField,val);
	}
	
	public static void use(){
		use(DB_FILE);
	}
	
	public static void use(String dbName){
		if(mongoClient == null){
			mongoClient = new MongoClient();
		}
		
		db = mongoClient.getDB(dbName);
	}
	
	public static String showDB(){
		return db.getName();
	}
	
	public static Collection showAll(){
		return db.getCollectionNames();
	}
	
	
//	public void connect(String coll){
//		this.collection = db.getCollection(coll);
//	}

	public Object save(Object data,String coll){
		if(data == null)return null;
		
		if(data instanceof Map){
			WriteResult result = null;
			result = updateByQuery((Map)data,coll);
			if(result != null){
				return result;
			}
			
			ObjectId id = this.readId((Map)data);
			if(id == null){
				return this.insert((Map)data, coll);
			}else {
				return this.update((Map)data, coll);
			}
		}else if(data instanceof List){
			List<WriteResult> results = new ArrayList<WriteResult>();
			List<Map> inserts = new ArrayList<Map>();
			for(Map d :(List<Map>)data){
				if(d == null){
					continue;
				}
				
				WriteResult r = updateByQuery(d,coll);
				if(r != null){
					results.add(r);
					continue;
				}
				
				ObjectId id = this.readId(d);
				if(id != null){
					results.add(this.update(d, coll));
				}else {
					inserts.add(d);
				}
			}
			
			results.add(this.insert(inserts, coll));
			return results;
		}
		return null;
	}

	public WriteResult updateByQuery(Map data,String coll){
		if(data == null)return null;
		Object update = data.get("update");
		boolean multi = MapHelper.readValue(data,"multi",Boolean.class,false);
		boolean upsert = MapHelper.readValue(data,"upsert",Boolean.class,false);
		
		if(update != null && update instanceof Map){
			Map query = MapHelper.readValue(data,"query",Map.class,null);
			
			return this.update((Map)update, (Map)query, coll,upsert,multi);
		}
		return null;
	}
	
	public WriteResult update(Map data,String coll){
		ObjectId id = this.readId(data);
		this.removeId(data);
		
		
		BasicDBObject idQuery = DaoHelper.getObjectIdQuery(id);
		return this.update(data,idQuery, coll,false,false);
	}
	
	public WriteResult update(Map data,Map query,String coll,boolean upsert,boolean multi){
		DBCollection dbc = db.getCollection(coll);
		
		//dbc.mapReduce(map, reduce, outputTarget, query);
		
		BasicDBObject dbQuery = DaoHelper.dbQuery(query);
//		BasicDBObject dbData = new BasicDBObject("$set",data);
		BasicDBObject dbData = new BasicDBObject(data);
		
		System.out.println("------------ update dbQuery : " + dbQuery);
		System.out.println("------------ update dbData : " + dbData);
		
		try {
			WriteResult wr = dbc.update(dbQuery, dbData,upsert,multi);
			
			System.out.println("wr : " + wr);
			return wr;
		}catch(RuntimeException e){
			System.out.println(e.getMessage());
			//e.getStackTrace();
			
			throw e;
		}
	}

	public WriteResult insert(Map data,String coll){
		DBCollection dbc = db.getCollection(coll);
		
		BasicDBObject dbData = new BasicDBObject(data);
		return dbc.insert(dbData);
	}
	
	public WriteResult insert(List<Map> datas,String coll){
		DBCollection dbc = db.getCollection(coll);
		
		List<DBObject> list = new ArrayList<DBObject>();
		for(Map data : datas){
			BasicDBObject dbData = new BasicDBObject(data);
			list.add(dbData);
		}
		
		return dbc.insert(list);
	}
	
	public WriteResult remove(Object id,String coll){
		BasicDBObject idQuery = DaoHelper.getObjectIdQuery(id);
		return this.remove(idQuery, coll);
	}
	
	public WriteResult remove(Map query,String coll){
		DBCollection dbc = db.getCollection(coll);
		BasicDBObject dbQuery = DaoHelper.dbQuery(query);
		return dbc.remove(dbQuery);
	}
	
 	public List<Object> find(Map query,String coll){
		DBCollection dbc = db.getCollection(coll);
		
		BasicDBObject dbQuery = DaoHelper.dbQuery(query);
		DBCursor cursor = dbc.find(dbQuery);
		
		return this.toList(cursor.iterator());
	}
	
	public List<Object> find(Map query,int size,boolean random,String coll){
		List<Object> list = this.find(query, coll);
		if(Utils.isEmpty(list)){
			return list;
		}
		
		if(size < 0){
			size = list.size();
		}
		size = size > list.size()?list.size():size;
		
		if(!random){
			return list.subList(0,size);
		}
		
		List<Object> results = new ArrayList<Object>();
		List<Integer> used = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){
			int idx = MathFunction.random(0, list.size());
			while(used.contains(idx)){
				idx = MathFunction.random(0, list.size());
			}
			
			used.add(idx);
			results.add(list.get(idx));
		}
		
		return results;
	}
	
	public Map<String, Object> getByID(Object oid, String collName) { 
		return this.findOne(DaoHelper.getObjectIdQuery(oid), collName,0);
	}
	
	public Object getInnerObject(String collName,String innerName,Object oid){
		List<DBObject> pipe = new ArrayList<DBObject>();
		DBCollection dbc = db.getCollection(collName);
		
		BasicDBObject query = new BasicDBObject(innerName+"._id",DaoHelper.getObjectId(oid,false));
		
		pipe.add(new BasicDBObject("$match",query));
		pipe.add(new BasicDBObject("$unwind","$"+innerName));
		pipe.add(new BasicDBObject("$match",query));
		pipe.add(new BasicDBObject("$project",new BasicDBObject().append(innerName,1).append("_id",0)));
		
		AggregationOutput aout = dbc.aggregate(pipe);
		Iterator<DBObject> ie = aout.results().iterator();
		
		return this.findOne(ie, 0).get(innerName);
	}
	
	public Map<String, Object> findOne(Map query, String collName,int index) {
		BasicDBObject dbQuery = new BasicDBObject(query);
		return this.findOne(dbQuery, collName,index);
	}
	
	public List<Object> aggregate(List query,String collName){
		DBCollection dbc = db.getCollection(collName);
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		for(Map m:(List<Map>)query){
			pipeline.add(new BasicDBObject(m));
		}
		AggregationOutput out = dbc.aggregate(pipeline);
		
		return this.toList(out.results().iterator());
	}
	
	private void removeId(Map data){
		if(data == null)return;
		
		data.remove("_id");
		data.remove("id");
	}
	private ObjectId readId(Map data){
		ObjectId id = this.readId(data,"_id");
		if(id == null){
			id = this.readId(data,"id");
		}
		return id;
	}
	
	private ObjectId readId(Map data,String idKey){
		if(data == null)return null;
		
		Object id = data.get(idKey);
		if(id != null){
			if(id instanceof ObjectId){
				return (ObjectId)id;
			}
			
			try {
				return new ObjectId(id.toString());
			}catch(Exception e){}
		}
		return null;
	}
	
	
	private List<Object> toList(Iterator<DBObject> ie) {
		List<Object> results = new ArrayList<Object>();
		while (ie.hasNext()) {
			results.add(ie.next());
		}
		return results;
	}
	
	private BasicDBObject findOne(BasicDBObject idQuery, String collName,int index) {
		DBCollection dbc = db.getCollection(collName);
		
		DBCursor cursor = dbc.find(idQuery);
		if(index < 0 || index >= cursor.count()){
			index = 0;
		}
		
		return this.findOne(cursor, index);
	}
	
	private BasicDBObject findOne(Iterator<DBObject> ie,int index) {
		if(index < 0){
			index = 0;
		}
		
		int curr = 0;
		BasicDBObject result = new BasicDBObject();
		while (ie.hasNext()) {
			result = (BasicDBObject) ie.next();
			if(curr++ == index){
				return result;
			}
		}

		return result;
	}
	
	
	public void removeAll(String coll){
		DBCollection dbc = db.getCollection(coll);
		
		dbc.remove(new BasicDBObject());
	}

	public DBCollection getDBCollection(String name){
		return db.getCollection(name);
	}
}
