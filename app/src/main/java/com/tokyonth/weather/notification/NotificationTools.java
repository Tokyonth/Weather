package com.tokyonth.weather.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;

import com.tokyonth.weather.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.model.WeatherModel;
import com.tokyonth.weather.model.WeatherModelImpl;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.sundry.DateUtil;

import org.litepal.crud.DataSupport;

import static android.app.Notification.VISIBILITY_SECRET;

public class NotificationTools implements OnWeatherListener {

    private Context context;
    private static NotificationTools utils;

    private String channel_id = BaseApplication.getContext().getResources().getString(R.string.app_name);
    private RemoteViews remoteViews;
    private NotificationCompat.Builder builder;

    public NotificationTools(Context context) {
        this.context = context;
        NotificationBrodcaseRecever.setmCallBack(key -> {
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
                    utils = new NotificationTools(context);
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
            channel.enableLights(false);
            //锁屏显示通知
            channel.setLockscreenVisibility(VISIBILITY_SECRET);
            channel.setSound(null, null);
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

        DefaultCity defaultCity = DataSupport.find(DefaultCity.class,1);
        if(defaultCity != null){
            WeatherModel weatherModel = new WeatherModelImpl();
            weatherModel.loadLocationWeather(defaultCity,this);
        } else {
            Log.d("-------->", "默认城市不存在");
        }

        builder = getNotificationBuilder();
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_custom_notifition);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_cust_ll, pendingIntent);

        builder.setOngoing(true);//设置无法取消
        builder.setAutoCancel(false);//点击后不取消
        builder.setCustomContentView(remoteViews);

    }

    @Override
    public void loadSuccess(Weather weather) {
        int weatherIconPath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        //Icon icon = Icon.createWithResource(BaseApplication.getContext(), weatherIconPath);
        //remoteViews.setImageViewIcon(R.id.notification_iv, icon);
        remoteViews.setImageViewResource(R.id.notification_iv, weatherIconPath);
        remoteViews.setTextViewText(R.id.city, weather.getInfo().getCityName());
        remoteViews.setTextViewText(R.id.weather, weather.getInfo().getWeather() + "\t" + weather.getInfo().getTemp() + "℃");
        remoteViews.setTextViewText(R.id.time, DateUtil.getSystemDate());

        NotificationUtil.getNotificationManager(context).notify(notificationId, builder.build());
    }

    @Override
    public void loadFailure(String msg) {

    }

    @Override
    public void loadOffline() {

    }

}

