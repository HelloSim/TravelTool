package com.sim.traveltool;

/**
 * @Auther Sim
 * @Time 2020/12/8 10:21
 * @Description 常量类
 */
public class AppHelper {

    public static final String SUCCESS = "0";


    //bugly
    public static final String Bugly_APPID = "c7dbd02c92";
    public static final String Bugly_APPKEY = "34b65563-6853-4b74-93c1-8d3175a815d0";

    //极光推送
    public static final String jPush_APPID = "63c1f262d4358f4acf9a0712";
    public static final String jPuh_MasterSecret = "a2d952b97ad9726c8b1ebb60";

    //Bmob后端云
    public static final String Bmob_ApplicationID = "62550b32bf5600010781ceeebc0e92ac";
    public static final String Bmob_RESTAPIKey = "dbc7ef19309b3a17895d70fcd2ba875b";
    public static final String Bmob_Secret_Key = "e35d936748f5a791";
    public static final String Bmob_Master_Key = "f88f9725ef5fb9df1faf9b44f493fdfd";


    //服务器地址
    public static final String USER_BASE_URL = "https://api.apiopen.top";//用户服务器地址
    public static final String USER_API_KEY = "cf9fb9e5ff32ad787d9d2ed3910ccc94";//用户服务器key

    public static final String BUS_BASE_URL = "http://www.zhbuswx.com";//公交服务器地址
    public static final String ROUTE_BASE_URL = "http://restapi.amap.com";//出行路线服务器地址

//    public static final String smzdm_BASE_URL = "https://homepage-api.smzdm.com";//什么值得买服务器地址
//    @GET("v1/home")
//    Observable <SmzdmDataBean> getHome(@Query("page") String page, @Query("limit") String limit, @Query("time") String time);


    //BUS搜索页面跳转
    public static final int RESULT_BUS = 1000;//跳转实时公交路线搜索
    public static final int RESULT_START_STATION = 10001;//跳转出行路线起点位置搜索
    public static final int RESULT_END_STATION = 1002;//跳转出行路线终点位置搜索


    //user用户信息相关
    public static final String userSpName = "userState";//sp文件名
    public static final String userSpStateKey = "isLogIn";//sp键名-是否登录
    public static final String userSpAccountNumber = "accountNumber";//sp键名-用户密码
    public static final String userSpPasswordKey = "password";//sp键名-用户密码
    public static final String userSpUserInfoKey = "userInfo";//sp键名-用户信息
    public static final int USER_IsLogIn = 2001;//已登录
    public static final int USER_noLogIn = 2002;//未登录
    public static final int USER_UpDate = 2003;//修改信息

}
