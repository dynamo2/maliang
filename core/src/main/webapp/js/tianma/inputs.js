function buildInputs(json){
	var options = readForm(json);
	var ig = InputsGenerator({inputs:options.inputs});
	return ig.build();
}

function HTMLGenerator(options){
	this.ul = function(){
		var u = $("<ul />");
	};
}

function InputsGenerator(options) {
	var _ = this;
	var DEFAULT = "DEFAULT";

	this.build = function() {
		//var form = _.gForm();
		var box = _.gBox();

		var boxCnt = _.boxContent(box);
		var inputs = options.inputs;
		$.each(inputs, function() {
			var row = _.gRow();
			row.append(_.gColumnLabel(this));
			row.append(_.gColumnInput(this));

			boxCnt.append(row);
		});
		return box;

//		form.append(box);
//		return form;
	};

	this.boxContent = function(be) {
		var bce = _.trigger(options.boxContent, [ be ]);
		if (!bce) {
			bce = _.innestElement(be);
		}

		return bce;
	}

	this.innestElement = function(el) {
		if (el && el.children() && el.children().length > 0) {
			return _.innestElement($(el.children()[0]));
		}
		return el;
	};

	this.gForm = function() {
		var form = null;
		try {
			form = _.custom(options.form);
		} catch (err) {
		}

		if (!form) {
			form = $("<form action='/flows/flow.htm' />");
		}

		_.trigger(options.afterForm, [ form ]);

		return form;
	};

	this.gBox = function() {
		try {
			return _.custom(options.box);
		} catch (err) {
			return $("<div class='portlet'><div class='portlet-boxy'><div class='form-box-jquery' /></div><div class='test-content'></div></div>");
		}
	};

	this.gRow = function() {
		try {
			return _.custom(options.row);
		} catch (err) {
			return $("<div class='form-default' />");
		}
	};

	this.custom = function(c) {
		if (!c) {
			throw DEFAULT;
		}

		if ($.isFunction(c)) {
			return r.call(r);
		}
		;

		if ($.type(c) === 'string') {
			return $(c);
		}

		return c;
	};

	this.gColumnLabel = function(opts) {
		label = $("<label />").text(opts.label);

		_.trigger(options.afterColumnLabel, [ label, opts ]);
		
		return label;
	};

	this.gColumnInput = function(opts) {
		var colDiv = null;
		try {
			colDiv = _.custom(options.columnInput);
		} catch (err) {
			colDiv = $("<div class='form-default' />");
		}

		colDiv.append(_.gInput(opts));
		return colDiv;
	};

	this.gInput = function(opts) {
		var input = TM_formBuilder.newInputElement(_.readInputOptions(opts));

		_.trigger(options.afterInput, [ input, opts ]);

		return input;
	};

	this.trigger = function(fun, args) {
		if (!$.isFunction(fun))
			return;

		return fun.apply(fun, args);
	}
	
	this.readLabel = function(opts){
		return (opts && opts.label)?opts.label:'';
	};
	
	this.readInputOptions = function(opts){
		var inpOpts = {};
		utils.copy(opts,inpOpts,['newLine','label']);
		
		return inpOpts;
	};
}
