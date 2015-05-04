package com.stkj.xtools;

import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class HttpCallBack implements Listener<JSONObject>, ErrorListener {

	private static final String TAG = "HttpCallBack";

	@Override
	public void onResponse(JSONObject response) {

	}

	@Override
	public void onErrorResponse(VolleyError error) {
        Log.from(this, "net work error : " + error);
        Toast.makeText(XTool.getInstance().getContext(), R.string.network_err, Toast.LENGTH_SHORT).show();
	}

}
