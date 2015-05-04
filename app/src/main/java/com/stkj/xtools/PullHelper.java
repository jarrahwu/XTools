package com.stkj.xtools;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;

/**
 * Created by jarrah on 2015/4/29.
 */
public class PullHelper {

    private PullToRefreshAdapterViewBase base;

    public void refresh(PullToRefreshAdapterViewBase base, Pull refresh) {
        this.base = base;
        base.setOnRefreshListener(refresh);
        base.setOnLastItemVisibleListener(refresh);
    }
}
