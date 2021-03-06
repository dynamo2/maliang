package com.maliang.core.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.Business;
import com.maliang.core.model.Linked;
import com.maliang.core.model.MongodbModel;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Project;
import com.maliang.core.model.Subproject;
import com.maliang.core.model.VariableLinked;
import com.maliang.core.util.Utils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class AbstractDao  {
	protected static MongoClient mongoClient;
	protected static DB db;
	protected static String DB_TIANMA = "tianma";
	protected static String DB_JIRA = "jira";
	private final static List<String> SYSTEM_DB_COLLECTIONS = new ArrayList<String>();

	@SuppressWarnings("rawtypes")
	protected static Map<String,Class> INNER_TYPE = new HashMap<String,Class>();
	static {
		mongoClient = new MongoClient();
		db = mongoClient.getDB(DB_TIANMA);
		
		SYSTEM_DB_COLLECTIONS.add("ObjectMetadata");
		SYSTEM_DB_COLLECTIONS.add("Business");
		SYSTEM_DB_COLLECTIONS.add("Project");
		SYSTEM_DB_COLLECTIONS.add("UCType");
	}

	protected DBCollection getDBCollection(String name){
		if(!isSystemCollection(name)){
			Project project = getSessionProject();
			if(project != null){
				name = project.getKey()+"_"+name;
			}
			
			//Test  电商项目
			//name = "EB_"+name;
		}
		//System.out.println("name : " + name);
		return db.getCollection(name);
	}
	
	protected boolean isSystemCollection(String name){
		return SYSTEM_DB_COLLECTIONS.contains(name);
	}
	
	protected static Project getSessionProject(){
		Object bus = Utils.getSessionValue("SYS_BUSINESS");
		if(bus != null && bus instanceof Business){
			MongodbModel bpro = ((Business)bus).getProject();
			if(bpro instanceof Project){
				return (Project)bpro;
			}else if(bpro instanceof Subproject){
				ProjectDao dao = new ProjectDao();
				
				return dao.findOne(new BasicDBObject("subprojects._id",bpro.getId()));
			}
		}
		
		Project testProject = new Project();
		testProject.setKey("EB");
		testProject.setId(new ObjectId("5adf31a89f7b032e782aa27c"));
		return testProject;
	}
	
	public void renameCollection(String collName,String newName){
		if(db.getCollectionNames().contains(collName)){
			db.getCollection(collName).rename(newName);
		}
	}
	
	protected BasicDBObject getDBObject(String collName,String oid){
		return (BasicDBObject)db.getCollection(collName).findOne(this.getObjectId(oid));
	}
	
	protected BasicDBObject getObjectId(Object oid){
		if(oid instanceof ObjectId) {
			return new BasicDBObject("_id",(ObjectId)oid);
		}
		
		try {
			return new BasicDBObject("_id",new ObjectId(oid.toString()));
		}catch(IllegalArgumentException e){
			return new BasicDBObject("_id",new ObjectId());
		}
	}
	
	protected <T> List<T> readCursor(DBCursor cursor,Class<T> cls){
		List<T> result = new ArrayList<T>();
		while(cursor.hasNext()){
			result.add(decode((BasicDBObject)cursor.next(),cls));
		}
		
		return result;
	}
	
	protected <T> T loadLinkedObject(String oid,Class<T> cls){
		com.maliang.core.model.Collection coll = (com.maliang.core.model.Collection)cls.getAnnotation(com.maliang.core.model.Collection.class);
		if(coll == null)return null;
		
		String collName = coll.name();
		BasicDBObject fval = this.getDBObject(collName,oid);
		return this.decode(fval,cls);
	}
	
	protected MongodbModel loadVariableLinkedObject(String val){
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> T decode(BasicDBObject dbObj,Class<T> cls) {
		if(dbObj == null)return null;
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls);
			T result = cls.newInstance();
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds){
				Method readMethod = pd.getReadMethod();
				if(readMethod != null && readMethod.getName().startsWith("is")) {
					//System.out.println("pd.getReadMethod().getName() : " + pd.getReadMethod().getName());
					continue;
				}
				
				//new code
				String fname = pd.getName();
				String dbName = fname;
				if(dbName.equalsIgnoreCase("id"))dbName = "_id";
				
				if(!dbObj.containsField(dbName))continue;

				Object value = dbObj.get(dbName);
				
				//load linked object
				try {
					Field field = cls.getDeclaredField(fname);
					Linked link = field.getAnnotation(Linked.class);
					if(link != null && value != null){
						value = loadLinkedObject((String)value,field.getType());
	    			}
				}catch(Exception e){
					value = null;
				}
				
				//load VariableLinked object
				try {
					Field field = cls.getDeclaredField(fname);
					VariableLinked link = field.getAnnotation(VariableLinked.class);
					if(link != null && value != null){
						value = loadVariableLinkedObject((String)value);
	    			}
				}catch(Exception e){
					value = null;
				}

				
				if(value instanceof BasicDBObject){
					if(pd.getPropertyType().isAssignableFrom(Map.class)){
						value = ((BasicDBObject)value).toMap();
					}else {
						value = decode((BasicDBObject)value,pd.getPropertyType());
					}
				}else if(value instanceof BasicDBList){
					List vlist = new ArrayList();
					Class innerCls = INNER_TYPE.get(cls.getSimpleName()+"."+pd.getName());
					
					//System.out.println("key : " + cls.getSimpleName()+"."+pd.getName());
					
					for(Object dbj:(BasicDBList)value){
						if(dbj == null)continue;
						
						
						if(dbj instanceof BasicDBObject && !innerCls.isAssignableFrom(Map.class)){
							vlist.add(decode((BasicDBObject)dbj,innerCls));
						}else {
							vlist.add(dbj);
						}
					}
					
					value = vlist;
				}
				
				if(pd.getName().equalsIgnoreCase("id")){
					value = dbObj.getObjectId("_id");
				}

				pd.getWriteMethod().invoke(result,value);
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void main(String[] args) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(ObjectMetadata.class);
			
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds){
				if(pd.getName().equals("class") || pd.getName().equals("id")){
					continue;
				}
				Linked lc = ObjectMetadata.class.getDeclaredField(pd.getName()).getAnnotation(Linked.class);
				if(lc != null){
					
				}
				System.out.println(pd.getName() + " : " + lc);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String ecodeLinkedValue(MongodbModel mval){
		if(mval == null)return null;
		
		if(mval.getId() != null){
			return mval.getId().toString();
		}
		return null;
	}
	
	protected BasicDBObject encode(Object obj,boolean insertID) {
		BasicDBObject doc = new BasicDBObject();
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
	        if(pds != null && pds.length>0){
	            for(PropertyDescriptor pd : pds){
	            	//new code
	            	String dbName = pd.getName();
	            	if(dbName.equalsIgnoreCase("class")) continue;
	            	
	            	try {
	            		Object value = pd.getReadMethod().invoke(obj);
	            		if(insertID && dbName.equalsIgnoreCase("id")){
	            			dbName = "_id";
	            			if(value == null){
	            				value = new ObjectId();
	            			}
	            		}
	            		
	            		if(value == null) continue;
	            		
	            		if(value instanceof MongodbModel){
	            			Linked link = obj.getClass().getDeclaredField(dbName).getAnnotation(Linked.class);
	            			if(link != null){
	            				value = ecodeLinkedValue((MongodbModel)value);
	            			}else {
	            				value = encode(value,insertID);
	            			}
	            		}else if(value instanceof Collection){
	            			List<Object> vl = new ArrayList<Object>();
							for(Object val : (Collection)value){
	            				if(val instanceof MongodbModel){
	            					val = encode(val,insertID);
	            				}
	            				
	            				vl.add(val);
	            			}
	            			
	            			value = vl;
	            		}
	            		
	            		doc.put(dbName, value);
	            		//doc.append(dbName,value);
	            	}catch(Exception e){
	            		System.out.println("+++++++++++ e : " + e.getMessage());
	            		doc.append(dbName,null);
	            	}
	            }
	        }
		} catch (IntrospectionException e1) {}
        
		return doc;
	}
}
