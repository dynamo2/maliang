var utils = new Utils();

/********************Array prototype ***********************/
/**
 * 清空数组
 * **/
Array.prototype.empty = function(){
	this.splice(0,this.length);
};

/**
 * 判断数组是否为空
 * **/
Array.prototype.isEmpty = function(){
	return this.length == 0;
};

function Utils(){
	var _ = this;
	
	this.ajaxForm = function(form,fun){
		var reqData = readFormDatas(form);
		
		$.ajax("/flows/ajax.htm",{
			data:reqData,
			dataType:'json',
			type:'POST',
			async:false
		}).done(fun);
	},
	
	this.put = function(obj,key,val){
		if(!$.isPlainObject(obj)){
			obj = {};
		}
		
		if(_.isString(key)){
			var ks = key.split(".");
			if(ks.length > 1){
				var last = obj;
				var i = 0;
				
				for(i = 0; i < ks.length-1; i++){
					var k = ks[i];
					
					if(!last[k]){
						last[k] = {};
					}
					last = last[k];
				}
				last[ks[i]] = val;
			}else {
				obj[key] = val;
			}
		}
		
		return obj;
	};
	
	this.get = function(obj,key){
		return _.readValue(obj,key);
	};
	
	this.isString = function (obj){
		return $.type(obj) === 'string';
	};
	
	this.eq = function(v1,v2){
		if(!v1 || !v2)return false;

		return $.trim(v1.toString()) === $.trim(v2.toString());
	};
	
	/***
	 * 将自增长数组名：
	 *    如：provice.0.city 返回 province.1.city
	 * **/
	this.nextArrayName = function(name){
		return _.nextArrayName(name,-1);
	};
	
	/***
	 * 将自增长数组名：
	 *    如：provice.0.city 返回 province.1.city
	 * **/
	this.nextArrayName = function(name,idx){
		if(!utils.isString(name))return;

		var newName = '';
		var ns = name.split(".");
		var iid = 0;
		
		if($.isNumeric(idx)){
			idx = Number(idx);
		}else {
			idx = -1;
		}
		if(ns.length > 1){
			for(var i = 0; i<ns.length;i++){
				var val = ns[i];
				if(!val)continue;
				
				if(newName.length > 0){
					newName += '.';
				}
				
				if($.isNumeric(val)){
					console.log('idx : ' + idx);
					console.log('(idx < 0) : ' + (idx < 0));
					console.log('(idx < 0 || iid == idx) : ' + (idx < 0 || iid == idx));
					if(idx < 0 || iid == idx){
						val = Number(val);
						val++;
					}
					iid++;
				}
				
				newName += val;
			}
		}else {
		    newName = name;
		}
		
		return newName;
	};
	
	/***
	 * 将input.name转换成数组格式：
	 *    如：provice.city 转换成 province.0.city
	 * **/
	this.toArrayName = function(name,arrayIndex){
		if(!utils.isString(name))return;
		
		if(name.charAt(name.length-1) == '.'){
			return name.substr(0,name.length-1);
		}
		
		var n = '';
		var ns = name.split(".");
		if(ns.length > 1){
			for(var i = 0; i<ns.length-1;i++){
				if(n.length > 0){
					n += '.';
				}
				n += ns[i];
			}
		}
		
		if(n.length > 0){
			n += '.';
		}
		n += arrayIndex+'.'+ns[ns.length-1];
		
		return n;
	};
	
	/***
	 * 复制对象属性， exts：不操作的属性
	 */
	 this.copy = function (fromObj, toObj, exts) {
		 if(!$.isPlainObject(fromObj)){
			return toObj; 
		 }
		 
		 if(!$.isPlainObject(toObj)){
			 toObj = {};
		 }
		 
		 $.each(fromObj,function(k,v){
			if(_.hasName(exts, k) || !v){
				return;
			}
			
			toObj[k] = v;
		 });
		 return toObj;
	};
	
	this.clone = function(obj){
		var curr = this;
		var newObj = obj;
		if($.isArray(obj)){
			newObj = [];
			$.each(obj,function(){
				newObj.push(curr.clone(this));
			});
		}
		
		if($.isPlainObject(obj)){
			newObj = curr.copy(obj,null,null);
		}
		
		return newObj;
	};
	
	this.addEvent = function(element,eveName,funs){
		if(element && eveName && funs){
			if(_.isString(funs)){
				funs = eval(funs);
			}
			
			element.on(eveName,funs);
		}
	};

	this.hasName = function (names, n) {
		if (!names)
			return false;

		var is = false;
		if ($.isArray(names)) {
			$.each(names, function(i, v) {
				if (v === n) {
					is = true;
					return;
				}
			});
		} else {
			is = (names === x);
		}
		return is;
	};
	
	this.readValue = function (obj, key,dv) {
		var val = this.readValue(obj,key);
		if(!val){
			return dv;
		}
		return val;
	};
	
	/**
	 * eq.: 
	 *   obj : {"name":"臻秀修护美颜水125ML","brand":{"id":"aaaaa","name":"雪花秀"}} ,
	 *   if key = "name" 
	 *     return:"臻秀修护美颜水125ML" 
	 *   if key = "brand"
	 *     return:{"id":"aaaaa","name":"雪花秀"}
	 *   if key = 'brand.id'
	 *     return:'aaaaa'
	 */
	this.readValue = function (obj, key) {
		if(!obj || !key)return null;
		if(!$.isPlainObject(obj))return null;
		
		if (key == 'this') {
			return obj;
		}

		var ks = key.split('.');
		var temp = obj;
		var v = null;
		$.each(ks,function(){
			if(temp){
				v = temp[this];
				temp = v;
			}else {
				v = null;
				return;
			}
		});

		return v;
	};
}


/**
 * eq.: str : <a href='edit.htm?id=dddd_${brand.id}'>${name}</a> obj :
 * {"name":"臻秀修护美颜水125ML","brand":{"id":"aaaaa","name":"雪花秀"}} return : <a
 * href='edit.htm?id=dddd_aaaaa'>臻秀修护美颜水125ML</a>
 */
function replaceVar(str, obj) {
	return str.replace(/\$\{([\w.]+)\}/g, function(expre, key) {
		var v = readValue(obj, key);
		if(v == null)v = '';
		
		if (v instanceof Object) {
			v = JSON.stringify(v);
		}
		return v;
	});
}

/**
 * eq.: obj : {"name":"臻秀修护美颜水125ML","brand":{"id":"aaaaa","name":"雪花秀"}} if key =
 * "name" return："臻秀修护美颜水125ML" if key = "brand"
 * return：{"id":"aaaaa","name":"雪花秀"}
 */
function readValue(obj, key) {
	if(!obj || !key)return null;
	if(!$.isPlainObject(obj))return null;
	
	if (key == 'this') {
		return obj;
	}

	var ks = key.split('.');
	var temp = obj;
	var v = null;
	$.each(ks,function(){
		if(temp){
			v = temp[this];
			temp = v;
		}else {
			v = null;
			return;
		}
	});

	return v;
}

function ts(obj) {
	return JSON.stringify(obj);
}