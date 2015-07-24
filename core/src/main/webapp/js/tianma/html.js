/***
  eq:
    options = {element:'a',href:'http://www.tm.com',target:'_blank',text:'首页'};
	return : <a href='http://www.tm.com' target='_blank'>首页</a>
  **/
  function buildHtmlElement(options){
    var etag = options.tag;
	var eobj = $("<"+etag+" />");
	
	if(etag == 'form'){
	  eobj.prop("method","post");
	}
	
	setHtmlProperties(eobj,options);
	return eobj;
  }
  
  /**
  * eq.: jObj = $("<a></a>")
         props = {"href":"http://www.tm.com","text":"首页"}
	return
		<a href="http://www.tm.com">首页</a>
  *
  **/
  function setHtmlProperties(jObj,props){
    if(!(jObj || props)){
	  return;
	}

    $.each(props,function(key,value){
    	if(key == 'tag'){
    		return true;
    	}
    	
    	if(key == 'text'){
    		jObj.text(value);
    		return true;
    	}
    	
    	if(key == 'html'){
    		jObj.html(value);
    		return true;
    	}
    	
    	jObj.prop(key,value);
    });
    
	return jObj;
  }