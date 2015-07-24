package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maliang.core.arithmetic.ArithmeticExpression;
import com.maliang.core.dao.ObjectMetadataDao;
import com.maliang.core.model.FieldType;
import com.maliang.core.model.ObjectMetadata;

public class If {
	public static void main(String[] args) {
		String str = "if(2<3){product:'雪花秀',price:345.90}";
		
//		List<Map<String,Object>> products = DBData.list("Product");
//		Map<String,Object> params = new HashMap<String,Object>();
//		Map<String,Object> user = DBData.getRandom("User");
//		
//		params.put("products", products);
//		params.put("user", user);
		
		//str = "2<3";
		
		str = "each(metadata.fields){{type:if(this.type=8){this.type}}}";
		str = "{name:{prefix:'objectMetadata',name:'name',label:'名称',type:'text',value:metadata.name},"
				+ "id:{prefix:'objectMetadata',name:'id',label:null,type:'hidden',value:metadata.id},"
				+ "label:{prefix:'objectMetadata',name:'label',label:'标签',type:'text',value:metadata.label},"
				+ "fields:{item-labels:{name:'名称',label:'标签',type:'字段类型'},prefix:'objectMetadata',"
					+ "name:'fields',label:'字段',type:'list',item-prefix:'objectMetadata.fields',"
					+ "value:each(metadata.fields){{name:{prefix:'objectMetadata.fields',name:'name',type:'text',value:this.name},"
						+ "id:{prefix:'objectMetadata.fields',name:'id',label:null,type:'hidden',value:this.id},"
						+ "label:{prefix:objectMetadata.fields,name:'label',type:'text',value:this.label},"
						+ "type:[{prefix:'objectMetadata.fields',name:'type',type:'select',value:this.type,"
							+ "options:each(fieldTypes){{key:this.code,label:this.name}}},"
						+ "if(this.type = 8){{prefix:'objectMetadata.fields',name:'linkedObject',label:'关联对象',type:'select',"
							+ "value:this.linkedObject,options:each(allMetadatas){{key:this.name,label:this.name}}}}]"
					+ "}}}}";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fieldTypes",FieldType.values());
		
		ObjectMetadataDao metadataDao = new ObjectMetadataDao();
		ObjectMetadata metadata = metadataDao.getByID("556424cabd777e5a5087db0a");
		params.put("metadata",metadata);
		
		List<ObjectMetadata> metadataList = metadataDao.list();
		params.put("allMetadatas",metadataList);
		
		Object v = ArithmeticExpression.execute(str,params);
		System.out.println(v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object value = function.executeExpression(params);
		
		if(value instanceof Boolean && (Boolean)value){
			//return FunctionBody.readBody(function).execute(params);
			return ArithmeticExpression.execute(function.getBody(), params);
		}
		return null;
	}
}
