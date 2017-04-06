package com.example.xm.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.xm.finebiopane.IMyAidlInterface;
import com.example.xm.finebiopane.R;
import com.xm.Bean.MessageBean;

import xm.mina.Client;
import xm.mina.ClientCallBack;

/**
 * Created by liuwei on 2017/3/29.
 */

public class ListenerService extends Service {


    private MyBinder binder;
    private MyServiceConnection conn;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if(binder ==null){
            binder = new MyBinder();
        }
        conn = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ListenerService.this.bindService(new Intent(ListenerService.this, RemoteKeepLiveService.class), conn, Context.BIND_IMPORTANT);
//
//        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setTicker("核酸提取仪")
//                .setContentIntent(contentIntent)
//                .setContentTitle("")
//                .setAutoCancel(true)
//                .setContentText("")
//                .setWhen( System.currentTimeMillis());
//
//        //把service设置为前台运行，避免手机系统自动杀掉改服务。
//        startForeground(startId, builder.build());
//        startForeground(startId, new Notification());
        return START_STICKY;
    }



    class MyBinder extends IMyAidlInterface.Stub{

        @Override
        public String getProcessName() throws RemoteException {
            // TODO Auto-generated method stub
            return "LocalService";
        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ListenerService.this.startService(new Intent(ListenerService.this, RemoteKeepLiveService.class));
            ListenerService.this.bindService(new Intent(ListenerService.this, RemoteKeepLiveService.class), conn, Context.BIND_IMPORTANT);
        }

    }

}
