package com.example.chengchao.instantfix;

import android.util.Log;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test {

    private static Test $change = null;

    public String show() {
        if($change != null){
            return $change.show();
        }
        return "This is test1";
    }

    public int add(int x, int y) {
        if($change != null){
            return $change.add(x,y);
        }
        return x+y;
    }

    public String getInfo(){
        if($change != null){
            return $change.getInfo();
        }
        return new Module().toString();
    }

    public String getInfo(Module modle) {
        if($change != null){
            return $change.getInfo(modle);
        }
        return modle.toString();
    }
}
