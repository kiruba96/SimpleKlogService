package com.example.nasa.klog;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 23-01-2018.
 */

public class MyService extends Service {
    EditText et;
    private String path = Environment.getExternalStorageDirectory().getPath()+"/";
    private RecursiveFileObserver observer = new RecursiveFileObserver(path, this);
    @Override
    public void onCreate() {
        super.onCreate();
        observer.startWatching();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand: started");
        return super.onStartCommand(intent, flags, startId);    }

    @Override
    public void onDestroy() {
        Log.d("Service", "stopped");
        super.onDestroy();
        observer.stopWatching();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
