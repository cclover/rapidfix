package com.example.instantlib;

import android.app.Application;
import android.content.Context;

/**
 * Created by chengchao on 2016/11/11.
 */

public class InstantFixApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //apply patch
        InstantFix.patch(base);
    }
}
