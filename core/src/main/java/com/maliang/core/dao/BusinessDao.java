package com.maliang.core.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.maliang.core.model.Block;
import com.maliang.core.model.Business;
import com.maliang.core.model.Workflow;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class BusinessDao extends AbstractDao {
	protected static String COLLECTION_NAME = "Business";
	protected DBCollection dbColl = null;
	protected CollectionDao collectionDao = new CollectionDao();
	
	static {
		INNER_TYPE.put("Business.workflows",Workflow.class);
		INNER_TYPE.put("Business.blocks",Block.class);
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
	
	public Workflow getWorkFlowById(String oid){
		List<DBObject> pipe = new ArrayList<DBObject>();
		
		pipe.add(new BasicDBObject("$match",new BasicDBObject("workflows._id",new ObjectId(oid))));
		pipe.add(new BasicDBObject("$unwind","$workflows"));
		pipe.add(new BasicDBObject("$project",new BasicDBObject().append("workflows",1).append("_id",0)));
		
		AggregationOutput aout = dbColl.aggregate(pipe);
		Iterator<DBObject> ie = aout.results().iterator();
		while(ie.hasNext()){
			return decode((BasicDBObject)ie.next().get("workflows"), Workflow.class);
		}
		
		return null;
	}
	
	public Block getBlock(String canonicalName){
		String[] names = canonicalName.split("\\.");
		
		List<DBObject> pipe = new ArrayList<DBObject>();
		
		pipe.add(new BasicDBObject("$match",new BasicDBObject("uniqueCode",names[0])));
		pipe.add(new BasicDBObject("$unwind","$blocks"));
		pipe.add(new BasicDBObject("$match",new BasicDBObject("blocks.name",names[1])));
		pipe.add(new BasicDBObject("$project",new BasicDBObject().append("blocks",1).append("_id",0)));
		
		AggregationOutput aout = dbColl.aggregate(pipe);
		Iterator<DBObject> ie = aout.results().iterator();
		while(ie.hasNext()){
			return decode((BasicDBObject)ie.next().get("blocks"), Block.class);
		}
		
		return null;
	}
	
	public Map<String,Object> updateBySet(Map<String,Object> values){
		values = this.collectionDao.correctData(values,COLLECTION_NAME,false,false);
		return this.collectionDao.updateBySet(values, COLLECTION_NAME);
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
		
//		List<DBObject> pipe = new ArrayList<DBObject>();
//		//pipe.add(new BasicDBObject("$match",new BasicDBObject("work_flow._id",new ObjectId("57970a3e7b591da2d1fa854e"))));
//		pipe.add(new BasicDBObject("$match",new BasicDBObject("work_flows._id",new ObjectId("57970a3e7b591da2d1fa854e"))));
//		pipe.add(new BasicDBObject("$unwind","$work_flows"));
//		pipe.add(new BasicDBObject("$project",new BasicDBObject().append("work_flows",1).append("_id",0)));
//		
//		AggregationOutput aout = dao.dbColl.aggregate(pipe);
//		Iterator<DBObject> ie = aout.results().iterator();
//		while(ie.hasNext()){
//			System.out.println("================");
//			//System.out.println("ie : " + ie.next().get("work_flows"));
//			
//			WorkFlow wf = dao.decode((BasicDBObject)ie.next().get("work_flows"), WorkFlow.class);
//			
//			System.out.println(wf.getId());
//			System.out.println(wf);
//			
//			//System.out.println(ie.next().get("work_flows"));
//		}
		
		//System.out.println(dao.dbColl.find(dao.getObjectId("56f9fac750776d16e7891bb5")).toArray());
		//dao.dbColl.update(new BasicDBObject(), new BasicDBObject("$rename",new BasicDBObject("work_flows","workFlows")));
		//System.out.println(dao.dbColl.find(dao.getObjectId("56f9fac750776d16e7891bb5")).toArray());
		
		//System.out.println(dao.find(null));
		//System.out.println(dao.dbColl.find(dao.getObjectId("56d64e7ffe559fe3d66284da")).toArray());
		
		//dao.dbColl.update(dao.getObjectId("56f230758f77e1cb5890dd59"), new BasicDBObject("$rename",new BasicDBObject("work_flows","workFlows")));
		//dao.dbColl.update(new BasicDBObject(), new BasicDBObject("$rename",new BasicDBObject("workFlows","workflows")),false,true);
		
//		dao.dbColl.update(new BasicDBObject("workflows._id",new ObjectId("57970a3e7b591da2d1fa854e")), new BasicDBObject("$set",new BasicDBObject("workflows.$.step",1)));
//		System.out.println(dao.getByID("56f9fac750776d16e7891bb5"));
//		System.out.println(dao.dbColl.find(dao.getObjectId("56f9fac750776d16e7891bb5")).toArray());
		
		System.out.println(dao.getBlock("SYS.NAV"));
		
		
		/*
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
		
		Business bus = dao.getByID("56d64e7ffe559fe3d66284da");
		for(WorkFlow flow : bus.getWorkFlows()){
			System.out.println("====================");
			//System.out.println(flow.getCode());
			if(flow == null)continue;
			System.out.println(flow.getCode());
		}
		//System.out.println(dao.getByID("56d64e7ffe559fe3d66284da"));
		
		
		
		
//		Business bbs = dao.list();
//		System.out.println(bbs.getId());
 * 
 */
	}

}
 