package com.tokyonth.weather.view.sunrisesunsetview.formatter;

import com.tokyonth.weather.view.sunrisesunsetview.model.Time;

import java.util.Locale;

/**
 * SunriseSunsetLabelFormatter 简单实现
 */
public class SimpleSunriseSunsetLabelFormatter implements SunriseSunsetLabelFormatter {

    @Override
    public String formatSunriseLabel(Time sunrise) {
        return formatTime(sunrise);
    }

    @Override
    public String formatSunsetLabel(Time sunset) {
        return formatTime(sunset);
    }

    public String formatTime(Time time) {
        return String.format(Locale.getDefault(), "%d:%d", time.hour, time.minute);
    }

}
