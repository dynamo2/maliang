package com.model.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.model.dao.impl.DaoImpl;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class BsonUtil {
    public static <T> List<T> toBeans(List<Document> documents, Class<T> clazz)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        List<T> list = new ArrayList<T>();
        for (int i = 0; null != documents && i < documents.size(); i++) {
            list.add(toBean(documents.get(i), clazz));
        }
        return list;
    }
    
    public static String readKey(Field field) {
        String fname = field.getName();
        Column column = field.getAnnotation(Column.class);
        if (null != column && null != column.name()) {
        	return column.name();
        } else if ("id".equals(fname)) {
        	return "_id";
        }
        
        return fname;
    }
    
    private static boolean isLinkedDoc(Object docVal,Field field) {
    	if(field.getName().equals("id")) {
    		return false;
    	}
    	
    	return ((docVal instanceof String) || (docVal instanceof ObjectId)) && !isJavaClass(field.getType()) ;
    }

    /*
     * 将Bson 转化为对象
     * 
     * @param:Bson文档
     * 
     * @param:类pojo
     * 
     * @param:返回对象
     */
    public static <T> T toBean(Document document, Class<T> clazz)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
    	if(document == null) {
    		return null;
    	}

        T obj = clazz.newInstance();// 声明一个对象
        Field[] fields = clazz.getDeclaredFields();// 获取所有属性
        Method[] methods = clazz.getMethods();// 获取所有的方法
        /*
         * 查找所有的属性，并通过属性名和数据库字段名通过相等映射
         */
        for (int i = 0; i < fields.length; i++) {
        	Field field = fields[i];
            String fieldName = fields[i].getName();
            String key = readKey(fields[i]);
            Object bson = document.get(key);
            
            if(key.equals("deviceDicts")) {
            	System.out.println(key + " is list -----------" + bson);
            	//System.out.println(key + " is list -----------" + bson.getClass().getName());
            }
            
            if (null == bson) {
                continue;
            }else if(isLinkedDoc(bson,field)) {
            	bson = DaoImpl.get(field.getType(), bson);
            } else if (bson instanceof Document) {// 如果字段是文档了递归调用
                bson = toBean((Document) bson, fields[i].getType());
            } else if (bson instanceof List) {// 如果字段是文档集了调用colTOList方法
                bson = colToList((List<Object>)bson, fields[i]);
            }
            
            for (int j = 0; j < methods.length; j++) {// 为对象赋值
                String metdName = methods[j].getName();
                if (equalFieldAndSet(fieldName, metdName)) {
                    methods[j].invoke(obj, bson);
                    break;
                }
            }
        }
        return obj;
    }

    public static List<Document> toBsons(List<Object> objs)
            throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchFieldException {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; null != objs && i < objs.size(); i++) {
            documents.add(toBson(objs.get(i)));
        }
        return documents;
    }
    
    public static ObjectId readId(Object obj) {
    	if(obj == null) {
    		return null;
    	}
    	
		Method[] linkedMs = obj.getClass().getDeclaredMethods();
    	for(Method m : linkedMs) {
    		if("getId".equals(m.getName())) {
				try {
					Object id = m.invoke(obj);
					if(id != null && (id instanceof ObjectId)) {
						return (ObjectId)id;
					}
				} catch (Exception e) {}
					
    		}
    	}
    	return null;
	}
    
    public static boolean notColumn(Field field) {
    	NotColumn notColumn = field.getAnnotation(NotColumn.class);// 获取否列
        return notColumn != null;
    }

    /*
     * 将对象转化为Bson文档
     * 
     * @param:对象
     * 
     * @param:类型
     * 
     * @return:文档
     */
    public static Document toBson(Object obj) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchFieldException {
        if (null == obj) {
            return null;
        }
        Class<? extends Object> clazz = obj.getClass();
        Document document = new Document();
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; null != fields && i < fields.length; i++) {
        	Field field = fields[i];
            if(notColumn(field)) {
            	continue;
            }
            
            //Column column = field.getAnnotation(Column.class);// 获取列注解内容
            String key = readKey(field);// 对应的文档键值
            String fieldName = field.getName();
            
            /*
             * 获取对象属性值并映射到Document中
             */
            for (int j = 0; null != methods && j < methods.length; j++) {
                String methdName = methods[j].getName();
                Class returnClazz = methods[j].getReturnType();
                if (null != fieldName && equalFieldAndGet(fieldName, methdName)) {

                    Object val = methods[j].invoke(obj);// 得到值
                	//System.out.println(fieldName + "---- " + methdName +",  val : " + val);
                    if (null == val) {
                        continue;
                    }
                    
                    if(val instanceof ObjectId) {
                    	document.append(key, val);
                    	continue;
                    }
                    
                    if(isList(returnClazz)) {
                    	@SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) val;
                        List<Document> documents = new ArrayList<Document>();
                        for (Object obj1 : list) {
                            documents.add(toBson(obj1));
                        }
                        
                        document.append(key, documents);
                        continue;
                    }
                    
                    if (isJavaClass(returnClazz)) {
                        document.append(key, val);
                    } else {// 自定义类型
                    	ObjectId lid = readId(val);
                    	if(lid != null) {
                    		document.append(key,lid);
                    	}else {
                            document.append(key, toBson(val));                    		
                    	}
                    }
                }
            }
        }
        return document;
    }
    
    public static boolean isList(Class cls) {
    	if(cls == null)return false;
    	
    	return cls.getName().equals("java.util.List");
    }

    /*
     * 是否是自定义类型】
     * 
     * false:是自定义
     */
    private static boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }
    
    private static List<Object> colToList(List<Object> datas, Field field)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
    	if(datas == null) {
    		return null;
    	}
    	
        ParameterizedType pt = (ParameterizedType) field.getGenericType();// 获取列表的类型
        
        List<Object> objs = new ArrayList<Object>();
       
        for(Object obj : datas) {
        	@SuppressWarnings("rawtypes")
            Class clz = (Class) pt.getActualTypeArguments()[0];// 获取元素类型
        	
        	Object bean = obj;
        	if(obj instanceof Document) {
        		bean = toBean((Document)obj, clz);
        	}
    		objs.add(bean);
        }
        return objs;
    }

    /*
     * 将文档集转化为列表
     * 
     * @param:文档集
     * 
     * @param:属性类型
     * 
     * @return:返回列表
     */
    private static List<Object> colToList(Object bson, Field field)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();// 获取列表的类型
        
        System.out.println(" pt : " + pt.getActualTypeArguments()[0]);
        
        List<Object> objs = new ArrayList<Object>();
        @SuppressWarnings("unchecked")
        MongoCollection<Document> cols = (MongoCollection<Document>) bson;
        MongoCursor<Document> cursor = cols.find().iterator();
        while (cursor.hasNext()) {
            Document child = cursor.next();
            @SuppressWarnings("rawtypes")
            Class clz = (Class) pt.getActualTypeArguments()[0];// 获取元素类型
            @SuppressWarnings("unchecked")
            Object obj = toBean(child, clz);
            System.out.println(child);
            objs.add(obj);

        }
        return objs;
    }

    /*
     * 比较setter方法和属性相等
     */
    private static boolean equalFieldAndSet(String field, String name) {
        if (name.toLowerCase().matches("set" + field.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 比较getter方法和属性相等
     */
    private static boolean equalFieldAndGet(String field, String name) {
        if (name.toLowerCase().matches("get" + field.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }
}

