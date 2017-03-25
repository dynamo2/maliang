package com.maliang.core.arithmetic.function;

import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;

public class AssignFunction {
	public static Object set(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		if(key.contains(".")){
			Object newVal = function.executeExpression(params);
			
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-2; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-2],newVal);
			return newVal;
		}
		
		return null;
	}
	
	/***
	 * 待实现
	 * 
	 * 
	 * HTML_listPage:{
        type:'HT',
        template:'${theme.metronic}',
        content:{
            type:'page',
            header:{
                title:'产品列表',
                small:'全部产品',
                breadcrumb:[
                    {text:'Home',href:'home.html',icon:'home'},
                    {text:'Product',href:'product.html',icon:'angle-right'}
                ],
                toolbar:{
                    type:'dropdownMenu',
                    text:'更多操作',
                    menus:[
                        {text:'新增产品',href:'edit.html'},
                        {text:'上月报表',href:'edit.html'},
                        'divider',
                        {text:'上季度报表',href:'edit.html'}
                    ]
                }
            },
            body:{
                type:'portlet',
                title:{
                    caption:'产品列表',
                    icon:'shopping-cart',
                    actions:[
                        {text:'新增产品',href:'add.html',icon:'plus'},
                        {
                            type:'dropdownMenu',
                            text:'Tools',
                            menus:[
                                {text:'导出Excel',href:'excel.html'},
                                {text:'导出CVS',href:'cvs.html'},
                                {text:'导出SQL',href:'sql.html'},
                            ]
                        }
                    ],
                },
                body:{
                    type:'HT',
                    template:'<div class="table-container">
                        <div id="datatable_orders_wrapper" class="dataTables_wrapper dataTables_extended_wrapper no-footer">
                            <ht::paginate> 
                            <ht::datastable> 
                            <ht::paginate> 
                        </div></div>',
                    paginate:{type:'paginate'},
                    datastable:productsTable
                }
            }
        }
    }
    
	 * productsTable:{
        type:'ScrollableTable',
        head:{
            heading:[
                {width:'30%',text:'产品名称'},
                {width:'10%',text:'价格'},
                {width:'60%',text:'操作'}
            ]
        },
        body:each(products){[
            this.name,
            this.price,
            '查看/编辑/删除'
        ]}
    },
    
    html:HTML_listPage.content.update({
        header:{
            title:'产品列表',small:'全部产品',
            breadcrumb:[{text:'Home',href:'home.html',icon:'home'}]
        },
        body:{
            title:{
                caption:'产品列表',icon:'shopping-cart'
            },
            body {
                paginate:{type:'paginate'},
                datastable:productsTable
            }
        }
    })
	 * **/
	public static Object update(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		if(key.contains(".")){
			Object newVal = function.executeExpression(params);
			
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-2; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-2],newVal);
			return newVal;
		}
		
		return null;
	}
	
	public static Object append(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		if(key.contains(".")){
			Object newVal = function.executeExpression(params);
			
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-2; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-2],newVal);
			return newVal;
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		String s = "{aaa:'aaa',c:b.eee.fff.cc.ccc.ggg.bb.bbb.set({cc:{ccc:{ggg:[999,33,22,77]}}}),d:b.eee.fff.cc.ccc.ggg.bb.bbb.cc.ccc.ggg.size}";
		Object v = AE.execute(s);
		
		System.out.println(v);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(operatedObj == null || !(operatedObj instanceof Comparable)){
			return false;
		}
		return null;
	}
}
