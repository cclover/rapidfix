package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test {

    private static Object stub = null;

    public String show() {

        if(stub != null){
            return ((Test)stub).show();
        }
        return "This is test1";
    }

    public int add(int x, int y) {
        if(stub != null){
            return ((Test)stub).add(x,y);
        }
        return x+y;
    }

    public String getInfo(){
        if(stub != null){
            return ((Test)stub).getInfo();
        }
        return new Module().toString();
    }

    public String getInfo(Module modle) {
        return ((Test)stub).toString();
    }
}
