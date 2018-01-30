package com.example.nasa.klog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


public class RecursiveFileObserver extends FileObserver {

    public static int CHANGES_ONLY = CLOSE_WRITE | MOVE_SELF | MOVED_FROM;

    private String TAG = "DOWNLOAD_OBSERVER";
    String b="ATTRIB",c="CLOSE_NOWRITE",d="CLOSE_WRITE",e="CREATE",f="DELETE",g="DELETE_SELF",h="MODIFY",i="MOVE_SELF",j="MOVED_FROM",k="MOVED_TO",l="OPEN";
    List<SingleFileObserver> mObservers;
    String mPath;
    int mMask;
    private Context serviceContext;

    public RecursiveFileObserver(String path , Context context) {
        this(path, ALL_EVENTS,context);
    }

    public RecursiveFileObserver(String path, int mask , Context context) {
        super(path, mask);
        mPath = path;
        mMask = mask;
        serviceContext = context;
    }

    @Override
    public void startWatching() {
        if (mObservers != null) return;
        mObservers = new ArrayList<SingleFileObserver>();
        Stack<String> stack = new Stack<String>();

        stack.push(mPath);
//        Log.d(TAG, "startWatching: " + mPath);

        while (!stack.empty()) {
            String parent = stack.pop();
            Log.d(TAG, "startWatching: "+parent);
            mObservers.add(new SingleFileObserver(parent, mMask));
            File path = new File(parent);
            File[] files = path.listFiles();
            if (files == null) continue;
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory() && !files[i].getName().equals(".")
                        && !files[i].getName().equals("..")) {
                    stack.push(files[i].getPath());
                }
            }
        }
        for (int i = 0; i < mObservers.size(); i++)
            mObservers.get(i).startWatching();
    }

    @Override
    public void stopWatching() {
        if (mObservers == null) return;

        for (int i = 0; i < mObservers.size(); ++i)
            mObservers.get(i).stopWatching();

        mObservers.clear();
        mObservers = null;
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.ACCESS:
                Log.i(TAG, "ACCESS: " + path);
                break;
            case FileObserver.ATTRIB:
                Log.i(TAG, "ATTRIB: " + path);
                record(path,b);
                break;
            case FileObserver.CLOSE_NOWRITE:
                Log.i(TAG, "CLOSE_NOWRITE: " + path);
                record(path,c);
                break;
            case FileObserver.CLOSE_WRITE:
                Log.i(TAG, "CLOSE_WRITE: " + path);
                record(path,d);
                break;
            case FileObserver.CREATE:
                Log.i(TAG, "CREATE: " + path);
                record(path,e);
                break;
            case FileObserver.DELETE:
                Log.i(TAG, "DELETE: " + path);
                record(path,f);
                break;
            case FileObserver.DELETE_SELF:
                Log.i(TAG, "DELETE_SELF: " + path);
                record(path,g);
                break;
            case FileObserver.MODIFY:
                Log.i(TAG, "MODIFY: " + path);
                record(path,h);
                break;
            case FileObserver.MOVE_SELF:
                Log.i(TAG, "MOVE_SELF: " + path);
                record(path,i);
                break;
            case FileObserver.MOVED_FROM:
                Log.i(TAG, "MOVED_FROM: " + path);
                record(path,j);
                break;
            case FileObserver.MOVED_TO:
                Log.i(TAG, "MOVED_TO: " + path);
                Log.d(TAG, "onEvent: launching apk");
                openApk(path);
                record(path,k);
                break;
            case FileObserver.OPEN:
                Log.i(TAG, "OPEN: " + path);
                record(path,l);
                break;
//            default:
//                Log.i(TAG, "DEFAULT(" + event + "): " + path);
//                break;
        }
    }
    public void record(String t1,String t2){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String fileName = formatter.format(now) + ".txt";//like 2016_01_12.txt
        try
        {
            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Klog",t2);
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);
            boolean c1 = t1.matches(".*\\b.lock\\b.*");
            boolean c2 =t1.matches(".*\\b.tmp\\b.*");
            if(!c1){
                if(!c2){
                    FileWriter writer = new FileWriter(gpxfile, true);
                    writer.append(t1 + "\n\n");
                    writer.flush();
                    writer.close();
                }

            }
            //Toast.makeText(this, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    private class SingleFileObserver extends FileObserver {
        private String mPath;

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + "/" + path;
            // Log.d(TAG, "onEvent: "+newPath);
            RecursiveFileObserver.this.onEvent(event, newPath);
        }

    }

    public void openApk(String path) {
        if(path.endsWith(".apk")) {
            Intent intent = new Intent(serviceContext.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("path",path);
            Log.d(TAG, "openApk: "+ path);
            serviceContext.startActivity(intent);
        }
    }

}
