package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Test$override extends Test {

    private static Object $change = null;
    private static Boolean $obsolete = false;

    public String show() {

        if(!$obsolete && $change != null){
            return ((Test$override)$change).show();
        }
        return "This is test2";
    }

    public int add(int x, int y) {
        if(!$obsolete && $change != null){
            return ((Test$override)$change).add(x,y);
        }
        return x+y;
    }

    public String getInfo(){
        if(!$obsolete && $change != null){
            return ((Test$override)$change).getInfo();
        }
        return new Module().toString();
    }

    public String getInfo(Module modle) {

        if(!$obsolete && $change != null){
            return ((Test$override)$change).getInfo(modle);
        }
        return modle.toString();
    }
}
