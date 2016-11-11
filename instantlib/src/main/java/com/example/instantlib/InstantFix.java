package com.example.instantlib;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by chengchao on 2016/11/10.
 */

public class InstantFix {

    public final static String TAG = "INSTANT_FIX";
    public final static String PATCH_NAME = "patch.jar";
    public final static String PATCH_LOADER_IMPL_CLASS_NAME = "com.example.instantlib.PatchesLoaderImpl";
    public static String patchFilePath = null;

    public static boolean patch(Context context){
        if(hasPatch(context)){
            Log.d(TAG, "patch");
            try {
                // load patch
                Log.d(TAG, "create the class loader");
                DexClassLoader patchClassLoader = new DexClassLoader(patchFilePath, context.getFilesDir().getAbsolutePath(), null, context.getClassLoader());

                Log.d(TAG, "Load the PatchesLoaderImpl");
                Class<?> clazz = patchClassLoader.loadClass(PATCH_LOADER_IMPL_CLASS_NAME);

                Log.d(TAG, "New the PatchesLoaderImpl");
                PatchesLoader loader = (PatchesLoader)clazz.newInstance();

                Log.d(TAG, "PatchesLoaderImpl classloader is : " + clazz.getClassLoader().toString());

                Log.d(TAG, "Invoke the load()");
                return loader.load();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean revertPatch(Context context){
        if(hasPatch(context)){
            Log.d(TAG, "revert patch");
            try {
                // load patch
                Log.d(TAG, "create the class loader");
                DexClassLoader patchClassLoader = new DexClassLoader(patchFilePath, context.getFilesDir().getAbsolutePath(), null, context.getClassLoader());

                Log.d(TAG, "Load the PatchesLoaderImpl");
                Class<?> clazz = patchClassLoader.loadClass(PATCH_LOADER_IMPL_CLASS_NAME);

                Log.d(TAG, "New the PatchesLoaderImpl");
                PatchesLoader loader = (PatchesLoader)clazz.newInstance();

                Log.d(TAG, "PatchesLoaderImpl classloader is : " + clazz.getClassLoader().toString());

                Log.d(TAG, "Invoke the unload()");
                return loader.unLoad();
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                removePatch(context);
            }
        }
        return false;
    }


    private static boolean hasPatch(Context context) {

        //Check if the path exist in app local folder
        File patchFile = new File(context.getFilesDir(), PATCH_NAME);
        if (patchFile.exists()) {
            patchFilePath = patchFile.getAbsolutePath();
            return true;
        }
        return false;
    }

    //for test
    public static boolean getPatchFromAsset(Context context){
        boolean ret = AssetUtils.hasAssetPatch(context, PATCH_NAME);
        if (ret) {
            try {
                //copy fix.patch from asset tp app file folder
                patchFilePath = AssetUtils.copyAsset(context, PATCH_NAME, context.getFilesDir());
                Log.d(TAG, "Copy patch file from asset to " + patchFilePath);
                return true;
            } catch (Exception ex) {
                Log.d("TAG", "Copy patch failed: " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

    //remove patch file
    public static boolean removePatch(Context context){
        File patchFile = new File(context.getFilesDir(), PATCH_NAME);
        if (patchFile.exists()) {
            return patchFile.delete();
        }
        return false;
    }
}
