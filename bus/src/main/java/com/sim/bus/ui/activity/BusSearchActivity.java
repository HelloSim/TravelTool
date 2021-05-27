package com.sim.bus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sim.bean.BusLocationBean;
import com.sim.bean.BusRealTimeLineBean;
import com.sim.bus.R;
import com.sim.bus.adapter.BusLineNameAdapter;
import com.sim.bus.adapter.BusStationNameAdapter;
import com.sim.common.base.BaseActivity;
import com.sim.common.base.BaseAdapter;
import com.sim.common.base.BaseViewHolder;
import com.sim.common.utils.LogUtil;
import com.sim.common.utils.ToastUtil;
import com.sim.common.views.TitleView;
import com.sim.http.APIFactory;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * @author Sim --- 实时公交、出行线路站点的搜索页面
 */
public class BusSearchActivity extends BaseActivity {

    private TitleView titleView;
    private EditText tvSearch;
    private TextView tvNotFound;
    private RecyclerView rlData;

    private int searchType;

    private BusStationNameAdapter stationNameAdapter;
    private ArrayList<BusLocationBean.TipsBean> startLocationDataBeanList = new ArrayList<>();

    private ArrayList<BusRealTimeLineBean.DataBean> lineListByLineNameBeanList = new ArrayList<>();
    private BusLineNameAdapter busLineNameAdapter;
    private boolean hasResult = false;

    public static final int RESULT_BUS = 1000;//跳转实时公交路线搜索

    @Override
    protected int getLayoutRes() {
        return R.layout.bus_activity_search;
    }

    @Override
    protected void bindViews(Bundle savedInstanceState) {
        titleView = findViewById(R.id.titleView);
        tvSearch = findViewById(R.id.tv_search);
        tvNotFound = findViewById(R.id.tv_not_found);
        rlData = findViewById(R.id.rl_data);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void onClick(View leftView) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        searchType = getIntent().getIntExtra("searchType", RESULT_BUS);
    }

    @Override
    protected void initView() {
        if (searchType == 0) {
            initData();
        }
        if (searchType == RESULT_BUS) {
            rlData.setLayoutManager(new LinearLayoutManager(this));
            busLineNameAdapter = new BusLineNameAdapter(lineListByLineNameBeanList);
            busLineNameAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(BaseViewHolder holder, int position) {
                    Intent intent = new Intent(BusSearchActivity.this, BusRealTimeActivity.class);
                    intent.putExtra("busName", lineListByLineNameBeanList.get(position).getName());
                    intent.putExtra("lineId", lineListByLineNameBeanList.get(position).getId());
                    intent.putExtra("fromStation", lineListByLineNameBeanList.get(position).getFromStation());
                    intent.putExtra("toStation", lineListByLineNameBeanList.get(position).getToStation());
                    intent.putExtra("beginTime", lineListByLineNameBeanList.get(position).getBeginTime());
                    intent.putExtra("endTime", lineListByLineNameBeanList.get(position).getEndTime());
                    intent.putExtra("price", lineListByLineNameBeanList.get(position).getPrice());
                    startActivity(intent);
                }
            });
            rlData.setAdapter(busLineNameAdapter);
        } else {
            rlData.setLayoutManager(new LinearLayoutManager(this));
            stationNameAdapter = new BusStationNameAdapter(startLocationDataBeanList);
            stationNameAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(BaseViewHolder holder, int position) {
                    Intent intent = new Intent();
                    intent.putExtra("name", String.valueOf(startLocationDataBeanList.get(position).getName()));
                    setResult(searchType, intent);
                    finish();
                }
            });
            rlData.setAdapter(stationNameAdapter);
        }
        //editext的内容变化监听
        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchType == RESULT_BUS) {
                    if (editable == null || editable.toString().equals("")) {
                        tvNotFound.setVisibility(View.GONE);
                        rlData.setVisibility(View.GONE);
                    } else {
                        if (lineListByLineNameBeanList != null) {
                            lineListByLineNameBeanList.clear();
                        }
                        getLineListByLineName(editable.toString());
                    }
                } else {
                    if (editable == null || editable.toString().equals("")) {
                        rlData.setVisibility(View.GONE);
                    } else {
                        //内容变化请求数据
                        getStartLocation(editable.toString());
                    }
                }
            }
        });
    }

    /**
     * 搜索实时公交路线的网络请求
     *
     * @param key
     */
    private void getLineListByLineName(String key) {
        //这里做请求
        APIFactory.getInstance().getLineListByLineName(new Subscriber<BusRealTimeLineBean>() {
            @Override
            public void onCompleted() {
                if (!hasResult) {
                    rlData.setVisibility(View.GONE);
                    tvNotFound.setVisibility(View.VISIBLE);
                } else {
                    if (lineListByLineNameBeanList == null) {
                        rlData.setVisibility(View.GONE);
                        tvNotFound.setVisibility(View.VISIBLE);
                    } else {
                        tvNotFound.setVisibility(View.GONE);
                        rlData.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.toast(BusSearchActivity.this, "搜索实时公交路线请求出错");
                LogUtil.d(getClass(), "搜索实时公交路线出错: " + e);
            }

            @Override
            public void onNext(BusRealTimeLineBean data) {
                if (data.getFlag() == 1002) {
                    hasResult = true;
                    lineListByLineNameBeanList.addAll(data.getData());
                    busLineNameAdapter.notifyDataSetChanged();
                } else if (data.getFlag() == 1004) {
                    hasResult = false;
                }
            }
        }, "GetLineListByLineName", key);
    }

    /**
     * 搜索位置的网络请求
     *
     * @param keywords
     */
    public void getStartLocation(String keywords) {
        startLocationDataBeanList.clear();
        APIFactory.getInstance().getStartOrEndLocation(new Subscriber<BusLocationBean>() {
            @Override
            public void onCompleted() {
                stationNameAdapter.notifyDataSetChanged();
                rlData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.toast(BusSearchActivity.this, "位置请求出错！");
                LogUtil.d(getClass(), "位置请求出错: " + e);
            }

            @Override
            public void onNext(BusLocationBean busLocationDataBean) {
                startLocationDataBeanList.addAll(busLocationDataBean.getTips());
            }
        }, keywords);
    }

}
