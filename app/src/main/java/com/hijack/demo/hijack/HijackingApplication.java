package com.hijack.demo.hijack;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HijackingApplication {
    private static final String TAG = HijackingApplication.class.getSimpleName();
    private static List<String> packageList = new ArrayList<>();
    public static boolean hasProgressBeHijacked(String packageStr){
        if (packageList.contains(packageStr)){
            return true;
        }
        return false;
    }

    public static void addProgressHijacked(String packageStr){
        packageList.add(packageStr);
        Log.w(TAG,"packageList.size() = "+ packageList.size());
    }

    public static void clearProgressHijacked(){
        packageList.clear();
        Log.w(TAG,"packageList.size() = "+ packageList.size());
    }

}
