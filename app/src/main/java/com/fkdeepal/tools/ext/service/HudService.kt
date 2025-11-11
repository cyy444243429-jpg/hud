package com.fkdeepal.tools.ext.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class HudService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
    }
}