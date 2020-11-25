package com.sim.traveltool.internet;

import com.sim.traveltool.bean.BusRealTimeDataBean;
import com.sim.traveltool.bean.BusRealTimeLineDataBean;
import com.sim.traveltool.bean.BusRealTimeByLineDataBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Auther Sim
 * @Time 2019/4/22 1:05
 * @Description Bus 接口API类
 */
public interface BusAPIService {

    /**
     * 实时公交路线查询
     *
     * @param handlerName
     * @param key
     * @param time
     * @return
     */
    @GET("/Handlers/BusQuery.ashx")
    Observable<BusRealTimeLineDataBean> getLineListByLineName(@Query("handlerName") String handlerName, @Query("key") String key,
                                                              @Query("_") String time);

    /**
     * 公交路线查询
     *
     * @param handlerName
     * @param lineId
     * @param time
     * @return
     */
    @GET("/Handlers/BusQuery.ashx")
    Observable<BusRealTimeByLineDataBean> getStationList(@Query("handlerName") String handlerName, @Query("lineId") String lineId,
                                                         @Query("_") String time);

    /**
     * 实时公交查询
     *
     * @param handlerName
     * @param lineName
     * @param fromStation
     * @param time
     * @return
     */
    @GET("/Handlers/BusQuery.ashx")
    Observable<BusRealTimeDataBean> getBusListOnRoad(@Query("handlerName") String handlerName, @Query("lineName") String lineName,
                                                     @Query("fromStation") String fromStation, @Query("_") String time);

}