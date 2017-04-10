package com.maliang.core.arithmetic.function;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.maliang.core.arithmetic.AE;
import com.maliang.core.service.MapHelper;
import com.maliang.core.util.Utils;

public class AssignFunction {
	public static Object set(Function function,Map<String,Object> params){
		String key = function.getKeySource();
		key = key.substring(0,key.length()-".set".length());
		
		if(!Utils.isEmpty(key)){
			Object newVal = function.executeExpression(params);
			return set(key,newVal,params);
		}
		
		
		return null;
	}
	
	private static Object set(String key,Object newVal,Map<String,Object> params){
		if(!Utils.isEmpty(key)){
			String[] names = key.split("\\.");
			Map<String,Object> parent = params;
			for(int i = 0; i < names.length-1; i++){
				Object v = MapHelper.readValue(parent,names[i]);
				if(v == null || !(v instanceof Map)){
					v = new LinkedMap();
					
					parent.put(names[i],v);
				}
				
				parent = (Map<String,Object>)v;
			}
			
			parent.put(names[names.length-1],newVal);
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
		System.out.println("------- update ");
		
		String key = function.getKeySource();
		key = key.substring(0,key.length()-".update".length());
		if(Utils.isEmpty(key))return null;
		
		Object oldVal = MapHelper.readValue(params,key);
		Object newVal = function.executeExpression(params);
		if(oldVal == null || !(oldVal instanceof Map) 
				|| !(newVal instanceof Map)){
			return set(key,newVal,params);
		}

		return merge((Map<String,Object>)oldVal,(Map<String,Object>) newVal);
	}
	
	private static Map<String,Object> merge(Map<String,Object> left,Map<String,Object> right){
		if(!Utils.isEmpty(right)){
			for(String k:right.keySet()){
				Object lv = left.get(k);
				Object rv = right.get(k);
				
				if(left.containsKey(k)){
					if(lv instanceof Map && rv instanceof Map){
						merge((Map<String,Object>)lv,(Map<String,Object>) rv);
						continue;
					}
				}
				left.put(k, rv);
			}
		}

		return left;
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
		String sss = "addToParams({html:{body:{content:{body:{portlet:{title:{caption:aa.bb,icon:'aaa.jpg'}}}}}},"
				+ "c:[html.body.content.body.portlet.title.update({caption:'香缇卡',icon:'cart.jpg'}),"
				+ "print(html)]})";

		String s2 = "addToParams({brand:{title:{caption:aa.bb,icon:'aaa.jpg'}},"
				+ "c:[brand.title.update({caption:'香缇卡',icon:'cart.jpg'}),"
				+ "print(brand)]})";
		
		String s = "addToParams({a:{b:{b1:'aaa',b2:'bbb'},c:'222',e:{h:'aaaa',j:[1,3,5]}},"
				+ "c:[a.update({b:{tt:'111111',bb:'rrrrrr'},c:{c1:'c11111',c2:'c222222'},e:{e1:'addd',e2:'add222'}}),"
				+ "print('---------'),print(a)]})";
		Map<String,Object> params = new HashMap<String,Object>(); 
		Object v = AE.execute(sss,params);
		
//		s = "[a.b.update({tt:'111111',bb:'rrrrrr'}),print('-------------'),print(a)]";
//		
//		params = (Map<String,Object>)v;
//		v = AE.execute(s,params);
	}
	
	public static Object execute(Function function,Map<String,Object> params){
		Object operatedObj = function.getKeyValue();
		if(operatedObj == null || !(operatedObj instanceof Comparable)){
			return false;
		}
		return null;
	}
}
