var ARRAY = [];
var EMPTY = ARRAY[-1];
var LINE_BREAK = '[n]';

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

function build(json) {
	if (utils.isString(json)) {
		return $("<span />").text(json);
	} else if ($.isPlainObject(json)) {
		if (json.html) {
			return $(json.html);
		}
	} else if ($.isArray(json) && json.length > 0) {
		var type = json[0];
		if (utils.isString(type)) {
			return buildOne(json);
		} else {
			var comps = [];
			$.each(json, function() {
				var ccs = build(this);
				if ($.isArray(ccs)) {
					comps = comps.concat(ccs);
				} else {
					comps.push(ccs);
				}
			});

			return comps;
		}
	}

	return $("<span />");
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
		return Form(json);
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
	} else if (type === 'checkbox') {
		return buildCheckbox(json);
	} else if (type === 'bind') {
		return buildBind(json);
	} else if (type === 'ajax') {
		return buildAjax(json);
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

function ajax(reqData, doneFun) {
	if (reqData && !reqData.bid) {
		if (data && data.bid) {
			reqData.bid = data.bid;
		}
	}

	if (reqData && !reqData.fid) {
		if (data && data.fid) {
			reqData.fid = data.fid;
		}
	}

	$.ajax('/business/ajax.htm', {
		data : reqData,
		dataType : 'json',
		type : 'POST',
		async : false
	}).done(doneFun ? doneFun : function(result, status) {
		var js = '/business/js.htm?bid=' + reqData.bid + '&fid=' + reqData.fid;

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

function addChildren(parent, json) {
	if ($.isArray(json)) {
		parent.append(build(json));
	} else if ($.isPlainObject(json)) {
		if (json.bind) {
			toBind(parent, json.bind);
		}

		if (json.ajax) {
			var key = 'json';
			if (json.bind) {
				key = json.bind;
			}

			ajax(json.ajax, function(result, status) {
				parent.append(build(result[key]));
			});
		}

		var opts = utils.copy(json, {}, [ 'bind', 'ajax' ]);
		setHtmlProps(parent, opts);
	} else {
		parent.text(json);
	}

	return parent;
}

function setHtmlProps(element, opts) {
	if (opts.html) {
		element.append($(opts.html));
	}
	if (opts.text) {
		element.text(opts.text);
	}

	/**
	 * * read events
	 */
	var eveNames = [ 'click', 'mouseover', 'mouseout', 'change', 'focus' ];
	$.each(eveNames, function() {
		var event = opts[this];
		if (event) {
			utils.addEvent(element, this, event);
		}
	});
	if (opts.events) {
		$.each(opts.events, function(k, v) {
			utils.addEvent(element, k, v);
		});
	}

	// 剔除已经处理过的属性
	var excludes = [ 'html', 'text', 'events' ];
	excludes = excludes.concat(eveNames);
	opts = utils.copy(opts, {}, excludes);

	if (!utils.isString(opts) && $.isPlainObject(opts)) {
		$.each(opts, function(k, v) {
			element.prop(k, v);
		});
	}
}

function buildA(json) {
	var a = $("<a />");

	addChildren(a, json[1]);

	var reqs = json[2];
	var href = "/business/business.htm?";
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

function buildCheckbox(json) {
	if ($.isArray(json) && json.length > 1) {
		var props = json[1];

		if ($.isPlainObject(props)) {
			props.type = "checkbox";
			return TM_formBuilder.newInputElement(props);
		}
	}
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

function buildAjax(json) {
	var span = $("<span />");

	ajax(json[1], function(result, statuc) {
		var key = 'json';
		if (json.length >= 3) {
			key = json[2];
		}
		return span.append(build(result[key]));
	});

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
		} else{
			bid = '';
		}
		
		var href = '/business/business.htm?' + bid;
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

	// table properties
	if (json.length >= 4) {
		$.each(json[3], function(k, v) {
			table.prop(k, v);
		});
	}

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

