package com.stkj.xtools;

import com.android.volley.NetworkResponse;
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
		try {
			NetworkResponse response = error.networkResponse;
			Log.from(this, String.format("net work error %s >> %d", error.toString(), response.statusCode));
			switch (error.networkResponse.statusCode) {
				case 403:
					onAuthFailure(response);
					break;
				case 400:
					onMethodNotAllow(response);
					break;
				case 404:
					onUrlNotFound(response);
					break;
				case 500:
					onServerError(response);
					break;
				default:
					onDefaultError(response);
					break;
			}
		}catch (Exception e) {
			Log.from(this, "uncaught exception " + e + "|" + error);
			Util.toast(R.string.network_err);
		}
	}

	public void onAuthFailure(NetworkResponse response) {
		Util.toast(R.string.auth_failure);
	}

	public void onMethodNotAllow(NetworkResponse response) {
		Util.toast(R.string.network_err);
	}

	public void onUrlNotFound(NetworkResponse response) {
		Util.toast(R.string.network_err);
	}

	public void onServerError(NetworkResponse response) {
		Util.toast(R.string.server_error);
	}

	public void onDefaultError(NetworkResponse response) {
		Util.toast(R.string.network_err);
	}

}
