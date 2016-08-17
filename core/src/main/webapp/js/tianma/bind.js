var $bind = {};

function bind() {
	var name = null;
	var binder = null;
	var _ = this;

	this.init = function(n) {
		_.name = n;
	};

	this.set = function(json) {
		_.activeBinder();
		
		_.binder.empty();
		addChildren(_.binder,json);
	};
	
	this.activeBinder = function(){
		if(!_.binder){
			_.binder = $('#' + _.name);
		}
	};

	this.append = function(cont) {

	};
}

function appendToBind(id) {
	$bind[id] = new bind();
	$bind[id].init(id);
}

function buildBind(json){
	var span = $("<span />");
	var id = json[1];
	
	span.prop("id",id);
	appendToBind(id);
	
	for(i in json){
		if(i > 1 && json[i]){
			addChildren(span,json[i]);
		}
	}
	
	return span;
}