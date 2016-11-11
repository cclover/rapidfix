package com.example.chengchao.instantfix;

import android.content.Context;
import android.util.Log;

import com.example.instantlib.InstantFixApplication;

/**
 * Created by chengchao on 2016/11/11.
 */

public class TestApplication extends InstantFixApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("TEST", "This is TestApplication attachBaseContext1");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TEST", "This is TestApplication onCreate1");
    }
}
