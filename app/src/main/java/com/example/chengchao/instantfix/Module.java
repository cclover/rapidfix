package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Module {

    private static Object $change = null;
    private static Boolean $obsolete = false;

    public String toString() {
        if(!$obsolete && $change != null){
            return ((Module)$change).toString();
        }
        return "Module1";
    }
}
