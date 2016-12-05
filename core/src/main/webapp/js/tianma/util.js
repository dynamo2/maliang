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
		return readValue(obj,key);
	};
	
	this.isString = function (obj){
		return $.type(obj) === 'string';
	};
	
	/***
	 * 复制对象属性， exts：不操作的属性
	 */
	 this.copy = function (fromObj, toObj, exts) {
		for (x in fromObj) {
			if (_.hasName(exts, x))
				continue;

			toObj[x] = fromObj[x];
		}
		return toObj;
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

	ks = key.split('.');
	var v = obj;
	for (idx in ks) {
		v = v[ks[idx]];
		
		if(!v)return null;
	}
	
	return v;
}

function ts(obj) {
	return JSON.stringify(obj);
}