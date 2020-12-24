package com.sim.traveltool.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sim.baselibrary.utils.LogUtil;
import com.sim.baselibrary.utils.ToastUtil;
import com.sim.traveltool.R;
import com.sim.traveltool.adapter.BusRouteAdapter;
import com.sim.traveltool.base.AppActivity;
import com.sim.traveltool.bean.BusLocationDesignatedDataBean;
import com.sim.traveltool.bean.BusRouteDataBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @Time: 2020/6/10 22:57
 * @Author: HelloSim
 * @Description :显示出行方案的页面
 */
public class BusRouteActivity extends AppActivity {

    @BindView(R.id.tv_from_and_to_location)
    TextView tvFromAndToLocation;
    @BindView(R.id.rl_location_list)
    RecyclerView rlLocationList;

    private String tvStartLocation;//要显示的起始位置
    private String tvEndLocation;//要显示的终点位置
    private String origin;//用作请求的起始位置
    private String destination;//用作请求的终点位置


    private BusLocationDesignatedDataBean busLocationDesignatedDataBean;
    private ArrayList<BusRouteDataBean.RouteBean.TransitsBean> routeDataList = new ArrayList<>();
    private ArrayList<BusLocationDesignatedDataBean.PoisBean> startLocationList = new ArrayList<>();
    private ArrayList<BusLocationDesignatedDataBean.PoisBean> endLocationList = new ArrayList<>();
    private BusRouteAdapter routeAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bus_route;
    }

    protected void initData() {
        tvStartLocation = getIntent().getStringExtra("tvStartStation");
        tvEndLocation = getIntent().getStringExtra("tvEndStation");
        getLocation(true, tvStartLocation);
        getLocation(false, tvEndLocation);
    }

    @SuppressLint("SetTextI18n")
    protected void initView() {
        tvFromAndToLocation.setText(tvStartLocation + " -> " + tvEndLocation);

        routeAdapter = new BusRouteAdapter(this, routeDataList);
        rlLocationList.setLayoutManager(new LinearLayoutManager(this));
        routeAdapter.setOnItemClickListerer(new BusRouteAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(BusRouteActivity.this, BusRouteDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", routeDataList.get(position));
                bundle.putString("tvStartLocation", tvStartLocation);
                bundle.putString("tvEndLocation", tvEndLocation);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        rlLocationList.setAdapter(routeAdapter);
    }

    /**
     * 起始位置和终点位置的位置信息请求
     *
     * @param isStart
     * @param location
     */
    private void getLocation(boolean isStart, String location) {
        retrofitUtil.getLocation(new Subscriber<BusLocationDesignatedDataBean>() {
            @Override
            public void onCompleted() {
                if (isStart) {//起点
                    startLocationList.addAll(busLocationDesignatedDataBean.getPois());
                    origin = String.valueOf(startLocationList.get(0).getLocation());
                } else {//终点
                    endLocationList.addAll(busLocationDesignatedDataBean.getPois());
                    destination = String.valueOf(endLocationList.get(0).getLocation());
                }
                if (origin != null && destination != null) {
                    getRoute(origin, destination);
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(BusRouteActivity.class, "位置信息请求出错: " + e);
            }

            @Override
            public void onNext(BusLocationDesignatedDataBean dataBean) {
                busLocationDesignatedDataBean = dataBean;
            }
        }, location);
    }

    /**
     * 出行方案的网络请求
     *
     * @param origin
     * @param destination
     */
    BusRouteDataBean busRouteDataBean;

    private void getRoute(String origin, String destination) {
        retrofitUtil.getRoute(new Subscriber<BusRouteDataBean>() {
            @Override
            public void onCompleted() {
                if (routeDataList == null || routeDataList.size() == 0) {
                    ToastUtil.toast(BusRouteActivity.this, "未查询到换乘信息！");
                    finish();
                }
                routeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(BusRouteActivity.class, "出行方案请求出错: " + e);
            }

            @Override
            public void onNext(BusRouteDataBean dataBean) {
                routeDataList.addAll(dataBean.getRoute().getTransits());
            }
        }, origin, destination);
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

}
