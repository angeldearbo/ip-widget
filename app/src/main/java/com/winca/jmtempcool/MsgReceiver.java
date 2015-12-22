package com.winca.jmtempcool;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MsgReceiver extends BroadcastReceiver {

    private CanData data = CanData.getData();
    private Context mContext;
    private GPSTracker _gpsTracker = MyWidget.getGpsTracker();

    private void notifyOutsideT(String temperatura) {
        String temperatura_icon = (new StringBuilder("status_")).append(temperatura).toString().replace("-", "_").replace("º", "");
        int id_icon = mContext.getResources().getIdentifier(temperatura_icon, "drawable", mContext.getPackageName());
        String localidad = "";
        if (_gpsTracker != null) {
            LocationData data = _gpsTracker.getLocationData();
            if (data != null) {
                localidad = data.get_name();
            }
        }
        if (localidad == "") {
            localidad = mContext.getString(R.string.outsideT);
        }
        ((NotificationManager) mContext.getSystemService("notification")).notify(2, (new Builder(mContext)).setContentTitle(temperatura).setContentText(localidad).setSmallIcon(id_icon).getNotification());
    }

    public void onReceive(Context context, Intent intent) {
        byte var3 = 1;
        mContext = context;
        if (intent.getAction().compareTo("com.android.canbus.UPDATE") == 0) {
            byte[] var4 = intent.getByteArrayExtra("com.android.canbus.DATA");
            if (var4.length > 3 && var4[var3] == 24) {
                switch (var4[2]) {
                    case 1:
                        byte var5 = var4[4];
                        if (var4[3] != 0) {
                            var3 = -1;
                        }
                        int var6 = var5 * var3;
                        if (data.Outside_T != var6) {
                            data.Outside_T = var6;
                            notifyOutsideT(Integer.toString(data.Outside_T) + "º");
                            MyWidget.update();
                            AnalogClockWidgetProvider.update();
                            return;
                        }
                        break;
                }
            }
        }
    }
}
