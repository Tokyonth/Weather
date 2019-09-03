package com.tokyonth.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getSystemDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
