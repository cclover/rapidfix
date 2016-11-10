package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test$override implements IFunction {

    private static IFunction stub = null;

    @Override
    public String show() {
        return "This is test2";
    }

    @Override
    public int add(int x, int y) {
        return x+y+100;
    }

    @Override
    public String getInfo(){
        return new Module$override().toString();
    }


    @Override
    public String getInfo(IModule modle) {
        return modle.toString();
    }
}
