package com.winca.jmtempcool;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

    private static CanData data = CanData.getData();
    private static AppWidgetManager mAppWidgetManager = null;
    private static Context mContext = null;
    private static GPSTracker _gpsTracker = null;

    public static GPSTracker getGpsTracker() {
        return _gpsTracker;
    }

    public static void update() {
        if (mAppWidgetManager != null) {
            RemoteViews var0 = new RemoteViews(mContext.getPackageName(), R.layout.widget);
            var0.setTextViewText(R.id.textView1, Integer.toString(data.GetOutSideT()) + "º");
            if (data.GetOutSideT() <= 12) {
                var0.setImageViewBitmap(R.id.imageView1, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.temperature_iconazul));
            } else if (data.GetOutSideT() > 12 && data.GetOutSideT() <= 21) {
                var0.setImageViewBitmap(R.id.imageView1, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.temperature_icon));
            } else if (data.GetOutSideT() > 21 && data.GetOutSideT() <= 29) {
                var0.setImageViewBitmap(R.id.imageView1, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.temperature_iconnaranja));
            } else {
                var0.setImageViewBitmap(R.id.imageView1, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.temperature_iconrojo));
            }
            if (_gpsTracker != null) {
                LocationData data = _gpsTracker.getLocationData();
                if (data != null) {
                    Log.v("widgetUpdate", "obtiene datos location --> " + data.get_name());
                    var0.setTextViewText(R.id.textView2, data.get_name() + "\n");
                }
            }
            ComponentName var1 = new ComponentName(mContext, MyWidget.class);
            mAppWidgetManager.updateAppWidget(var1, var0);
        }
    }

    public void onDeleted(Context var1, int[] var2) {
        super.onDeleted(var1, var2);
        if (_gpsTracker != null) {
            _gpsTracker = null;
        }
    }

    public void onDisabled(Context var1) {
        super.onDisabled(var1);
        if (_gpsTracker != null) {
            _gpsTracker = null;
        }
    }

    public void onEnabled(Context var1) {
        super.onEnabled(var1);
        _gpsTracker = new GPSTracker(var1);
    }

    public void onReceive(Context var1, Intent var2) {
        super.onReceive(var1, var2);
    }

    public void onUpdate(Context var1, AppWidgetManager var2, int[] var3) {
        mContext = var1;
        mAppWidgetManager = var2;
        if (_gpsTracker == null) {
            _gpsTracker = new GPSTracker(var1);
        }
        super.onUpdate(var1, var2, var3);
        update();
    }
}
