package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.List;

import com.maliang.core.model.Business;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.WorkFlow;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class BusinessDao extends AbstractDao {
	protected static String COLLECTION_NAME = "Business";
	protected DBCollection dbColl = null;
	static {
		INNER_TYPE.put("Business.workFlows",WorkFlow.class);
	}
	
	public BusinessDao(){
		dbColl = this.getDBCollection();
	}
	
	private DBCollection getDBCollection(){
		return this.getDBCollection(COLLECTION_NAME);
	}
	
	public void save(Business om) {
		BasicDBObject doc = encode(om);
		this.dbColl.save(doc);
		
		if(om.getId() == null){
			om.setId(doc.getObjectId("_id"));
		}
	}
	
	public Business getByID(String oid){
		DBCursor cursor = this.dbColl.find(this.getObjectId(oid));
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), Business.class) ;
		}
		
		return null;
	}
	
	public Business getByName(String name){
		DBCursor cursor = this.dbColl.find(new BasicDBObject("name",name));
		while(cursor.hasNext()){
			return this.decode((BasicDBObject)cursor.next(), Business.class);
		}
		
		return null;
	}
	
	public List<Business> list(){
		DBCursor cursor = this.dbColl.find();
		
		return readCursor(cursor,Business.class);
	}
	
	public void remove(String oid){
		this.dbColl.remove(this.getObjectId(oid));
	}
	
	public List<DBObject> find(BasicDBObject query){
		DBCursor cursor = this.dbColl.find(query);
		return cursor.toArray();
	}
	
	public static void main(String[] args) {
		BusinessDao dao = new BusinessDao();
		
		List<WorkFlow> wfs = new ArrayList<WorkFlow>();
		
		WorkFlow wf = new WorkFlow();
		wf.setCode("addToParams({p2:db.Product.save(request.product),brands:db.Brand.search(),products:db.Product.search()})");
		wf.setRequestType("{fid:'int',bid:'int'}");
		wf.setResponse("{html_template:'<div id='edit_form'></div>',"
				+ "contents:[{type:'form',html_parent:'edit_form',action:'',name:'product.edit.form',"
				+ "children:{"
				+ "inputs:["
				+ "{name:'fid',type:'hidden',value:2},"
				+ "{name:'product.id',type:'hidden'},"
				+ "{name:'product.name',label:'名称',type:'text'},"
				+ "{name:'product.brand',label:'品牌',type:'select',options:each(brands){{key:this.id,label:this.name}}},"
				+ "{name:'product.price',label:'价格',type:'number'}],"
				+ "ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],"
				+ "data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},"
				+ "brand:{type:'a',href:'/detail.htm?id='+this.brand.id,text:this.brand.name},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},"
				+ "operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}"
				+ "}}]}");
		wf.setStep(1);
		wfs.add(wf);
		
		Business bs = new Business();
		bs.setName("editProduct");
		bs.setWorkFlows(wfs);
		
		System.out.println(bs.getWorkFlows());
		//dao.save(bs);
		
		//55657643bd77ce55499002c8
		//556576fdbd773acdc6a0c3d1
//		Business bs2 = dao.getByID(bs.getId().toString());
//		System.out.println(bs2);
//		System.out.println(bs2.getWorkFlows().get(0).getResponse());
		
		System.out.println(dao.getByID("dd"));
//		Business bbs = dao.list();
//		System.out.println(bbs.getId());
	}

}
 