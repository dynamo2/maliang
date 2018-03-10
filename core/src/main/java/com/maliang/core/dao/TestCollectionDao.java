package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.arithmetic.calculator.DateCalculator;
import com.maliang.core.model.ModelType;
import com.maliang.core.model.ObjectMetadata;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class TestCollectionDao{
	public static ObjectMetadataDao metaDao = new ObjectMetadataDao();
	public static CollectionDao collDao = new CollectionDao();
	
	public static void main(String[] args) {
		
		String s = "addToParams({d:db.Department.get('5a9768157c1c243380e322c9'),"
//				+ "c:d.parent.set('5a97682e7c1c243380e322cb'),"
				+ "c:db.Department.save(d)"
				+ "})";
		
		Object o = AE.execute(s);
		System.out.println(o);
		
		
//		String id = "586361c6c47924723d7efbb3";
//		
//		Map<String,Object> user = dao.getByID(id, "User");
//		Object consignee = MapHelper.readValue(user,"consignees");
//		
//		System.out.println(consignee);
//		
//		
//		String s = "db.User.consignees.updateAll(['586361c6c47924723d7efbb3',{default:0}])";
//		dao.update(me("{id:'586361c6c47924723d7efbb3'}"), me("{consignees.2.default:1111}"), "User");
//
//		user = dao.getByID(id, "User");
//		consignee = MapHelper.readValue(user,"consignees");
//		
//		System.out.println(consignee);
		
//		String s = "db.User.aggregate([{$match:{consignees.default:111}},"
//				+ "{$unwind :{path:'$consignees'}},"
//				+ "{$match:{consignees.default:111}},"
//				+ "{$project:{consignees.consignee:1,consignees.default:1,consignees._id:1,cgnIndex:1,_id:0}}])";
//		Object v = AE.execute(s);
//		System.out.println(v);
		
		
		
		
//		s = "db.gt({items.product.price:'333.2'})";
//		s = "db.like({items.product.brand.name:'榛�'})";
//		s = "db.Cart.items.page({match:db.eq({user.name:'ww'}),query:db.gt({items.product.price:'333.2'})})";
//		s = "db.Cart.items.page({match:db.eq({user.name:'ww'})})";
//		//v = AE.execute(s);
//		
//		
//		System.out.println("=========== before ==============");
//		//System.out.println(v);
//		System.out.println("v.size : " + ((List)((Map)v).get("datas")).size());
//		
//		s = "db.Cart.items.search(db.in({items.product.price:'3199'}))";
//		s = "db.Cart.items.search(db.in({items.id:['5911368ce14fbac8c348fcc7','5911368ce14fbac8c348fcc3']}))";
//		//v = AE.execute(s);
//		
//		System.out.println("=========== result by search ==============");
//		System.out.println(v);
//		System.out.println("v.size : " + ((List)v).size());
		
		
		
//		v = dao.parseQueryData(v,"Cart");
//		System.out.println("");
//		System.out.println("=========== after ==============");
//		System.out.println(v);
//		
//		v = dao.findByMap((Map<String,Object>)v, "Cart");
//		System.out.println("");
//		System.out.println("=========== result list ==============");
//		System.out.println(v);
		
		//testPull();
	}
	
	private static void testArrayQuery(){
		
		CollectionDao dao = new CollectionDao();
		String items = "5911368ce14fbac8c348fcc7";
		// find,findOne
		String s = "db.Cart.items.find(db.in({items.id:['5911368ce14fbac8c348fcc7']}))";
		s = "db.in({items.id:['5911368ce14fbac8c348fcc7']})";
		s = "db.Brand.get('592645ca784fe94a84286d5d')";
		s = "db.Brand.search()";
		Object v = AE.execute(s);
		
		
		
		System.out.println(v);
		//System.out.println(((Map)v).get("createdDate").getClass());
	}
	
	private static void testPull(){
		ObjectId queryId = new ObjectId("5911368ce14fbac8c348fcc7");
		ObjectId itemsId = new ObjectId("591d55dac39fd842416810e4");
		
		CollectionDao dao = new CollectionDao();
		DBCollection db = dao.getDBCollection("Cart");
		
		BasicDBObject query = new BasicDBObject("items._id",queryId);
		DBObject result = db.findOne(query);
		
		System.out.println("result : " + result);
		
		BasicDBObject pull = new BasicDBObject("$pull",new BasicDBObject("items",new BasicDBObject("_id",itemsId)));
		db.update(query, pull);
		
//		dao.getDBCollection("").updateMulti(query,
//				new BasicDBObject("$pull", innerName+".$"));
	}
	
	/***
	 * db.Cart.items.page({
      page:request.page,
      match:{user:user.id},
      query:'',
      order:'',
  })
  
  aggregateOne
  
  piple:[],
  
  
  db.Cart.page({
  	match:{},
  	unwind:{}
  })
	 * **/
	private static void testPage(){
		Map<String,Object> params = new HashMap<String,Object>();
		
		String s = "db.User.get({name:'ww'})";
		Object v = AE.execute(s);
		params.put("user",v);
		
		s = "db.Product.search(db.like({name:'AQ'}))";
		v = AE.execute(s);
		params.put("products",v);
		
		//System.out.println("----- products : " + MapHelper.readValue(v, "id"));
//		
//		s = "db.Cart.items.page({})";
		
		
		String ss = "db.Cart.items.page({"
				+ "match:{user:user.id},"
				+ "query:db.gte({items.num:1}),"
				+ "page:{page:1,pageSize:2}"
				+ "})";
		
		//ss = "db.Cart.aggregate([{$unwind :'$items'}])";
		Object val = AE.execute(ss,params);
		
		//System.out.println("----- val : " + MapHelper.readValue(val, "size"));
		//System.out.println("----- val : " + ((List)val).size());
		System.out.println("----- val : " + val);
	}
	
	private static Map<String,Object> me(String s){
		return (Map<String,Object>)AE.execute(s);
	}
	
	private static List<Object> le(String s){
		return (List<Object>)AE.execute(s);
	}
	
	public static void testUpdateAll(){
		Object val = collDao.find(null, "PostStrategy");
		System.out.println("======== BEFORE =============");
		for(Map mv:(List<Map>)val){
			System.out.println(mv.get("name")+","+mv.get("default"));
		}

		AE.execute("db.PostStrategy.updateAll({default:0})");
		
		val = collDao.find(null, "PostStrategy");
		System.out.println("");
		System.out.println("======== AFTER =============");
		for(Map mv:(List<Map>)val){
			System.out.println(mv.get("name")+","+mv.get("default"));
		}
	}
	
	/********************** Test ****************************/
	public static void printList(List<Map<String, Object>> ps) {
		for (Map<String, Object> md : ps) {
			for (Map.Entry<String, Object> me : md.entrySet()) {
				if (me.getValue() instanceof Date) {
					me.setValue(new TMDate((Date) me.getValue()));
				}
			}
			md.remove("id");

			System.out.println(md);
		}
	}

	static String collName = "TBrand";
	static CollectionDao dao = new CollectionDao();

	public static void testUpdate() {
		Map m = new HashMap();
		m.put("name", "pola");
		m.put("id", "5582390ffc7770b40ef01787");
		m.put("modified_date", new Date());

		// dao.update(m, collName);
	}

	private static void testSet() {
		DBCollection db = dao.getDBCollection("TOrder");

		BasicDBObject query = new BasicDBObject();
		query.put("array1.name", "POLA BA澶忎箣鏅ㄥ厜鍖栧姘�120ML_array10");

		BasicDBObject vb = new BasicDBObject();
		vb.put("array1.$.name", "POLA BA璧嬮鏅ㄥ厜鎸夋懇鑶�120鍏�");
		vb.put("array1.$.description",
				"POLA B.A纰ц壘鎸夋懇鑶忕壒鍚缇庤劯閮ㄦ暣褰㈢骇鍒殑鏂般�屽棰溿�嶇簿鍗庢垚鍒嗭紝涓嶄粎璁╁眬閮ㄨ倢鑲ゆ洿鍔犵編涓斤紝鏇寸潃鍔涗簬鏁翠釜闈㈤儴鑲岃偆杞粨鐨勫閫狅紝浠呬娇鐢ㄤ竴娆★紝鍗冲埢鎺掑嚭鑰佸簾鐗╄川鍜屽浣欒剛鑲紝鏁翠釜鑴搁儴鏇村姞绱ц嚧锛岃疆寤撴洿鍔犳竻鏅般��");

		BasicDBObject dbo = new BasicDBObject("$set", vb);
		// BasicDBObject dbo = new BasicDBObject("$set",vb);
		// dbo.put("$currentDate", new BasicDBObject("lastModified",true));

		db.update(query, dbo);
	}

	private static void testUpdateOperator() {
		/**
		 * Outer { name:'', price:45.0, age:34, create_date:, modified_date:,
		 * description:, status:1,
		 * 
		 * inner1:{ name:'', price:45.0, age:34, create_date:, modified_date:,
		 * description:, status:1,
		 * 
		 * inner11:{ name:'', price:45.0, age:34, create_date:, modified_date:,
		 * description:, status:1,
		 * 
		 * inner111:{ name:'', price:45.0, age:34, create_date:, modified_date:,
		 * description:, status:1 } } } }
		 * 
		 * **/

		Map temp = new HashMap();
		temp.put("name", "POLA BA澶忎箣鏅ㄥ厜鍖栧姘�120ML");
		temp.put("price", 750.00);
		temp.put("age", 34);
		temp.put("description",
				"绾崇背娓楅�忔妧鏈紝鎰熷彈濡傞浘鑸殑杩呴�熷惛鏀讹紝杈惧埌娣卞眰婊嬫鼎銆傛姷寰¤倢鑲ゅ涔忥紝濉戦�犳按娑﹀脊鎬э紝娓呴�忕櫧鐨欙紝鍏呮弧鍏夋辰鐨勮倢鑲ゃ��");
		temp.put("status", 1);
		// temp.put("create_date", new Date());
		// temp.put("modified_date", new Date());

		Map outer = tempMap(temp, null);
		Map inner1 = tempMap(temp, "inner1");
		Map inner11 = tempMap(temp, "inner11");
		Map inner111 = tempMap(temp, "inner111");

		List array1 = new ArrayList();
		array1.add(tempMap(temp, "array10"));
		array1.add(tempMap(temp, "array11"));
		array1.add(tempMap(temp, "array12"));

		outer.put("inner1", inner1);
		outer.put("array1", array1);
		inner1.put("inner11", inner11);
		inner11.put("inner111", inner111);

		// dao.save(outer, "TOrder");
		testSet();

		System.out.println(stringMap(outer, null));

		List<Map<String, Object>> ps = dao.find(null, "TOrder");
		String mapStr = "";
		for (Map<String, Object> map : ps) {
			mapStr += "--------------\n";
			mapStr += stringMap(map, null);
		}
		System.out.println(mapStr);

		// outer = new HashMap();
		// inner1 = new HashMap();
		// inner11 = new HashMap();
		// inner111 = new HashMap();
		// outer.put("id", "55839567fc77dbb7e3ab9ed9");
		// inner111.put("name","POLA鏋佸厜骞诲僵绮惧崕50鍏�");
		//
		// outer.put("inner1",inner1);
		// inner1.put("inner11",inner11);
		// inner11.put("inner111",inner111);
		// dao.update(outer, "TOrder");
		//
		// ps = dao.find(null,"TOrder");
		// mapStr = "";
		// for(Map<String,Object> map : ps){
		// mapStr += "\n--------------\n";
		// mapStr += stringMap(map,null);
		// }
		// System.out.println(mapStr);
	}

	private static String stringMap(Map<String, Object> map, String prefix) {
		String singleIndentation = "    ";
		if (prefix == null)
			prefix = "";
		String subFix = singleIndentation + prefix;

		StringBuffer sbf = new StringBuffer();
		sbf.append("{\n");
		for (Map.Entry entry : map.entrySet()) {
			String vlStr = entry.getValue().toString();
			if (entry.getValue() instanceof Map) {
				vlStr = stringMap((Map) entry.getValue(), subFix);
			}
			if (entry.getValue() instanceof List) {
				vlStr = "[";
				int i = 0;
				for (Object m : (List<Map>) entry.getValue()) {
					if (m instanceof Map) {
						vlStr += stringMap((Map) m, subFix);
					} else {
						vlStr += ((i++ > 0) ? "," : "") + m.toString();
					}
				}
				vlStr += "]\n";
			}
			sbf.append(subFix).append(entry.getKey()).append(":").append(vlStr)
					.append("\n");
		}
		sbf.append(prefix).append("}\n");

		return sbf.toString();
	}

	private static Map tempMap(Map temp, String suffix) {
		Map map = new HashMap();
		map.putAll(temp);
		map.put("name", map.get("name") + (suffix == null ? "" : "_" + suffix));

		return map;
	}

	private static void mapQuery() {
		String query = "[{$project:{name:1,_id:0,totalPay:{$add:['$age','$price']}}},"
				+ "{$group:{_id:'$name',sumTotalPay:{$sum:'$totalPay'}}}]";
		List<Map<String, Object>> list = (List<Map<String, Object>>) ArithmeticExpression
				.execute(query, null);

		List<Map<String, Object>> results = dao.aggregateByMap(list, "TOrder");
		System.out.println(results);
	}

	public static void initTBrand() {
		String[] bs = { "闆姳绉�", "绉戜附濡�", "Oshadhi", "ACCA KAPPA", "ACCA KAPPA",
				"L'occitane", "Jason Natural", "La colline", "Sisley",
				"Albion", "Anius", "Avalon organics", "Biologique Recherche",
				"Caudalie" };

		int i = 0;
		String collName = "TBrand";
		CollectionDao dao = new CollectionDao();
		dao.removeAll(collName);
		for (String s : bs) {
			Map m = new HashMap();
			m.put("name", s);

			long time = System.currentTimeMillis() + (i++) * 1000;
			m.put("create_date", new Date(time));
			m.put("modified_date", new Date(time));

			dao.save(m, collName);
		}
	}
	
	public static void printTest(String id, String collName) {
		String str = "db." + collName + ".get('" + id + "')";
		Map<String, Object> val = (Map<String, Object>) ArithmeticExpression
				.execute(str, null);
		System.out.println("val : " + val);
		System.out.println("");
	}

	public static void testUpdateBySet() {
		// String str = "{account:{account:'zhanghui',password:'123456',"
		// + "personal_profile:{real_name:'寮犳儬',email:'zh@tm.com',age:100,"
		// +
		// "address:[{province:'姹熻嫃鐪�',city:'鍗椾含甯�',zone:'榧撴ゼ鍖�'},{province:'娴欐睙鐪�',city:'婀栧窞甯�',zone:'瀹夊悏鍘�'}]}}}";
		//

		System.out.println("============== before update =================");
		printTest("56e0e4fb8f778c15692b9eaf", "Test");

		// ObjectMetadataDao omDao = new ObjectMetadataDao();
		// CollectionDao collDao = new CollectionDao();
		// BasicDBObject query = new BasicDBObject("_id",new
		// ObjectId("56e0e4fb8f778c15692b9eaf"));
		//
		// String str =
		// "{F3:{F31:[{F311:{F3114:[{F31141:'F31141_1'}]},id:'56e0e4fb8f778c15692b9ead'}]}}";
		String str = "db.Test.save({id:'56e0e4fb8f778c15692b9eaf',F3:{F31:[{F312:'F312_2',F313:'F313_2'},{F311:{F3114:[{F31142:'F31142_1',id:'56e0fbe28f77546a3d590d58'}]}}]}})";
		str = "db.Test.save({id:'56e0e4fb8f778c15692b9eaf',F3:{F31:[{id:'56e0e4fb8f778c15692b9ead',F311:{F3113 : 'F3113_12_1',F3111 : 'F3111_12_1',F3112 : 'F3112_12_1'}}]}})";

		// str =
		// "db.Test.innerObjectById({F3:{F31:{id:'56e0e4fb8f778c15692b9ead'}}})";
		// str = "{id:'56e0e4fb8f778c15692b9eaf'}";
		Map<String, Object> params = (Map<String, Object>) ArithmeticExpression
				.execute(str, null);
		System.out.println("params : " + params);

		System.out.println("============== after update =================");
		printTest("56e0e4fb8f778c15692b9eaf", "Test");

		// CollectionDao collDao = new CollectionDao();
		// DBCollection db = collDao.getDBCollection("Test");
		// db.find();
		//
		// Object f311 = collDao.innerObjectById(params,"Test");
		// System.out.println("F311 : " + f311);

		// System.out.println("TEST : " + params);

		// ObjectMetadata meta = omDao.getByName("Test");
		// List<Map<String,BasicDBObject>> updates = new
		// ArrayList<Map<String,BasicDBObject>>();
		// Map<String,Object> daoMap =
		// buildUpdates(meta.getFields(),params,null,updates,query);
		//
		// if(daoMap != null && daoMap.size() > 0){
		// Map<String,BasicDBObject> bdbMap = new
		// HashMap<String,BasicDBObject>();
		// bdbMap.put("query", query);
		// bdbMap.put("update", new BasicDBObject("$set",daoMap));
		// updates.add(bdbMap);
		// }
		//
		// for(Map<String,BasicDBObject> um : updates){
		// System.out.println(um);
		//
		// //WriteResult daoResult =
		// dao.getDBCollection("Test").update(um.get("query"),
		// um.get("update"));
		// //System.out.println("daoResult : " + daoResult);
		// }

		// daoMap = new HashMap<String,Object>();
		// daoMap.put("F3.F31.$.F311.F3114.$1.F31143","F31143_1");
		//
		// WriteResult daoResult = dao.getDBCollection("Test").update(
		// new BasicDBObject("F3.F31.F311.F3114._id",new
		// ObjectId("56e0fbe28f77546a3d590d58")),
		// new BasicDBObject("$set",daoMap));
		// System.out.println("daoResult : " + daoResult);

		// System.out.println("");
		// System.out.println("============== after update =================");
		// printTest("56e0e4fb8f778c15692b9eaf");
		// System.out.println(updates);

		// DBCursor cursor = collDao.getDBCollection("Test").find(new
		// BasicDBObject("F3.F31.F311.F3114._id",new
		// ObjectId("56e0fbe28f77546a3d590d58")));
		//
		// while(cursor.hasNext()){
		// BasicDBObject doc = (BasicDBObject)cursor.next();
		//
		// System.out.println(doc);
		// }

		// System.out.println("daoMap : " + daoMap);

		// ObjectMetadata meta = omDao.getByName("Test");
		// List<String> updates = new ArrayList<String>();
		// Map<String,Object> daoMap =
		// encodeInner(meta.getFields(),params,null,updates);

		// System.out.println("daoMap : " + daoMap);
		// testEncode();

		// String str =
		// "{F3:{F31:[{F311:{F3114:[{F31141:'F31141_1'}]},id:'56e0e4fb8f778c15692b9ead'}]}}";
		str = "db.Test.save({id:'56e0e4fb8f778c15692b9eaf',F3:{F31:[{F312:'F312_2',F313:'F313_2'},{F311:{F3114:[{F31142:'F31142_1',id:'56e0fbe28f77546a3d590d58'}]}}]}})";
		str = "db.Test.save({id:'56e0e4fb8f778c15692b9eaf',F3:{F31:[{id:'56e0e4fb8f778c15692b9ead',F311:{F3113 : 'F3113_12_111aaa',F3111 : 'F3111_12_111aaa',F3112 : 'F3112_12_111aaa'}}]}})";

		// str =
		// "db.Test.innerObjectById({F3:{F31:{id:'56e0e4fb8f778c15692b9ead'}}})";
		// str = "{id:'56e0e4fb8f778c15692b9eaf'}";

		// str =
		// "each(['娴欐睙','姹熻嫃','瀹夊窘','姹熻タ','婀栧崡','婀栧寳','绂忓缓','骞夸笢','骞胯タ','灞辫タ','闄曡タ','涓婃捣','鍖椾含','澶╂触','閲嶅簡','鍥涘窛']){db.Region.save({province:{name:this}})}";
		str = "db.Region.get('56f0e61b8f772c9814bdedb7')";
		str = "db.Region.remove({province:null})";
		str = "db.Region.save({id:'56f0e61b8f772c9814bdedb7',province:{id:'56f0e61b8f772c9814bdedb6',cities:each(['缁嶅叴','鍙板窞']){{name:this}}}})";
		str = "db.Region.save({id:'56f0e61b8f772c9814bdedb7',province:{cities:{id:'56f0f0ef8f77e0edd2b5a12f',districts:['瑗挎箹','鎷卞','姹熷共','涓嬪煄','涓婂煄','婊ㄦ睙','钀у北','浣欐澀']}}})";
		// str = "db.Region.query({province.name:'娴欐睙'})";
		// districts

		// str =
		// "db.Region.aggregate([{$project:{province.cities:1}},{$match:{province.name:'娴欐睙'}}])";
		// Object val = ArithmeticExpression.execute(str,null);
		// System.out.println("params : " + val);

		str = "db.Region.aggregate([{$project:{province.name:1,city:'$province.cities.name',_id:0}},{$match:{province.name:'娴欐睙'}}])";
		// str =
		// "db.Region.aggregate([{$match:{province.name:'娴欐睙'}},{$group:{_id:'$province.cities.name'}}])";
		str = "db.Region.aggregateOne([{$match:{province.name:'娴欐睙'}},{ $unwind :'$province.cities'},"
				+ "{$group:{_id:{$cond:{if:{$eq:['$province.cities.name','缁嶅叴']},then:{ $ifNull:[ '$province.cities.districts',[]]},else:[]}}}},"
				+ "{$redact:{$cond:{if:{$gt:[{$size:'$_id'},0]},then:'$$DESCEND',else:'$$PRUNE'}}}])";

		// str =
		// "db.Region.save({id:'56f0e61b8f772c9814bdedb7',province:{cities:{id:'56f0f0ef8f77e0edd2b5a12f',districts:['瑗挎箹','鎷卞','姹熷共','涓嬪煄','涓婂煄','婊ㄦ睙','钀у北','浣欐澀']}}})";

		// printTest("56dd3903e45701ce0113bdda","Account");

		CollectionDao dao = new CollectionDao();

		BasicDBObject query = dao
				.executeDBObject("{id:'56dd3903e45701ce0113bdda',personal_profile:{address:{default:2}}}");
		BasicDBObject set = dao
				.executeDBObject("{$set:{personal_profile:{address:[{province:'姹熻嫃鐪�',city:'鍗椾含甯�',zone:'榧撴ゼ鍖�'} , {province:'娴欐睙鐪�',city:'婀栧窞甯�',zone:'瀹夊悏鍘�'}]}}}");
		set = dao
				.executeDBObject("{$set:{personal_profile:{address.$:{default:0}}}}");
		// WriteResult result = dao.getDBCollection("Account").update(query,
		// set);
		// System.out.println("result : " + result);

		str = "{$group:{_id:{$cond:{if:{$eq:['$province.cities.name','缁嶅叴']},then:{ $ifNull:[ '$province.cities.districts',[]]},else:[]}}}}";
		// +
		// "{$redact:{$cond:{if:{$gt:[{$size:'$_id'},0]},then:'$$DESCEND',else:'$$PRUNE'}}}";
		set = dao.executeDBObject(str);
		// System.out.println("set : " + set);
		// set = dao.executeDBObject("{personal_profile.address.$.default:3}");

		query = dao
				.executeDBObject("{id:'56dd3903e45701ce0113bdda',personal_profile:{address:{province:'娴欐睙鐪�'}}}");
		// query =
		// dao.executeDBObject("{id:'56dd3903e45701ce0113bdda',personal_profile.address.city:'婀栧窞甯�'}");
		set = dao
				.executeDBObject("{$set:{personal_profile:{address:{province:'娴欐睙鐪�'}}}}");
		// set =
		// dao.executeDBObject("{$set:{personal_profile.address.$.province:'娴欐睙鐪�'}}");
		// dao.dbSet(query, set, "Account",true);

		// System.out.println("query : " + set);
		// set = dao.executeDBObject("{personal_profile.address.$:1,_id:0}");
		// DBCursor c = dao.getDBCollection("Account").find(query, set);
		// while(c.hasNext()){
		// System.out.println(c.next());
		// }

		str = "db.Account.set([{id:'56dd3903e45701ce0113bdda',personal_profile:{address:{default:1}}},{personal_profile.address.$.default:8}])";

		str = "db.Account.get('56dfa951ba59a3035d169d79')";
		str = "db.Cart.get('570da7958f77400ffbc705e2')";
		str = "db.Cart.search()";

		str = "db.Product.removeAll()";
		Object val = ArithmeticExpression.execute(str, null);
		System.out.println("val : " + val);

		// printTest("56dd3903e45701ce0113bdda","Account");
	}
	
	

	static class TMDate {
		private Date date;

		public TMDate(Date date) {
			this.date = date;
		}

		public String toString() {
			return DateCalculator.dateFormat.format(this.date);
		}
	}
}
