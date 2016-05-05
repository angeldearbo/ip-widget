package com.adearbo.ipwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;


public class MyWidget extends AppWidgetProvider {

    private static AppWidgetManager mAppWidgetManager = null;
    private static Context mContext = null;

    public void onDeleted(Context var1, int[] var2) {
        super.onDeleted(var1, var2);
    }

    public void onDisabled(Context var1) {
        super.onDisabled(var1);
        mContext.getApplicationContext().unregisterReceiver(receiver);
    }

    public void onEnabled(Context var1) {
        super.onEnabled(var1);
    }

    public void onReceive(Context var1, Intent var2) {
        super.onReceive(var1, var2);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mAppWidgetManager = appWidgetManager;
        mContext.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        update();
    }

    public void update() {
        if (mAppWidgetManager != null) {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget);
            ComponentName componentName = new ComponentName(mContext, MyWidget.class);
            remoteViews.setTextViewText(R.id.ipwifi_iptext, getWifiIp());

            mAppWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }

    private  String getWifiIp() {
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        Log.e("IP in Mask Integer", mWifiInfo.getIpAddress() + "");
        Log.e("IP Address", intToIP(mWifiInfo.getIpAddress()) + "");
        Log.e("ip-widget", "Supplicant state" + mWifiInfo.getSupplicantState());

        if(mWifiManager.isWifiEnabled() && (mWifiInfo.getSupplicantState() == SupplicantState.ASSOCIATED || mWifiInfo.getSupplicantState() == SupplicantState.COMPLETED)) {
            return mWifiInfo.getSSID() + " - " + intToIP(mWifiInfo.getIpAddress());
        }

        return "Disconnected";
    }

    public String intToIP(int i) {
        return (( i & 0xFF)+ "."+((i >> 8 ) & 0xFF)+
                "."+((i >> 16 ) & 0xFF)+"."+((i >> 24 ) & 0xFF));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ip-widget", "receiver");
            update();
        }
    };

}
