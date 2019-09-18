package com.tokyonth.weather.view.sunrisesunsetview.formatter;

import com.tokyonth.weather.view.sunrisesunsetview.model.Time;

/**
 * 日出日落标签格式化
 */
public interface SunriseSunsetLabelFormatter {

    String formatSunriseLabel(Time sunrise);

    String formatSunsetLabel(Time sunset);

}
