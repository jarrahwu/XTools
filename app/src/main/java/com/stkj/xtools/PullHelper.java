package com.stkj.xtools;

import com.stkj.xtools.pull2refresh.PullToRefreshAdapterViewBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jarrah on 2015/4/29.
 */
public class PullHelper {

    private static final String TAG = "PullHelper";

    public static void load(PullToRefreshAdapterViewBase base, Pull pull) {
        base.setOnRefreshListener(pull);
        base.setOnLastItemVisibleListener(pull);
    }


    public static <E> void easyLoad(final Pull.Pack pack, Class<E> cls, final ArrayAdapterCompat<E> adapterCompat, final PullToRefreshAdapterViewBase base) {

        Pull<E> pull = new Pull<E>(cls) {
            @Override
            protected void onLoadMoreCallBack(ArrayList<E> array) {
                base.onRefreshComplete();
                adapterCompat.addAll(array);
            }

            @Override
            protected void onRefreshCallBack(ArrayList<E> array) {
                base.onRefreshComplete();
                adapterCompat.clear();
                adapterCompat.addAll(array);
            }

            @Override
            protected Pack BuildLoadMore(JSONObject response) {
                return safeNextPack(response);
            }

            @Override
            protected Pack BuildRefresh() {
                return pack;
            }
        };
        base.setOnRefreshListener(pull);
        base.setOnLastItemVisibleListener(pull);
        pull.refresh();
    }

    private static Pull.Pack safeNextPack(JSONObject response) {
        Pull.Pack pack = new Pull.Pack();
        try {
            JSONArray links = response.optJSONArray("links");
            for (int i = 0; i < links.length(); i++) {
                JSONObject item = links.optJSONObject(i);
                String rel = item.optString("rel");
                if (rel.equals("next")) {
                    String href = item.optString("href");
                    pack = Pull.Pack.make(href, null);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("Parse JSON Error @ pull helper");
        }
        return pack;
    }

}
