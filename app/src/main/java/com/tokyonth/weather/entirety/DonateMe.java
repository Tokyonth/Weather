package com.tokyonth.weather.entirety;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.view.widget.CustomDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class DonateMe {

    public static void show(final Context context) {
        if (context == null) return;
        View view = View.inflate(context, R.layout.donation_layout, null);
        CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(context.getResources().getString(R.string.title_donate));
        dialog.setCustView(view);
        dialog.setYesOnclickListener(context.getResources().getString(R.string.title_alipay), () -> {
            if (haveInstalledAlipay(context)) {
                jumpToAlipyScreen(context);
            } else {
                showSaveQRCodeDialog(context, R.drawable.alpay_qr);
            }
        });
        dialog.setNoOnclickListener(context.getResources().getString(R.string.title_wechat), () -> showSaveQRCodeDialog(context, R.drawable.wechart_qr));
        dialog.show();

    }

    private static void showSaveQRCodeDialog(Context context, int resId) {
        final ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        CustomDialog dialog = new CustomDialog(context);
        dialog.setCustView(imageView);
        dialog.setTitle("二维码");
        dialog.setNoOnclickListener(context.getResources().getString(android.R.string.cancel), () -> dialog.dismiss());
        dialog.setYesOnclickListener(context.getResources().getString(R.string.text_save_qr_code), () -> saveImage(imageView));
        dialog.show();
    }

    private static void saveBitmap(Bitmap mBitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        File qrCode = new File("/sdcard/Pictures", "qrcode.jpg");
        saveBitmap(bitmap, qrCode);
        imageView.setDrawingCacheEnabled(false);
        imageView.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + qrCode.getAbsolutePath())));
        Toast.makeText(imageView.getContext(), R.string.text_qr_code_saved, Toast.LENGTH_SHORT).show();
    }

    public static boolean haveInstalledAlipay(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void jumpToAlipyScreen(Context context) {
        String qrcode = URLEncoder.encode("https://qr.alipay.com/fkx03925kmlkahhjhsrcec1");
        String url = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

}