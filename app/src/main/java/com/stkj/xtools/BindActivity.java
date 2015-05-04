package com.stkj.xtools;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public abstract class BindActivity extends ActionBarActivity implements
        IBind<BindActivity> {

	private ViewBinder<BindActivity> mViewBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(onLoadViewResource());

		mViewBinder = new ViewBinder<BindActivity>(this);
		mViewBinder.initViews();
		mViewBinder.bindClicks();

		onViewDidLoad(savedInstanceState);
	}

	protected abstract int onLoadViewResource();

	protected abstract void onViewDidLoad(Bundle savedInstanceState);

	@Override
	public BindActivity getClassOwner() {
		return this;
	}

	@Override
	public View id(int id) {
		return findViewById(id);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T id(int id, Class<T> clz) {
		return (T) id(id);
	}
}
