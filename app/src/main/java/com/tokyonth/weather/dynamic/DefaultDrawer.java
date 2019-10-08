package com.tokyonth.weather.dynamic;

import android.content.Context;
import android.graphics.Canvas;

public class DefaultDrawer extends BaseDrawer{

	public DefaultDrawer(Context context) {
		super(context, true);
	}

	@Override
	public boolean drawWeather(Canvas canvas, float alpha) {
		return false;
	}

	@Override
	protected int[] getSkyBackgroundGradient() {
		return SkyBackground.BLACK;
	}

}
