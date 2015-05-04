package com.stkj.xtools;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BindFragment extends BaseFragment implements
        IBind<BindFragment> {

	private ViewBinder<BindFragment> mInjector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        com.stkj.xtools.Log.from(this, "onCreate");
		mInjector = new ViewBinder<BindFragment>(this);
	}

	@Override
	protected void onViewDidLoad(Bundle savedInstanceState) {
		mInjector.initViews();
		mInjector.bindClicks();
	}

	@Override
	public BindFragment getClassOwner() {
		return self();
	}

	public BindFragment self() {
		return this;
	}

	@Override
	public View id(int id) {
		return _rootView.findViewById(id);
	}

	/**
	 * retrieve and cast type
	 * 
	 * @param id
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T id(int id, Class<T> clz) {
		return (T) id(id);
	}

	public void log(Object o) {
		if (Constant.DBG) {
			Log.e(this.getClass().getName(), o.toString());
		}
	}
}
