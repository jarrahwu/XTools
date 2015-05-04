package com.stkj.xtools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BindView extends FrameLayout implements
        IBind<BindView> {

	protected LayoutInflater mLayoutInflater;
	
	protected View mContentView;
	
	private ViewBinder<BindView> mViewBinder;

	public BindView(Context context) {
		super(context);
		init();
	}

	public BindView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BindView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}


	private void init() {
		mLayoutInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		setContentView(onLoadViewResource());
		mViewBinder = new ViewBinder<BindView>(this);
		mViewBinder.initViews();
		mViewBinder.bindClicks();
		onViewDidLoad();
	}

	public abstract void onViewDidLoad();
	public abstract int onLoadViewResource();

	public void setContentView(int layoutId) {
		if (getChildCount() > 0)
			removeAllViews();
		mContentView = mLayoutInflater.inflate(layoutId, this, true);
	}

	@Override
	public BindView getClassOwner() {
		return this;
	}

	@Override
	public View id(int id) {
		return mContentView.findViewById(id);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T id(int id, Class<T> clz) {
		return (T) id(id);
	}
	
	public void bindClickEvent(View v, String methodName) {
		mViewBinder.bindClickEvent(v, methodName);
	}
}
