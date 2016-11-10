package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test implements IFunction {

    private static IFunction stub = null;

    @Override
    public String show() {

        if(stub != null){
            return stub.show();
        }
        return "This is test1";
    }

    @Override
    public int add(int x, int y) {
        if(stub != null){
            return stub.add(x,y);
        }
        return x+y;
    }

    @Override
    public String getInfo(){
        if(stub != null){
            return stub.getInfo();
        }
        return new Module().toString();
    }

    @Override
    public String getInfo(IModule modle) {
        return modle.toString();
    }
}
