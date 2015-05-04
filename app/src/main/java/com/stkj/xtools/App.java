package com.stkj.xtools;

import android.app.Application;

/**
 * Created by jarrah on 2015/4/29.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        XTool.init(getApplicationContext());
    }
}
