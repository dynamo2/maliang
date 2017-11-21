package com.maliang.core.dao.sql;

public class Order {
/**
 * ************** SQL : 
 * pdb.Order.aggregate([
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
                hour:{
                    $hour:'$createDate'
                },
                product:'$buyItems.product'
            },
            totalNum:{
                $sum:'$buyItems.num'
            },
            totalPrice:{
                $sum:{
                    $multiply:['$buyItems.price','$buyItems.num']
                }
            },
            count:{$sum:1}
        }
    },
    {
        $group:{
            _id:'$_id.date',
            ps:{
                $push:{
                    hour:'$_id.hour',
                    product:'$_id.product',
                    num:'$totalNum',
                    price:'$totalPrice',
                    orderCount:'$count'
                }
            },
            totalNum:{$sum:'$totalNum'},
            totalPrice:{$sum:'$totalPrice'}
        }
    }
])

******************** RESULTS *********************
*{ 
        "_id" : "2017-07-06" , 
        "ps" : [ 
            { "product" : { "$oid" : "5965d17ff75f959c6b494dd9"} , "hour" : 13 , "price" : 240800 , "num" : 2408 , "orderCount" : 1194} , 
            { "product" : { "$oid" : "5965d17ff75f959c6b494dd9"} , "hour" : 11 , "price" : 178900 , "num" : 1789 , "orderCount" : 895} , 
            { "product" : { "$oid" : "5965d17ff75f959c6b494dd9"} , "hour" : 10 , "price" : 179300 , "num" : 1793 , "orderCount" : 892} , 
            { "product" : { "$oid" : "5965d17ff75f959c6b494ddd"} , "hour" : 10 , "price" : 354400 , "num" : 1772 , "orderCount" : 901} , 
            { "product" : { "$oid" : "5965d17ff75f959c6b494ddd"} , "hour" : 13 , "price" : 486400 , "num" : 2432 , "orderCount" : 1210} , 
            { "product" : { "$oid" : "5965d17ff75f959c6b494ddd"} , "hour" : 11 , "price" : 356000 , "num" : 1780 , "orderCount" : 905}
        ] , 
        "totalNum" : 11974 , 
        "totalPrice" : 1795800
    }, 
 * **/
	
	
	
	
	
	
	
/***
 * 
 * 
 * pdb.Order.aggregate([
    {
        $match:db.and([
                db.eq({buyItems.product:oid('5965d17ff75f959c6b494ddd')})
        ])
        
    },
    {
        $unwind:'$buyItems'
    },
    {
        $group:{
            _id:{
                user:'$user',
                date:{
                    $dateToString:{
                            format:'%Y-%m-%d %H:00',
                            date:'$createDate'
                        }
                }
            },
            totalNum:{
                $sum:'$buyItems.num'
            },
            totalPrice:{
                $sum:{
                    $multiply:['$buyItems.price','$buyItems.num']
                }
            },
            count:{$sum:1}
        }
    }
])
 * 
 * 
 * 
 * 
 * 
 * pdb.Order.aggregate([
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
                user:'$user',
                date:{
                    $dateToString:{
                            format:'%Y-%m-%d %H:00',
                            date:'$createDate'
                        }
                }
            },
            totalNum:{
                $sum:'$buyItems.num'
            },
            totalPrice:{
                $sum:{
                    $multiply:['$buyItems.price','$buyItems.num']
                }
            },
            count:{$sum:1}
        }
    },
    {
        $group:{
            _id:'$_id.date',
            users:{
                $addToSet:'$_id.user'
            },
            totalNum:{
                $sum:'$totalNum'
            },
            totalPrice:{
                $sum:'$totalPrice'
            },
            count:{$sum:'$count'}
        }
    },
    {
        $project:{
            _id:1,
            userCount:{$size:'$users'},
            totalNum:1,
            totalPrice:1,
            count:1
        }
    }
])
 * 
 * 
 * 
 * 
 * 
 * 
 * ***/
	
	
	
	
}
