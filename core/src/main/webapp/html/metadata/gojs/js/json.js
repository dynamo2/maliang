/**
 * 注册
 * ***/
['form','account',[
	['$bid',,'hidden',bid],
	['$fid',,'hidden',2],
	['account','账号','\n'],
	['password','密码','\n']
]];

/**
 * 完善个人资料
 * **/
['form','account.personalProfile',[
	['$fid',,'hidden',2],  //以'$'开头的名称不加前缀prefix
	['realName','真实姓名','\n'],
	['sfz','身份证'],
	['email','Email','\n'],
	['mobile','手机'],
	['birthday','生日','date','\n'],
	['address','地址',['group',[
		['province','?省',['select',['江苏','浙江']]],
		['city','?市',['select',['南京','苏州','杭州']]],
		['zone','?区',['select',['鼓楼','秦淮','白下','玄武']]],
		['address',,'textarea','\n']]]
	,'\n']
]];

/**
 * 个人资料详情
 * **/
['table',[
	['真实姓名',app.real_name,'\n'],
	['Email',app.email,'\n'],
	['手机',app.mobile],
	['生日',app.birthday,'\n'],
	['地址',sum(each(app.address){this.province+'省'+this.city+'市'+this.zone+'区'+this.address}),'\n']
]];

/**
 * 个人资料详情列表
 * **/
['tableList',
 	['账号','密码','真实姓名','Email','手机号码','操作'],
 	each(accounts){[this.account,this.password,this.personal_profile.real_name,
 	                this.personal_profile.email,this.personal_profile.mobile,
 	                [['a','修改','bid='+bid+'&fid=4&id='+this.id],
 	                ['a','查看','bid='+bid+'&fid=6&id='+this.id]]
 	]}
]
 	
{
	title:'账号列表',
	html:'<p>
	            <a href="/business/business2.htm?bid='+bid+'">注册</a>&nbsp;&nbsp;
	            <a href="/business/business2.htm?bid='+bid+'&fid=3">列表</a>
	         </p>',
	json:['tableList',
	    ['账号','密码','真实姓名','Email','手机号码','操作'],
	    each(accounts){[this.account,this.password,this.personal_profile.real_name,
	            this.personal_profile.email,this.personal_profile.mobile,
	           {html:'<a href="/business/business2.htm?bid='+bid+'&fid=4&id='+this.id+'">修改</a>
	                     <a href="/business/business2.htm?bid='+bid+'&fid=6&id='+this.id+'">查看</a>'}]}
	]}

