function deleteDiv(event) {
	deleteItem(event, 'div');
}

function deleteTd(event) {
	deleteItem(event, 'td');
}

function deleteTr(event) {
	deleteItem(event, 'tr');
}

function deleteTable(event) {
	deleteItem(event, 'table');
}

function deleteItem(event, tag) {
	var item = $(event.currentTarget).closest(tag);
	item.remove();
}

function init() {
	$("#dialog").dialog({
		resizable : false,
		height : 500,
		width : 500,
		autoOpen : false,
		buttons : {
			Cancel : function() {
				$(this).dialog("close");
			}
		}
	});

	if (result && result.title) {
		$("#title").text(result.title);
	}

	if (htmlCode) {
		$("#html").html(htmlCode);
	}

	if (json) {
		$("#main").append(build(json));
	}
}

function build(json) {
	if (utils.isString(json)) {
		return $("<span />").text(json);
	} else if ($.isPlainObject(json)) {
		if (json.html) {
			return $(json.html);
		}
	} else if ($.isArray(json)) {
		var type = json[0];
		if (utils.isString(type)) {
			return buildOne(json);
		} else {
			var comps = [];
			$.each(json, function() {
				var ccs = build(this);
				if ($.isArray(ccs)) {
					comps = comps.concat(css);
				} else {
					comps.push(ccs);
				}
			});

			return comps;
		}
	}

	return null;
}

function buildOne(json) {
	var type = json[0];

	if (type === 'tableBlock') {
		return TableBlock(json);
	} else if (type === 'tableList') {
		return TableList(json);
	} else if (type === 'menu') {
		return Menu(json);
	} else if (type === 'error') {
		return Error(json);
	} else if (type === 'dialog') {
		appendToDialog(json);
		return null;
	} else if (type === 'form') {
		var options = readForm(json);
		pt(ts(options));
		var ft = new FormTable();
		ft.init(options);

		return ft.form;
	} else if (type === 'a') {
		return buildA(json);
	} else if (type === 'img') {
		return buildImg(json);
	} else if (type === 'button') {
		return buildButton(json);
	} else if (type === 'div') {
		return buildDiv(json);
	} else if (type === 'span') {
		return buildSpan(json);
	} else if (type === 'input') {
		return buildInput(json);
	} else if (type === 'bind') {
		return buildBind(json);
	}
}

function appendToDialog(json) {
	$("#dialogPanel").empty();
	$("#dialogPanel").append(build(json[1]));

	/**
	 * * Dialog options
	 */
	if (json.length >= 3) {
		var opts = json[2];
		var dopts = {};
		dopts.buttons = {};

		for (x in opts) {
			if (x === 'buttons') {
				var btns = opts.buttons;

				for (bn in btns) {
					dopts.buttons[bn] = function() {
						eval(btns[bn]);
					};
				}
			} else {
				dopts[x] = opts[x];
			}
		}

		if (!dopts.buttons.Cancel) {
			dopts.buttons.Cancel = function() {
				$(this).dialog("close");
			};
		}

		$("#dialog").dialog(dopts);
	}
	$("#dialog").dialog("open");
}

function ajax(data, doneFun) {
	$.ajax('/business/ajax.htm', {
		data : data,
		dataType : 'json',
		type : 'POST',
		async : false
	}).done(doneFun ? doneFun : function(result, status) {
		var js = '/business/js.htm?bid=' + data.bid + '&fid=' + data.fid;

		$.getScript(js, function() {
			if (result && result.json) {
				$("#main").append(build(result.json));
			}
		});
	});
}

function ajaxForm(formId, doneFun) {
	var form = $("#" + formId);
	var reqDatas = readFormDatas(form);
	
	ajax(reqDatas, doneFun);
}

function readFormDatas(form) {
	var inputs = form.find(":input");

	var reqDatas = {};
	$.each(inputs, function() {
		if ($(this).attr("type") == "radio") {
			if (!this.checked) {
				return;
			}
		}
		
		var key = $(this).attr("name");
		var oldVal = reqDatas[key];
		if(reqDatas[key]){
			var val = reqDatas[key];
			if(!$.isArray(val)){
				val = [val];
				reqDatas[key] = val;
			}
			val.push($(this).val());
		}else {
			reqDatas[key] = $(this).val();
		}
	});

	return reqDatas;
}

function addChildren(parent, json) {
	if ($.isArray(json)) {
		parent.append(build(json));
	} else if ($.isPlainObject(json)) {
		parent.html(json.html);
	} else {
		parent.text(json);
	}
	return parent;
}

function buildA(json) {
	var a = $("<a />");

	addChildren(a, json[1]);

	var reqs = json[2];
	var href = "/business/business2.htm?";
	if (utils.isString(reqs)) {
		href += reqs;
	} else if ($.isPlainObject(reqs)) {
		if (!reqs.bid) {
			reqs.bid = data.bid;
		}

		var s = "";
		$.each(reqs, function(k, v) {
			if (s.length > 0) {
				s += "&";
			}
			s += k + "=" + v;
		});
		href += s;
	}
	a.attr("href", href);
	return a;
}

function buildInput(json) {
	var input = $("<input />");
	var type = json[1];
	type = type && type.length > 0 ? type : 'text';
	var name = json[2];
	var val = json[3];

	return input.attr("type", type).attr("name", name).val(val);
}

function buildButton(json) {
	var bnt = $("<input type='button' />");
	bnt.val(json[1]);
	bnt.attr("onclick", json[2]);

	return bnt;
}

function buildImg(json) {
	var img = $("<img />");
	img.attr("src", json[1]);

	return img;
}

function buildDiv(json) {
	var div = $("<div style='padding:3px 5px' />");

	for (i in json) {
		if (i > 0) {
			addChildren(div, json[i]);
		}
	}

	return div;
}

function buildSpan(json) {
	var span = $("<span style='margin-right:5px;' />");

	for (i in json) {
		if (i > 0) {
			addChildren(span, json[i]);
		}
	}

	return span;
}

function Error(json) {
	var err = $("<div class='error'/>");

	if (json.length > 1) {
		addChildren(err, json[1]);
	}
	return err;
}

function Menu(json) {
	var menu = $("<p class='menu' />");

	if ($.isArray(json) && json.length > 1) {
		var menuList = json[1];
		var bid = menuList[0];

		if (utils.isString(bid) && $.trim(bid).length > 0) {
			bid = 'bid=' + bid;
		} else
			bid = '';

		var href = '/business/business2.htm?' + bid;
		for (var i = 1; i < menuList.length; i++) {
			var opts = menuList[i];

			var a = $("<a />").appendTo(menu);
			if (utils.isString(opts)) {
				a.attr('href', href);
				a.text(opts);
			} else if ($.isPlainObject(opts)) {
				for ( var x in opts) {
					a.text(x);

					var tail = opts[x];
					if (utils.isString(tail)) {
						a.attr('href', href + tail);
					} else if ($.isNumeric(tail)) {
						a.attr('href', href + '&fid=' + tail);
					}
				}
			}
		}
	}
	return menu;
}

/**
 * 个人资料详情列表 ** ['tableList', ['账号','密码','真实姓名','Email','手机号码','操作'],
 * each(accounts){[this.account,this.password,this.personal_profile.real_name,
 * this.personal_profile.email,this.personal_profile.mobile, {html:'<a
 * href="/business/business2.htm?bid='+bid+'&fid=4&id='+this.id+'">修改</a> <a
 * href="/business/business2.htm?bid='+bid+'&fid=6&id='+this.id+'">查看</a>'}]} ]
 */
function TableList(json) {
	var table = $("<table class='tableList' cellpadding='0' cellspacing='1' />");

	var tr = $("<tr />").appendTo(table);
	$.each(json[1], function() {
		addChildren($("<th class='header' />"), this).appendTo(tr);
	});

	$.each(json[2], function() {
		tr = $("<tr />").appendTo(table);
		$.each(this, function() {
			addChildren($("<td />"), this).appendTo(tr);
		});
	});

	return table;
}

function addRow(table, datas) {
	var tbody = table.children("tbody");
	if (tbody == null || tbody == undefined) {
		tbody = table;
	}

	var tr = $("<tr />").appendTo(tbody);
	refreshRow(tr, datas);
}

function refreshRow(tr, datas) {
	tr.empty();
	$.each(datas, function() {
		addChildren($("<td />"), this).appendTo(tr);
	});
}

function removeRow(tr) {
	tr.remove();
}

function TableBlock(json) {
	var table = $("<table class='tableBlock' cellpadding='0' cellspacing='0' />");

	$.each(json[1], function() {
		var tr = $("<tr />").appendTo(table);
		addChildren($("<td class='label' />"), this[0]).appendTo(tr);
		addChildren($("<td />"), this[1]).appendTo(tr);
	});

	return table;
}

function readForm(json) {
	var _ = this;
	var source = json;

	var tag = 'form';
	var htmlOption = {
		tag : 'form'
	};

	var prefix = null;
	var inputs = null;

	this.read = function() {
		_.readHtmlOption();
		_.readInputs();
	};

	this.readHtmlOption = function() {
		if ($.isArray(source) && source.length >= 2) {
			var obj = source[1];

			if ($.isPlainObject(obj)) {
				utils.copy(obj, htmlOption, null);
			} else if (utils.isString(obj)) {
				htmlOption.id = obj + '.' + tag;
				prefix = obj;
			}
		}
	};

	this.readInputs = function() {
		inputs = [];
		if ($.isArray(source) && source.length >= 3) {
			var obj = source[2];
			if ($.isArray(obj)) {
				$.each(obj, function() {
					var input = readInput(this, prefix);
					inputs.push(input);
				});
			}
		}
	};

	_.read();
	htmlOption.inputs = inputs;
	return htmlOption;
}

function readInput(opts, prefix) {
	var _ = this;
	var input = {};

	this.read = function() {
		var obj = opts;
		if ($.isArray(obj)) {
			if ($.isArray(obj[0])) {
				input = [];

				$.each(obj, function() {
					input.push(readInput(this, prefix));
				});
			} else {
				input.newLine = _.newLine();
				input.name = _.readName();
				input.label = _.readLabel();
				input.type = _.readType();
				input.value = _.readValue();

				var others = _.readOthers();
				if (others) {
					input.events = _.readEvents(others);
				}
			}
		} else if ($.isPlainObject(obj)) {
			input = {
				name : obj.name ? obj.name : obj.n,
				label : obj.label ? obj.label : obj.l,
				type : obj.type ? obj.type : obj.t,
				value : obj.value ? obj.value : obj.v,
				newLine : obj.newLine ? obj.newLine : obj.nl
			};
		}
	};

	this.readEvents = function(defOpts) {
		if (!defOpts)
			return EMPTY;

		var events = {};
		var eventNames = [ 'change', 'click', 'dbclick', 'blur', 'focus' ];
		$.each(eventNames, function() {
			if (defOpts[this]) {
				events[this] = defOpts[this];
				defOpts[this] = EMPTY;
			}
		});

		return events;
	};

	this.readType = function() {
		var type = opts[2];
		if (!type) {
			type = 'text';
		} else if ($.isArray(type)) {
			var topts = {};
			topts.name = type[0];

			var list = type.length >= 2 ? type[1] : null;
			if (topts.name == 'select' || topts.name == 'radio') {
				topts.options = list;

				if (type.length >= 3 && $.isPlainObject(type[2])) {
					var defOpts = type[2];
					topts.events = _.readEvents(defOpts);
					utils.copy(defOpts, topts, [ 'options' ]);
				}
			} else if (topts.name == 'group') {
				if ($.isArray(list)) {
					topts.inputs = [];
					$.each(list, function() {
						topts.inputs.push(readInput(this, input.name));
					});
				}
			} else if (topts.name == 'list') {
				topts.header = type[1];
				topts.inputs = [];
				$.each(type[2], function(idx, val) {
					topts.inputs[idx] = [];
					$.each(val, function() {
						topts.inputs[idx].push(readInput(this, input.name));
					});
				});
			}

			type = topts;
		}
		return type;
	};

	this.readName = function() {
		var name = opts[0];
		if (name && name.slice(0, 1) == '$') {
			return name.slice(1);
		}

		return prefix ? prefix + '.' + name : name;
	};

	this.readLabel = function() {
		return opts[1];
	};

	this.readValue = function() {
		return opts[3];
	};

	this.readOthers = function() {
		if (opts.length >= 4) {
			return opts[4];
		}

		return null;
	};

	this.newLine = function() {
		var last = opts[opts.length - 1];
		if (last == LINE_BREAK) {
			return opts.pop();
		}
		return opts[-1];
	};

	_.read();
	return input;
}

function apt(str) {
	var txt = $("#print").text();
	pt(txt ? txt + str : str);
}

function pt(str) {
	if ($.isPlainObject(str)) {
		str = ts(str);
	}
	$("#print").text(str);
}
