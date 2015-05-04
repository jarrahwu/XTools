package com.stkj.xtools;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseHttpRequest extends JsonObjectRequest {

	private Map<String, String> mHeader;

	public BaseHttpRequest(String url, JSONObject jsonRequest,
                           Listener<JSONObject> listener, ErrorListener errorListener) {
		super(url, jsonRequest, listener, errorListener);
		mHeader = new HashMap<String, String>();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeader;
	}

	public void addRequstHeader(String key, String value) {
		mHeader.put(key, value);
	}
}
