package mobile.indoorbuy.com.opengles_learn_csdn.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by BMW on 2018/7/5.
 */

public class AlarmclockReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type",0);
        Log.e("tag","type = "+type);
    }
}