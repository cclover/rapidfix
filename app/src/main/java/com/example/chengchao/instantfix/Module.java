package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Module {

    private static Object stub = null;

    public String toString() {
        if(stub != null){
            return ((Module)stub).toString();
        }
        return "Module2";
    }
}
