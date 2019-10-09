package com.tokyonth.weather.dynamic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

import com.tokyonth.weather.dynamic.BaseDrawer.Type;

public class DynamicWeatherView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = DynamicWeatherView.class.getSimpleName();
	private DrawThread mDrawThread;
	private SurfaceHolder surfaceHolder;
	private Canvas canvas;

    private BaseDrawer preDrawer, curDrawer;
    private float curDrawerAlpha = 0f;
    private Type curType = Type.DEFAULT;
    private int mWidth, mHeight;

	public DynamicWeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		curDrawerAlpha = 0f;
		mDrawThread = new DrawThread();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.RGBA_8888);
		mDrawThread.start();
	}

	private void setDrawer(BaseDrawer baseDrawer) {
		if (baseDrawer == null) {
			return;
		}
		curDrawerAlpha = 0f;
		if (this.curDrawer != null) {
			this.preDrawer = curDrawer;
		}
		this.curDrawer = baseDrawer;
	}

	public void setDrawerType(Type type) {
		if (type == null) {
			return;
		}
		if (type != curType) {
			curType = type;
			setDrawer(BaseDrawer.makeDrawerByType(getContext(), curType));
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
	}

	private boolean drawSurface(Canvas canvas) {
		final int w = mWidth;
		final int h = mHeight;
		if (w == 0 || h == 0) {
			return true;
		}
		boolean needDrawNextFrame = false;
		if (curDrawer != null) {
			curDrawer.setSize(w, h);
			needDrawNextFrame = curDrawer.draw(canvas, curDrawerAlpha);
		}
		if (preDrawer != null && curDrawerAlpha < 1f) {
			needDrawNextFrame = true;
			preDrawer.setSize(w, h);
			preDrawer.draw(canvas, 1f - curDrawerAlpha);
		}
		if (curDrawerAlpha < 1f) {
			curDrawerAlpha += 0.04f;
			if (curDrawerAlpha > 1) {
				curDrawerAlpha = 1f;
				preDrawer = null;
			}
		}
		return needDrawNextFrame;

	}

	public void onResume() {
		synchronized (mDrawThread) {
			mDrawThread.mRunning = true;
			mDrawThread.notify();
		}
		Log.i(TAG, "onResume");
	}

	public void onPause() {
		synchronized (mDrawThread) {
			mDrawThread.mRunning = false;
			mDrawThread.notify();
		}
		Log.i(TAG, "onPause");
	}

	public void onDestroy() {
		synchronized (mDrawThread) {
			mDrawThread.mQuit = true;
			mDrawThread.notify();
		}
		Log.i(TAG, "onDestroy");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (mDrawThread) {
			surfaceHolder = holder;
			mDrawThread.notify();
		}
		Log.i(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (mDrawThread) {
			surfaceHolder = holder;
			mDrawThread.notify();
			while (mDrawThread.mActive) {
				try {
					mDrawThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		holder.removeCallback(this);
		Log.i(TAG, "surfaceDestroyed");
	}

	private class DrawThread extends Thread {

		boolean mRunning;
		boolean mActive;
		boolean mQuit;

		@Override
		public void run() {
			while (true) {
				DrawOperation();
				synchronized (this) {
					while (surfaceHolder == null || !mRunning) {
						if (mActive) {
							mActive = false;
							notify();
						}
						if (mQuit) {
							return;
						}
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!mActive) {
						mActive = true;
						notify();
					}

					final long startTime = AnimationUtils.currentAnimationTimeMillis();
					final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
					final long needSleepTime = 16 - drawTime;

					if (needSleepTime > 0) {
						try {
							Thread.sleep(needSleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
	}

	private void DrawOperation() {
		try {
			canvas = surfaceHolder.lockCanvas();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                drawSurface(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

}
