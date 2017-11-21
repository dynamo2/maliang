package com.maliang.core.arithmetic.function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bson.types.ObjectId;

import com.maliang.core.dao.PrimitiveDao;
import com.maliang.core.util.Utils;

public class MathFunction {
	public static void main(String[] args) {
		/**
		 * db.Order.mapReduce(
    function(){
        this.buyItems.forEach(function(item){ 
            emit(item.product,item.num);
    });},
    function(key,values){
        var p = DBRef('Product',key).$id;
        p = db['Product'].findOne({_id:key});
        return {p:p,num:Array.sum(values)};
    },
    {
        out:{inline:1}
    }
)




pdb.Order.aggregate([
    {
        $match:{
            createDate:{$ne:null}
        }
    },
    {
        $unwind:'$buyItems'
    },
    {
        $group:{
            _id:{
                date:{
                    $dateToString:{
                        format:'%Y-%m-%d',
                        date:'$createDate'
                    }
                },
                product:'$buyItems.product'
            },
            totalNum:{
                $sum:'$buyItems.num'
            }
        }
    }
])



pdb.Order.aggregate([
    {
        $match:{
            createDate:{$ne:null}
        }
    },
    {
        $unwind:'$buyItems'
    },
    {
        $group:{
            _id:{
                date:{
                    $dateToString:{
                        format:'%Y-%m-%d',
                        date:'$createDate'
                    }
                },
                product:'$buyItems.product'
            },
            totalNum:{
                $sum:'$buyItems.num'
            },
            ps:{
                $push:{
                    $group:{
                        _id:'$buyItems.product',
                        num:{
                            $sum:'$buyItems.num'
                        }
                    }
                }
            }
        }
    }
])






pdb.Order.aggregate([
    {
        $match:{
            createDate:{$ne:null}
        }
    },
    {
        $unwind:'$buyItems'
    },
    {
        $group:{
            _id:{
                date:{
                    $dateToString:{
                        format:'%Y-%m-%d',
                        date:'$createDate'
                    }
                },
                product:'$buyItems.product'
            },
            totalNum:{
                $sum:'$buyItems.num'
            }
        }
    },
    {
        $group:{
            _id:{
                date:'$_id.date'
            },
            ps:{
                $push:{
                    p:'$_id.product',
                    totalNum:'$totalNum'
                }
            }
        }
    }
])
		 * 
		 * 
		 * 
		 * ***/
		String[] users = {"wmx","wf","wzq"};
		
		int min=1000;
		int max=8000;
		PrimitiveDao dao = new PrimitiveDao();
		
        List<Integer> ls = randomDailySales(min,max,100000);
        System.out.println("ls : " + ls);
        
        //18~20点的销量：[80%,100%]
        
        int year = 2017;
        int month = 6;
        int date = 1;
        
        List<Date> dates = new ArrayList<Date>();
        List<Map> orders = new ArrayList<Map>();
        for(int dn:ls){
        	int hour = 18;
            int minitue = 0;
            int second = 0;
            
        	int p = random(80,100);
        	int hn = dn*p/100;
        	
        	List<Integer> hs = new ArrayList<Integer>();
        	int nn = hn;
        	for(int i = 0; i < 2; i++){
        		p = random(20,40);
        		
        		int hnn = hn*p/100;
        		hs.add(hnn);
        		
        		nn -= hnn;
        	}
        	hs.add(nn);
        	//System.out.println("dn: " + dn + ", p : " + p + "%, hn: " + hn+", list : " + hs);
        	
        	for(int ii = 0; ii < 3; ii++){
        		hour += ii;
        		
        		int loop = hs.get(ii);
        		for(int iii = 0; iii < loop; iii++){
        			minitue = random(1,59);
                	second = random(1,59);
                	
                	Calendar cal = Calendar.getInstance();
                	cal.set(year, month, date, hour, minitue,second);
                	
                	while(dates.contains(cal)){
                		minitue = random(1,59);
                    	second = random(1,59);
                    	cal.set(year, month, date, hour, minitue,second);
                	}
                	//dates.add(cal);
                	
                	Map order = new HashMap();
                	
                	
                	List<Map> items = new ArrayList<Map>();
                	Map item = new HashMap();
                	item.put("product",new ObjectId("5965d17ff75f959c6b494ddd"));
                	item.put("price",200);
                	item.put("num",random(1,3));
                	items.add(item);
                	
                	item = new HashMap();
                	item.put("product",new ObjectId("5965d17ff75f959c6b494dd9"));
                	item.put("price",100);
                	item.put("num",random(1,3));
                	items.add(item);
                	
                	//order.put("hour",hour);
                	order.put("createDate",cal.getTime());
                	order.put("buyItems", items);
                	order.put("user", users[random(1,3)-1]+"_"+random(100,200));
                	
                	dao.save(order,"Order");
                	//orders.add(order);
                	
                	//dates.add(cal.getTime());
        		}
        	}
        	date++;
        }
        
        //System.out.println(orders);
	}
	
	/**
	 * 随机产生一个月的日销量
	 * 1. 销量总额为1000
	 * 2. 每日销量额为：[10,80]
	 * ***/
	public static List<Integer> randomDailySales(int min,int max,int total){
        int loop = 10000;
        List<Integer> ls = null;
        for(int li = 0; li < loop; li++){
        	int rr = 0;
        	ls = new ArrayList<Integer>();
        	
            for(int i = 0; i < 30; i++){
            	int s = random(min,max);
            	ls.add(s);
            	
            	rr += s;
            }
            
            if(rr == total){
            	break;
            }
        }
        return ls;
	}
	
	public static Object random(Function function,Map<String,Object> params){
		Object val = function.executeExpression(params);
		if(val == null){
			val = function.getKeyValue();
		}
		
		int min = 1;
		int max = Integer.MAX_VALUE;
		
		if(Utils.isArray(val)){
			val = Utils.toArray(val);
			int size = ((Object[])val).length;
			if(size == 1){
				max = toInt(((Object[])val)[0],Integer.MAX_VALUE);
			}else if(size > 1){
				min = toInt(((Object[])val)[0],1);
				max = toInt(((Object[])val)[1],Integer.MAX_VALUE);
			}
		}else {
			max = toInt(val,Integer.MAX_VALUE);
		}
		
		return random(min,max);
	}
	
	public static int random(int min,int max){
		Random random = new Random();
		return random.nextInt(max)%(max-min+1) + min;
	}
	
	private static int toInt(Object v,int dv){
		try {
			if(v instanceof Number){
				return ((Number)v).intValue();
			}else {
				try {
					return Integer.valueOf(v.toString());
				}catch(Exception e){
					return Double.valueOf(v.toString()).intValue();
				}
			}
		}catch(Exception e){
			return dv;
		}
	}
}
