package com.maliang.core.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.model.Block;
import com.maliang.core.model.Business;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectField;
import com.maliang.core.model.ObjectMetadata;
import com.maliang.core.model.Workflow;
import com.maliang.core.util.Utils;

@Controller
@RequestMapping(value = "generate")
public class GeneraterController extends BasicController {
	
	@RequestMapping(value = "test.htm")
	@ResponseBody
	public String tttttt(String oid,String bname) {
		System.out.println("oid="+oid+",bname="+bname);
		return "oid="+oid+",bname="+bname;
	}
	
	@RequestMapping(value = "all.htm")
	@ResponseBody
	public String generateAll(String oid,String bname) {
		ObjectMetadata om = this.metadataDao.getByID(oid);
		if(Utils.isEmpty(bname)){
			bname = om.getLabel()+"管理";
		}
		
		Map<String,Object> query = newMap("name",bname);
		if(om.getProject() != null){
			query.put("project", om.getProject().getId().toString());
		}
		Business business = this.businessDao.findOne(query);
		if(business == null){
			business = new Business();
			business.setProject(om.getProject());
			business.setName(bname);
			business.setUniqueCode(om.getName());
		}
		
		if(business.getWorkflows() == null){
			business.setWorkflows(new ArrayList<Workflow>());
		}
		if(business.getBlocks() == null){
			business.setBlocks(new ArrayList<Block>());
		}
		
		int maxStep = 0;
		for(Workflow wf : business.getWorkflows()){
			if(wf == null || wf.getStep() == null){
				continue;
			}
			
			int step = wf.getStep();
			if(step < 0)step = 0;
			
			maxStep = maxStep>=step?maxStep:step;
		}
		
		int editStep = ++maxStep;
		int saveStep = ++maxStep;
		int listStep = ++maxStep;
		int deleteStep = ++maxStep;
		int detailStep = ++maxStep;
		business.getWorkflows().add(this.gEdit(om,editStep,saveStep));
		business.getWorkflows().add(this.gSave(om,saveStep,listStep));
		business.getWorkflows().add(this.gList(om,editStep,listStep,deleteStep,detailStep));
		business.getWorkflows().add(this.gDelete(om, listStep, deleteStep));
		business.getWorkflows().add(this.gDetail(om, detailStep));
		
		business.getBlocks().add(this.gNavBlock(om, editStep, listStep));
		
		this.businessDao.save(business);
		
		return "ok";
	}
	
	private String toKeyName(String name){
		return name.substring(0, 1).toLowerCase()+name.substring(1);
	}
	
	/***
	 * Generate edit work flow for collection.
	 * **/
	private Workflow gEdit(ObjectMetadata om,int editStep,int saveStep){
		
		String omName = this.toKeyName(om.getName());
		StringBuffer codeSbf = new StringBuffer();
		codeSbf.append("addToParams({");
		codeSbf.append(omName).append(":").append("db.").append(om.getName()).append(".get(request.id)");
		
		StringBuffer sbf = new StringBuffer();
		sbf.append("[").append("'form'").append(",").append("'").append(omName).append("'").append(",");
		
		sbf.append("[");
		sbf.append("['$fid','','hidden',"+saveStep+"],");
		sbf.append("['$bid','','hidden',bid],");
		sbf.append("['id','','hidden',"+omName+".id]");
		
		this.gInputs(sbf, codeSbf, omName, om.getFields());
		
		sbf.append(",['$submit','','submit','保存','[n]']]]");
		codeSbf.append("})");
		
		String responce = "{title:'编辑"+om.getLabel()+"',json:[${SYS.NAV},${NAV},"+sbf.toString()+"]}";
		
		Workflow flow = new Workflow();
		flow.setStep(editStep);
		flow.setCode(codeSbf.toString());
		flow.setResponse(responce);
		
		return flow;
	}
	
	/***
	 * Generate edit work flow for collection.
	 * **/
	private Workflow gDetail(ObjectMetadata om,int detailStep){
		
		String omName = this.toKeyName(om.getName());
		StringBuffer codeSbf = new StringBuffer();
		codeSbf.append("addToParams({");
		codeSbf.append(omName).append(":").append("db.").append(om.getName()).append(".get(request.id)");
		codeSbf.append("})");
		
		String responce = "{title:'"+om.getLabel()+"详情',json:[${SYS.NAV},${NAV},"+gTableBlock(omName,om.getFields())+"]}";
		
		Workflow flow = new Workflow();
		flow.setStep(detailStep);
		flow.setCode(codeSbf.toString());
		flow.setResponse(responce);
		
		return flow;
	}
	
	private String gTableBlock(String parentName,List<ObjectField> fields){
		StringBuffer sbf = new StringBuffer();
		sbf.append("[").append("'tableBlock'").append(",").append("[");
		
		int index = 0;
		for(ObjectField f:fields){
			if(index++ > 0){
				sbf.append(",");
			}

			sbf.append("[");
			sbf.append("'").append(f.getLabel()).append("'").append(",");
			
			if(FieldType.INNER_COLLECTION.is(f.getType())){
				sbf.append(gTableBlock(parentName+"."+f.getName(),f.getFields()));
			}else if(FieldType.ARRAY.is(f.getType()) && FieldType.INNER_COLLECTION.is(f.getElementType())){
				sbf.append(gTableList(parentName+"."+f.getName(),f.getFields()));
			}else {
				sbf.append(parentName).append(".").append(f.getName());
				
				if(FieldType.DATE.is(f.getType())){
					sbf.append(".df('cn')");
				}else if(FieldType.LINK_COLLECTION.is(f.getType())){
					sbf.append(".name");
				}
			}
			sbf.append("]");
		}
		sbf.append("]]");
		
		return sbf.toString();
	}

	private String gTableList(String parentName,List<ObjectField> fields){
		if(Utils.isEmpty(fields)){
			return "";
		}
		
		StringBuffer tableSbf = new StringBuffer();
		StringBuffer headSbf = new StringBuffer();
		StringBuffer tbodySbf = new StringBuffer();

		headSbf.append("[");
		tbodySbf.append("each(").append(parentName).append("){[");
		int index = 0;
		for(ObjectField f:fields){
			if(index++ > 0){
				headSbf.append(",");
				tbodySbf.append(",");
			}
			
			headSbf.append("'").append(f.getLabel()).append("'");
			
			tbodySbf.append("this.").append(f.getName());
			if(FieldType.DATE.is(f.getType())){
				tbodySbf.append(".df('cn')");
			}else if(FieldType.LINK_COLLECTION.is(f.getType())){
				tbodySbf.append(".name");
			}
		}
		headSbf.append("]");
		tbodySbf.append("]}");
		
		tableSbf.append("[").append("'tableList'").append(",").append(headSbf).append(",").append(tbodySbf).append("]");
		
		return tableSbf.toString();
	}

	private void gInputs(StringBuffer sbf,StringBuffer codeSbf,String omName,List<ObjectField> fields){
		List<String> loadedLinked = new ArrayList<String>();
		for(ObjectField f : fields){
			String type = "'text'";
			if(FieldType.DATE.is(f.getType())){
				type = "'date'";
			}else if(FieldType.LINK_COLLECTION.is(f.getType())){
				String linkedName = this.toKeyName(f.getLinkedObject())+"s";
				type = "['select',each("+linkedName+"){{key:this.id,label:this.name}}]";
				
				if(!loadedLinked.contains(f.getLinkedObject())){
					codeSbf.append(",").append(linkedName).append(":").append("db.").append(f.getLinkedObject()).append(".search()");
					loadedLinked.add(f.getLinkedObject());
				}
			}else if(FieldType.INNER_COLLECTION.is(f.getType())){
				StringBuffer groupSbf = new StringBuffer();
				this.gInputs(groupSbf, codeSbf, omName+"."+f.getName(), f.getFields());
				
				type = "['group',["+groupSbf+"]]";
			}
			
			if(sbf.length() > 0){
				sbf.append(",");
			}
			
			sbf.append("[");
			sbf.append("'").append(f.getName()).append("'").append(",");
			sbf.append("'").append(f.getLabel()).append("'").append(",");
			sbf.append(type).append(",");
			sbf.append(omName).append(".").append(f.getName());
			if(FieldType.DATE.is(f.getType())){
				sbf.append(".df('-')");
			}
			sbf.append(",");
			sbf.append("'[n]'").append("]");
		}
	}
	
	private Workflow gSave(ObjectMetadata om,int saveStep,int listStep){
		String omName = this.toKeyName(om.getName());
		
		String code = "addToParams({c:db."+om.getName()+".save(request."+omName+")})";
		String responce = "business({bid:bid,fid:"+listStep+"})";
		
		Workflow flow = new Workflow();
		flow.setStep(saveStep);
		flow.setCode(code);
		flow.setResponse(responce);
		
		return flow;
	}
	
	private Workflow gList(ObjectMetadata om,int editStep,int listStep,int deleteStep,int detailStep){
		String omName = this.toKeyName(om.getName());
		
		String code = "addToParams({"+omName+"s:db."+om.getName()+".search()})";
		
		String head = "[";
		String tbody = "each("+omName+"s){[";
		for(ObjectField f : om.getFields()){
			head += "'"+f.getLabel()+"',";
			tbody += "this."+f.getName()+",";
		}
		head += "'操作']";
		tbody += "[['a','查看',{id:this.id,fid:"+detailStep+"}],['a','编辑',{id:this.id,fid:"+editStep+"}],['a','删除',{id:this.id,fid:"+deleteStep+"}]]]}";
		String tableList = "['tableList',"+head+","+tbody+"]";
		String responce = "{title:'"+om.getLabel()+"列表',data:{bid:bid},json:[${SYS.NAV},${NAV},"+tableList+"]}";
		
		Workflow flow = new Workflow();
		flow.setStep(listStep);
		flow.setCode(code);
		flow.setResponse(responce);
		
		return flow;
	}
	
	private Workflow gDelete(ObjectMetadata om,int listStep,int deleteStep){
		String code = "addToParams({c:db."+om.getName()+".delete(request.id)})";
		String responce = "business({bid:bid,fid:"+listStep+"})";
		
		Workflow flow = new Workflow();
		flow.setStep(deleteStep);
		flow.setCode(code);
		flow.setResponse(responce);
		
		return flow;
	}
	
	private Block gNavBlock(ObjectMetadata om,int editStep,int listStep){
		String code = "['menu',[bid,{新增:"+editStep+"},{列表:"+listStep+"}]]";
		
		Block block = new Block();
		block.setName("NAV");
		block.setCode(code);
		
		return block;
	}
	
	public static void main(String[] args) {
		//GeneraterController gc = new GeneraterController();
		//gc.generateAll("UserGrade");
		//String name = gc.toKeyName("UserGradeProduct");
		//System.out.println(name);
		
//		String s = "db.UserGrade.search()";
//		Object v = AE.execute(s);
//		System.out.println(v);
		
		StringBuffer t = new StringBuffer();
		StringBuffer h = new StringBuffer();
		StringBuffer b = new StringBuffer();
		
		
		h.append("[h1,h2,h3]");
		b.append("[b1,b2,b3]");
		
		t.append("[").append(h).append(",").append(b).append("]");
		System.out.println(t);
		
	}
	private void appendTo(StringBuffer sbf,String val,boolean isString){
		if(isString){
			sbf.append("'");
		}
		
		sbf.append(val);
		
		if(isString){
			sbf.append("'");
		}
	}
}
