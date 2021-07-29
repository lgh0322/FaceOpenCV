package com.bittle.myapplication;

import android.app.Application;
import android.content.Context;



import java.util.HashMap;


/**
 * Application实现类
 */
public class LaunchApplication extends Application {
    private static Application app = null;
    private static LaunchApplication mApplication;

    private String baseurl ;
    public static LaunchApplication getInstance(){
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;



    }





    /**
     * 获取Application的Context
     **/
    public static Context getAppContext() {
        if (app == null)
            return null;
        return app.getApplicationContext();
    }





}
