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
	
	private String indent(int num){
		if(num <= 0){
			return "";
		}
		
		String indent = "    ";
		String s = null;
		for(int i = 0;i < num; i++){
			if(s == null){
				s = indent;
			}else {
				s += indent;
			}
		}
		return s;
	}
	
	/***
	 * Generate edit work flow for collection.
	 * **/
	private Workflow gEdit(ObjectMetadata om,int editStep,int saveStep){
		
		String omName = this.toKeyName(om.getName());
		StringBuffer codeSbf = new StringBuffer();
		
		codeSbf.append("addToParams({\n");
		codeSbf.append(indent(1)).append(omName).append(":").append("db.").append(om.getName()).append(".get(request.id),\n");
		
		
		StringBuffer formSbf = new StringBuffer();
		formSbf.append(indent(1)).append("editForm:{\n").append(indent(2)).append("type:'formBody',\n").append(indent(2)).append("groups:[\n");
		formSbf.append(indent(3)).append("{type:'hidden',name:'bid',value:bid},\n");
		formSbf.append(indent(3)).append("{type:'hidden',name:'fid',value:"+saveStep+"},\n");
		formSbf.append(indent(3)).append("{type:'hidden',name:'"+omName+".id',value:"+omName+".id}");
		
		this.gInputs(formSbf, codeSbf, omName, om.getFields(),3);
		formSbf.append("\n").append(indent(1)).append("]},\n");
		
		codeSbf.append(formSbf.toString());
		
		codeSbf.append(indent(1)).append("${G.editHtml}\n").append("})");

		String responce = "{\n    data:{\n        bid: bid,fid:fid,pageSidebarMenu:${G.pageSidebarMenu},activeMenu:'userNav'},generator:'MGenerator',html:editHtml}";
		
		Workflow flow = new Workflow();
		flow.setStep(editStep);
		flow.setCode(codeSbf.toString());
		flow.setResponse(responce);
		
		return flow;
	}
	
	/***
	 * Generate edit work flow for collection.
	 * **/
	private Workflow gEdit_bak(ObjectMetadata om,int editStep,int saveStep){
		
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
		
		this.gInputs_bak(sbf, codeSbf, omName, om.getFields());
		
		sbf.append(",['$submit','','submit','提交','[n]']]]");
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
		
		String responce = "{title:'"+om.getLabel()+"璇︽儏',json:[${SYS.NAV},${NAV},"+gTableBlock(omName,om.getFields())+"]}";
		
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
	
	/***
	 * 
	 * addToParams({
  product:db.Product.get(request.id),
  posts:db.PostStrategy.search(),
  brands:db.Brand.search(),
  c:if(not(product.pictures.size()>0)){product.pictures.set([''])},
  
  editForm:{
        type:'formBody',
        groups:[
            {type:'hidden',name:'bid',value:bid},
            {type:'hidden',name:'fid',value:2},
            {type:'hidden',name:'product.id',value:product.id},
            {type:'text',name:'product.name',label:'名称',value:product.name,required:true,has-err:true},
            {type:'text',name:'product.price',label:'价格',required:true,icon:'money',value:product.price},
            {type:'select2',css:'input-medium',name:'product.brand',label:'品牌',
                    value:product.brand.id,options:each(brands){{key:this.id,label:this.name}}},
            {type:'select',css:'input-medium',name:'product.postage',label:'邮递策略',
                    value:product.postage,options:[{key:'',label:'默认'}]+each(posts){{key:this.id,label:this.name}}},
            {type:'text',name:'product.stock',label:'仓库库存',help:'实际库存',value:product.stock},
            {type:'text',name:'product.orderStock',label:'订单库存',help:'可以购买的库存',value:product.orderStock},
            {type:'summernote',name:'product.description',label:'详情描述',value:product.description}
        ]
  }
  
  ${editHtml}
})
	 * ***/
	private void gInputs(StringBuffer sbf,StringBuffer codeSbf,String omName,List<ObjectField> fields,int ind){
		List<String> loadedLinked = new ArrayList<String>();
		for(ObjectField f : fields){
			String type = "'text'";
			String options = null;
			if(FieldType.DATE.is(f.getType())){
				type = "'date'";
			}else if(FieldType.LINK_COLLECTION.is(f.getType())){
				String linkedName = this.toKeyName(f.getLinkedObject())+"s";
				type = "'select2'";
				options = "each("+linkedName+"){{key:this.id,label:this.name}}";
				
				if(!loadedLinked.contains(f.getLinkedObject())){
					codeSbf.append(indent(1)).append(linkedName).append(":").append("db.").append(f.getLinkedObject()).append(".search(),\n");
					loadedLinked.add(f.getLinkedObject());
				}
			}else if(FieldType.INNER_COLLECTION.is(f.getType())){
				StringBuffer groupSbf = new StringBuffer();
				this.gInputs(groupSbf, codeSbf, omName+"."+f.getName(), f.getFields(),ind+2);
				
				type = "['group',["+groupSbf+"]]";
			}
			
			if(sbf.length() > 0){
				sbf.append(",\n");
			}

			sbf.append(indent(ind)).append("{");
			
			//type
			sbf.append("type:").append(type);
			//name
			sbf.append(",").append("name:").append("'").append(omName).append(".").append(f.getName()).append("'");
			//label
			sbf.append(",").append("label:").append("'").append(f.getLabel()).append("'");
			//value
			sbf.append(",").append("value:").append(omName).append(".").append(f.getName());
			if(FieldType.DATE.is(f.getType())){
				sbf.append(".df('-')");
			}
			//options
			if(options != null){
				sbf.append(",").append("options:").append(options);
			}

			sbf.append("}");
		}
	}

	/***
	 * 
	 * addToParams({
  product:db.Product.get(request.id),
  posts:db.PostStrategy.search(),
  brands:db.Brand.search(),
  c:if(not(product.pictures.size()>0)){product.pictures.set([''])},
  
  editForm:{
        type:'formBody',
        groups:[
            {type:'hidden',name:'bid',value:bid},
            {type:'hidden',name:'fid',value:2},
            {type:'hidden',name:'product.id',value:product.id},
            {type:'text',name:'product.name',label:'名称',value:product.name,required:true,has-err:true},
            {type:'text',name:'product.price',label:'价格',required:true,icon:'money',value:product.price},
            {type:'select2',css:'input-medium',name:'product.brand',label:'品牌',
                    value:product.brand.id,options:each(brands){{key:this.id,label:this.name}}},
            {type:'select',css:'input-medium',name:'product.postage',label:'邮递策略',
                    value:product.postage,options:[{key:'',label:'默认'}]+each(posts){{key:this.id,label:this.name}}},
            {type:'text',name:'product.stock',label:'仓库库存',help:'实际库存',value:product.stock},
            {type:'text',name:'product.orderStock',label:'订单库存',help:'可以购买的库存',value:product.orderStock},
            {type:'summernote',name:'product.description',label:'详情描述',value:product.description}
        ]
  },
  
  
  
  ['$fid', '', 'hidden', 2],
            ['$bid', '', 'hidden', bid],
            ['id', '', 'hidden', task.id],
            ['title', '主题', 'text', task.title, '[n]'],
            ['type', '问题类型', ['select', each(taskTypes) {{key: this.id,label: this.name}}], task.type, '[n]'],
            ['priority', '优先级', ['select', each(taskPrioritys) {{key: this.id,label: this.name}}], task.priority, '[n]'],
            ['dateDue', '到期日', 'date', task.dateDue.df('-'), '[n]'],
            ['operator', '经办人', ['select', each(users) {{key: this.id,label: this.account}}], task.operator, '[n]'],
            ['reporter', '报告人', ['select', each(users) {{key: this.id,label: this.account}}], task.reporter, '[n]'],
            ['estimatedTime', '预估时间', 'text', task.estimatedTime.value, '[n]'],
            ['remainingTime', '剩余时间', 'text', task.remainingTime.value, '[n]'],
            ['environment', '环境', 'html', task.environment, '[n]'],
            ['description', '描述', 'html', task.description, '[n]'],
            ['remark', '备注', 'html', task.remark, '[n]'],
            ['t1', '测试1', '', task.t1, '[n]'],
            ['t2', '测试2', '', task.t2, '[n]'],
            ['$submit', '', 'submit', '保存', '[n]']
  
  ${editHtml}
})
	 * ***/
	private void gInputs_bak(StringBuffer sbf,StringBuffer codeSbf,String omName,List<ObjectField> fields){
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
				this.gInputs_bak(groupSbf, codeSbf, omName+"."+f.getName(), f.getFields());
				
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
	
	/**
	 * 
	 * 
	 * addToParams({
		  query:db.and([
		    db.like({name:request.ps.name}),
		    db.eq({stock:request.ps.stock.int()}),
		    db.eq({orderStock:request.ps.orderStock.int()}),
		    db.eq({brand:request.ps.brand}),
		    db.between({price:[request.ps.price.from.double(),request.ps.price.to.double()]})
		  ]),

		  page:db.Product.page({
		    page:request.page,
		    query:query,
		    sort:{price:1}
		  }),
		  
		  products:page.datas,
		  brands:db.Brand.search(),
		  defaultPost:db.PostStrategy.get({default:1}),
		  c:each(products){if(isNull(this.postage.id)){this.postage.set(defaultPost)}},
		  
		  listTable:{
	         type:'ScrollableTable',
	         table:{id:'productsTable'},
	         head:{
	             heading:[
	                 {width:'30%',text:'产品名称'},
	                 {width:'10%',text:'价格'},
	                 {width:'10%',text:'品牌'},
	                 {width:'10%',text:'实际库存'},
	                 {width:'10%',text:'订单库存'},
	                 {width:'40%',text:'操作'}
	             ],
	             filter:[
	                {type:'text',name:'ps.name',value:request.ps.name},
	                {type:'between',input:'text',layout:'v',name:'ps.price',value:request.ps.price},
	                {type:'select2',name:'ps.brand',value:request.ps.brand,
	                    options:[{key:'',label:'所有品牌'}]+each(brands){{key:this.id,label:this.name}}},
	                {type:'text',name:'ps.stock',value:request.ps.stock},
	                {type:'text',name:'ps.orderStock',value:request.ps.orderStock},
	                [
	                    {type:'div',css:'margin-bottom-5',
	                            body:{type:'button',name:'ok',css:'yellow',icon:'search',text:' Search '}},
	                    {type:'reset',name:'reset',css:'red',icon:'times',text:' Reset '}
	                ]
	            ]
        	},
	        body:each(products){[
	            this.name,
	            this.price+'元',
	            {type:'a',text:this.brand.name,href:'/flows/flow.htm?bid=58e85b37f2a3bc01a3757402&fid=5&id='+this.brand.id},
	            this.stock,
	            this.orderStock,
	            [
	                {type:'a',text:'查看',href:'/flows/flow.htm?bid='+bid+'&fid=5&id='+this.id},
	                {type:'a',text:'编辑',href:'/flows/flow.htm?bid='+bid+'&fid=1&id='+this.id},
	                {type:'a',text:'删除',href:'/flows/flow.htm?bid='+bid+'&fid=4&id='+this.id}
	            ]
	        ]}
    	},

    	${G.listHtml},

	    c:listHtml.body.content.update({
	        header:{
	            title:'产品列表',
	            small:'全部产品',
	        },
	        body:{
	            portlet:{
	                title:{
	                    caption:'产品列表',
	                }
	            }
	        }
	    })
	})
	 * 
	 * ***/
	private Workflow gList2(ObjectMetadata om,int editStep,int listStep,int deleteStep,int detailStep){
		String omName = this.toKeyName(om.getName());
		
		String code = "addToParams({"+omName+"s:db."+om.getName()+".search()})";
		
		String head = "[";
		String tbody = "each("+omName+"s){[";
		for(ObjectField f : om.getFields()){
			head += "'"+f.getLabel()+"',";
			tbody += "this."+f.getName()+",";
		}
		head += "'列表']";
		tbody += "[['a','查看',{id:this.id,fid:"+detailStep+"}],['a','编辑',{id:this.id,fid:"+editStep+"}],['a','删除',{id:this.id,fid:"+deleteStep+"}]]]}";
		String tableList = "['tableList',"+head+","+tbody+"]";
		String responce = "{title:'"+om.getLabel()+"列表',data:{bid:bid},json:[${SYS.NAV},${NAV},"+tableList+"]}";
		
		Workflow flow = new Workflow();
		flow.setStep(listStep);
		flow.setCode(code);
		flow.setResponse(responce);
		
		return flow;
	}
	
	/**
	 * addToParams({
  query:db.and([
    db.like({name:request.ps.name}),
    db.eq({stock:request.ps.stock.int()}),
    db.eq({orderStock:request.ps.orderStock.int()}),
    db.eq({brand:request.ps.brand}),
    db.between({price:[request.ps.price.from.double(),request.ps.price.to.double()]})
  ]),

  page:db.Product.page({
    page:request.page,
    query:query,
    sort:{price:1}
  }),
  products:page.datas,
  brands:db.Brand.search(),
  defaultPost:db.PostStrategy.get({default:1}),
  c:each(products){if(isNull(this.postage.id)){this.postage.set(defaultPost)}},




   listTable:{
        type:'ScrollableTable',
        table:{id:'productsTable'},
        head:{
            heading:[
                {width:'30%',text:'产品名称'},
                {width:'10%',text:'价格'},
                {width:'10%',text:'品牌'},
                {width:'10%',text:'实际库存'},
                {width:'10%',text:'订单库存'},
                {width:'40%',text:'操作'}
            ],
            filter:[
                {type:'text',name:'ps.name',value:request.ps.name},
                {type:'between',input:'text',layout:'v',name:'ps.price',value:request.ps.price},
                {type:'select2',name:'ps.brand',value:request.ps.brand,
                    options:[{key:'',label:'所有品牌'}]+each(brands){{key:this.id,label:this.name}}},
                {type:'text',name:'ps.stock',value:request.ps.stock},
                {type:'text',name:'ps.orderStock',value:request.ps.orderStock},
                [
                    {type:'div',css:'margin-bottom-5',
                            body:{type:'button',name:'ok',css:'yellow',icon:'search',text:' Search '}},
                    {type:'reset',name:'reset',css:'red',icon:'times',text:' Reset '}
                ]
            ]
        },
        body:each(products){[
            this.name,
            this.price+'元',
            {type:'a',text:this.brand.name,href:'/flows/flow.htm?bid=58e85b37f2a3bc01a3757402&fid=5&id='+this.brand.id},
            this.stock,
            this.orderStock,
            [
                {type:'a',text:'查看',href:'/flows/flow.htm?bid='+bid+'&fid=5&id='+this.id},
                {type:'a',text:'编辑',href:'/flows/flow.htm?bid='+bid+'&fid=1&id='+this.id},
                {type:'a',text:'删除',href:'/flows/flow.htm?bid='+bid+'&fid=4&id='+this.id}
            ]
        ]}
    },

    ${G.listHtml},

    c:listHtml.body.content.update({
        header:{
            title:'产品列表',
            small:'全部产品',
        },
        body:{
            portlet:{
                title:{
                    caption:'产品列表',
                }
            }
        }
    })
})
	 * 
	 * ***/
	private Workflow gList(ObjectMetadata om,int editStep,int listStep,int deleteStep,int detailStep){
		String omName = this.toKeyName(om.getName());
		
		String code = "addToParams({"+omName+"s:db."+om.getName()+".search()})";
		
		String head = "[";
		String tbody = "each("+omName+"s){[";
		for(ObjectField f : om.getFields()){
			head += "'"+f.getLabel()+"',";
			tbody += "this."+f.getName()+",";
		}
		head += "'列表']";
		tbody += "[['a','鏌ョ湅',{id:this.id,fid:"+detailStep+"}],['a','缂栬緫',{id:this.id,fid:"+editStep+"}],['a','鍒犻櫎',{id:this.id,fid:"+deleteStep+"}]]]}";
		String tableList = "['tableList',"+head+","+tbody+"]";
		String responce = "{title:'"+om.getLabel()+"鍒楄〃',data:{bid:bid},json:[${SYS.NAV},${NAV},"+tableList+"]}";
		
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
		String code = "['menu',[bid,{鏂板:"+editStep+"},{鍒楄〃:"+listStep+"}]]";
		
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
