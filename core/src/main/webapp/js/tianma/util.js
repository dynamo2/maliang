var utils = new Utils();

function Utils(){
	var _ = this;
	
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
	if (key == 'this') {
		return obj;
	}

	ks = key.split('.');
	var v = obj;
	for (idx in ks) {
		v = v[ks[idx]];
	}

	if (v == null)
		return '';

	return v;
}

function ify(obj) {
	return JSON.stringify(obj);
}