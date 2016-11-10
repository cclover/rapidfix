package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test {

    private static Object $change = null;
    private static Boolean $obsolete = false;

    public String show() {

        if(!$obsolete && $change != null){
            return ((Test)$change).show();
        }
        return "This is test1";
    }

    public int add(int x, int y) {
        if(!$obsolete && $change != null){
            return ((Test)$change).add(x,y);
        }
        return x+y;
    }

    public String getInfo(){
        if(!$obsolete && $change != null){
            return ((Test)$change).getInfo();
        }
        return new Module().toString();
    }

    public String getInfo(Module modle) {
        if(!$obsolete && $change != null){
            return ((Test)$change).getInfo(modle);
        }
        return modle.toString();
    }
}
