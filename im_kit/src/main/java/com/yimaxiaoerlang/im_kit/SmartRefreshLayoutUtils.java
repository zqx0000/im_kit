package com.yimaxiaoerlang.im_kit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.header.MaterialHeader;

public class SmartRefreshLayoutUtils {

    public static void init() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(android.R.color.holo_blue_dark, android.R.color.white);
                return (RefreshHeader) new MaterialHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableArrowSize(20f);
            }
        });

    }

    public static void initRefresh(SmartRefreshLayout layout, OnRefreshListener refresh, OnLoadMoreListener loadmore) {
        layout.setEnableAutoLoadMore(false);
        layout.setEnableLoadMore(loadmore != null);
        layout.setEnableRefresh(true);
        if (refresh != null) {
            layout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    refresh.onRefresh(refreshLayout);
                    refreshLayout.finishRefresh();
                }
            });
        }
        if (loadmore != null) {
            layout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    loadmore.onLoadMore(refreshLayout);
                    refreshLayout.finishLoadMore();
                }
            });
        }
    }
}
