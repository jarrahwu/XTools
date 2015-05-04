package com.stkj.xtools;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityMain extends BindActivity {

    public static final String URL = "http://192.168.17.132:8888/apks";
    @Bind(id = R.id.list)
    PullToRefreshListView mListView;
    Adapter mAdapter;
    @Override
    protected int onLoadViewResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewDidLoad(Bundle savedInstanceState) {
        mAdapter = new Adapter(this);
        mListView.setAdapter(mAdapter);
        PullHelper helper = new PullHelper();
        helper.refresh(mListView, getPull());
    }

    private Pull<Item> getPull() {
        return new Pull<Item>(Item.class) {
            @Override
            protected void onLoadMoreCallBack(ArrayList<Item> array) {
                mListView.onRefreshComplete();
                mAdapter.addAll(array);
            }

            @Override
            protected void onRefreshCallBack(ArrayList<Item> array) {
                mListView.onRefreshComplete();
                mAdapter.clear();
                mAdapter.addAll(array);
            }

            @Override
            protected Pack BuildLoadMore(JSONObject response) {
                return Pack.make(URL, null);
            }

            @Override
            protected Pack BuildRefresh() {
                return Pack.make(URL, null);
            }
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Item {
        private String title;
        private String size;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    class Adapter extends AdapterWrapper<Item, TextView> {

        public Adapter(Context context) {
            super(context);
        }

        @Override
        protected void onBindView(int position, Item item, TextView view) {
            view.setText(String.format("APK : %S [size %s]", item.title, item.size));
        }

        @Override
        public TextView newView(int position, LayoutInflater lf, View convertView, ViewGroup parent) {
            return new TextView(getContext());
        }
    }

}
