package com.fkdeepal.tools.ext.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.fkdeepal.tools.ext.ui.HudDisplayActivity
import timber.log.Timber

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("开机","BootReceiver receiver")
        if (context!=null && intent!=null){
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                Timber.i("开机启动")
                if (Settings.canDrawOverlays(context)){
                    Timber.i("开机启动,canDrawOverlays")
                    Handler(Looper.getMainLooper()).postDelayed({
                        runCatching {
                            Timber.i("开机启动,start HudDisplayActivity")
                            HudDisplayActivity.startActivity(context)
                        }.onFailure {
                            Timber.e(it,"开机启动,start HudDisplayActivity error")
                        }

                                                                },2000)
                }
            }
        }
    }
}