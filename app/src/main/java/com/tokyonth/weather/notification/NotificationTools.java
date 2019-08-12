package com.tokyonth.weather.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.view.activity.MainActivity;

import static android.app.Notification.VISIBILITY_SECRET;

public class NotificationTools {

    private Context context;
    private static NotificationTools utils;

    private String channel_id = BaseApplication.getContext().getResources().getString(R.string.app_name);

    public NotificationTools(Context context) {
        this.context = context;
        ListenerNotificationBrodcaseRecever.setmCallBack(key -> {
            if (key == CLOSE_WEATHER_NOTIFICATION) {
                NotificationUtil.getNotificationManager(context).cancel(notificationId);
                isCreate = false;
            } else if (key == OPEN_WEATHER_NOTIFICATION) {
                sendCustomNotification();
            }
        });
    }

    public static NotificationTools getInstance(Context context) {
        if (utils == null) {
            synchronized (NotificationTools.class) {
                if (utils == null) {
                    utils = new NotificationTools( context );
                }
            }
        }
        return utils;
    }

    @NonNull
    public NotificationCompat.Builder getNotificationBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, channel_id,
                    NotificationManager.IMPORTANCE_DEFAULT );
            //是否绕过请勿打扰模式
           // channel.canBypassDnd();
            //闪光灯
            channel.enableLights( true );
            //锁屏显示通知
            channel.setLockscreenVisibility( VISIBILITY_SECRET );
            //闪关灯的灯光颜色
            channel.setLightColor( Color.RED );
            //桌面launcher的消息角标
            channel.canShowBadge();
            //是否允许震动
            channel.enableVibration( true );
            //获取系统通知响铃声音的配置
            channel.getAudioAttributes();
            //获取通知取到组
            channel.getGroup();
            //设置可绕过  请勿打扰模式
            channel.setBypassDnd( true );
            //设置震动模式
           // channel.setVibrationPattern( new long[]{100, 100, 200} );
            //是否会有灯光
            channel.shouldShowLights();
            NotificationUtil.getNotificationManager(context).createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channel_id);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setAutoCancel(true);
        return notification;
    }

    /**
     * 自定义布局。RemoteViews介绍：https://www.jianshu.com/p/13d56fb221e2
     */
    private static boolean isCreate = false;
    private int notificationId = 0;

    public static int CLOSE_WEATHER_NOTIFICATION = 10;
    public static int OPEN_WEATHER_NOTIFICATION = 11;


    public void sendCustomNotification() {
        if (!NotificationUtil.isOpenPermission(context)) {
            return;
        }

        if (isCreate) {
          //  Logs.eprintln( "只能创建一个自定义Notification" );
            isCreate = true;
            return;
        }
        NotificationCompat.Builder builder = getNotificationBuilder();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_custom_notifition );
        remoteViews.setTextViewText(R.id.city, "鄞州区");
        remoteViews.setTextViewText(R.id.weather, "多云 29 优");
        remoteViews.setTextViewText(R.id.time, "20:00");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    //    remoteViews.setOnClickPendingIntent( R.id.turn_next, pendingIntent );

        Intent intent2 = new Intent( "close" );//过滤action为close
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast( context, 0, intent2, 0 );
       // remoteViews.setOnClickPendingIntent( R.id.iv_close, pendingIntentClose );

        builder.setOngoing( true );//设置无法取消
        builder.setAutoCancel( false );//点击后不取消
        builder.setCustomContentView( remoteViews );
        NotificationUtil.getNotificationManager( context ).notify( notificationId, builder.build() );
    }


    /**
     * 自定义布局的按钮广播注册
     *
     * @param con
     */
    public static void registerBoradcastReceiver(Context con) {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction( "close" );//如果之定义布局有多个按钮则可以定义多个过滤条件
        myIntentFilter.addAction( "next" );
        myIntentFilter.addAction( "previous" );
        //注册广播
        con.registerReceiver( new ListenerNotificationBrodcaseRecever(), myIntentFilter );
    }
}

