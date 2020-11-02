package com.sim.traveltool.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sim.traveltool.R;
import com.sim.traveltool.adapter.news.NewsAdapter;
import com.sim.traveltool.bean.news.WangYiNewsBean;
import com.sim.traveltool.ui.activity.news.NewsDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Subscriber;

import static com.sim.traveltool.ui.activity.BaseActivity.retrofitUtil;

/**
 * “网易”Fragment
 * Created by Sim on 2020/4/27
 */
public class SpeedFragment extends Fragment {

    private RecyclerView newsRecyclerView;
    private ArrayList<WangYiNewsBean.ResultBean> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;

    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speed, container, false);
        initRecyclerView(view);
        getWangYiNew();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //初始化RecyclerView
    private void initRecyclerView(View root) {
        newsRecyclerView = root.findViewById(R.id.recycle_view);
        newsAdapter = new NewsAdapter(getActivity(), newsList);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsAdapter.setOnItemClickListerer(new NewsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("news", (Serializable) newsAdapter.getNews().get(i));
                startActivity(intent);
            }
        });
        newsRecyclerView.setAdapter(newsAdapter);

        //freshLayout控件下拉刷新
        refreshLayout = root.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList.clear();
                newsAdapter.notifyDataSetChanged();
                getWangYiNew();
            }
        });

        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {//RecyclerView最后一个item继续向下滑动
            boolean isSlidingToLast = false;//用来标记是否正在向最后一个滑动，既是否向下滑动

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();//获取最后一个完全显示的ItemPosition
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {// 判断是否滚动到底部
                        getWangYiNew();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向:大于0表示，正在向下滚动；小于等于0 表示停止或向上滚动
                isSlidingToLast = dy > 0;
            }
        });
    }

    /**
     * 获取网易新闻的网络请求
     */
    private void getWangYiNew() {
        retrofitUtil.getWangYiNew(new Subscriber<WangYiNewsBean>() {
            @Override
            public void onCompleted() {
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Sim", "getWangYiNewOnError: " + e);
            }

            @Override
            public void onNext(WangYiNewsBean wangYiNewsBean) {
                newsList.addAll(wangYiNewsBean.getResult());
                newsAdapter.notifyDataSetChanged();
            }
        }, String.valueOf((int) (0 + Math.random() * 1000)), "20");
    }

}
