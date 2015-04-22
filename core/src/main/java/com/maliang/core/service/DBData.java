package com.maliang.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bson.types.ObjectId;

public class DBData {
	public final static Map<String,Object> ObjectMetadataMap = new HashMap<String,Object>();
	public final static Map<String,Object> DBDataMap = new HashMap<String,Object>();
	
	
	static {
		initMetadata();
		initBrands();
		initProductType();
		initProduct();
		
		initUserGrade();
		initUser();
		initUserAddress();
		//System.out.println(DBDataMap.get("Product"));
	}
	
	public static void main(String[] args) {
		Random r = new Random();
		
		for(int i = 0; i < 10;i++){
			System.out.println(r.nextInt(10));
		}
	}
	
	
	/**
	 * 
	 * Product:[{name:滋阴乳液125ml,product_type:面霜/乳液,brand:雪花秀,price:368.00,picture:pic,description:aa},"
				+ "{name:弹力面霜75ml,product_type:面霜/乳液,brand:雪花秀,price:520.00,picture:pic,description:aa},"
				+ "{name:天气丹华泫平衡化妆水150ml,product_type:保湿水,brand:Whoo,price:478.00,picture:pic,description:aa}]
	 * **/
	private static void initProduct(){
		String ps = "Product:[{name:滋阴乳液125ml,product_type:面霜/乳液,brand:雪花秀,price:368.00,picture:pic,description:aa},"
				+ "{name:弹力面霜75ml,product_type:面霜/乳液,brand:雪花秀,price:520.00,picture:pic,description:aa},"
				+ "{name:天气丹华泫平衡化妆水150ml,product_type:保湿水,brand:Whoo,price:478.00,picture:pic,description:aa}]";
		DBDataMap.putAll(MapHelper.curlyToMap(ps));
		
		rightFieldValue("Product");
	}
	
	public static List<Map<String,Object>> list(String coll){
		return (List<Map<String,Object>>)DBData.DBDataMap.get(coll);
	}
	
	public static Map<String,Object> getRandom(String collSymbol){
		List<Map<String,Object>> products = (List<Map<String,Object>>)DBDataMap.get(collSymbol);
		if(products == null || products.size() == 0){
			return null;
		}

		return products.get(new Random().nextInt(products.size()));
	}
	
	private static void innerObject(Map<String,Object> obj,String fieldName,String collName){
		innerObject(obj,fieldName,collName,"name");
	}
	
	private static void innerObject(Map<String,Object> obj,String fieldName,String collName,String columnName){
		Object value = obj.get(fieldName);
		value = get(collName,columnName,value);
		obj.put(fieldName, value);
	}
	
	public static Map<String,Object> get(String collection,String key,Object value){
		List<Map<String,Object>> list = (List<Map<String,Object>>)DBDataMap.get(collection);
		for(Map<String,Object> obj:list){
			if(value.equals(obj.get(key))){
				return obj;
			}
		}
		
		return null;
	}
	
	private static void initBrands(){
		String[] bnames = new String[]{"海蓝之谜","sisley","雪花秀","黛珂","Whoo","pola","科莱丽"};
		List<Map<String,Object>> brands = new ArrayList<Map<String,Object>>();
		DBDataMap.put("Brand", brands);
		
		for(int i = 0; i < bnames.length; i++){
			ObjectId id = new ObjectId();
			Map<String,Object> brand = new HashMap<String,Object>();
			brand.put("id", id);
			brand.put("name", bnames[i]);
			
			brands.add(brand);
		}
	}
	
	private static void initProductType(){
		String[] tnames = new String[]{"洁面","卸妆","爽肤水","保湿水","精华","面霜/乳液","眼霜","面膜","去角质产品"};
		List<Map<String,Object>> types = new ArrayList<Map<String,Object>>();
		DBDataMap.put("ProductType", types);
		
		for(int i = 0; i < tnames.length; i++){
			ObjectId id = new ObjectId();
			Map<String,Object> type = new HashMap<String,Object>();
			type.put("id", id);
			type.put("name", tnames[i]);
			
			types.add(type);
		}
	}
	
	private static void initUserGrade(){
		String us = "UserGrade:[{name:普通会员,discount:90.00},"
				+ "{name:黄金会员,discount:85.00},"
				+ "{name:白金会员,discount:80.00},"
				+ "{name:钻石会员,discount:75.00}]";
		
		DBDataMap.putAll(MapHelper.curlyToMap(us));
		
		rightFieldValue("UserGrade");
	}
	
	private static void rightFieldValue(String coll){
		List<Map<String,Object>> dataList = (List<Map<String,Object>>)DBDataMap.get(coll);
		List<Map<String,Object>> fields = (List<Map<String,Object>>)MapHelper.readValue(ObjectMetadataMap, coll+".fields");
		for(Map<String,Object> data:dataList){
			data.put("id", new ObjectId());
			
			for(Map<String,Object> field : fields){
				Object type = field.get("type");
				String fieldName = (String)field.get("name");
				Object value = data.get(fieldName);
				if("double".equals(type)){
					try {
						data.put(fieldName, new Double(value.toString()));
					}catch(Exception e){}
				}else if("int".equals(type)){
					try {
						data.put(fieldName, new Integer(value.toString()));
					}catch(Exception e){}
				}else if("object".equals(type)){
					String related = (String)field.get("related");
					
					innerObject(data,fieldName,related);
				}
			}
		}
	}
	
	public static Map<String,Object> getMetadata(String coll){
		return (Map<String,Object>)ObjectMetadataMap.get(coll);
	}
	
	private static void initUser(){
		String us = "User:[{user_name:wangmx,password:111111,real_name:王美霞,mobile:13456789776,email:wmx@tm.com,user_grade:黄金会员},"
				+ "{user_name:wangfan,password:111111,real_name:王凡,mobile:13298790987,email:wf@tm.com,user_grade:普通会员},"
				+ "{user_name:wangzq,password:111111,real_name:王梓青,mobile:13876598708,email:wzq@tm.com,user_grade:钻石会员},"
				+ "{user_name:wangyn,password:111111,real_name:王易楠,mobile:13609873451,email:wyn@tm.com,user_grade:白金会员},"
				+ "{user_name:wangml,password:111111,real_name:王美良,mobile:13876789034,email:wml@tm.com,user_grade:普通会员},"
				+ "{user_name:zhanghui,password:111111,real_name:张惠,mobile:13543456765,email:zh@tm.com,user_grade:黄金会员},"
				+ "{user_name:zhaoms,password:111111,real_name:赵默笙,mobile:13456789776,email:zms@tm.com,user_grade:白金会员}]";
		
		DBDataMap.putAll(MapHelper.curlyToMap(us));
		rightFieldValue("User");
	}
	
	private static void initUserAddress(){
		String us = "UserAddress:[{address:南京市鼓楼区龙江小区蓝天园26号402室内,consignee:王美霞,mobile:13456789776,default:1,user:wangmx},"
				+ "{address:东阳市商城5号楼12号毛线摊,consignee:王振华,mobile:18957951177,default:0,user:wangmx},"
				+ "{address:上海市浦东区张杨路1348号305室,consignee:王凡,mobile:13761136601,default:0,user:wangmx}]";
		
		Map<String,Object> data = MapHelper.curlyToMap(us);
		DBDataMap.putAll(data);
		
		List<Map<String,Object>> addresses = (List<Map<String,Object>>)data.get("UserAddress");
		for(Map<String,Object> address:addresses){
			address.put("id", new ObjectId());
			
			innerObject(address,"user","User","user_name");
		}
	}
	
	/**
	 * Product {
	 *   _id:objectId,
	 * 	 name:'Product',
	 * 	 fields:[{name:name,type:string,label:名称,unique:{scope:brand}},
	 *       {name:product_type,type:object,related:ProductType,label:分类,edit:{type:select}},
	 *       {name:brand,type:object,related:Brand,label:品牌},
	 *       {name:price,type:double,label:价格},
	 *       {name:production_date,type:date,label:生产日期},
	 *       {name:expiry_date,type:date,label:有效期},
	 *       {name:picture,type:string,label:图片,edit:{type:file}},
	 *       {name:description,type:string,label:描述,edit:{type:html}}
	 * 	 ]
	 * }
	 * 
	 * 
	 * User {
	 *   _id:objectId,
	 *   name:User,
	 *   fields:[{name:user_name,type:string,label:用户名},
	 *   {name:password,type:string,label:密码},
	 *   {name:real_name,type:string,label:真实姓名},
	 *   {name:birthday,type:date,label:真实姓名},
	 *   {name:mobile,type:string,label:手机号码},
	 *   {name:email,type:string,label:email},
	 *   {name:user_grade,type:object,related:UserGrade,label:会员等级}]
	 * }
	 * 
	 * UserGrade {
	 *   _id:objectId,
	 *   name:'UserGrade',
	 *   fields:[{name:'name',type:'string',label:'等级 名称'},
	 *   {name:'discount',type:'double',label:'商品折扣'}]
	 * }
	 * 
	 * UserAddress {
	 * 	 _id:objectId,
	 *   name:UserAddress,
	 *   fields:[{name:address,type:string,label:详细地址},
	 *   {name:consignee,type:string,label:收货人},
	 *   {name:mobile,type:string,label:手机号码},
	 *   {name:default,type:int,label:是否默认},
	 *   {name:user,type:object,related:User,label:所属会员}]
	 * }
	 * 
	 * 
	 * Order {sn,totalPrice,user,status,pay_status,totalNum,
	 * 		address:{address,consignee,mobile}
	 * 		items:{product,num,price,status}}
	 * 
	 * 
	 * Order {
	 * 	 _id:objectId,
	 *   name:Order,
	 *   fields:[{name:sn,type:string,label:订单编号},
	 *   {name:total_price,type:double,label:总价},
	 *   {name:user,type:object,related:User,label:会员},
	 *   {name:status,type:int,label:状态},
	 *   {name:pay_statys,type:int,label:支付状态},
	 *   {name:total_num,type:int,label:总数量},
	 *   {name:address,type:object,related:inner,label:收货地址},
	 *   {name:items,type:list,related:inner,label:订单明细}],
	 *   address:{
	 *     	fields:[{name:address,type:string,label:详细地址},
	 *     	{name:consignee,type:string,label:收货人},
	 *     	{name:mobile,type:string,label:手机号码}]},
	 *   items:{
	 *   	fields:[{name:product,type:object,related:Product,label:商品},
	 *   	{name:num,type:int,label:订购数量},
	 *   	{name:price,type:double,label:订购价格},
	 *   	{name:status,type:int,label:状态}]}
	 * }
	 * **/
	private static void initMetadata(){
		String product = "{_id:objectId,name:Product,"
				+ "fields:[{name:name,type:string,label:名称,unique:{scope:brand}},"
				+ "{name:product_type,type:object,related:ProductType,label:分类,edit:{type:select}},"
				+ "{name:brand,type:object,related:Brand,label:品牌,edit:{type:select}},"
				+ "{name:price,type:double,label:价格},"
				+ "{name:production_date,type:date,label:生产日期},"
				+ "{name:expiry_date,type:date,label:有效期},"
				+ "{name:picture,type:string,label:图片,edit:{type:file}},"
				+ "{name:description,type:string,label:描述,edit:{type:html}}]}";
		ObjectMetadataMap.put("Product", MapHelper.curlyToMap(product));
		
		String brand = "{_id:objectId,name:Brand,fields:[{name:name,type:string,label:名称}]}";
		ObjectMetadataMap.put("Brand", MapHelper.curlyToMap(brand));
		
		String productType =" {_id:objectId,name:ProductType,fields:[{name:name,type:string,label:名称}]}";
		ObjectMetadataMap.put("ProductType", MapHelper.curlyToMap(productType));
		
		String user = "{_id:objectId,name:User,"
				+ "fields:[{name:user_name,type:string,label:用户名},"
					+ "{name:password,type:string,label:密码},"
					+ "{name:real_name,type:string,label:真实姓名},"
					+ "{name:birthday,type:date,label:生日},"
					+ "{name:mobile,type:string,label:手机号码},"
					+ "{name:email,type:string,label:email},"
					+ "{name:user_grade,type:object,related:UserGrade,label:会员等级}]}";
		ObjectMetadataMap.put("User", MapHelper.curlyToMap(user));
		
		String userGrade = "{_id:objectId,name:UserGrade,"
				+ "fields:[{name:name,type:string,label:等级名称},"
					+ "{name:discount,type:double,label:商品折扣}]}";
		ObjectMetadataMap.put("UserGrade", MapHelper.curlyToMap(userGrade));
		
		String order = "{_id:objectId,name:Order,"
				+ "fields:[{name:sn,type:string,label:订单编号},"
					+ "{name:total_price,type:double,label:总价},"
					+ "{name:user,type:object,related:User,label:会员},"
					+ "{name:status,type:int,label:状态},"
					+ "{name:pay_statys,type:int,label:支付状态},"
					+ "{name:total_num,type:int,label:总数量},"
					+ "{name:address,type:object,related:inner,label:收货地址},"
					+ "{name:items,type:list,related:inner,label:订单明细}],"
				+ "address:{"
					+ "fields:[{name:address,type:string,label:详细地址},"
						+ "{name:consignee,type:string,label:收货人},"
						+ "{name:mobile,type:string,label:手机号码}]},"
				+ "items:{"
					+ "fields:[{name:product,type:object,related:Product,label:商品},"
						+ "{name:num,type:int,label:订购数量},"
						+ "{name:price,type:double,label:订购价格},"
						+ "{name:status,type:int,label:状态}]}}";
		ObjectMetadataMap.put("Order", MapHelper.curlyToMap(order));
	}
}
