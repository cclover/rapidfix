package com.example.chengchao.instantfix;

/**
 * Created by chengchao on 2016/11/10.
 */

public class Module {

    private static Module $change = null;

    public String toString() {
        if($change != null){
            return $change.toString();
        }
        return "Module1";
    }
}
