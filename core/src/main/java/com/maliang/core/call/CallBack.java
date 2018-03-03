package com.maliang.core.call;

import java.io.Serializable;

public interface CallBack extends Serializable {
	public static final long serialVersionUID = -5809782578272943999L;
	
	public Object doCall();
}
