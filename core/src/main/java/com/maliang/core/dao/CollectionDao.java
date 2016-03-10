package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.DateCalculator;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.ui.controller.Pager;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class CollectionDao extends BasicDao {
	public static void main(String[] args) {
//		String str = "{account:{account:'zhanghui',password:'123456',"
//				+ "personal_profile:{real_name:'张惠',email:'zh@tm.com',age:100,"
//				+ "address:[{province:'江苏省',city:'南京市',zone:'鼓楼区'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}}}";
//		
//		str = "{acc:db.Account.get('56dfbe2dba596ca3c6c9f971')}";
//		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(str,null);
//		
//		str = "{app:acc.personal_profile}";
//		Object v = ArithmeticExpression.execute(str,params);
//		
//		System.out.println(v);
//		
//		str = "{real_name:'张惠',email:'zh@tm.com',age:100,"
//				+ "address:[{province:'江苏省',city:'南京市',zone:'鼓楼区'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}";
//		str = "{personal_profile.address:[{province:'江苏省',city:'南京市',zone:'鼓楼区'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}";
//		
//		
//		str = "{personal_profile.address.$.province:'江苏省',personal_profile.address.$.city:'苏州市'}";
//		Map<String,Object> accountMap = (Map<String,Object>)ArithmeticExpression.execute(str,null);
////		for(Map<String,Object> map : (List<Map<String,Object>>)accountMap.get("personal_profile.address")){
////			map.put("_id",new ObjectId());
////		}
//		CollectionDao dao = new CollectionDao();
//		System.out.println("accountMap : " + accountMap);
//		//dao.update(accountMap,"Account");
//		
//		dao.getDBCollection("Account").update(new BasicDBObject("personal_profile.address._id",new ObjectId("56dfe161ba594151e4e9ebd6")), 
//				new BasicDBObject("$set",accountMap));
//		
//		
//		
//		System.out.println("========= after update ==============");
//		
//		str = "{acc:db.Account.get('56dfbe2dba596ca3c6c9f971')}";
//		params = (Map<String,Object>)ArithmeticExpression.execute(str,null);
//		
//		str = "{app:acc.personal_profile}";
//		v = ArithmeticExpression.execute(str,params);
//		
//		System.out.println(v);
		
		System.out.println("============== before update =================");
		printTest("56e0e4fb8f778c15692b9eaf");
		
		ObjectMetadataDao omDao = new ObjectMetadataDao();
		CollectionDao collDao = new CollectionDao();
		BasicDBObject query = new BasicDBObject("_id",new ObjectId("56e0e4fb8f778c15692b9eaf"));
		
		String str = "{F3:{F31:[{F311:{F3114:[{F31141:'F31141_1'}]},id:'56e0e4fb8f778c15692b9ead'}]}}";
		str = "{F3:{F31:[{F312:'F312_2',F313:'F313_2'},{F311:{F3114:[{F31142:'F31142_1',id:'56e0fbe28f77546a3d590d58'}]}}]}}";
		Map<String,Object> params = (Map<String,Object>)ArithmeticExpression.execute(str,null);
		
		//System.out.println("TEST : " + params);
		
		ObjectMetadata meta = omDao.getByName("Test");
		List<Map<String,BasicDBObject>> updates = new ArrayList<Map<String,BasicDBObject>>();
		Map<String,Object> daoMap = encodeInner(meta.getFields(),params,null,updates,query);
		
		if(daoMap != null && daoMap.size() > 0){
			Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
			bdbMap.put("query", query);
			bdbMap.put("update", new BasicDBObject("$set",daoMap));
			updates.add(bdbMap);
		}
		
		for(Map<String,BasicDBObject> um : updates){
			System.out.println(um);
			
			//WriteResult daoResult = dao.getDBCollection("Test").update(um.get("query"), um.get("update"));
			//System.out.println("daoResult : " + daoResult);
		}
		
//		daoMap = new HashMap<String,Object>();
//		daoMap.put("F3.F31.$.F311.F3114.$1.F31143","F31143_1");
//		
//		WriteResult daoResult = dao.getDBCollection("Test").update(
//				new BasicDBObject("F3.F31.F311.F3114._id",new ObjectId("56e0fbe28f77546a3d590d58")), 
//				new BasicDBObject("$set",daoMap));
//		System.out.println("daoResult : " + daoResult);
		
		System.out.println("");
		System.out.println("============== after update =================");
		printTest("56e0e4fb8f778c15692b9eaf");
		//System.out.println(updates);
		
//		DBCursor cursor = collDao.getDBCollection("Test").find(new BasicDBObject("F3.F31.F311.F3114._id",new ObjectId("56e0fbe28f77546a3d590d58")));
//		
//		while(cursor.hasNext()){
//			BasicDBObject doc = (BasicDBObject)cursor.next();
//			
//			System.out.println(doc);
//		}
		
		
		//System.out.println("daoMap : " + daoMap);
		
		
		
//		ObjectMetadata meta = omDao.getByName("Test");
//		List<String> updates = new ArrayList<String>();
//		Map<String,Object> daoMap = encodeInner(meta.getFields(),params,null,updates);
		
		//System.out.println("daoMap : " + daoMap);
		//testEncode();
	}
	
	private static void printTest(String id){
		String str = "db.Test.get('"+id+"')";
		Map<String,Object> val = (Map<String,Object>)ArithmeticExpression.execute(str,null);
		System.out.println("val : " + val);
		System.out.println("");
	}
	
	
	
	private static boolean hasId(Object obj){
		if(obj == null)return false;
		
		if(obj instanceof List){
			for(Object oo : (List)obj){
				if(hasId(oo)){
					return true;
				}
			}
		}
		
		if(obj instanceof Map){
			if(((Map)obj).get("id") != null){
				return true;
			}
			
			for(Object oo : ((Map)obj).values()){
				if(hasId(oo)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static Map<String,Object> encodeInner(List<ObjectField> fields,Map<String,Object> innMap,
			String prefix,List<Map<String,BasicDBObject>> updates,BasicDBObject updateQuery){
		
		Map<String,Object> daoMap = new HashMap<String,Object>();
		String preName = "";
		if(prefix != null && prefix.trim().length() > 0){
			preName = prefix+".";
		}
		
		for(ObjectField ff : fields){
			if(!innMap.containsKey(ff.getName()))continue;
			
			String key = preName+ff.getName();
			Object value = innMap.get(ff.getName());
			
			if(FieldType.INNER_COLLECTION.is(ff.getType())){
				if(value != null && value instanceof Map && ((Map)value).size() > 0){
					Map<String,Object> valMap = (Map<String,Object>)value;

					Map<String,Object> m = encodeInner(ff.getFields(),(Map<String,Object>)value,key,updates,updateQuery);
					daoMap.putAll(m);
				}
			}else if(FieldType.ARRAY.is(ff.getType()) && FieldType.INNER_COLLECTION.is(ff.getElementType())){
				if(value != null && value instanceof List && ((List)value).size() > 0){
					List<Map<String,Object>> valList = (List<Map<String,Object>>)value;
					for(Map<String,Object> valMap:valList){
						
						
						boolean hasId = hasId(valMap);
						if(hasId){
							String id = (String)valMap.remove("id");
							BasicDBObject currQuery = updateQuery;
							if(id != null){
								currQuery = new BasicDBObject(key +"._id",new ObjectId(id));
							}

							Map<String,Object> updMap = encodeInner(ff.getFields(),valMap,key+".$",updates,currQuery);
							
//							System.out.println("update array : ");
//							System.out.println("      query : " + key +"._id: " + id);
//							System.out.println("      val : " + updMap);
							
							if(updMap != null && updMap.size() > 0){
								Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
								bdbMap.put("query", currQuery);
								bdbMap.put("update", new BasicDBObject("$set",updMap));
								updates.add(bdbMap);
							}
						}else {
							//push
							
							//Map<String,Object> pushMap = encodeInner(ff.getFields(),valMap,key);
							//System.out.println("\npush array : " + valMap+"\n    prefix="+prefix+", key="+key+"\n");
							
//							System.out.println("push array : ");
//							System.out.println("      query : Object._id");
//							System.out.println("      "+key+".val : " + valMap);
							
							valMap.put("_id",new ObjectId());
							
							Map<String,BasicDBObject> bdbMap = new HashMap<String,BasicDBObject>();
							bdbMap.put("query", updateQuery);
							bdbMap.put("update", new BasicDBObject("$push",new BasicDBObject(key,valMap)));
							updates.add(bdbMap);
							
//							dao.getDBCollection("Account").update(new BasicDBObject("personal_profile.address._id",new ObjectId("56dfe161ba594151e4e9ebd6")), 
//									new BasicDBObject("$push",new BasicDBObject(key,valMap)));
						}
					}
				}
			}else {
				daoMap.put(key, value);
			}
		}
		
		return daoMap;
	}
	
	private static void testEncode(){
		ObjectMetadataDao omDao = new ObjectMetadataDao();
		ObjectMetadata meta = omDao.getByName("Account");
		
		
		String str = "{account:{account:'zhanghui',password:'123456',"
				+ "personal_profile:{real_name:'张惠',email:'zh@tm.com',age:100,"
				+ "address:[{province:'江苏省',city:'南京市',zone:'鼓楼区'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}}}";
		
		//str = "{account:'zhanghui',password:'123456',personal_profile:{real_name:'张惠',email:'zh@tm.com',mobile:'1345678909',address:[{province:'江苏省',city:'南京市',zone:'鼓楼区'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}}";
		str = "{personal_profile:{address:[{province:'江苏省',city:'南京市',zone:'鼓楼区',address:[{street:'龙园西路',village:'龙江小区蓝天园',house_number:'26号402室内',id:'222'}],id:'111'},{province:'浙江省',city:'湖州市',zone:'安吉县'}]}}";
		Map<String,Object> account = (Map<String,Object>)ArithmeticExpression.execute(str,null);
		System.out.println("account : " + account);
		
		List<Map<String,BasicDBObject>> updates = new ArrayList<Map<String,BasicDBObject>>();
		Map<String,Object> daoMap = encodeInner(meta.getFields(),account,null,updates,null);
		//ObjectId id = new ObjectId(account.remove("id")+"");
		
		System.out.println("daoMap : " + daoMap);
	}
	
	public void save(Map value,String collName) {
		BasicDBObject doc = this.build(value);
		if(doc == null){
			return;
		}
		
		this.getDBCollection(collName).save(doc);
		value.put("id", doc.getObjectId("_id").toByteArray());
	}
	
	public void update(Map value,String collName) {
		DBCollection db = this.getDBCollection(collName);
		String id = (String)value.remove("id");
		
		BasicDBObject doc = this.build(value);
		db.update(this.getObjectId(id), new BasicDBObject("$set",doc));
	}
	
	public Map<String,Object> getByID(String oid,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(this.getObjectId(oid));
		
		while(cursor.hasNext()){
			BasicDBObject doc = (BasicDBObject)cursor.next();
			
			Map<String,Object> map = doc.toMap();
			mergeLinkedObject(map,collName);
			
			return map;
		}
		
		return this.emptyResult();
	}
	
	/***
	 * 分页查询
	 * **/
	public List<Map<String,Object>> findByMap(Map<String,Object> query,Map<String,Object> sort,Pager pg,String collName){
		return this.find(build(query), build(sort),pg,collName);
	}
	
	/***
	 * 分页查询
	 * **/
	public List<Map<String,Object>> find(BasicDBObject query,BasicDBObject sort,Pager pg,String collName){
		if(pg == null){
			pg = new Pager();
		}
		
		int limit = pg.getPageSize();
		int skip = (pg.getCurPage()-1)*pg.getPageSize();
		
		DBCursor cursor = this.getDBCollection(collName).find(query).sort(sort).skip(skip).limit(limit);
		
		int totalRow = cursor.count();
		pg.setTotalRow(totalRow);
		
		return this.readCursor(cursor, collName);
	}
	
	public List<Map<String,Object>> findByMap(Map<String,Object> query,String collName){
		return this.find(build(query), collName);
	}
	
	public List<Map<String,Object>> find(BasicDBObject query,String collName){
		DBCursor cursor = this.getDBCollection(collName).find(query);
		
		return readCursor(cursor,collName);
	}
	
	private List<Map<String,Object>> readCursor(DBCursor cursor,String collName){
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		for(DBObject dob : cursor.toArray()){
			Map<String,Object> dataMap = dob.toMap();
			mergeLinkedObject(dataMap,collName);
			
			//dataMap.remove("_id");
			results.add(dataMap);
		}
		
		return results;
	}
	
	public List<Map<String,Object>> aggregateByMap(List<Map<String,Object>> query,String collName){
		if(query == null || query.isEmpty()){
			return this.emptyResults();
		}
		
		List<DBObject> pipeline = new ArrayList<DBObject>();
		for(Map<String,Object> map : query){
			if(map.isEmpty())continue;
			
			pipeline.add(new BasicDBObject(map));
		}

		return this.aggregate(pipeline, collName);
	}
	
	private Map<String,Object> emptyResult(){
		return new HashMap<String,Object>();
	}
	
	private List<Map<String,Object>> emptyResults(){
		return new ArrayList<Map<String,Object>>();
	}
	
	public List<Map<String,Object>> aggregate(List<DBObject> pipeline,String collName){
		if(pipeline == null || pipeline.isEmpty()){
			return this.emptyResults();
		}
		
		DBCollection db = this.getDBCollection(collName);
		AggregationOutput aout = db.aggregate(pipeline);
		Iterator<DBObject> ie = aout.results().iterator();
		
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		while(ie.hasNext()){
			results.add(ie.next().toMap());
		}
		return results;
	}
	
	public void remove(String oid,String collName){
		this.getDBCollection(collName).remove(this.getObjectId(oid));
	}
	
	public void removeAll(String collName){
		this.getDBCollection(collName).remove(new BasicDBObject());
	}
	
	private void mergeLinkedObject(Map<String,Object> dataMap,String collName){
		//dataMap.put("id",dataMap.remove("_id").toString());
		
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		if(metedata == null)return;
		
		correctField(dataMap,metedata.getFields());
		
		/*
		for(ObjectField of : metedata.getFields()){
			String fieldName = of.getName();
			if(FieldType.LINK_COLLECTION.is(of.getType())){
				String linkCollName = getLinkedCollectionName(of.getLinkedObject());
				if(linkCollName == null)continue;
				
				Object fieldValue = dataMap.get(fieldName);
				if(fieldValue != null && fieldValue instanceof String && !((String)fieldValue).trim().isEmpty()){
					String linkOid = ((String)fieldValue).trim();
					fieldValue = this.getByID(linkOid, linkCollName);
				}
				dataMap.put(fieldName, fieldValue);
			}else if(FieldType.INNER_COLLECTION.is(of.getType())){
				
			}
		}*/
	}
	
	private void correctField(Map<String,Object> dataMap,List<ObjectField> fields){
		if(dataMap.get("_id") != null){
			dataMap.put("id",dataMap.remove("_id").toString());
		}

		for(ObjectField field : fields){
			String fieldName = field.getName();
			if(FieldType.LINK_COLLECTION.is(field.getType())){
				String linkCollName = getLinkedCollectionName(field.getLinkedObject());
				if(linkCollName == null)return;
				
				Object fieldValue = dataMap.get(fieldName);
				if(fieldValue != null && fieldValue instanceof String && !((String)fieldValue).trim().isEmpty()){
					String linkOid = ((String)fieldValue).trim();
					fieldValue = this.getByID(linkOid, linkCollName);
				}
				dataMap.put(fieldName, fieldValue);
			}else if(FieldType.INNER_COLLECTION.is(field.getType())){
				Object fValue = dataMap.get(fieldName);
				if(fValue instanceof Map){
					Map<String,Object> innMap = (Map<String,Object>)fValue;
					correctField(innMap,field.getFields());
				}
			}else if(FieldType.ARRAY.is(field.getType()) && FieldType.INNER_COLLECTION.is(field.getElementType())){
				Object fValue = dataMap.get(fieldName);
				if(fValue instanceof List){
					List<Map<String,Object>> innList = (List<Map<String,Object>>)fValue;
					for(Map<String,Object> map:innList){
						correctField(map,field.getFields());
					}
				}
			}
		}
	}
	
	private String getLinkedCollectionName(String linkedObjectId){
		ObjectMetadata linkMeta = this.metaDao.getByID(linkedObjectId);
		if(linkMeta != null){
			return linkMeta.getName();
		}
		return null;
	}

	/********************** Test ****************************/
	public static void printList(List<Map<String,Object>> ps){
		for(Map<String,Object> md : ps){
			for(Map.Entry<String, Object> me: md.entrySet()){
				if(me.getValue() instanceof Date){
					me.setValue(new TMDate((Date)me.getValue()));
				}
			}
			md.remove("id");
			
			System.out.println(md);
		}
	}
	
	static String collName = "TBrand";
	static CollectionDao dao = new CollectionDao();

	public static void testUpdate(){
		Map m = new HashMap();
		m.put("name", "pola");
		m.put("id","5582390ffc7770b40ef01787");
		m.put("modified_date", new Date());
		
		dao.update(m, collName);
	}
	
	private void testSet(){
		DBCollection db = this.getDBCollection("TOrder");
		
		BasicDBObject query = new BasicDBObject();
		query.put("array1.name", "POLA BA夏之晨光化妆水120ML_array10");
		
		BasicDBObject vb = new BasicDBObject();
		vb.put("array1.$.name", "POLA BA赋颜晨光按摩膏120克");
		vb.put("array1.$.description", "POLA B.A碧艾按摩膏特含媲美脸部整形级别的新「塑颜」精华成分，不仅让局部肌肤更加美丽，更着力于整个面部肌肤轮廓的塑造，仅使用一次，即刻排出老废物质和多余脂肪，整个脸部更加紧致，轮廓更加清晰。");
		
		BasicDBObject dbo = new BasicDBObject("$set",vb);
		//BasicDBObject dbo = new BasicDBObject("$set",vb);
		//dbo.put("$currentDate", new BasicDBObject("lastModified",true));
		
		db.update(query, dbo);
	}
	
	private static void testUpdateOperator(){
		/**
		 * Outer {
		 *   name:'',
		 *   price:45.0,
		 *   age:34,
		 *   create_date:,
		 *   modified_date:,
		 *   description:,
		 *   status:1,
		 *   
		 *   inner1:{
		 *   	name:'',
		 *      price:45.0,
		 *      age:34,
		 *      create_date:,
		 *      modified_date:,
		 *      description:,
		 *      status:1,
		 *      
		 *   	inner11:{
		 *   		name:'',
		 *      	price:45.0,
		 *      	age:34,
		 *      	create_date:,
		 *      	modified_date:,
		 *      	description:,
		 *      	status:1,
		 *      
		 *   		inner111:{
		 *   			name:'',
		 *   			price:45.0,
		 *   			age:34,
		 *   			create_date:,
		 *   			modified_date:,
		 *   			description:,
		 *   			status:1
		 *   		}
		 *   	}
		 *   }
		 * }
		 * 
		 * **/
		
		Map temp = new HashMap();
		temp.put("name", "POLA BA夏之晨光化妆水120ML");
		temp.put("price", 750.00);
		temp.put("age", 34);
		temp.put("description", "纳米渗透技术，感受如雾般的迅速吸收，达到深层滋润。抵御肌肤夏乏，塑造水润弹性，清透白皙，充满光泽的肌肤。");
		temp.put("status", 1);
		//temp.put("create_date", new Date());
		//temp.put("modified_date", new Date());
		
		Map outer = tempMap(temp,null);
		Map inner1 = tempMap(temp,"inner1");
		Map inner11 = tempMap(temp,"inner11");
		Map inner111 = tempMap(temp,"inner111");
		
		List array1 = new ArrayList();
		array1.add(tempMap(temp,"array10"));
		array1.add(tempMap(temp,"array11"));
		array1.add(tempMap(temp,"array12"));
		
		outer.put("inner1", inner1);
		outer.put("array1", array1);
		inner1.put("inner11", inner11);
		inner11.put("inner111", inner111);
		
		//dao.save(outer, "TOrder");
		dao.testSet();
		
		System.out.println(stringMap(outer,null));
		
		List<Map<String,Object>> ps = dao.find(null,"TOrder");
		String mapStr = "";
		for(Map<String,Object> map : ps){
			mapStr += "--------------\n";
			mapStr += stringMap(map,null);
		}
		System.out.println(mapStr);
		
//		outer = new HashMap();
//		inner1 = new HashMap();
//		inner11 = new HashMap();
//		inner111 = new HashMap();
//		outer.put("id", "55839567fc77dbb7e3ab9ed9");
//		inner111.put("name","POLA极光幻彩精华50克");
//		
//		outer.put("inner1",inner1);
//		inner1.put("inner11",inner11);
//		inner11.put("inner111",inner111);
//		dao.update(outer, "TOrder");
//		
//		ps = dao.find(null,"TOrder");
//		mapStr = "";
//		for(Map<String,Object> map : ps){
//			mapStr += "\n--------------\n";
//			mapStr += stringMap(map,null);
//		}
//		System.out.println(mapStr);
	}
	
	private static String stringMap(Map<String,Object> map,String prefix){
		String singleIndentation = "    ";
		if(prefix == null)prefix = "";
		String subFix = singleIndentation+prefix;
		
		StringBuffer sbf = new StringBuffer();
		sbf.append("{\n");
		for(Map.Entry entry : map.entrySet()){
			String vlStr = entry.getValue().toString();
			if(entry.getValue() instanceof Map){
				vlStr = stringMap((Map)entry.getValue(),subFix);
			}if(entry.getValue() instanceof List){
				vlStr = "[";
				int i = 0;
				for(Object m:(List<Map>)entry.getValue()){
					if(m instanceof Map){
						vlStr += stringMap((Map)m,subFix);
					}else {
						vlStr += ((i++ > 0)?",":"")+m.toString();
					}
				}
				vlStr += "]\n";
			}
			sbf.append(subFix).append(entry.getKey()).append(":").append(vlStr).append("\n");
		}
		sbf.append(prefix).append("}\n");
		
		return sbf.toString();
	}
	
	private static Map tempMap(Map temp,String suffix){
		Map map = new HashMap();
		map.putAll(temp);
		map.put("name", map.get("name")+ (suffix == null?"":"_"+suffix));
		
		return map;
	}
	
	private static void mapQuery(){
		String query = "[{$project:{name:1,_id:0,totalPay:{$add:['$age','$price']}}},"
				+ "{$group:{_id:'$name',sumTotalPay:{$sum:'$totalPay'}}}]";
		List<Map<String,Object>> list = (List<Map<String,Object>>)ArithmeticExpression.execute(query,null);

		List<Map<String,Object>> results = dao.aggregateByMap(list,"TOrder");
		System.out.println(results);
	}
	
	public static void initTBrand(){
		String[] bs = {"雪花秀","科丽妍","Oshadhi","ACCA KAPPA","ACCA KAPPA","L'occitane","Jason Natural","La colline","Sisley","Albion","Anius","Avalon organics","Biologique Recherche","Caudalie"};
		
		int i = 0;
		String collName = "TBrand";
		CollectionDao dao = new CollectionDao();
		dao.removeAll(collName);
		for(String s : bs){
			Map m = new HashMap();
			m.put("name", s);
			
			long time = System.currentTimeMillis()+(i++)*1000;
			m.put("create_date", new Date(time));
			m.put("modified_date", new Date(time));
			
			dao.save(m, collName);
		}
	}
	
	static class TMDate{
		private Date date;
		public TMDate(Date date){
			this.date = date;
		}
		
		public String toString(){
			return DateCalculator.dateFormat.format(this.date);
		}
	}
	
	public static void testProduct() {
		CollectionDao dao = new CollectionDao();
		
//		Map m = new HashMap();
//		m.put("name", "雪花秀");
//		
//		//dao.save(m, "Brand");
//		
//		Map bdc = dao.getByID("5562fd11bd77137b45adcb44", "Brand");
//		System.out.println(bdc);
//		
//		m = new HashMap();
//		m.put("name", "珍雪面霜60ml");
//		m.put("brand", "5562fd11bd77137b45adcb44");
//		m.put("price", "2285.00");
//		
//		//dao.save(m, "Product");
//		bdc = dao.getByID("5563e32cbd779fb4ab91984c", "Product");
//		System.out.println(bdc);


		//dao.remove("55648ebdbd77bd914c6194b6", "Product");
		//dao.remove("556490abbd77524954cecd70", "Product");
		
		List<Map<String,Object>> ps = dao.find(null, "Product");
		
		System.out.println(ps);
	}
}
