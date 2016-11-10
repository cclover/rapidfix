package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Module$override implements IModule{

    private static IModule stub = null;

    @Override
    public String toString() {
        if(stub != null){
           return stub.toString();
        }
        return "Module2";
    }
}
