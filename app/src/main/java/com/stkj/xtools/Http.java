package com.stkj.xtools;

import com.android.volley.RequestQueue;

public class Http extends AbstractHttp<Http> {

	public Http() {

	}

	public Http(String cancelTag) {
		requestTag(cancelTag);
	}

	@Override
	public RequestQueue getQueue() {
		return XTool.getInstance().getRequestQueue();
	}

	public static boolean isOK(int code) {
		boolean ret = false;
		switch (code) {
		case 0:
			ret = true;
			break;

		default:
			break;
		}
		return ret;
	}

}
