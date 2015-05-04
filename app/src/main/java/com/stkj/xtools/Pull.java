package com.stkj.xtools;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jarrah on 2015/5/4.
 * ABS network loader
 * @param <E>    Item type
 */
public abstract class Pull<E> implements PullToRefreshAdapterViewBase.OnRefreshListener, PullToRefreshAdapterViewBase.OnLastItemVisibleListener {

    private Http mHttp;
    private Class<E> mType;

    private CallBack<E> mRefreshCB;
    private CallBack<E> mLoadMoreCB;

    private Pack mRefreshPack;
    private Pack mLoadMorePack;


    /**
     * for Pull request
     */
    public static class Pack {
        String url;
        JSONObject jo;

        static Pack make(String url, JSONObject jo) {
            Pack request = new Pack();
            request.url = url;
            request.jo = jo;
            return request;
        }
    }

    public Pull(Class<E> type) {
        mHttp = new Http();
        mType = type;
        mRefreshPack = BuildRefresh();
        //callback for load
        mRefreshCB = getRefreshCallBack();
        //callback for load more
        mLoadMoreCB = getLoadMoreCallBack();
    }

    private CallBack<E> getLoadMoreCallBack() {
        return new CallBack<E>(mType) {
            @Override
            public void onResult(ArrayList<E> array) {
                super.onResult(array);
                onLoadMoreCallBack(array);
            }
        };
    }

    private CallBack<E> getRefreshCallBack() {
        return new CallBack<E>(mType) {
            @Override
            protected void escape(JSONObject response) {
                super.escape(response);
                mLoadMorePack = BuildLoadMore(response);
            }

            @Override
            public void onResult(ArrayList<E> array) {
                super.onResult(array);
                onRefreshCallBack(array);
            }
        };
    }

    /**
     * onLoadMore call back
     * @param array the call back result
     */
    protected abstract void onLoadMoreCallBack(ArrayList<E> array);

    /**
     * onRefresh call back
     * @param array the call back result
     */
    protected abstract void onRefreshCallBack(ArrayList<E> array);

    /**
     * for next request
     * @param response the response from refresh
     * @return request pack
     */
    protected abstract Pack BuildLoadMore(JSONObject response);

    /**
     * refresh request pack
     * @return
     */
    protected abstract Pack BuildRefresh();

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        Log.from(this, "on load");
        if (mRefreshPack == null) {
            Log.from(this, "check build load");
        } else {
            fetch(mRefreshPack.url, mRefreshPack.jo, mRefreshCB);
        }
    }

    private void fetch(String url, JSONObject jo, CallBack<E> cb) {
        mHttp.url(url);
        if (jo != null) {
            mHttp.JSON(jo).post(cb);
        } else {
            mHttp.get(cb);
        }
    }

    @Override
    public void onLastItemVisible() {
        Log.from(this, "on load more");
        if (mLoadMorePack == null) {
            Log.from(this, "check build load more");
        } else {
            fetch(mLoadMorePack.url, mLoadMorePack.jo, mLoadMoreCB);
        }
    }


    //Http callback handler
    public static class CallBack<T> extends HttpCallBack {

        private Class<T> type;

        public CallBack(Class<T> type) {
            this.type = type;
        }


        // correct logic
        protected boolean isResponseCorrect(JSONObject response) {
            return response != null;
        }

        @Override
        public void onResponse(JSONObject response) {
            super.onResponse(response);
            if (isResponseCorrect(response)) {
                Log.from(this, "on response correct");
                escape(response);
            } else {
                Log.from(this, "on response incorrect");
            }
        }

        /**
         * escape http response
         * @param response
         */
        protected void escape(JSONObject response) {
            JSONArray items = escapeResponse(response);
            ArrayList<T> array = new ArrayList<T>();
            if (items != null) {
                array = escapeItems(items, array);
                onResult(array);
            } else {
                Log.from(this, "escape items is null, return empty array");
            }
        }

        /**
         * escape items response
         * @param response
         * @return
         */
        private JSONArray escapeResponse(JSONObject response) {
            return response.optJSONArray("items");
        }

        protected ArrayList<T> escapeItems(JSONArray items, ArrayList<T> array) {
            for (int i = 0; i < items.length(); i++) {
                //item decode
                T item = JacksonWrapper.json2Bean(items.optJSONObject(i), type);

                //add item
                if (item != null) {
                    array.add(item);
                } else {
                    Log.from(this, String.format("item decode failure : obj >> %s", items.optJSONObject(i)));
                }
            }
            return array;
        }

        //for adapter view override
        public void onResult(ArrayList<T> array) {
        }
    }
}
