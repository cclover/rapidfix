package com.example.instantlib;

/**
 * Created by chengchao on 2016/11/10.
 */

public class PatchesLoaderImpl extends AbstractPatchesLoaderImpl {

    @Override
    public String[] getPatchedClasses() {
        String[] list = {
            "com.example.chengchao.instantfix.Test$override",
            "com.example.chengchao.instantfix.Module$override"
        };
        return list;
    }
}
