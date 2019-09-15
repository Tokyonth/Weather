package com.tokyonth.weather.notification;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationUtil {

    private static String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean isOpenPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API19+
            if (!NotificationUtil.isNotifacationEnabled( context )) {
                new AlertDialog.Builder( context )
                        .setIconAttribute( android.R.attr.alertDialogIcon )
                        .setTitle( "权限没有开" ).setMessage( "请手动打开通知栏的权限" )
                        .setPositiveButton( "确定", (dialog, which) -> {
                            Intent mIntent = new Intent()
                                    .setAction( "android.settings.APPLICATION_DETAILS_SETTINGS" )//启动权限设置
                                    .setData( Uri.fromParts( "package", context.getApplicationContext().getPackageName(), null ) );
                            context.startActivity( mIntent );
                        }).show();
                return false;
            }
        }
        return true;
    }

    /**
     * 判断通知栏的权限是否有打开
     *
     * @param con
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //表示versionCode=19 也就是4.4的系统以及以上的系统生效。4.4以下系统默认全部打开状态。
    public static boolean isNotifacationEnabled(Context con) {
        if (Build.VERSION.SDK_INT >= 24) {//7.0
            if (getNotificationManager( con ) != null) {
                return getNotificationManager( con ).areNotificationsEnabled();
            }
        }
        AppOpsManager mAppOps = (AppOpsManager) con.getSystemService( Context.APP_OPS_SERVICE );
        ApplicationInfo info = con.getApplicationInfo();
        String pag = con.getApplicationContext().getPackageName();
        int uid = info.uid;
        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName( AppOpsManager.class.getName() );
            Method meth = appOpsClass.getMethod( CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class );
            Field field = appOpsClass.getDeclaredField( OP_POST_NOTIFICATION );
            int value = (int) field.get( Integer.class );
            return ((int) meth.invoke( mAppOps, value, uid, pag ) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static NotificationManager mNotificationManager;

    public static NotificationManager getNotificationManager(Context context) {
        if (mNotificationManager == null) {
            synchronized (NotificationUtil.class) {
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
                }
            }
        }
        return mNotificationManager;
    }

}
