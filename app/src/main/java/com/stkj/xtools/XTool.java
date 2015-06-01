package com.stkj.xtools;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;


public class XTool {

    private static XTool INSTANCE;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;


    private XTool(Context appContext) {
        mContext = appContext;
        mRequestQueue = Volley.newRequestQueue(appContext);
        useCookieStore();
        mImageLoader = new ImageLoader(this.mRequestQueue, Cache.getInstance());
    }

    public static final XTool init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new XTool(context);
        }
        return INSTANCE;
    }

    public static XTool getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("call init(context) first!");
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void cancelRequest(String tag) {
        mRequestQueue.cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public Context getContext() {
        if (mContext == null)
            throw new RuntimeException("call init(context) first");
        return mContext;
    }

    public void useCookieStore() {
        CookieStore cookieStore = new XTCookieStore(getContext());
        CookieManager manager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }
}
