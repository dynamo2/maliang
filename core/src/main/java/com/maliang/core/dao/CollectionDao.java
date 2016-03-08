package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class CollectionDao extends BasicDao {
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
		dataMap.put("id",dataMap.remove("_id").toString());
		
		ObjectMetadata metedata = this.metaDao.getByName(collName);
		if(metedata == null)return;
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
	
	public static void main(String[] args) {
		String str = "{type:{code:9,next:{code:9}}}";
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
